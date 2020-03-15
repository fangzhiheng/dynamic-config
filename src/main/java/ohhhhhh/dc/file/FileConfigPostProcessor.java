package ohhhhhh.dc.file;

import ohhhhhh.dc.AbstractConfigDescription;
import ohhhhhh.dc.AbstractConfigPostProcessor;
import ohhhhhh.dc.CommonConfigDescription;
import ohhhhhh.dc.ConfigRegistry;
import ohhhhhh.dc.util.FileWatcher;
import ohhhhhh.dc.util.ResourceUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 文件配置后置处理器
 *
 * @author fzh
 * @since 1.0
 */
public class FileConfigPostProcessor extends AbstractConfigPostProcessor<FileConfig> {

    private final Map<Path, FileReloadablePropertySource> registeredPropertySources = new ConcurrentHashMap<>();

    private final FileWatcher fileWatcher;

    public FileConfigPostProcessor(ConfigRegistry registry, FileWatcher fileWatcher) {
        super(registry, FileConfig.class);
        this.fileWatcher = fileWatcher;
        fileWatcher.onUpdate(path -> {
            FileReloadablePropertySource fileReloadablePropertySource = registeredPropertySources.get(path);
            if (fileReloadablePropertySource != null) {
                try {
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    fileReloadablePropertySource.reload(path);
                    stopWatch.stop();
                    if (logger.isInfoEnabled()) {
                        logger.info("reload {} for PropertySource {} success. [cost {}ms]", path, fileReloadablePropertySource.getName(), stopWatch.getLastTaskTimeMillis());
                    }
                } catch (IOException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("reload " + path + " for PropertySource " + fileReloadablePropertySource.getName() + " failed.", e);
                    }
                }
            }
        });
    }

    @Override
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object bean, FileConfig supportedAnnotation) throws IOException {
        String[] locations = supportedAnnotation.locations();
        if (locations.length != 0) {
            FileReloadablePropertySource reloadablePropertySource;
            Map<Path, Resource> resources = Arrays.stream(locations)
                    .map(ResourceUtils::getResource)
                    .filter(Resource::exists)
                    .collect(Collectors.toMap(resource -> {
                        try {
                            return Paths.get(resource.getFile().getPath());
                        } catch (IOException e) {
                            return null;
                        }
                    }, Function.identity()));
            try {
                reloadablePropertySource = new FileReloadablePropertySource(configName, resources);
                for (Path path : resources.keySet()) {
                    fileWatcher.watch(path);
                    registeredPropertySources.put(path, reloadablePropertySource);
                }
                bean = Enhancer.create(bean.getClass(),
                        new FileBeanGetterInterceptor(bean, supportedAnnotation.treatMemberNameAsPlaceholder(), supportedAnnotation.callInitiatedIfNotFound(), reloadablePropertySource));
            } catch (Exception e) {
                throw new BeanInitializationException("exception happened when create bean " + configName, e);
            }
            CommonConfigDescription description = new CommonConfigDescription(configName, bean);
            reloadablePropertySource.setReloadHandler(fileReloadablePropertySource -> description.touch());
            reloadablePropertySource.init();
            return description;
        } else {
            return new CommonConfigDescription(configName, bean);
        }
    }

}

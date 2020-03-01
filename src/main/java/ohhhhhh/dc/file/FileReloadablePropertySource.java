package ohhhhhh.dc.file;

import ohhhhhh.dc.ReloadablePropertySource;
import ohhhhhh.dc.util.FileWatcher;
import ohhhhhh.dc.util.PropertySourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class FileReloadablePropertySource extends ReloadablePropertySource {

    private static final Logger logger = LoggerFactory.getLogger(FileReloadablePropertySource.class);

    private static final Map<Path, FileReloadablePropertySource> registeredPropertySources = new ConcurrentHashMap<>();

    private static final FileWatcher fileWatcher = FileWatcher.getFileWatcher();

    static {
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

    private final Map<Path, Resource> resources;

    public FileReloadablePropertySource(String name, List<Resource> resources) throws Exception {
        super(name);
        Objects.requireNonNull(resources, getClass() + " need a nonnull resources.");
        this.resources = asMap(resources);
        init();
    }

    private Map<Path, Resource> asMap(List<Resource> resources) throws Exception {
        Map<Path, Resource> map = new HashMap<>();
        for (Resource resource : resources) {
            File file = resource.getFile();
            Path path = Paths.get(file.getPath());
            registeredPropertySources.put(path, this);
            fileWatcher.watch(path);
            map.put(path, resource);
        }
        return map;
    }

    private void init() throws IOException {
        // 1. load resources
        // 2. append to mutablePropertySources
        // 3. register reload handler
        for (Map.Entry<Path, Resource> entry : resources.entrySet()) {
            Path path = entry.getKey();
            Resource resource = entry.getValue();
            loadResource(path, resource);
        }
        onReload((reloadablePropertySource, path, resource) -> loadResource(path, resource));
    }

    private void loadResource(Path path, Resource resource) throws IOException {
        MapPropertySource propertySource = PropertySourceUtils.load(path.toString(), resource);
        getSource().add(propertySource);
    }

    private void reload(Path path) throws IOException {
        notifyReload(path, resources.get(path));
    }

}

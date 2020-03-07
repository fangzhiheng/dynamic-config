package ohhhhhh.dc.file;

import ohhhhhh.dc.ReloadablePropertySource;
import ohhhhhh.dc.util.PropertySourceUtils;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class FileReloadablePropertySource extends ReloadablePropertySource {

    private final Map<Path, Resource> resources;

    private Consumer<FileReloadablePropertySource> reloadHandler;

    public FileReloadablePropertySource(String name, Map<Path, Resource> resources) {
        super(name);
        Objects.requireNonNull(resources, getClass() + " need a nonnull resources.");
        this.resources = resources;
    }

    void setReloadHandler(Consumer<FileReloadablePropertySource> reloadHandler) {
        this.reloadHandler = reloadHandler;
    }

    void init() throws IOException {
        // 1. load resources
        // 2. append to mutablePropertySources
        // 3. register reload handler
        for (Map.Entry<Path, Resource> entry : resources.entrySet()) {
            Path path = entry.getKey();
            Resource resource = entry.getValue();
            loadResource(path, resource);
        }
        onReload((reloadablePropertySource, path, resource) -> {
            loadResource(path, resource);
            if (reloadHandler != null) {
                reloadHandler.accept(this);
            }
        });
    }

    private void loadResource(Path path, Resource resource) throws IOException {
        MapPropertySource propertySource = PropertySourceUtils.load(path.toString(), resource);
        getSource().remove(propertySource);
        getSource().add(propertySource);
    }

    void reload(Path path) throws IOException {
        notifyReload(path, resources.get(path));
    }

}

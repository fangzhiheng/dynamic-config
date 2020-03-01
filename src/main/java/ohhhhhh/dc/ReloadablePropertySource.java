package ohhhhhh.dc;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class ReloadablePropertySource extends PropertySource<Set<MapPropertySource>> {

    private ReloadHandler reloadHandler;

    public ReloadablePropertySource(String name) {
        super(name, new HashSet<>());
    }

    @Nullable
    @Override
    public Object getProperty(@NonNull String name) {
        for (MapPropertySource mapPropertySource : getSource()) {
            if (mapPropertySource.containsProperty(name)) {
                return mapPropertySource.getProperty(name);
            }
        }
        return null;
    }

    protected void notifyReload(Path path, Resource resource) throws IOException {
        if (reloadHandler != null) {
            reloadHandler.handle(this, path, resource);
        }
    }

    protected void onReload(ReloadHandler handler) {
        this.reloadHandler = handler;
    }

    @FunctionalInterface
    public interface ReloadHandler {

        void handle(ReloadablePropertySource reloadablePropertySource, Path path, Resource resource) throws IOException;

    }

}

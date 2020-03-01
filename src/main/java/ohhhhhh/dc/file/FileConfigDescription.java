package ohhhhhh.dc.file;

import ohhhhhh.dc.AbstractConfigDescription;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * TODO
 *
 * @author fzh
 * @since 1.0
 */
public class FileConfigDescription extends AbstractConfigDescription {

    public FileConfigDescription(String configName, Object config) {
        super(configName, config);
    }

    @Override
    protected Object doGetProperty(@NonNull String propertyName) {
        return null;
    }

    @Override
    protected void doSetProperty(@NonNull String propertyName, @Nullable Object value) {

    }

    @Override
    public Set<String> getPropertyKeys() {
        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return null;
    }

}

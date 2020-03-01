package ohhhhhh.dc;

import ohhhhhh.dc.util.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fzh
 * @since 1.0
 */
public class CommonConfigDescription extends AbstractConfigDescription {

    private final Map<String, PropertyDescriptor> properties;

    public CommonConfigDescription(String configName, Object config) {
        super(configName, config);
        this.properties = Arrays.stream(BeanUtils.getPropertyDescriptors(config.getClass()))
                .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
    }

    @Override
    protected Object doGetProperty(@NonNull String propertyName) {
        PropertyDescriptor descriptor = properties.get(propertyName);
        return descriptor == null ? null : ObjectUtils.retrieve(getConfig(), descriptor);
    }

    @Override
    protected void doSetProperty(@NonNull String propertyName, @Nullable Object value) {
        PropertyDescriptor descriptor = properties.get(propertyName);
        if (descriptor != null) {
            ObjectUtils.padding(getConfig(), descriptor, value);
        }
    }

    @Override
    public Set<String> getPropertyKeys() {
        return properties.keySet();
    }

    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> result = new HashMap<>();
        getPropertyKeys().forEach(key -> result.put(key, getProperty(key)));
        return result;
    }

}

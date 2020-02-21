package ohhhhhh.dc;

import ohhhhhh.dc.util.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fzh
 * @since 1.0
 */
public class ConfigDescription {

    private final String configName;

    private final Object config;

    private final Map<String, PropertyDescriptor> properties;

    private volatile long latestVersion = -1L;

    private Runnable afterUpdateHook = null;

    private Runnable afterRegisterHook = null;

    public ConfigDescription(String configName, Object config) {
        this.configName = configName;
        this.config = config;
        properties = Arrays.stream(BeanUtils.getPropertyDescriptors(config.getClass()))
                .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity()));
    }

    /**
     * get item from config
     *
     * @param itemName item name
     * @return item value
     */
    @Nullable
    public Object getItem(@NonNull String itemName) {
        PropertyDescriptor descriptor = properties.get(itemName);
        return descriptor == null ? null : ObjectUtils.retrieve(config, descriptor);
    }

    /**
     * set item for config
     *
     * @param itemName item name
     * @param value    item value
     */
    public void setItem(@NonNull String itemName, @Nullable Object value) {
        PropertyDescriptor descriptor = properties.get(itemName);
        if (descriptor != null) {
            ObjectUtils.padding(config, descriptor, value);
            touchLatestVersion();
        }
    }

    /**
     * whether the config contains the item
     *
     * @param itemName item name
     * @return true if the config contains the item
     */
    public boolean contains(@NonNull String itemName) {
        return properties.containsKey(itemName);
    }

    /**
     * update the latest version to current time.
     */
    protected void touchLatestVersion() {
        this.latestVersion = System.currentTimeMillis();
        if (afterUpdateHook != null) {
            afterUpdateHook.run();
        }
    }

    /**
     * @return the latest config version.
     */
    public long getLatestVersion() {
        return latestVersion;
    }

    /**
     * @return the config name.
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * @return the config instance.
     */
    public Object getConfig() {
        return config;
    }

    public void afterUpdate(Runnable runnable) {
        this.afterUpdateHook = runnable;
    }

    public void setAfterRegisterHook(Runnable registerHook) {
        this.afterRegisterHook = registerHook;
    }

    public Runnable getAfterRegisterHook() {
        return afterRegisterHook;
    }
}

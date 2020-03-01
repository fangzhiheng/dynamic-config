package ohhhhhh.dc;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * @author fzh
 * @since 1.0
 */
public abstract class AbstractConfigDescription {

    private final String configName;

    private final Object config;

    private volatile long latestVersion = -1L;

    private Runnable afterUpdateHook = null;

    private Runnable afterRegisterHook = null;

    public AbstractConfigDescription(String configName, Object config) {
        this.configName = configName;
        this.config = config;
    }

    /**
     * get property from config
     *
     * @param propertyName property name
     * @return property value
     */
    @Nullable
    public Object getProperty(@NonNull String propertyName) {
        return doGetProperty(propertyName);
    }

    protected abstract Object doGetProperty(@NonNull String propertyName);

    /**
     * set property for config
     *
     * @param propertyName property name
     * @param value        property value
     */
    public void setProperty(@NonNull String propertyName, @Nullable Object value) {
        doSetProperty(propertyName, value);
        touchLatestVersion();
    }

    protected abstract void doSetProperty(@NonNull String propertyName, @Nullable Object value);

    public abstract Set<String> getPropertyKeys();

    public abstract Map<String, Object> getProperties();

    /**
     * whether the config contains the property
     *
     * @param propertyName property name
     * @return true if the config contains the property
     */
    public boolean contains(@NonNull String propertyName) {
        return getProperty(propertyName) != null;
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

    public void setAfterUpdateHook(Runnable runnable) {
        this.afterUpdateHook = runnable;
    }

    public void setAfterRegisterHook(Runnable registerHook) {
        this.afterRegisterHook = registerHook;
    }

    public Runnable getAfterRegisterHook() {
        return afterRegisterHook;
    }

}

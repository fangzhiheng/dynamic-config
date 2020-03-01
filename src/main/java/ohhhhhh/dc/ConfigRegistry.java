package ohhhhhh.dc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzh
 * @since 1.0
 */
public class ConfigRegistry {

    private final Map<String, AbstractConfigDescription> configs = new HashMap<>();

    private final boolean allowOverride;

    private volatile long latestVersion = -1L;

    public ConfigRegistry() {
        this(false);
    }

    public ConfigRegistry(boolean allowOverride) {
        this.allowOverride = allowOverride;
    }

    public void register(String configName, AbstractConfigDescription config) {
        AbstractConfigDescription exist = configs.put(configName, config);
        if (exist != null && !allowOverride) {
            throw new IllegalStateException(String.format("config [%s] contains multiple[old: %s, new: %s].", configName, config, exist));
        }
        Runnable afterRegisterHook = config.getAfterRegisterHook();
        if (afterRegisterHook != null) {
            afterRegisterHook.run();
        }
        config.setAfterUpdateHook(() -> this.setLatestVersion(config.getLatestVersion()));
    }

    public AbstractConfigDescription getConfig(String configName) {
        return configs.get(configName);
    }

    public long getLatestVersion() {
        return this.latestVersion;
    }

    protected void setLatestVersion(long latestVersion) {
        this.latestVersion = latestVersion;
    }

}

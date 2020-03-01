package ohhhhhh.dc;

/**
 * @author fzh
 * @since 1.0
 */
public class CommonConfigPostProcessor extends AbstractConfigPostProcessor<Config> {

    protected CommonConfigPostProcessor(ConfigRegistry registry) {
        super(registry);
    }

    @Override
    protected Object enhanceConfigBean(String beanName, Object bean, Config configAnnotation) {
        // needn't to enhance
        return bean;
    }

    @Override
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object enhancedConfigBean) {
        return new CommonConfigDescription(configName, enhancedConfigBean);
    }

}

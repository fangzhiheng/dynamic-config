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
    protected AbstractConfigDescription resolveConfigDescription(String configName, Object bean, Config supportedAnnotation) {
        return new CommonConfigDescription(configName, bean);
    }

}

package ohhhhhh.dc.util;

/**
 * 所有工具类必须继承该类，以防止工具类实例化
 *
 * @author fzh
 * @since 1.0
 */
public abstract class Utils {

    protected Utils() {
        throw new IllegalStateException("Util class" + getClass() + " cannot be instantiated.");
    }

}

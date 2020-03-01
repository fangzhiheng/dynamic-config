package ohhhhhh.dc.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * @author fzh
 * @since 1.0
 */
public final class ResourceUtils extends Utils {

    private static final ResourcePatternResolver RESOURCE_LOADER = new PathMatchingResourcePatternResolver();

    @NonNull
    public static Resource[] getResources(@NonNull String location) throws IOException {
        return RESOURCE_LOADER.getResources(location);
    }

    @NonNull
    public static Resource getResource(@NonNull String location) {
        return RESOURCE_LOADER.getResource(location);
    }

}

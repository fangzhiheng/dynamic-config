package ohhhhhh.dc.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author fzh
 * @since 1.0
 */
public final class MapUtils extends Utils {

    public static <K, V> Map<K, V> merge(List<Map<K, V>> maps) {
        return merge(maps.stream(), HashMap::new);
    }

    public static <K, V> Map<K, V> merge(Stream<Map<K, V>> maps) {
        return merge(maps, HashMap::new);
    }

    public static <K, V> Map<K, V> merge(Stream<Map<K, V>> maps, Supplier<Map<K, V>> supplier) {
        Map<K, V> map = supplier.get();
        return maps.reduce(map, (m1, m2) -> {
            m1.putAll(m2);
            return m1;
        });
    }

}

package ohhhhhh.dc.util;

import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fzh
 * @since 1.0
 */
public final class PropertySourceUtils extends Utils {

    private static final String PROPERTIES_SUFFIX = ".properties";

    private static final String YAML_SUFFIX = ".yaml";

    private static final String YML_SUFFIX = ".yml";

    private static final String DEFAULT_KEY = "$$default$$";

    public static MapPropertySource load(String name, Resource resource) throws IOException {
        if (resource == null) {
            throw new IllegalArgumentException("resource must not be null.");
        }
        if (!resource.exists()) {
            throw new IllegalArgumentException("resource must exists.");
        }
        String filename = resource.getFilename();
        if (filename == null) {
            filename = resource.getFile().getName();
        }
        if (filename.endsWith(PROPERTIES_SUFFIX)) {
            return new PropertiesPropertySource(name, new Properties(), resource);
        } else if (filename.endsWith(YML_SUFFIX) || filename.endsWith(YAML_SUFFIX)) {
            return new YamlPropertySource(name, new Yaml(), resource);
        } else {
            throw new IllegalArgumentException("unsupported resource type " + filename);
        }
    }


    private static class PropertiesPropertySource extends MapPropertySourceAdapter {

        private PropertiesPropertySource(String name, Properties properties, Resource resource) throws IOException {
            super(name, new ConcurrentHashMap<>());
            init(properties, resource, getSource());
        }

        @SuppressWarnings("unchecked")
        private void init(Properties properties, Resource resource, Map<String, Object> source) throws IOException {
            properties.load(resource.getInputStream());
            for (String name : properties.stringPropertyNames()) {
                String[] segments = StringUtils.split(name, ".").toArray(new String[0]);
                Map<String, Object> map = source;
                for (int i = 0, segmentsLength = segments.length; i < segmentsLength - 1; i++) {
                    String segment = segments[i];
                    Map<String, Object> sub = (Map<String, Object>) map.get(segment);
                    if (sub == null) {
                        map.put(segment, new HashMap<>());
                        map = (Map<String, Object>) map.get(segment);
                    } else {
                        map = sub;
                    }
                }
                if (segments[segments.length - 1].equals(name)) {
                    (((Map<String, Object>) map.get(name))).put(DEFAULT_KEY, properties.get(name));
                } else
                    map.put(segments[segments.length - 1], properties.get(name));
            }
        }

    }

    private static class YamlPropertySource extends MapPropertySourceAdapter {

        private YamlPropertySource(String name, Yaml yaml, Resource resource) throws IOException {
            super(name, new ConcurrentHashMap<>());
            init(yaml, resource, getSource());
        }

        private void init(Yaml yaml, Resource resource, Map<String, Object> source) throws IOException {
            Object object = yaml.load(resource.getInputStream());
            source.putAll(asMap(object));
        }

        /**
         * @see org.springframework.beans.factory.config.YamlProcessor {@code #asMap(Object)}
         */
        @SuppressWarnings("unchecked")
        private Map<String, Object> asMap(Object object) {
            // YAML can have numbers as keys
            Map<String, Object> result = new LinkedHashMap<>();
            if (!(object instanceof Map)) {
                // A document can be a text literal
                result.put("document", object);
                return result;
            }

            Map<Object, Object> map = (Map<Object, Object>) object;
            map.forEach((key, value) -> {
                if (value instanceof Map) {
                    value = asMap(value);
                }
                if (key instanceof CharSequence) {
                    result.put(key.toString(), value);
                } else {
                    // It has to be a map key in this case
                    result.put("[" + key.toString() + "]", value);
                }
            });
            return result;
        }

    }

    private static abstract class MapPropertySourceAdapter extends MapPropertySource {

        public MapPropertySourceAdapter(String name, Map<String, Object> source) {
            super(name, source);
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean containsProperty(@NonNull String name) {
            List<String> segments = StringUtils.split(name, ".");
            Map<String, Object> map = getSource();
            Object o;
            for (Iterator<String> iterator = segments.iterator(); iterator.hasNext(); ) {
                String segment = iterator.next();
                o = map.get(segment);
                if (o == null) {
                    return false;
                }
                if (!iterator.hasNext()) {
                    return true;
                }
                if (o instanceof Map) {
                    map = (Map<String, Object>) o;
                }
            }
            return false;
        }

        @Nullable
        @SuppressWarnings("unchecked")
        public Object getProperty(@NonNull String name) {
            List<String> segments = StringUtils.split(name, ".");
            Map<String, Object> map = getSource();
            Object result = null;
            for (Iterator<String> iterator = segments.iterator(); iterator.hasNext(); ) {
                String segment = iterator.next();
                result = map.get(segment);
                if (result instanceof Map) {
                    map = (Map<String, Object>) result;
                    if (!iterator.hasNext()) {
                        return map.get(DEFAULT_KEY);
                    }
                } else if (result instanceof String) {
                    return resolvePlaceholder((String) result);
                } else if (result instanceof List) {
                    return ((List<Object>) result).stream()
                            .map(item -> item instanceof String ? resolvePlaceholder((String) item) : item)
                            .collect(Collectors.toList());
                }
            }
            return result;
        }

        private Object resolvePlaceholder(String str) {
            return PlaceholderUtils.resolve(str, s -> {
                Object o = getProperty(s);
                return o instanceof String || o == null ? (String) o : o.toString();
            });
        }

    }

}

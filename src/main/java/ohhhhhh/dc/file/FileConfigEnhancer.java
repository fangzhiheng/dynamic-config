package ohhhhhh.dc.file;

import ohhhhhh.dc.ReloadablePropertySource;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.Type;
import org.springframework.cglib.core.*;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.CallbackFilter;
import org.springframework.cglib.proxy.Factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fzh
 * @since 1.0
 */
public class FileConfigEnhancer<T> extends AbstractClassGenerator<T> {

    private static final String PROPERTY_SOURCE_FIELD = "CGLIB$PROPERTY_SOURCE";

    private static final Type RELOADABLE_PROPERTY_SOURCE = Type.getType(ReloadablePropertySource.class);

    private Class<?>[] interfaces;

    private CallbackFilter filter;

    private Callback[] callbacks;

    private Type[] callbackTypes;

    private boolean classOnly;

    private Class<?> superclass;

    private Class<?>[] argumentTypes;

    private Object[] arguments;

    private boolean useFactory = true;

    private Long serialVersionUID;

    private boolean interceptDuringConstruction = true;

    public interface FileConfigEnhancerKey {

        Object newInstance(String type, String[] interfaces,
                           CallbackFilter filter, Type[] callbackTypes,
                           Long serialVersionUID);

    }


    protected FileConfigEnhancer(Source source) {
        super(source);
    }

    @Override
    protected ClassLoader getDefaultClassLoader() {
        if (superclass != null) {
            return superclass.getClassLoader();
        } else if (interfaces != null) {
            return interfaces[0].getClassLoader();
        } else {
            return null;
        }
    }

    @Override
    protected Object firstInstance(Class type) {
        if (classOnly) {
            return type;
        } else {
            return createUsingReflection(type);
        }
    }

    private Object createUsingReflection(Class<?> type) {
        if (argumentTypes != null) {
            return ReflectUtils.newInstance(type, argumentTypes, arguments);
        } else {
            return ReflectUtils.newInstance(type);
        }
    }

    @Override
    protected Object nextInstance(Object instance) {
        Class<?> protoClass = (instance instanceof Class) ? (Class<?>) instance : instance.getClass();
        if (classOnly) {
            return protoClass;
        } else if (instance instanceof Factory) {
            if (argumentTypes != null) {
                return ((Factory) instance).newInstance(argumentTypes, arguments, callbacks);
            } else {
                return ((Factory) instance).newInstance(callbacks);
            }
        } else {
            return createUsingReflection(protoClass);
        }
    }

    @Override
    public void generateClass(ClassVisitor classVisitor) {
        Class<?> sc = (superclass == null) ? Object.class : superclass;
        if (TypeUtils.isFinal(sc.getModifiers())) {
            throw new IllegalArgumentException("Cannot subclass final class " + sc);
        }
        List<Constructor<?>> constructors = new ArrayList<>(Arrays.asList(sc.getDeclaredConstructors()));
        filterConstructors(sc, constructors);

        List<Method> actualMethods = new ArrayList<>();
        List<Method> interfaceMethods = new ArrayList<>();
        final Set<Object> forcePublic = new HashSet<>();
        getMethods(sc, interfaces, actualMethods, interfaceMethods, forcePublic);

        List<MethodInfo> methodInfoList = actualMethods.stream().map(method -> {
            int modifiers = Constants.ACC_FINAL
                    | (method.getModifiers()
                    & ~Constants.ACC_ABSTRACT
                    & ~Constants.ACC_NATIVE
                    & ~Constants.ACC_SYNCHRONIZED);
            if (forcePublic.contains(MethodWrapper.create(method))) {
                modifiers = (modifiers & ~Constants.ACC_PROTECTED) | Constants.ACC_PUBLIC;
            }
            return ReflectUtils.getMethodInfo(method, modifiers);
        }).collect(Collectors.toList());
        ClassEmitter e = new ClassEmitter(classVisitor);
        e.begin_class(Constants.V1_8,
                Constants.ACC_PUBLIC,
                getClassName(),
                Type.getType(sc),
                TypeUtils.getTypes(interfaces),
                Constants.SOURCE_FILE);

        List<MethodInfo> constructorInfo = constructors.stream()
                .map(ReflectUtils::getMethodInfo)
                .collect(Collectors.toList());

        e.declare_field(Constants.ACC_PRIVATE, PROPERTY_SOURCE_FIELD, RELOADABLE_PROPERTY_SOURCE, null);
        // TODO
    }

    protected void filterConstructors(Class<?> sc, List<Constructor<?>> constructors) {
        CollectionUtils.filter(constructors, new VisibilityPredicate(sc, true));
        if (constructors.size() == 0)
            throw new IllegalArgumentException("No visible constructors in " + sc);
    }

    private static void getMethods(Class<?> superclass, Class<?>[] interfaces, List<Method> methods, List<Method> interfaceMethods, Set<Object> forcePublic) {
        ReflectUtils.addAllMethods(superclass, methods);
        List<Method> target = (interfaceMethods != null) ? interfaceMethods : methods;
        if (interfaces != null) {
            for (Class<?> itf : interfaces) {
                if (itf != Factory.class) {
                    ReflectUtils.addAllMethods(itf, target);
                }
            }
        }
        if (interfaceMethods != null) {
            if (forcePublic != null) {
                forcePublic.addAll(MethodWrapper.createSet(interfaceMethods));
            }
            methods.addAll(interfaceMethods);
        }
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_STATIC));
        CollectionUtils.filter(methods, new VisibilityPredicate(superclass, true));
        CollectionUtils.filter(methods, new DuplicatesPredicate());
        CollectionUtils.filter(methods, new RejectModifierPredicate(Constants.ACC_FINAL));
    }

}

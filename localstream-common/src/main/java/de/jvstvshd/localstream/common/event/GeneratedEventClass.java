package de.jvstvshd.localstream.common.event;

import de.jvstvshd.localstream.common.network.util.LoadingMap;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

public class GeneratedEventClass {

    private static final Map<Class<? extends Event>, GeneratedEventClass> CACHE;

    private final MethodHandle constructor;

    private final MethodHandle[] setters;

    static {
        CACHE = LoadingMap.of(clazz -> {
            try {
                return new GeneratedEventClass(clazz);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static GeneratedEventClass generate(Class<? extends Event> event) {
        return CACHE.get(event);
    }

    public static void preGenerate() {
        for (Class<? extends Event> eventType : EventDispatcher.getKnownEventTypes())
            generate(eventType);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private GeneratedEventClass(Class<? extends Event> eventClass) throws Throwable {
        DynamicType.Builder.FieldDefinition.Optional.Valuable<AbstractEvent> valuable = null;
        TypeDescription forLoadedType = new TypeDescription.ForLoadedType(eventClass);
        String eventClassSuffix = eventClass.getName().substring(Event.class.getPackage().getName().length());
        String generatedClassName = GeneratedEventClass.class.getPackage().getName() + eventClassSuffix;
        System.out.println("generatedClassName = " + generatedClassName);
        DynamicType.Builder<AbstractEvent> builder = (new ByteBuddy(ClassFileVersion.JAVA_V8))
                .subclass(AbstractEvent.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                .name(generatedClassName).implement(new TypeDefinition[]{forLoadedType})
                .method(ElementMatchers.isAnnotatedWith(Param.class))
                .intercept(FieldAccessor.of(NamedElement.WithRuntimeName::getInternalName))
                .method(ElementMatchers.named("getEventType")
                        .and((ElementMatcher) ElementMatchers.returns(Class.class))
                        .and(ElementMatchers.takesArguments(0)))
                .intercept(FixedValue.value(forLoadedType))
                .method(ElementMatchers.named("mhl").and((ElementMatcher) ElementMatchers.returns(MethodHandles.Lookup.class))
                        .and(ElementMatchers.takesArguments(0)))
                .intercept(MethodCall.invoke(MethodHandles.class.getMethod("lookup"))).withToString();
        Method[] properties = Arrays.stream(eventClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Param.class))
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Param.class).value())).toArray(Method[]::new);
        for (Method method : properties) {
            System.out.println(method.getName());
            valuable = builder.defineField(method.getName(), method.getReturnType(), Visibility.PRIVATE);
        }
        Class<? extends AbstractEvent> generatedClass = valuable.make().load(GeneratedEventClass.class.getClassLoader()).getLoaded();
        this

                .constructor = MethodHandles.publicLookup().in(generatedClass).findConstructor(generatedClass, MethodType.methodType(void.class)).asType(MethodType.methodType(AbstractEvent.class));
        MethodHandles.Lookup lookup = ((AbstractEvent) this.constructor.invoke((Object) null)).mhl();
        this.setters = new MethodHandle[properties.length];
        for (int i = 0; i < properties.length; i++) {
            Method method = properties[i];
            this.setters[i] = lookup.findSetter(generatedClass, method.getName(), method.getReturnType())
                    .asType(MethodType.methodType(void.class, new Class[]{AbstractEvent.class, Object.class}));
        }
    }

    /*
    private GeneratedEventClass(Class<? extends Event> eventClass) throws Throwable {
        DynamicType.Builder.FieldDefinition.Optional.Valuable<AbstractEvent> valuable = null;
        TypeDescription forLoadedType = new TypeDescription.ForLoadedType(eventClass);
        String eventClassSuffix = eventClass.getName().substring(Event.class.getPackage().getName().length());
        String generatedClassName = GeneratedEventClass.class.getPackage().getName() + eventClassSuffix;
        DynamicType.Builder<AbstractEvent> builder = (new ByteBuddy(ClassFileVersion.JAVA_V8))
                .subclass(AbstractEvent.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS_OPENING)
                .name(generatedClassName).implement(new TypeDefinition[]{(TypeDefinition) forLoadedType})
                .method( ElementMatchers.isAnnotatedWith(Param.class))
                .intercept( FieldAccessor.of(NamedElement.WithRuntimeName::getInternalName))
                .method( ElementMatchers.named("getEventType")
                        .and((ElementMatcher) ElementMatchers.returns(Class.class))
                        .and( ElementMatchers.takesArguments(0)))
                .intercept( FixedValue.value( forLoadedType))
                .method( ElementMatchers.named("mhl").and((ElementMatcher) ElementMatchers.returns(MethodHandles.Lookup.class))
                        .and( ElementMatchers.takesArguments(0)))
                .intercept( MethodCall.invoke(MethodHandles.class.getMethod("lookup", new Class[0]))).withToString();
        Method[] properties = Arrays.stream(eventClass.getMethods()).filter(m -> m.isAnnotationPresent(Param.class)).sorted(Comparator.comparingInt(o -> o.getAnnotation(Param.class).value())).toArray(Method[]::new);
        for (Method method : properties) {
            valuable = builder.defineField(method.getName(), method.getReturnType(), new ModifierContributor.ForField[]{(ModifierContributor.ForField) Visibility.PRIVATE});
        }
        Class<? extends net.hitmc.masterserver.event.AbstractEvent> generatedClass = valuable.make().load(GeneratedEventClass.class.getClassLoader()).getLoaded();
        this

                .constructor = MethodHandles.publicLookup().in(generatedClass).findConstructor(generatedClass, MethodType.methodType(void.class, MasterServer.class)).asType(MethodType.methodType(net.hitmc.masterserver.event.AbstractEvent.class, MasterServer.class));
        MethodHandles.Lookup lookup = ((net.hitmc.masterserver.event.AbstractEvent)this.constructor.invoke((Object) null)).mhl();
        this.setters = new MethodHandle[properties.length];
        for (int i = 0; i < properties.length; i++) {
            Method method = properties[i];
            this.setters[i] = lookup.findSetter(generatedClass, method.getName(), method.getReturnType())
                    .asType(MethodType.methodType(void.class, new Class[]{net.hitmc.masterserver.event.AbstractEvent.class, Object.class}));
        }
    }
     */

    public Event newInstance(Object... properties) throws Throwable {
        if (properties.length != this.setters.length)
            throw new IllegalStateException("Unexpected number of properties. given: " + properties.length + ", expected: " + this.setters.length);
        AbstractEvent event = (AbstractEvent) this.constructor.invokeExact();
        for (int i = 0; i < this.setters.length; i++) {
            MethodHandle setter = this.setters[i];
            Object value = properties[i];
            setter.invokeExact(event, value);
        }
        return event;
    }
}

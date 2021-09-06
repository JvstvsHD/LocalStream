package de.jvstvshd.localstream.network.util;


import de.jvstvshd.localstream.network.packets.PacketBuffer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Deserializer {

    private static final Map<Class<?>, String> methods = new HashMap<Class<?>, String>() {
        {
            put(UUID.class, "UniqueId");
        }
    };

    public static <T> T deserialize(PacketBuffer buffer, Class<T> clazz) {
        List<Field> fields = new ArrayList<>();
        Object[] values;
        for (Field declaredField : clazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(NoSerialize.class))
                continue;
            fields.add(declaredField);
        }
        values = new Object[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            int finalI = i;
            getReadMethodForClass(fields.get(i).getType()).ifPresent(method -> {
                try {
                    values[finalI] = (method.invoke(buffer));
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        Class<?>[] classes = new Class[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = values[i].getClass();
        }
        for (Object value : values) {
            System.out.println(value);
        }
        try {
            return clazz.getDeclaredConstructor(classes).newInstance(values);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not decode class " + clazz.getName());
        }
    }

    private static <T> Optional<Method> getReadMethodForClass(Class<T> clazz) {
        try {
            return Optional.of(PacketBuffer.class.getDeclaredMethod(getReadMethodString(clazz)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private static String getReadMethodString(Class<?> clazz) {
        String prefix = "read";
        String methodName;
        String fullMethodName = "";
        methodName = methods.getOrDefault(clazz, clazz.getSimpleName());
        String first = methodName.substring(0, 1).toUpperCase();
        fullMethodName = prefix + first + methodName.substring(1);
        System.out.println("fullMethodName = " + fullMethodName);
        return fullMethodName;
    }
    /**
     * Marks a field non-(de)serializable. <br>
     * Fields with this Annotation will not bes (de)serialized via {@link Deserializer#deserialize(PacketBuffer, Class)}
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface NoSerialize {

    }
}

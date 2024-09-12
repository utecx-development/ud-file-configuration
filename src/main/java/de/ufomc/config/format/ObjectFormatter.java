package de.ufomc.config.format;

import de.ufomc.config.core.UDObject;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectFormatter {

    public static <T> T formateObject(Class<T> clazz, Object o) {
        try {

            T nestedObj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {

                for (Field oField : o.getClass().getDeclaredFields()) {

                    if (field.getName().equals(oField.getName())) {

                        oField.setAccessible(true);
                        field.setAccessible(true);

                        field.set(nestedObj, oField.get(o));

                    }

                }

            }

            return nestedObj;


        } catch (Exception e) {
            throw new RuntimeException("A problem appeared parsing " + clazz.getName(), e);
        }
    }

    public static Object objFromString(String type, String value) {

        return switch (type) {
            case "string", "object" -> value;

            case "int" -> Integer.parseInt(value);
            case "float" -> Float.parseFloat(value);
            case "long" -> Long.parseLong(value);
            case "boolean" -> Boolean.parseBoolean(value);

            default -> throw new RuntimeException("Unsupported type: " + type);
        };

    }

    @NonNull
    public static String type(final Object object) {
        return switch (object) {
            case String s -> "string";
            case Integer i -> "int";
            case Float f -> "float";
            case Map<?, ?> m -> {
                Object k = new ArrayList<>(m.keySet()).getFirst();
                Object v = new ArrayList<>(m.values()).getFirst();
                yield "map<" + type(k) + "," + type(v) + ">";
            }
            case List<?> l -> "list<" + type(l.getFirst()) + ">";
            default -> {
                //check if class extends UDObject (required for working with custom objects)
                if (!UDObject.class.isAssignableFrom(object.getClass())) {
                    throw new IllegalStateException("Unsupported type: " + object.getClass().getSimpleName());
                }
                yield "object";
            }
        };
    }

}

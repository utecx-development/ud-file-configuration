package dev.ufo.ufodata.core.format;

import dev.ufo.ufodata.core.UDObject;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class ObjectFormatter {

    /**
     * Formats a given object to a specific clazz if compatible
     * @param clazz To format this object to given class
     * @param object Input to work with
     * @return A newly formatted object
     * @param <T> generic type the return should have
     */
    @NonNull
    public static <T> T toObject(final Class<T> clazz, final Object object) {
        try {
            final T nestedObject = clazz.getDeclaredConstructor().newInstance();
            final Class<?> objectClass = object.getClass();

            //iterate through the given class fields
            for (final Field field : clazz.getDeclaredFields()) {
                //iterate through given objects fields
                for (final Field objectField : objectClass.getDeclaredFields()) {

                    //does the field match
                    if (field.getName().equals(objectField.getName())) {
                        objectField.setAccessible(true);
                        field.setAccessible(true);
                        field.set(nestedObject, objectField.get(object));
                        break; //we can close the inner for loop here!
                    }
                }
            }
            return nestedObject;
        } catch (final Exception exception) {
            throw new RuntimeException("An error occurred while parsing: '" + clazz.getName() + "'", exception);
        }
    }

    /**
     * Parse a given value to an object using the target type
     * @param type To cast / parse this format to the correct one.
     * @param value Input to work with
     * @return A newly formatted object
     */
    @NonNull
    public static Object toObject(final String type, final String value) {
        return switch (type) {
            case "string", "object" -> value;
            case "int" -> Integer.parseInt(value.trim());
            case "float" -> Float.parseFloat(value.trim());
            case "long" -> Long.parseLong(value.trim());
            case "boolean" -> Boolean.parseBoolean(value.trim());
            default -> throw new RuntimeException("Unsupported type: '" + type + "'");
        };
    }

    /**
     * Check of which type the given object is a part of
     * @param object object to check
     * @return the correct type
     */
    @NonNull
    public static String type(final Object object) {
        return switch (object) {
            case String s -> "string";
            case Integer i -> "int";
            case Float f -> "float";
            case Map<?, ?> m -> {
                final Object key = new ArrayList<>(m.keySet()).getFirst();
                final Object value = new ArrayList<>(m.values()).getFirst();
                yield "map<" + type(key) + "," + type(value) + ">";
            }
            case List<?> l -> "list<" + type(l.getFirst()) + ">";
            default -> {
                //check if class extends UDObject (required for working with custom objects)
                if (!UDObject.class.isAssignableFrom(object.getClass())) {
                    throw new RuntimeException("Unsupported type: '" + object.getClass().getSimpleName() + "'");
                }
                yield "object";
            }
        };
    }
}

package dev.ufo.ufodata.lib.checks;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

@UtilityClass
public final class CheckType {

    /**
     * Checks if a given type is a map or a list
     * @param type the given class
     * @return true if it's a map or a list
     */
    public static boolean isListOrMap(final Class<?> type) {
        return List.class.isAssignableFrom(type) ||
                Map.class.isAssignableFrom(type);
    }

    /**
     * Checks if a given type is not primitive
     * @param type the given class
     * @return true if it is a primitive type
     */
    public static boolean isNotPrimitive(final Class<?> type) {
        return !type.isPrimitive() &&
                type != String.class && //although a String is NOT a primitive - this is ok here.
                type != Integer.class &&
                type != Boolean.class &&
                type != Double.class &&
                type != Float.class &&
                type != Long.class &&
                type != Character.class &&
                type != Byte.class &&
                type != Short.class;
    }
}

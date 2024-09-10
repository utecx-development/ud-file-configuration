package de.ufomc.config.core;

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
        return type == Map.class || type == List.class;
    }

    public static boolean isPrimitive(Class<?> type) {
        return !type.isPrimitive() &&
                type != String.class &&
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

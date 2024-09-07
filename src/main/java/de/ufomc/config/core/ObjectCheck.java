package de.ufomc.config.core;

import java.util.List;
import java.util.Map;

public class ObjectCheck {

    public static boolean isListOrMap(Class<?> type) {
        return type == Map.class ||
                type == List.class;
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

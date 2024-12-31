package dev.ufo.io;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjSerializer {

    public static <V> V convert(Class<V> clazz, String json) {
        try {
            json = json.trim();
            return parseObject(clazz, json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <V> V parseObject(Class<V> clazz, String json) throws Exception {
        V instance = clazz.getDeclaredConstructor().newInstance();
        Map<String, String> keyValues = extractKeyValuePairs(json);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (keyValues.containsKey(fieldName)) {
                String value = keyValues.get(fieldName);
                if (isPrimitive(field.getType())) {
                    field.set(instance, parsePrimitive(field.getType(), value));
                } else if (field.getType().isAssignableFrom(List.class)) {
                    field.set(instance, parseList(value));
                } else if (field.getType().isAssignableFrom(Map.class)) {
                    field.set(instance, parseMap(value));
                } else {
                    field.set(instance, parseObject(field.getType(), value));
                }
            }
        }

        return instance;
    }

    private static Map<String, String> extractKeyValuePairs(String json) {
        Map<String, String> keyValues = new HashMap<>();
        json = json.substring(1, json.length() - 1).trim();

        String currentKey = null;
        boolean inQuotes = false;
        boolean isKey = true;
        int braceCount = 0;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{' || c == '[') {
                    braceCount++;
                } else if (c == '}' || c == ']') {
                    braceCount--;
                } else if (c == ':' && isKey && braceCount == 0) {
                    currentKey = buffer.toString().trim().replaceAll("^\"|\"$", "");
                    buffer.setLength(0);
                    isKey = false;
                    continue;
                } else if (c == ',' && braceCount == 0) {
                    keyValues.put(currentKey, buffer.toString().trim());
                    buffer.setLength(0);
                    isKey = true;
                    continue;
                }
            }

            buffer.append(c);
        }

        if (currentKey != null && !buffer.isEmpty()) {
            keyValues.put(currentKey, buffer.toString().trim());
        }

        return keyValues;
    }

    private static Object parsePrimitive(Class<?> type, String value) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == String.class) {
            return value.replaceAll("^\"|\"$", "");
        }
        throw new IllegalArgumentException("Unsupported primitive type: " + type);
    }

    private static List<Object> parseList(String json) throws Exception {

        json = json.substring(1, json.length() - 1).trim();
        List<Object> list = new ArrayList<>();
        int braceCount = 0;
        boolean inQuotes = false;
        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (!inQuotes) {
                if (c == '{' || c == '[') {
                    braceCount++;
                } else if (c == '}' || c == ']') {
                    braceCount--;
                } else if (c == ',' && braceCount == 0) {
                    list.add(parseValue(buffer.toString().trim()));
                    buffer.setLength(0);
                    continue;
                }
            }

            buffer.append(c);
        }

        if (!buffer.isEmpty()) {
            list.add(parseValue(buffer.toString().trim()));
        }

        return list;
    }

    private static Map<String, Object> parseMap(String json) throws Exception {
        Map<String, String> keyValues = extractKeyValuePairs(json);
        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<String, String> entry : keyValues.entrySet()) {
            map.put(entry.getKey(), parseValue(entry.getValue()));
        }

        return map;
    }

    private static Object parseValue(String value) throws Exception {
        value = value.trim();
        if (value.startsWith("{")) {
            return parseMap(value);
        } else if (value.startsWith("[")) {
            return parseList(value);
        } else if (value.startsWith("\"")) {
            return value.replaceAll("^\"|\"$", "");
        } else {
            try {
                if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                    return Boolean.parseBoolean(value);
                } else if (value.contains(".")) {
                    return Double.parseDouble(value);
                } else {
                    return Integer.parseInt(value);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Unsupported primitive type for value: " + value, e);
            }
        }
    }

    private static boolean isPrimitive(Class<?> type) {
        return type.isPrimitive() || type == String.class || Number.class.isAssignableFrom(type) || type == Boolean.class;
    }
}

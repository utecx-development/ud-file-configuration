package dev.ufo.io;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ObjSerializer {

    public static <V> V convert(Class<V> clazz, String s) {
        try {
            if (s.startsWith("{") && s.endsWith("}")) {
                s = s.substring(1, s.length() - 1);
            }

            V instance = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();

            Map<String, List<String>> keyValuePairs = parseKeyValuePairs(s);

            for (Field field : fields) {
                String fieldName = field.getName();
                field.setAccessible(true);

                if (keyValuePairs.containsKey(fieldName)) {
                    String value = keyValuePairs.get(fieldName).get(0);
                    Object parsedValue = parseValue(field.getType(), value, field);
                    field.set(instance, parsedValue);
                } else {
                    field.set(instance, getDefaultValue(field.getType()));
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while serializing '" + s + "'", e);
        }
    }

    private static Map<String, List<String>> parseKeyValuePairs(String s) {
        Map<String, List<String>> map = new LinkedHashMap<>();
        int braceLevel = 0;
        int mapLevel = 0;
        int listLevel = 0;
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        boolean readingKey = true;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (readingKey) {
                if (c == '=') {
                    readingKey = false;
                } else {
                    keyBuilder.append(c);
                }
            } else {
                if (c == '{') braceLevel++;
                if (c == '}') braceLevel--;
                if (c == '<') mapLevel++;
                if (c == '>') mapLevel--;
                if (c == '[') listLevel++;
                if (c == ']') listLevel--;

                if (c == ',' && braceLevel == 0 && mapLevel == 0 && listLevel == 0) {
                    String key = keyBuilder.toString().trim();
                    String value = valueBuilder.toString().trim();
                    map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
                    keyBuilder.setLength(0);
                    valueBuilder.setLength(0);
                    readingKey = true;
                } else {
                    valueBuilder.append(c);
                }
            }
        }

        if (!keyBuilder.isEmpty() && !valueBuilder.isEmpty()) {
            String key = keyBuilder.toString().trim();
            String value = valueBuilder.toString().trim();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        return map;
    }

    private static Object parseValue(Class<?> type, String value, Field field) {
        try {
            if (value.startsWith("\"") && value.endsWith("\"")) {
                return value.substring(1, value.length() - 1);
            }

            if (type == String.class) {
                return value;
            } else if (type == int.class || type == Integer.class) {
                return Integer.parseInt(value);
            } else if (List.class.isAssignableFrom(type)) {

                //LIST

                if (value.startsWith("[") && value.endsWith("]")) {
                    String inner = value.substring(1, value.length() - 1);
                    String[] elements = splitElements(inner);
                    Type genericType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    Class<?> listClass = Class.forName(genericType.getTypeName());
                    List<Object> list = new ArrayList<>();
                    for (String element : elements) {
                        list.add(parseValue(listClass, element.trim(), null));
                    }
                    return list;
                }
            } else if (Map.class.isAssignableFrom(type)) {

                //MAP

                if (value.startsWith("<") && value.endsWith(">")) {
                    String inner = value.substring(1, value.length() - 1);
                    String[] elements = splitElements(inner);
                    Map<Object, Object> map = new HashMap<>();
                    Type[] genericTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
                    Class<?> keyClass = Class.forName(genericTypes[0].getTypeName());
                    Class<?> valueClass = Class.forName(genericTypes[1].getTypeName());
                    for (String element : elements) {
                        String[] keyValue = element.split("-", 2);
                        Object key = parseValue(keyClass, keyValue[0].trim(), null);
                        Object mapValue = parseValue(valueClass, keyValue[1].trim(), null);
                        map.put(key, mapValue);
                    }
                    return map;
                }
            } else if (value.startsWith("{") && value.endsWith("}")) {
                return convert(type, value);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static String[] splitElements(String s) {
        List<String> elements = new ArrayList<>();
        int braceLevel = 0, mapLevel = 0, listLevel = 0;
        StringBuilder current = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (c == '{') braceLevel++;
            if (c == '}') braceLevel--;
            if (c == '<') mapLevel++;
            if (c == '>') mapLevel--;
            if (c == '[') listLevel++;
            if (c == ']') listLevel--;

            if (c == ',' && braceLevel == 0 && mapLevel == 0 && listLevel == 0) {
                elements.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            elements.add(current.toString());
        }

        return elements.toArray(new String[0]);
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) return false;
            if (type == char.class) return '\0';
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0.0f;
            if (type == double.class) return 0.0;
        } else if (Map.class.isAssignableFrom(type)) {
            return new HashMap<>();
        } else if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        }
        return null;
    }

}

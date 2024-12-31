package dev.ufo.io;

import java.util.List;
import java.util.Map;

public class ListingAdapter {

    /**
     * Used to parse items from a json string to a list
     * @param json the json string input
     * @param tClazz the list type
     * @param result the list, the items are being transferred to
     * @param <T> that´s the generic for the list type
     */
    static <T> void parseJsonToList(String json, Class<T> tClazz, List<T> result) {
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            String[] items = json.substring(1, json.length() - 1).split(",");
            for (String item : items) {
                result.add(convertStringToType(item.trim(), tClazz));
            }
        } else {
            throw new IllegalArgumentException("Invalid JSON array format: " + json);
        }
    }

    /**
     * Used to parse items from a json string to a list
     * @param json the json string input
     * @param vClazz the map type
     * @param result the map, the items are being transferred to
     * @param <V> that´s the generic for the map type
     */
    static <V> void parseJsonToMap(String json, Class<V> vClazz, Map<String, V> result) {
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            String[] entries = json.substring(1, json.length() - 1).split(",");
            for (String entry : entries) {
                String[] keyValue = entry.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replaceAll("^\"|\"$", "");
                    V value = convertStringToType(keyValue[1].trim(), vClazz);
                    result.put(key, value);
                } else {
                    throw new IllegalArgumentException("Invalid JSON object format: " + json);
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid JSON object format: " + json);
        }
    }

    /**
     * Used to parse items from a json string to a list
     * @param value the json string input
     * @param tClazz the type
     * @param <T> that´s the generic for the type
     * @return the result after casting
     */
    static <T> T convertStringToType(String value, Class<T> tClazz) {
        if (tClazz == String.class) {
            return tClazz.cast(value.replaceAll("^\"|\"$", ""));
        } else if (tClazz == Integer.class) {
            return tClazz.cast(Integer.valueOf(value));
        } else if (tClazz == Double.class) {
            return tClazz.cast(Double.valueOf(value));
        } else if (tClazz == Boolean.class) {
            return tClazz.cast(Boolean.valueOf(value));
        } else {
            throw new IllegalArgumentException("Unsupported type: " + tClazz);
        }
    }
}

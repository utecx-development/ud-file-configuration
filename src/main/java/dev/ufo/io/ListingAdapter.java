package dev.ufo.io;

import java.util.List;
import java.util.Map;

public class ListingAdapter {

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

    static <V> void parseJsonToMap(String json, Class<V> vClazz, Map<String, V> result) {
        // JSON parsing logic for map (basic implementation without libraries)
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

    static <T> T convertStringToType(String value, Class<T> tClazz) {
        // Convert string to the desired type (basic implementation)
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

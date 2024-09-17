package dev.ufo.ufodata.core.format;

import dev.ufo.ufodata.lib.checks.CheckType;
import dev.ufo.ufodata.lib.TypeValue;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class MapFormatter {

    @NonNull
    public static Map<?, ?> formatMap(String type, final String value) {
        final String[] types = type.replace("<" , "")
                .replace(">", "")
                .replace("map", "")
                .split(",");

        if (type.length() < 2) throw new RuntimeException("Could not find 2 types for map");

        final String[] entries = value.replace("{","")
                .replace("}", "")
                .replace("\"", "")
                .split(",");

        final Map<Object, Object> map = new HashMap<>();

        for (int i = 0; i != entries.length; i++){
            String[] keyValue = entries[i].split("-");

            map.put(ObjectFormatter.objFromString(types[0], keyValue[0]),
                    ObjectFormatter.objFromString(types[1], keyValue[1]));
        }

        return map;
    }

    public <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass, Map<String, TypeValue> fileContent) {

        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("There was no list for the key: " + key);
        }

        List<K> keys = getTempList(keyClass, key, true, fileContent);
        List<V> values = getTempList(valueClass, key, false, fileContent);
        Map<K, V> map = new HashMap<>();

        if (keys.size() != values.size()) {
            throw new RuntimeException("An error appeared during the encoding of " + key + ". Please report this error to our staff team");
        }

        for (int i = 0; i != keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        return map;

    }

    private <T> List<T> getTempList(Class<T> clazz, String key, boolean isKey, Map<String, TypeValue> fileContent) {

        if (!(fileContent.get(key).getValue() instanceof Map<?, ?> map)) {
            throw new RuntimeException("The key: " + key + " is not an instance of a List but a " + fileContent.get(key).getValue().getClass().getSimpleName());
        }

        List<?> list;

        if (isKey) {
            list = new ArrayList<>(map.keySet());
        } else {
            list = new ArrayList<>(map.values());
        }

        if (CheckType.isPrimitive(clazz)) {

            final List<T> objList = new ArrayList<>();

            for (Object t : list) {
                objList.add(ObjectFormatter.formateObject(clazz, t));
            }

            return objList;

        } else {

            if (!clazz.isInstance(list.getFirst())) {
                throw new RuntimeException("The class " + clazz.getName() + " is not an instance of " + fileContent.get(key).getValue().getClass().getName());
            }

            return (List<T>) list;

        }
    }

}

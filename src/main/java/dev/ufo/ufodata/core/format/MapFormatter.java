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

    /**
     * Format a given input in UfoData format to an actual map
     * @param type String containing the 2 required types for this map
     * @param value The formatted contents of this map
     * @return A Hashmap usable by this parser.
     */
    @NonNull
    public static Map<?, ?> formatMap(final String type, final String value) {
        //remove formatting tags and split to an array
        final String[] types = type
                .replace("<" , "").replace(">", "").replace("map", "")
                .split(",");

        //check if this can be a map!
        if (type.length() < 2) throw new RuntimeException("Could not find the 2 types for this map");

        //split the entries of this map!
        final String[] entries = value
                .replace("{","").replace("}", "").replace("\"", "")
                .split(",");

        //fill everything into an actual map
        final Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i != entries.length; i++){
            String[] keyValue = entries[i].split("-");
            map.put(ObjectFormatter.toObject(types[0], keyValue[0]), ObjectFormatter.toObject(types[1], keyValue[1]));
        }

        return map;
    }

    /**
     * Read a map from parsers cache structure
     * @param key Identifier of this map
     * @param keyClass Class of the key row
     * @param valueClass Class of the value row
     * @param fileContent Content of the UfoFile to read from
     * @return A new java map
     * @param <K> key type
     * @param <V> value type
     */
    @NonNull
    public <K, V> Map<K, V> getMap(final String key, final Class<K> keyClass, final Class<V> valueClass, final Map<String, TypeValue> fileContent) {
        if (!fileContent.containsKey(key)) { //check if there is a map by this key inside the file
            throw new RuntimeException("There was no map found for the key: '" + key + "'");
        }

        //read the contents from UfoFiles content
        final List<K> keys = getTempList(keyClass, key, true, fileContent);
        final List<V> values = getTempList(valueClass, key, false, fileContent);
        if (keys.size() != values.size()) {
            throw new RuntimeException("An error occurred during the encoding of '" + key + "'");
        }

        //fill the actual map
        final Map<K, V> map = new HashMap<>();
        for (int i = 0; i != keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        return map;
    }

    /**
     * Get either all keys or values of a map as a temporary list
     * @param clazz Type the list contents should be in
     * @param key Identifier of this map
     * @param isKey Do we need the key column or the value column
     * @param fileContent Content of the UfoFile to read from
     * @return List containing either all types or values of this map
     * @param <T> content type
     */
    @NonNull
    @SuppressWarnings("unchecked")
    private <T> List<T> getTempList(final Class<T> clazz, final String key, final boolean isKey, final Map<String, TypeValue> fileContent) {
        //check if there is the correct datastructure behind given identifier
        if (!(fileContent.get(key).getValue() instanceof Map<?, ?> map)) {
            throw new RuntimeException("The content behind key: '" + key + "' is not an instance of a map but a '" + fileContent.get(key).getValue().getClass().getSimpleName() + "'");
        }

        //fill the list with contents
        final List<?> list;
        if (isKey) {
            list = new ArrayList<>(map.keySet());
        } else {
            list = new ArrayList<>(map.values());
        }

        //parse the contents to actual objects
        if (CheckType.isNotPrimitive(clazz)) {
            final List<T> objectList = new ArrayList<>();
            for (final Object object : list) {
                objectList.add(ObjectFormatter.toObject(clazz, object));
            }
            return objectList;
        }

        //do the types match?
        if (!clazz.isInstance(list.getFirst())) {
            throw new RuntimeException("The class '" + clazz.getName() + "' is not an instance of '" + fileContent.get(key).getValue().getClass().getName() + "'");
        }

        return (List<T>) list;
    }
}

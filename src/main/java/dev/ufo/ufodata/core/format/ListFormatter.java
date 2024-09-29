package dev.ufo.ufodata.core.format;

import dev.ufo.ufodata.lib.checks.CheckType;
import dev.ufo.ufodata.lib.TypeValue;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class ListFormatter {

    /**
     * Format a given string in ufo format to a java list
     * @param type type with or without formatting tags
     * @param value value with formatting tags
     * @return parsed list
     */
    @NonNull
    public static List<?> formatList(String type, final String value) {
        //strip formatting tags to get type
        type = type.replace("<" , "")
                .replace(">", "")
                .replace("list", "");

        //strip formatting tags to read entries
        final String[] entries = value.replace("[","")
                .replace("]", "")
                .replace("\"", "")
                .trim()
                .split(",");

        //loop through entries and parse objects
        final List<Object> list = new ArrayList<>();
        for (int i = 0; i != entries.length; i++){
            list.add(ObjectFormatter.toObject(type, entries[i].trim())); //most cost expensive
        }

        return list;
    }

    /**
     * Get a list from the given file
     * @param key Identifier of this list
     * @param clazz Type the contents of this list should be in
     * @param fileContent Contents of file
     * @return List with contents of requested type
     * @param <T> content type
     */
    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> List<T> getList(final String key, final Class<T> clazz, final Map<String, TypeValue> fileContent) {
        //is there a list behind this identifier
        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("There was no list for the key: '" + key + "'");
        }

        //check if this even is a list
        if (!(fileContent.get(key).getValue() instanceof List<?> list)) {
            throw new RuntimeException("Behind the key: '" + key + "' there is not an instance of a list but a '" + fileContent.get(key).getValue().getClass().getSimpleName() + "'");
        }

        //check if the given lists contents are primitives
        if (CheckType.isNotPrimitive(clazz)) {
            final List<T> objectList = new ArrayList<>();
            for (Object object : list) {
                objectList.add(ObjectFormatter.toObject(clazz, object));
            }
            return objectList;
        } else {
            if (!clazz.isInstance(list.getFirst())) {
                throw new RuntimeException("The class '" + clazz.getName() + "' is not an instance of '" + fileContent.get(key).getValue().getClass().getName() + "'");
            }
            return (List<T>) fileContent.get(key).getValue();
        }
    }
}

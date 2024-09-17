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
            list.add(ObjectFormatter.objFromString(type, entries[i].trim())); //most cost expensive
        }

        return list;
    }

    public static <T> List<T> getList(String key, Class<T> clazz, Map<String, TypeValue> fileContent) {

        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("There was no list for the key: " + key);
        }

        if (!(fileContent.get(key).getValue() instanceof List<?> list)) {
            throw new RuntimeException("The key: " + key + " is not an instance of a List but a " + fileContent.get(key).getValue().getClass().getSimpleName());
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
            return (List<T>) fileContent.get(key).getValue();
        }

    }
}

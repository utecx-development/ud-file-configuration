package de.ufomc.config.format;

import de.ufomc.config.io.Reader;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

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
                .split(",");

        //loop through entries and parse objects
        final List<Object> list = new ArrayList<>();
        for (int i = 0; i != entries.length; i++){
            list.add(Reader.objFromString(type, entries[i])); //most cost expensive
        }

        return list;
    }
}

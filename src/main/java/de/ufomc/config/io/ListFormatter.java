package de.ufomc.config.io;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public final class ListFormatter {

    @NonNull
    public static List<?> formatList(String type, final String value) {
        type = type.replace("<" , "")
                .replace(">", "")
                .replace("list", "");

        String[] entries = value.replace("[","")
                .replace("]", "")
                .replace("\"", "")
                .split(",");

        final List<Object> list = new ArrayList<>();
        for (int i = 0; i != entries.length; i++){
            list.add(Reader.objFromString(type, entries[i]));
        }
        return list;
    }
}

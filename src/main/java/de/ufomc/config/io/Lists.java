package de.ufomc.config.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Lists {

    public static List<?> formateList(String type, String value) {
        type = type.replace("<" , "")
                .replace(">", "")
                .replace("list", "");

        String[] entries = value.replace("[","")
                .replace("]", "")
                .replace("\"", "")
                .split(",");

        List<Object> list = new ArrayList<>();

        for (int i = 0; i != entries.length; i++){
            list.add(Reader.objFromString(type, entries[i]));
        }

        return list;

    }

}

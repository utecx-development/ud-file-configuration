package de.ufomc.config.io;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Maps {

    public static Map<?, ?> formateMap(String type, String value) {
        String[] types = type.replace("<" , "")
                .replace(">", "")
                .replace("map", "")
                .split(",");

        if (type.length() < 2){
            throw new RuntimeException("Could not find 2 types for map");
        }

        String[] entries = value.replace("{","")
                .replace("}", "")
                .replace("\"", "")
                .split(",");

        Map<Object, Object> map = new HashMap<>();

        for (int i = 0; i != entries.length; i++){
            String[] keyValue = entries[i].split("-");

            map.put(Reader.objFromString(types[0], keyValue[0]), Reader.objFromString(types[1], keyValue[1]));
        }

        return map;
    }

}

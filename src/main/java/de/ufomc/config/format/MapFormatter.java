package de.ufomc.config.format;

import de.ufomc.config.io.Reader;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
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

            map.put(Reader.objFromString(types[0], keyValue[0]),
                    Reader.objFromString(types[1], keyValue[1]));
        }

        return map;
    }

}

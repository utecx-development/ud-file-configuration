package de.ufomc.config.format;

import de.ufomc.config.checks.CheckEquals;
import de.ufomc.config.core.UDObject;
import de.ufomc.config.io.Config;
import de.ufomc.config.pre.TypeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonFormatter {

    public static String toJson(Map<String, TypeValue> cache) {
        StringBuilder s = new StringBuilder();

        s.append("{\n");

        List<String> keys = new ArrayList<>(cache.keySet());
        List<TypeValue> typeValues = new ArrayList<>(cache.values());

        for (int i = 0; i != cache.size(); i++) {

            String key = keys.get(i);
            TypeValue value = typeValues.get(i);

            s.append("\"")
                    .append(key)
                    .append("\"")
                    .append(":");

            boolean b = !(value.getType().startsWith("map") || value.getType().startsWith("list") || value.getType().startsWith("object"));

            if (b) {
                s.append("\"");
            }

            if (value.getType().startsWith("object")){
                if (value.getValue() instanceof UDObject object) {
                    s.append(object.toJson());
                } else {
                    throw new RuntimeException("An error occurred while parsing an instance of: " + value.getValue().getClass().getSimpleName());
                }
            } else {
                s.append(value.getValue());
            }

            if (b) {
                s.append("\"");
            }

            if (i != cache.size() - 1) {
                s.append(",\n");
            }

        }

        s.append("\n}");

        return s.toString();
    }


}

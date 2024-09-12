package de.ufomc.config.io;

import de.ufomc.config.format.ListFormatter;
import de.ufomc.config.format.MapFormatter;
import de.ufomc.config.format.ObjectFormatter;
import de.ufomc.config.pre.TypeValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static Map<String, TypeValue> init(File file) {
        if (!file.exists()){
            initFile(file);
        }
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(file))) {

            String line;
            Map<String, TypeValue> map = new HashMap<>();

            //941940.7

            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                String type = line.split(":")[0];
                String value = line.substring(line.split("=")[0].length() + 1).replace(";", "");
                String key = line.substring(type.length() + 1, line.length() - value.length() - 2);

                if (type.startsWith("map<")) {
                    map.put(key, new TypeValue(type, MapFormatter.formatMap(type, value)));
                } else if (type.startsWith("list<")) {
                    map.put(key, new TypeValue(type, ListFormatter.formatList(type, value)));
                } else {
                    map.put(key, new TypeValue(type, ObjectFormatter.objFromString(type, value)));
                }
            }

            return map;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initFile(File file) {
        if (!file.exists()){
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print("");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String buildFile(Map<String, TypeValue> cache) {

        StringBuilder s = new StringBuilder();

        cache.forEach((key, value) ->{

            s.append(value.getType())
                    .append(":").append(key)
                    .append("=");

            if (value.getType().startsWith("map")){

                s.append(value.getValue().toString()
                        .replace("=", "-").trim());

            } else {
                s.append(value.getValue().toString());
            }

            s.append(";\n");

        });
        return s.toString();
    }

}

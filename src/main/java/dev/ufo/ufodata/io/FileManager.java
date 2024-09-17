package dev.ufo.ufodata.io;

import dev.ufo.ufodata.format.ListFormatter;
import dev.ufo.ufodata.format.MapFormatter;
import dev.ufo.ufodata.format.ObjectFormatter;
import dev.ufo.ufodata.pre.TypeValue;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    public static Map<String, TypeValue> init(File file) {

        if (!file.exists()){
            initFile(file);
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            Map<String, TypeValue> map = new HashMap<>();
            StringBuilder s = new StringBuilder();

            //941940.7

            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                s.append(line);

            }

            //System.out.println(s.toString().replace("\n", ""));

            try {
                for (String current : s.toString().replace("\n", "").split(";")){

                    if (current.isEmpty()) {
                        continue;
                    }

                    String[] typeRest = current.split(":");

                    String type = typeRest[0];
                    String key = typeRest[1].split("=")[0];
                    String value = typeRest[1].split("=")[1];

                    if (type.startsWith("map<")) {
                        map.put(key, new TypeValue(type, MapFormatter.formatMap(type, value)));
                    } else if (type.startsWith("list<")) {
                        map.put(key, new TypeValue(type, ListFormatter.formatList(type, value)));
                    } else {
                        map.put(key, new TypeValue(type, ObjectFormatter.objFromString(type, value)));
                    }

                }
            } catch (Exception e){
                throw new RuntimeException("Wrong syntax for: " + file.getParent() + "\\" + file.getName());
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

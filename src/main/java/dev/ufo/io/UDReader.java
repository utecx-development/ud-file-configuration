package dev.ufo.io;

import dev.ufo.etc.FieldSerializer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

class UDReader {

    static Map<String, Object> readData(File file){

        final Map<String, Object> cache = new HashMap<>();

        if (!file.exists()) {
            initFile(file);
            return cache;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){

            StringBuilder lines = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) continue;
                lines.append(line);
            }

            for (String s : lines.toString().replace("\n", "").split(";")) {

                String[] split = s.split("=");

                if (split.length < 2) System.err.println("line '" + s + "' contains an syntax error");

                if (!split[1].startsWith("{") && !split[1].startsWith("[")){
                    cache.put(split[0], FieldSerializer.serialize(split[1]));
                    cache.put(split[0], split[1]);
                    continue;
                }

                cache.put(split[0], s.replace(split[0] + "=", ""));

            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        return cache;

    }

    private static void initFile(File file) {

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print("");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

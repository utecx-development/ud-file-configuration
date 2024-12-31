package dev.ufo.io;

import dev.ufo.etc.FieldSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Map;

class UDReader {

    static void readData(File file, Map<String, Object> cache, Map<Integer, String> comments){

        if (!file.exists()) {
            initFile(file);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){

            StringBuilder lines = new StringBuilder();

            String line;
            int i = 0;
            while ((line = reader.readLine()) != null) {
                i++;
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) {
                    comments.put(i, line);
                    continue;
                }
                lines.append(line);
            }

            int level = 0;
            boolean readingKey = true;

            StringBuilder keyBuilder = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();

            for (char c : lines.toString().replace("\n", "").substring(1, lines.toString().length() - 1).toCharArray()) {
                switch (c) {
                    case '[', '{' -> {
                        if (!readingKey) {
                            valueBuilder.append(c);
                        }
                        level++;
                        continue;
                    }
                    case ']', '}' -> {
                        if (!readingKey) {
                            valueBuilder.append(c);
                        }
                        level--;
                        if (level == 0) {
                            cache.put(keyBuilder.toString().replace("\"", ""), FieldSerializer.serialize(valueBuilder.toString()));
                            readingKey = true;
                            keyBuilder.setLength(0);
                            valueBuilder.setLength(0);
                        }
                        continue;
                    }
                    case ':' -> {
                        if (readingKey) {
                            readingKey = false;
                            continue;
                        }
                    }
                    case ',' -> {
                        if (level == 0) {
                            continue;
                        }
                    }
                }

                if (readingKey) {
                    keyBuilder.append(c);
                } else {
                    valueBuilder.append(c);
                }
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    private static void initFile(File file) {

        try (PrintWriter writer = new PrintWriter(file)) {
            writer.print("{}");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}

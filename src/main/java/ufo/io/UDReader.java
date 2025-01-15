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
                if (line.startsWith("#") || !line.startsWith("\"")) {
                    comments.put(i, line.startsWith("#") ? line : "#" + line);
                    continue;
                }
                lines.append(line);
            }

            extractData(lines.toString(), cache);

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

    static void extractData(String lines, Map<String, Object> cache) {

        int level = 0;
        boolean readingKey = true;
        StringBuilder keyBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        // Entferne äußere { und }, falls vorhanden
        lines = lines.trim();
        if (lines.startsWith("{") && lines.endsWith("}")) {
            lines = lines.substring(1, lines.length() - 1);
        }

        for (char c : lines.toCharArray()) {
            switch (c) {
                case '{', '[' -> {
                    level++;
                    valueBuilder.append(c);
                }
                case '}', ']' -> {
                    level--;
                    valueBuilder.append(c);
                    if (level == 0 && !readingKey) {
                        cache.put(keyBuilder.toString().replace("\"", "").trim(),
                                FieldSerializer.serialize(valueBuilder.toString().trim()));
                        keyBuilder.setLength(0);
                        valueBuilder.setLength(0);
                        readingKey = true;
                    }
                }
                case ':' -> {
                    if (readingKey) {
                        readingKey = false;
                    } else {
                        valueBuilder.append(c);
                    }
                }
                case ',' -> {
                    if (level == 0) {
                        // Abschluss eines Schlüssel-Wert-Paars
                        cache.put(trim(keyBuilder),
                                FieldSerializer.serialize(valueBuilder.toString().trim()));
                        keyBuilder.setLength(0);
                        valueBuilder.setLength(0);
                        readingKey = true;
                    } else {
                        valueBuilder.append(c);
                    }
                }
                default -> {
                    if (readingKey) {
                        keyBuilder.append(c);
                    } else {
                        valueBuilder.append(c);
                    }
                }
            }
        }

        // Letztes Paar hinzufügen, falls vorhanden
        if (!keyBuilder.isEmpty() && !valueBuilder.isEmpty()) {
            cache.put(trim(keyBuilder),
                    FieldSerializer.serialize(valueBuilder.toString().trim()));
        }
    }

    private static String trim(StringBuilder b) {
        return b.toString().replace("\"", "").trim();
    }

}

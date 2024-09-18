package dev.ufo.ufodata.core.io;

import dev.ufo.ufodata.core.format.ListFormatter;
import dev.ufo.ufodata.core.format.MapFormatter;
import dev.ufo.ufodata.core.format.ObjectFormatter;
import dev.ufo.ufodata.lib.TypeValue;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class UfoSerializer {

    /**
     * Deserializes a files contents to a map usable by this parser.
     * @param file The file to read from.
     * @return Hashmap in a key -> value structure
     */
    @NonNull
    public static Map<String, TypeValue> deserialize(final File file) {
        if (!file.exists()) initFile(file); //create the file if it does not exist yet

        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Map<String, TypeValue> map = new HashMap<>();

            //read all of the files contents
            final StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) continue;
                builder.append(line);
            }
            final String[] lines = builder.toString().replace("\n", "").split(";"); //remove formatting tags

            try {
                for (final String current : lines) {
                    if (current.isEmpty()) continue; //empty line in file

                    //parse key - value
                    final String[] typeRest = current.split(":");
                    final String type = typeRest[0];
                    final String key = typeRest[1].split("=")[0];
                    final String value = typeRest[1].split("=")[1];

                    //format
                    if (type.startsWith("map<")) {
                        map.put(key, new TypeValue(type, MapFormatter.formatMap(type, value)));
                    } else if (type.startsWith("list<")) {
                        map.put(key, new TypeValue(type, ListFormatter.formatList(type, value)));
                    } else {
                        map.put(key, new TypeValue(type, ObjectFormatter.toObject(type, value)));
                    }
                }
                return map;

            } catch (final Exception exception) {
                throw new RuntimeException("Failed to load file: '" + file.getParent() + "\\" + file.getName() + "' because of wrong syntax");
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * If no file exists yet, this creates a new file.
     * @param file To initialize
     */
    private static void initFile(final File file) {
        if (!file.exists()) {
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print("");
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     * Serialize a map used by this parser to file contents
     * @param cache Hashmap used by parser
     * @return A string that can be written to a file
     */
    @NonNull
    public static String serialize(final Map<String, TypeValue> cache) {
        final StringBuilder builder = new StringBuilder();

        cache.forEach((key, value) -> {
            builder.append(value.getType()).append(":").append(key).append("=");

            if (value.getType().startsWith("map")) {
                builder.append(value.getValue().toString()
                        .replace("=", "-").trim());
            } else {
                builder.append(value.getValue().toString());
            }

            builder.append(";\n");
        });

        return builder.toString();
    }
}

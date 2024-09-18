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
public final class FileManager {

    /**
     * 
     * @param file
     * @return
     */
    @NonNull
    public static Map<String, TypeValue> init(final File file) {
        if (!file.exists()) initFile(file);
        try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Map<String, TypeValue> map = new HashMap<>();
            final StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) continue;
                if (line.startsWith("#")) continue;
                builder.append(line);
            }

            try {

                final String[] lines = builder.toString().replace("\n", "").split(";");
                for (final String current : lines){
                    if (current.isEmpty()) continue;

                    final String[] typeRest = current.split(":");
                    final String type = typeRest[0];
                    final String key = typeRest[1].split("=")[0];
                    final String value = typeRest[1].split("=")[1];

                    if (type.startsWith("map<")) {
                        map.put(key, new TypeValue(type, MapFormatter.formatMap(type, value)));
                    } else if (type.startsWith("list<")) {
                        map.put(key, new TypeValue(type, ListFormatter.formatList(type, value)));
                    } else {
                        map.put(key, new TypeValue(type, ObjectFormatter.objFromString(type, value)));
                    }
                }
                return map;

            } catch (final Exception exception){
                throw new RuntimeException("Failed to load file: '" + file.getParent() + "\\" + file.getName() + "' because of wrong syntax");
            }
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     *
     * @param file
     */
    public static void initFile(final File file) {
        if (!file.exists()) {
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print("");
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    /**
     *
     * @param cache
     * @return
     */
    @NonNull
    public static String buildFile(final Map<String, TypeValue> cache) {
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

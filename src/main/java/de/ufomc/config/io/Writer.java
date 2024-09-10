package de.ufomc.config.io;

import de.ufomc.config.core.UDObject;
import de.ufomc.config.pre.TypeValue;
import lombok.Getter;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public final class Writer {
    @Getter private final Reader reader;
    protected final Map<String, TypeValue> fileContent;
    private final String fileName;

    public Writer(String name) {
        this.fileName = name + ".ud";

        if (!new File(fileName).exists()){
            try (final PrintWriter writer = new PrintWriter(fileName)) {
                writer.print("");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        reader = new Reader(fileName);
        fileContent = reader.getFileContent();
    }

    public void write(String key, Object o){
        this.fileContent.put(key, new TypeValue(type(o), o));
    }

    public void save() {

        //Todo: For optimization reasons, this Threadpool should not be created new on every occuring write.
        //It should instead be handled by a custom ThreadPoolExecutor with a limited amount of threads & a capped queue length
        //We need to make this an attribute for this writer.
        //Also might want to log in a more united way

        Executors.newFixedThreadPool(2).execute(() -> { //this is very performance unoptimized!
            try (final PrintWriter writer = new PrintWriter(fileName)) {
                writer.print(buildFile());
                writer.flush();
            } catch (final Exception exception) {
                throw new RuntimeException("A problem occurred while saving to " + this.fileName + "!", exception);
            }
        });
    }

    private String buildFile() {

        StringBuilder s = new StringBuilder();

        fileContent.forEach((key, value) ->{

            s.append(value.getType())
                    .append(":").append(key)
                    .append("=");

            if (value.getType().startsWith("map")){

                s.append(value.getValue().toString()
                        .replace("=", "-").replace(" ", ""));

            } else {
                s.append(value.getValue().toString());
            }

            s.append(";\n");

        });
        return s.toString();
    }

    @NonNull
    private String type(final Object object) {
        return switch (object) {
            case String s -> "string";
            case Integer i -> "int";
            case Float f -> "float";
            case Map<?, ?> m -> {
                Object k = new ArrayList<>(m.keySet()).getFirst();
                Object v = new ArrayList<>(m.values()).getFirst();
                yield "map<" + type(k) + "," + type(v) + ">";
            }
            case List<?> l -> "list<" + type(l.getFirst()) + ">";
            default -> {
                //check if class extends UDObject (required for working with custom objects)
                if (!UDObject.class.isAssignableFrom(object.getClass())) {
                    throw new IllegalStateException("Unsupported type: " + object.getClass().getSimpleName());
                }
                yield "object";
            }
        };
    }
}

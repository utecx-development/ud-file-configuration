package de.ufomc.config.io;

import de.ufomc.config.core.UfObject;
import de.ufomc.config.pre.TV;
import lombok.Getter;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class Writer {

    @Getter
    private final Reader reader;
    protected final Map<String, TV> fileContent;
    private String fileName;

    public Writer(String fileName) {
        this.fileName = fileName + ".ud";

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
        this.fileContent.put(key, new TV(type(o), o));
    }

    public void save() {
        Executors.newFixedThreadPool(2).execute(() -> {
            try (final PrintWriter writer = new PrintWriter(fileName)) {
                writer.print(buildFile());
                writer.flush();
            } catch (Exception e) {
                throw new RuntimeException("A probleme appeared during the save process of " + this.fileName + " " + e);
            }
        });
    }

    private String buildFile() {

        StringBuilder s = new StringBuilder();

        fileContent.forEach((key, value) ->{

            s.append(value.getType())
                    .append(":").append(key)
                    .append("=").append(value.getValue().toString())
                    .append(";\n");

        });

        return s.toString();

    }

    private String type(Object o) {

        return switch (o) {
            case String s -> "string";
            case Integer i -> "int";
            case Float f -> "float";
            case List<?> l -> "list<" + type(l.getFirst()) + ">";
            default -> {
                if (UfObject.class.isAssignableFrom(o.getClass())){
                    yield "object";
                }
                throw new RuntimeException("unsupported type " + o.getClass().getSimpleName());
            }
        };
    }

}
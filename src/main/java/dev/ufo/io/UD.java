package dev.ufo.io;

import dev.ufo.etc.TypeCheck;
import dev.ufo.etc.UDObject;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class UD {

    private final File file;
    private static final String fileEnding = ".ud";
    private final Map<String, Object> cache;
    private boolean changed;

    UD(File file, boolean addShutdownHook) {
        this.file = file;
        this.cache = UDReader.readData(file);
        if (addShutdownHook) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> save(false)));
        }
    }

    public static UD init(File f, String fileName, boolean addShutdownHook) {
        return new UD(new File(f.getPath() + fileName + fileEnding), addShutdownHook);
    }

    public static UD init(String path, String fileName, boolean addShutdownHook) {
        return new UD(new File(path + File.separator + fileName + fileEnding), addShutdownHook);
    }

    public void set(String key, Object value) {

        if (TypeCheck.isPrimitive(value.getClass()) || TypeCheck.isListOrMap(value.getClass())) {
            cache.put(key, value);
        } else {
            cache.put(key, UDObject.serializeObject(value));
        }

        changed = true;
    }

    public <V> V get(Class<V> clazz, String key) {

        if (!cache.containsKey(key)) {
            throw new RuntimeException("The cache does not contain '" + key + "'");
        }

        Object o = cache.get(key);

        if (TypeCheck.isListOrMap(o.getClass())) {
            throw new RuntimeException("Wrong usage! Please use the getList or getMap methode for maps and lists!");
        }

        if (TypeCheck.isPrimitive(o.getClass())) {
            if (clazz.isAssignableFrom(o.getClass())) {
                return clazz.cast(o);
            } else {
                if (o.getClass() != String.class) {
                    throw new RuntimeException("The class '" + clazz.getName() + "' does not match '" + o.getClass().getName() + "'");
                }
            }
        }

        return ObjSerializer.convert(clazz, o.toString());

    }

    public void save(boolean force) {
        if (!force && !changed) return;

        try (final PrintWriter writer = new PrintWriter(new FileWriter(this.file))) { //Todo: Test if this overwrites current file contents (it should!)

            cache.forEach((key, value)->{
                writer.write(key + "=" + value + ";\n");
            });

            writer.flush();

            this.changed = false;

        } catch (final Exception exception) {
            throw new RuntimeException("An error occurred while trying to save contents to: '" + this.file.getName() + "'!", exception);
        }

        changed = false;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();

        cache.forEach((key, value) -> b.append(key).append("=").append(UDObject.serializeObject(value)));

        return b.toString();

    }

}

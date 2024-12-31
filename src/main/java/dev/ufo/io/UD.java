package dev.ufo.io;

import dev.ufo.etc.JsonConverter;
import dev.ufo.etc.TypeCheck;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UD {

    private final File file;
    private static final String fileEnding = ".ud";
    private final Map<String, Object> cache = new HashMap<>();
    private final Map<Integer, String> comments = new HashMap<>();

    private boolean changed;

    UD(File file, boolean addShutdownHook) {
        this.file = file;
        UDReader.readData(file, cache, comments);
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
        cache.put(key, value);

        changed = true;
    }

    public <V> V get(Class<V> clazz, String key) {

        cache.forEach((k, value) -> {
            System.out.println(k + " " + value);
        });

        if (!cache.containsKey(key)) {
            throw new RuntimeException("The cache does not contain '" + key + "'");
        }

        Object o = cache.get(key);

        if (TypeCheck.isListOrMap(o.getClass())) {
            throw new RuntimeException("Wrong usage! Please use the getList or getMap methode for maps and lists!");
        }

        if (clazz.isAssignableFrom(o.getClass())) {
            return clazz.cast(o);
        } else {
            try {
                return ObjSerializer.convert(clazz, o.toString());
            } catch (Exception e) {
                throw new RuntimeException("The class '" + clazz.getName() + "' does not match '" + o.getClass().getName() + "' and could also not be parsed!", e);
            }
        }

    }

    public <T> List<T> getList(Class<T> tClazz, String key) {

        if (cache.containsKey(key) && cache.get(key) instanceof String s) {
            List<T> result = new ArrayList<>();
            ListingAdapter.parseJsonToList(s, tClazz, result);
            return result;
        }

        throw new RuntimeException("List with the key '" + key + "' could not be parsed");
    }

    public <V> Map<String, V> getMap(Class<V> vClazz, String key) {
        if (cache.containsKey(key) && cache.get(key) instanceof String s) {
            Map<String, V> result = new HashMap<>();
            ListingAdapter.parseJsonToMap(s, vClazz, result);
            return result;
        }

        throw new RuntimeException("Map with the key '" + key + "' could not be parsed");
    }

    public void save(boolean force) {
        if (!force && !changed) return;

        try (final PrintWriter writer = new PrintWriter(new FileWriter(this.file))) { //Todo: Test if this overwrites current file contents (it should!)

            writer.write(toString());
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
        AtomicInteger i = new AtomicInteger();

        b.append("{\n");

        cache.forEach((key, value)->{
            i.getAndIncrement();
            b.append(JsonConverter.toJson(key)).
                    append(":").
                    append(JsonConverter.toJson(value));

            if (i.get() != cache.size()) {
                b.append(",");
            }

            b.append("\n");
        });

        b.append("}");

        return b.toString();

    }

}

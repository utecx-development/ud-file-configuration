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
import java.util.function.BiConsumer;

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

    /**
     * Gives you an initialized UD config
     * @param file The file that will be used. Make sure the path is accessible.
     * @param fileName The name of your file
     * @param addShutdownHook Do you want to add a shutdown hook for auto save after the shutdown? -> true
     */

    public static UD init(File file, String fileName, boolean addShutdownHook) {
        return new UD(new File(file.getPath() + fileName + fileEnding), addShutdownHook);
    }

    /**
     * Used to parse items from a json string to a list
     * @param path The path to your file. Make sure the path is accessible.
     */

    public static UD init(String path, String fileName, boolean addShutdownHook) {
        return new UD(new File(path + File.separator + fileName + fileEnding), addShutdownHook);
    }

    /**
     * Used to parse items from a json string to a list
     * @param key Your key for the json config.
     * @param value Your value for the json config.
     */
    public void set(String key, Object value) {
        cache.put(key, value);

        changed = true;
    }

    public void remove(String key) {
        cache.remove(key);
        changed = true;
    }

    /**
     * Used to get items from your json config
     * @param key Your key for the json config.
     * @param clazz The type you are expecting to get from that particular key.
     * @param <V> The generic used for clazz.
     * @return you will receive an object with an instance of V if the request is successful
     */
    public <V> V get(String key, Class<V> clazz) {

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
                V v = ObjSerializer.convert(clazz, o.toString());
                cache.put(key, v);
                return v;
            } catch (Exception e) {
                throw new RuntimeException("The class '" + clazz.getName() + "' does not match '" + o.getClass().getName() + "' and could also not be parsed!", e);
            }
        }

    }

    /**
     * Used to get lists from your json config
     * @param key Your key for the json config.
     * @param tClazz The type you are expecting the list to have.
     * @param <T> The generic used for tClazz.
     * @return the list
     */
    public <T> List<T> getList(Class<T> tClazz, String key) {

        if (cache.containsKey(key) && cache.get(key) instanceof String s) {
            List<T> result = new ArrayList<>();
            ListingAdapter.parseJsonToList(s, tClazz, result);

            cache.put(key, result);

            return result;
        }

        throw new RuntimeException("List with the key '" + key + "' could not be parsed");
    }

    /**
     * Used to get maps from your json config
     * @param key Your key for the json config.
     * @param vClazz The type you are expecting the map to have.
     * @param <V> The generic used for tClazz.
     * @return the map
     */

    public <V> Map<String, V> getMap(Class<V> vClazz, String key) {
        if (cache.containsKey(key) && cache.get(key) instanceof String s) {
            Map<String, V> result = new HashMap<>();
            ListingAdapter.parseJsonToMap(s, vClazz, result);

            cache.put(key, result);

            return result;
        }

        throw new RuntimeException("Map with the key '" + key + "' could not be parsed");
    }

    /**
     * Used to get save your json config
     * @param force force the save even if there were no changes according to UD
     *              (please enable if you work with immutables like maps or lists...)
     */

    public void save(boolean force) {
        if (!force && !changed) return;

        try (final PrintWriter writer = new PrintWriter(new FileWriter(this.file))) {

            writer.write(toString());
            writer.flush();

            this.changed = false;

        } catch (final Exception exception) {
            throw new RuntimeException("An error occurred while trying to save contents to: '" + this.file.getName() + "'!", exception);
        }

        changed = false;
    }

    /**
     * This methode will convert the cache to a json string.
     * @return the json string
     */

    @Override
    public String toString() {

        StringBuilder b = new StringBuilder();
        AtomicInteger i = new AtomicInteger();

        b.append("{\n");

        cache.forEach((key, value) -> {

            i.getAndIncrement();

            int j = i.get();

            if (comments.containsKey(j + 1)) {
                b.append(comments.get(j + 1))
                        .append("\n");
            }

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

    public String getString(String key) {
        return get(key, String.class);
    }

    public int getInt(String key) {
        return get(key, Integer.class);
    }

    public long getLong(String key) {
        return get(key, Long.class);
    }

    public double getDouble(String key) {
        return get(key, Double.class);
    }

    public boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

}

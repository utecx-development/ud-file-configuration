package dev.ufo.ufodata.core.io;

import dev.ufo.ufodata.lib.checks.CheckType;
import dev.ufo.ufodata.core.QueuedAsyncExecution;
import dev.ufo.ufodata.core.format.JsonFormatter;
import dev.ufo.ufodata.core.format.ListFormatter;
import dev.ufo.ufodata.core.format.MapFormatter;
import dev.ufo.ufodata.core.format.ObjectFormatter;
import dev.ufo.ufodata.lib.TypeValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class UfoFile {
    private static final String FILE_SUFFIX = ".ud";
    @Getter File file;
    Map<String, TypeValue> cache; //no getter since this would be a vulnerability (some data might not be saved)
    @Getter @Setter @NonFinal boolean prettyWriting;
    @Getter @Setter @NonFinal boolean changed; //if this has not changed there is no need to write into the file!

    //private constructor with direct file parameter
    private UfoFile(final File file) {
        this.file = file;
        this.cache = FileManager.init(file);
    }

    /**
     * Creates a new instance with the given file.
     * @param file The file to work with
     * @return new UfoFile instance
     */
    @NonNull
    public static UfoFile of(final File file) {
        return new UfoFile(file);
    }

    /**
     * Creates a new instance by a file found through a path and a filename.
     * @param path Folder the file should be in
     * @param fileName Name of the file to work with
     * @return new UfoFile instance
     */
    @NonNull
    public static UfoFile of(final String path, final String fileName) {
        final File file = new File(path, fileName + FILE_SUFFIX);
        return UfoFile.of(file);
    }

    /**
     * Creates a new instance by inputting a class, effectively trying to find its location and working in its directory
     * @param main Class the location of is found and file created next to.
     * @param fileName Name of the file to work with
     * @return new UfoFile instance (if successful)
     * @throws RuntimeException Thrown in case there is a problem with the given main class
     */
    @NonNull
    public static UfoFile of(final Class<?> main, final String fileName) throws RuntimeException {
        try {
            final String path = new File(main.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return UfoFile.of(path, fileName);
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Get a map from your UfoFile.
     * @param keyClazz Type the map's keys are
     * @param valueClazz Type the map's values are
     * @param key Identifier of this map
     * @return The requested map using the given types
     * @param <K> keyClazz type
     * @param <V> valueClazz type
     */
    @NonNull
    public <K, V> Map<K, V> getMap(final Class<K> keyClazz, final Class<V> valueClazz, final String key) {
        return MapFormatter.getMap(key, keyClazz, valueClazz, cache);
    }

    /**
     * Get a list from your UfoFile.
     * @param clazz Type which the map's contents are
     * @param key Identifier of this list
     * @return The requested list using the given type
     * @param <T> clazz type
     */
    @NonNull
    public <T> List<T> getList(final Class<T> clazz, final String key){
        return ListFormatter.getList(key, clazz, cache);
    }

    /**
     * Get entry by a specific type.
     * @param clazz Type which the entries content is
     * @param key Identifier of this entry
     * @return Requested object / primitive
     * @param <T> clazz type
     */
    @NonNull
    public <T> T get(final Class<T> clazz, final String key) {
        //check if the given clazz can be used here
        if (CheckType.isListOrMap(clazz)) {
            throw new RuntimeException("Wrong usage. Please use #getList for lists and getMap for maps!");
        }

        //retrieve object from cache
        if (!cache.containsKey(key)) {
            throw new RuntimeException("No entry could be found using identifier: '" + key + "'!");
        }
        final Object object = cache.get(key).getValue();

        //check if entry matches given type
        if (object.getClass() != clazz && !object.getClass().isAssignableFrom(clazz)) {
            throw new RuntimeException("Entry under identifier: '" + key + "' does not match given type: '" + clazz.getName() + "'");
        }

        //format or cast return type
        if (CheckType.isPrimitive(clazz)) {
            return ObjectFormatter.formateObject(clazz, object);
        }
        return clazz.cast(object);
    }

    /**
     * Write the caches contents into the file.
     */
    public void save() {
        QueuedAsyncExecution.queue(() -> {
            try (final PrintWriter writer = new PrintWriter(this.file)) { //Todo: Test if this overwrites current file contents (it should!)
                writer.print(FileManager.buildFile(this.cache));
                writer.flush();
            } catch (final FileNotFoundException exception) {
                throw new RuntimeException("An error occurred while trying to save contents to: '" + this.file.getName() + "'!", exception);
            }
        });
    }

    /**
     * Put a new element to the cache or replace the old value!
     * @param key Identifier of the element
     * @param object Object to save to the file
     */
    public void put(final String key, final Object object) {
        this.cache.put(key, new TypeValue(ObjectFormatter.type(object), object));
    }

    //Todo: Test if this is broken? Topic: Comments vs Line Removal Discussion.
    /**
     * Remove entry with given identifier from this file
     * @param key Identifier of the element
     */
    public void remove(final String key) {
        cache.remove(key);
    }

    /**
     * Feature: Convert this UfoFile to JSON to work with it
     * @return This file formatted to be a JSON
     */
    @NonNull
    public String toJson() {
        return JsonFormatter.toJson(cache);
    }

    /**
     * Feature: Overwrite this files contents with JSON contents
     * @param json JSON to overwrite this files contents with
     */
    @NonNull
    public void fromJson(final String json) {
        this.cache.clear(); //free cache
        this.cache.putAll(JsonFormatter.fromJson(json)); //add in all the contents of this JSON
    }

    /**
     * Feature: Add to this files contents with JSON contents
     * @param json JSON to add to this files contents
     */
    @NonNull
    public void addJson(final String json) {
        this.cache.putAll(JsonFormatter.fromJson(json)); //add in all the contents of this JSON
    }
}

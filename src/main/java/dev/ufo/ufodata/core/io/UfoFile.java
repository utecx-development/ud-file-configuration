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

    //private constructor getting the file directly
    private UfoFile(File file) {
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
    public static UfoFile of(final Class<?> main, final String fileName) throws RuntimeException {
        try {
            final String path = new File(main.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            return UfoFile.of(path, fileName);
        } catch (URISyntaxException exception) {
            throw new RuntimeException(exception);
        }
    }

    public <K, V> Map<K, V> getMap(Class<K> keyClazz, Class<V> valueClazz, String key) {
        return MapFormatter.getMap(key, keyClazz, valueClazz, cache);
    }

    public <T> List<T> getList(Class<T> clazz, String key){
        return ListFormatter.getList(key, clazz, cache);
    }

    public <T> T get(Class<T> clazz, String key) {
        if (!cache.containsKey(key)) {
            throw new RuntimeException("The key " + key + " was not found");
        }

        Object o = cache.get(key).getValue();

        if (o.getClass() != clazz && !o.getClass().isAssignableFrom(clazz)) {
            throw new RuntimeException("The key " + key + " is not an instance of " + clazz.getName());
        }

        if (CheckType.isListOrMap(clazz)) {
            throw new RuntimeException("Wrong methode! Please use getList or getMap.");
        }

        if (CheckType.isPrimitive(clazz)) {
            return ObjectFormatter.formateObject(clazz, o);
        }

        return clazz.cast(o);
    }

    public void save() {

        QueuedAsyncExecution.queue(() ->{
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print(FileManager.buildFile(cache));
                writer.flush();
            } catch (final Exception exception) {
                throw new RuntimeException("A problem occurred while saving to " + this.file.getName() + "!", exception);
            }
        });

    }

    public void put(String key, Object o) {
        this.cache.put(key, new TypeValue(ObjectFormatter.type(o), o));
    }

    public void remove(String key) {
        cache.remove(key);
    }

    public String toJson() {
        return JsonFormatter.toJson(cache);
    }

    public void fromJson(String json) {
        this.cache.putAll(JsonFormatter.fromJson(json));
    }

}

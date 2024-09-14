package de.ufomc.config.io;

import de.ufomc.config.checks.CheckType;
import de.ufomc.config.core.QueuedAsyncExecution;
import de.ufomc.config.format.JsonFormatter;
import de.ufomc.config.format.ListFormatter;
import de.ufomc.config.format.MapFormatter;
import de.ufomc.config.format.ObjectFormatter;
import de.ufomc.config.pre.TypeValue;
import lombok.Getter;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public final class Config {
    private final File file;
    private boolean prettyWriting;

    @Getter
    private final Map<String, TypeValue> cache;

    public Config(String fileName, Class<?> main) {
        try {
            this.file = new File(new File(main.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent(), fileName + ".ud");
        } catch (Exception e) {
            throw new RuntimeException("Could not find an execution path!", e);
        }

        this.cache = FileManager.init(file);
    }

    public Config(String path, String fileName) {
        this.file = new File(path, fileName + ".ud");
        this.cache = FileManager.init(file);
    }

    public Config(File file) {
        this.file = file;
        this.cache = FileManager.init(file);
    }

    public Config prettyWriting(){
        this.prettyWriting = true;
        return this;
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

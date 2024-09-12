package de.ufomc.config.io;

import de.ufomc.config.checks.CheckType;
import de.ufomc.config.format.ListFormatter;
import de.ufomc.config.format.MapFormatter;
import de.ufomc.config.pre.TypeValue;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reader {

    @Getter
    private final Map<String, TypeValue> fileContent;
    private final String fileName;

    public Reader(String fileName) {
        this.fileName = fileName;
        this.fileContent = readAllValuesFromFile();
    }

    public <T> T get(String key, Class<T> clazz) {

        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("The key " + key + " was not found");
        }

        Object o = fileContent.get(key).getValue();

        if (o.getClass() != clazz && !o.getClass().isAssignableFrom(clazz)) {
            throw new RuntimeException("The key " + key + " is not an instance of " + clazz.getName());
        }

        if (CheckType.isListOrMap(clazz)) {
            throw new RuntimeException("Wrong methode! Please use getList or getMap.");
        }

        if (CheckType.isPrimitive(clazz)) {
            return formateObject(clazz, o);
        }

        return clazz.cast(o);

    }

    protected Map<String, TypeValue> readAllValuesFromFile() {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;
            Map<String, TypeValue> map = new HashMap<>();

            //941940.7

            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                String type = line.split(":")[0];
                //String value = line.split("=")[1].replace(";", "");
                String value = line.substring(line.split("=")[0].length() + 1).replace(";", "");
                String key = line.substring(type.length() + 1, line.length() - value.length() - 2);

                if (type.startsWith("map<")) {
                    map.put(key, new TypeValue(type, MapFormatter.formatMap(type, value)));
                } else if (type.startsWith("list<")) {
                    map.put(key, new TypeValue(type, ListFormatter.formatList(type, value)));
                } else {
                    map.put(key, new TypeValue(type, objFromString(type, value)));
                }
            }

            return map;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected String readFileAsString() {

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            String line;
            StringBuilder s = new StringBuilder();

            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("#")) {
                    continue;
                }

                s.append(line);

            }

            return s.toString();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static Object objFromString(String type, String value) {

        return switch (type) {
            case "string", "object" -> value;

            case "int" -> Integer.parseInt(value);
            case "float" -> Float.parseFloat(value);
            case "long" -> Long.parseLong(value);
            case "boolean" -> Boolean.parseBoolean(value);

            default -> throw new RuntimeException("Unsupported type: " + type);
        };

    }

    public <K, V> Map<K, V> getMap(String key, Class<K> keyClass, Class<V> valueClass) {

        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("There was no list for the key: " + key + " in: " + fileName);
        }

        List<K> keys = getTempList(keyClass, key, true);
        List<V> values = getTempList(valueClass, key, false);
        Map<K, V> map = new HashMap<>();

        if (keys.size() != values.size()) {
            throw new RuntimeException("An error appeared during the encoding of " + key + ". Please report this error to our staff team");
        }

        for (int i = 0; i != keys.size(); i++) {
            map.put(keys.get(i), values.get(i));
        }

        return map;

    }

    private <T> List<T> getTempList(Class<T> clazz, String key, boolean isKey) {

        if (!(fileContent.get(key).getValue() instanceof Map<?, ?> map)) {
            throw new RuntimeException("The key: " + key + " is not an instance of a List but a " + fileContent.get(key).getValue().getClass().getSimpleName());
        }

        List<?> list;

        if (isKey) {
            list = new ArrayList<>(map.keySet());
        } else {
            list = new ArrayList<>(map.values());
        }

        if (CheckType.isPrimitive(clazz)) {

            final List<T> objList = new ArrayList<>();

            for (Object t : list) {
                objList.add(formateObject(clazz, t));
            }

            return objList;

        } else {

            if (!clazz.isInstance(list.getFirst())) {
                throw new RuntimeException("The class " + clazz.getName() + " is not an instance of " + fileContent.get(key).getValue().getClass().getName());
            }

            return (List<T>) list;

        }
    }

    public <T> List<T> getList(String key, Class<T> clazz) {

        if (!fileContent.containsKey(key)) {
            throw new RuntimeException("There was no list for the key: " + key + " in: " + fileName);
        }

        if (!(fileContent.get(key).getValue() instanceof List<?> list)) {
            throw new RuntimeException("The key: " + key + " is not an instance of a List but a " + fileContent.get(key).getValue().getClass().getSimpleName());
        }

        if (CheckType.isPrimitive(clazz)) {

            final List<T> objList = new ArrayList<>();

            for (Object t : list) {
                objList.add(formateObject(clazz, t));
            }

            return objList;

        } else {
            if (!clazz.isInstance(list.getFirst())) {
                throw new RuntimeException("The class " + clazz.getName() + " is not an instance of " + fileContent.get(key).getValue().getClass().getName());
            }
            return (List<T>) fileContent.get(key).getValue();
        }

    }

    private <T> T formateObject(Class<T> clazz, Object o) {
        try {

            T nestedObj = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {

                for (Field oField : o.getClass().getDeclaredFields()) {

                    if (field.getName().equals(oField.getName())) {

                        oField.setAccessible(true);
                        field.setAccessible(true);

                        field.set(nestedObj, oField.get(o));

                    }

                }

            }

            return nestedObj;


        } catch (Exception e) {
            throw new RuntimeException("A problem appeared parsing " + clazz.getName(), e);
        }
    }

    public String toJson() {

        try {
            String input = readFileAsString();

            input = input.trim();

            String[] entries = input.split(";");

            // JSON-String Aufbau
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            for (int i = 0; i < entries.length; i++) {
                String entry = entries[i].trim();
                if (entry.isEmpty()) continue;

                String[] keyValue = entry.split(":")[1].split("=", 2);
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();

                jsonBuilder.append("\"").append(key).append("\": ");

                if (value.startsWith("{") && value.endsWith("}")) {
                    jsonBuilder.append(parseObject(value));
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    jsonBuilder.append(parseList(value));
                } else {
                    jsonBuilder.append("\"").append(value).append("\"");
                }

                if (i < entries.length - 1) {
                    jsonBuilder.append(", ");
                }
            }

            jsonBuilder.append("}");
            return jsonBuilder.toString();

        } catch (Exception e) {
            throw new RuntimeException("A problem appeared during the json formatting", e);
        }


    }

    private static String parseObject(String input) {
        input = input.substring(1, input.length() - 1).trim();
        String[] entries = input.split(",");
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i].trim();
            String[] keyValue = entry.split("=", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            jsonBuilder.append("\"").append(key).append("\": ");

            if (value.startsWith("[") && value.endsWith("]")) {
                jsonBuilder.append(parseList(value));
            } else {
                jsonBuilder.append("\"").append(value).append("\"");
            }

            if (i < entries.length - 1) {
                jsonBuilder.append(", ");
            }
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }

    private static String parseList(String input) {
        input = input.substring(1, input.length() - 1).trim();
        String[] values = input.split(",");
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            jsonBuilder.append("\"").append(value).append("\"");

            if (i < values.length - 1) {
                jsonBuilder.append(", ");
            }
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }

}



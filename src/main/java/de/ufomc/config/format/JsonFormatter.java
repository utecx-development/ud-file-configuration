package de.ufomc.config.format;

import de.ufomc.config.core.UDObject;
import de.ufomc.config.io.Config;
import de.ufomc.config.pre.TypeValue;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class JsonFormatter {

    public static String toJson(Map<String, TypeValue> cache) {
        StringBuilder s = new StringBuilder();

        s.append("{\n");

        List<String> keys = new ArrayList<>(cache.keySet());
        List<TypeValue> typeValues = new ArrayList<>(cache.values());

        for (int i = 0; i != cache.size(); i++) {

            String key = keys.get(i);
            TypeValue value = typeValues.get(i);

            s.append("\"")
                    .append(key)
                    .append("\"")
                    .append(":");

            boolean b = !(value.getType().startsWith("map") || value.getType().startsWith("list") || value.getType().startsWith("object"));

            if (b) {
                s.append("\"");
            }

            if (value.getType().startsWith("object")){
                if (value.getValue() instanceof UDObject object) {
                    s.append(object.toJson());
                } else {
                    throw new RuntimeException("An error occurred while parsing an instance of: " + value.getValue().getClass().getSimpleName());
                }
            } else {
                s.append(value.getValue());
            }

            if (b) {
                s.append("\"");
            }

            if (i != cache.size() - 1) {
                s.append(",\n");
            }

        }

        s.append("\n}");

        return s.toString();
    }


    public static Map<String, TypeValue> fromJson(String json) {
        json = json.trim();

        if (!json.startsWith("{") || !json.endsWith("}")) {
            throw new RuntimeException("Invalide json: " + json);
        }

        json = json.substring(1, json.length() - 1).trim();

        final Map<String, TypeValue> jsonContent = new HashMap<>();
        int length = json.length();
        int index = 0;

        while (index < length) {

            index = skipSpace(json, index);

            if (json.charAt(index) != '"') {
                throw new RuntimeException("Invalid key! The key has to start with \"!");
            }

            int keyStart = index + 1;
            int keyEnd = json.indexOf('"', keyStart);
            String key = json.substring(keyStart, keyEnd);

            index = keyEnd + 1;

            index = skipSpace(json, index);

            if (json.charAt(index) != ':') {
                throw new RuntimeException("Invalid json format! No ':' found after key!");
            }

            index++;
            index = skipSpace(json, index);

            final TypeValue value;

            switch (json.charAt(index)) {

                case '{' -> {

                    int closingBraceIndex = findClosing(json, index, '{', '}');

                    value = new TypeValue("object", ObjectFormatter.objFromString(
                            "object", json.substring(index, closingBraceIndex + 1)
                    ));

                    index = closingBraceIndex + 1;

                }
                case '[' -> {

                    int closingBracketIndex = findClosing(json, index, '[', ']');
                    String arrayJson = json.substring(index, closingBracketIndex + 1);

                    if (arrayJson.length() > 2) {
                        value = parseArray(arrayJson);
                    } else {
                        value = null;
                    }

                    index = closingBracketIndex + 1;

                }
                case '"' -> {

                    int valueEnd = json.indexOf('"', index + 1);
                    String strValue = json.substring(index + 1, valueEnd);
                    value = new TypeValue("string", strValue);
                    index = valueEnd + 1;

                }

                default -> {

                    int valueEnd = findValueEnd(json, index);

                    value = parseType(json.substring(index, valueEnd));

                    index = valueEnd;

                }
            }

            if (value != null) {
                jsonContent.put(key, value);
            }

            index = skipSpace(json, index);

            if (index < length && json.charAt(index) == ',') {
                index++;
            }
        }


        return jsonContent;
    }

    private static int skipSpace(String json, int index) {
        //add +1 if the current char is a void (whitespace)
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        //return the next index with a char that is not a void
        return index;
    }

    //find closing to opening such as "[ -> ]; { -> }"
    private static int findClosing(String json, int index, char open, char close) {
        int braceCount = 0;
        for (int i = index; i < json.length(); i++) {
            if (json.charAt(i) == open) {
                braceCount++;
            } else if (json.charAt(i) == close) {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }

        throw new RuntimeException("no " + close + " found for " + open);

    }

    private static TypeValue parseArray(String jsonArray) {

        jsonArray = jsonArray.trim();
        if (!(jsonArray.startsWith("[") || jsonArray.endsWith("]"))) {
            throw new RuntimeException("No valid array!");
        }

        jsonArray = jsonArray.substring(1, jsonArray.length() - 1).trim();
        int length = jsonArray.length();
        int index = 0;

        String type = "";
        StringBuilder s = new StringBuilder();
        s.append("list<");
        List<Object> arrays = new ArrayList<>();

        while (index < length) {

            index = skipSpace(jsonArray, index);

            int valueEnd;
            switch (jsonArray.charAt(index)) {

                case '{' ->{

                    type = "object>";

                    valueEnd = findClosing(jsonArray, index, '{', '}');
                    arrays.add(jsonArray.substring(index, valueEnd + 1));

                    index = valueEnd + 1;

                }

                case '[' ->{

                    valueEnd = findClosing(jsonArray, index, '[', ']');
                    TypeValue tv = parseArray(jsonArray.substring(index, valueEnd + 1));
                    arrays.add(tv.getValue());

                    type = tv.getType() + ">";

                    index = valueEnd + 1;

                }

                case '"' ->{

                    valueEnd = jsonArray.indexOf('"', index + 1);
                    arrays.add(jsonArray.substring(index + 1, valueEnd));

                    type = "string>";

                    index = valueEnd + 1;

                }

                default -> {

                    valueEnd = findValueEnd(jsonArray, index);
                    TypeValue tv = parseType(jsonArray.substring(index, valueEnd));
                    if (tv != null){
                        type = tv.getType() + ">";
                        arrays.add(tv.getValue());
                    }

                    index = valueEnd;

                }
            }

            index = skipSpace(jsonArray, index);
            if (index < length && jsonArray.charAt(index) == ',') {
                index++;
            }
        }

        return new TypeValue(s.append(type).toString(), arrays);
    }

    private static int findValueEnd(String json, int index) {
        while (index < json.length() && !Character.isWhitespace(json.charAt(index))
                && json.charAt(index) != ',' && json.charAt(index) != ']' && json.charAt(index) != '}') {
            index++;
        }
        return index;
    }

    private static TypeValue parseType(String type) {
        switch (type) {
            case "null" -> {
                return null;
            }
            case "true" -> {
                return new TypeValue("boolean", true);
            }
            case "false" -> {
                return new TypeValue("boolean", false);
            }

            case null -> throw new RuntimeException("""
                    Type from json was null!
                    This error should not be happening if you do have a value after every key.
                    Please contact our staff team! https://discord.gg/gzxrub5ABQ
                    """);

            default -> {
                try {
                    return new TypeValue("int", Integer.parseInt(type));
                } catch (NumberFormatException e) {
                    try {
                        return new TypeValue("double", Double.parseDouble(type));
                    } catch (NumberFormatException ex) {
                        throw new RuntimeException("Unknown type: " + type);
                    }
                }
            }
        }
    }
}

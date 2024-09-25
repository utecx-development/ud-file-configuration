package dev.ufo.ufodata.core.format;

import dev.ufo.ufodata.core.UDObject;
import dev.ufo.ufodata.lib.TypeValue;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public final class JsonFormatter {

    /**
     * Generates JSON from the given UD contents
     * @param cache UfoFile's contents
     * @return The generated JSON
     */
    @NonNull
    public static String toJson(final Map<String, TypeValue> cache) {
        final StringBuilder builder = new StringBuilder();

        //open a new object
        builder.append("{\n");

        //loop through contents to generate JSON
        for (Map.Entry<String, TypeValue> entry : cache.entrySet()) {
            String key = entry.getKey();
            TypeValue value = entry.getValue();
            //format key
            builder.append("\"").append(key).append("\"").append(":");

            //check if given entry is a simple datatype
            final boolean primitive = !(value.getType().startsWith("map") || value.getType().startsWith("list") || value.getType().startsWith("object"));

            //open string
            if (primitive) builder.append("\"");

            //format object if extends UDObject
            if (value.getType().startsWith("object")) {
                if (value.getValue() instanceof final UDObject object) {
                    builder.append(object.toJson());
                } else {
                    throw new RuntimeException("An error occurred while parsing an instance of: " + value.getValue().getClass().getSimpleName());
                }
            } else {
                builder.append(value.getValue());
            }

            //close string
            if (primitive) builder.append("\"");

            //add a comma & a line break
            builder.append(",\n");
        }

        //remove the last 3 chars incase there is a content in the JSON
        if (builder.length() > 10) builder.setLength(builder.length() - 3); //10 is experimental here.
        //close the object
        builder.append("}");

        //return generated JSON
        return builder.toString();
    }

    /**
     *
     * @param json
     * @return
     */
    @NonNull
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

                    value = new TypeValue("object", ObjectFormatter.toObject(
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

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
        cache.forEach((key, value) -> {
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
        });

        //remove the last 3 chars incase there is a content in the JSON
        if (builder.length() > 10) builder.setLength(builder.length() - 3); //10 is experimental here.
        //close the object
        builder.append("}");
        //return generated JSON
        return builder.toString();
    }

    /**
     * Generate an UfoFiles contents from an input JSON string
     * Please ensure that you're input is in a compatible format!
     * @param json The JSON you want to convert
     * @return Contents ready to be used within an UfoFile
     */
    @NonNull
    public static Map<String, TypeValue> fromJson(String json) {
        json = json.trim(); //remove empty spaces & line breaks from JSON (cleanup)

        //check if at least basic premises are met by input
        if (!json.startsWith("{") || !json.endsWith("}")) throw new RuntimeException("Invalid JSON: '" + json + "'");
        //cut outer brackets!
        json = json.substring(1, json.length() - 1).trim();

        final Map<String, TypeValue> content = new HashMap<>();
        final int length = json.length();
        int index = 0;

        //loop through every character index within this string
        while (index < length) {
            index = skipSpace(json, index);

            //every key has to be a string!
            if (json.charAt(index) != '"') throw new RuntimeException("Invalid key! The key has to start with \"!");

            final int keyStart = index + 1;
            final int keyEnd = json.indexOf('"', keyStart);
            final String key = json.substring(keyStart, keyEnd);

            //invalid JSON
            index = skipSpace(json, keyEnd + 1);
            if (json.charAt(index) != ':') throw new RuntimeException("Invalid JSON: No ':' found after key!");

            //check what type of content is found here & read it
            index = skipSpace(json, index + 1);
            final TypeValue value;
            switch (json.charAt(index)) {
                case '{' -> {
                    int closingBraceIndex = findClosingBracketIndex(json, index, '{', '}');
                    value = new TypeValue("object", ObjectFormatter.toObject(
                            "object", json.substring(index, closingBraceIndex + 1)
                    ));
                    index = closingBraceIndex + 1;
                }
                case '[' -> {
                    int closingBracketIndex = findClosingBracketIndex(json, index, '[', ']');
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
                    value = findTypeAndBundle(json.substring(index, valueEnd));
                    index = valueEnd;
                }
            }

            //value actually can't be null here, but check for safety??
            if (value != null) content.put(key, value);

            //skip comma at the end of line
            index = skipSpace(json, index);
            if (index < length && json.charAt(index) == ',') {
                index++;
            }
        }
        return content;
    }

    /**
     * Skip all empty spaces (Todo: This should not be necessary! Use either json.trim() or this!)
     * @param json JSON blob to work in
     * @param index Current index
     * @return Next available index where there is actually content there
     */
    private static int skipSpace(final String json, int index) {
        //add +1 if the current char is a void (whitespace)
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
        //return the next index with a char that is not a void
        return index;
    }

    /**
     * find index of closing bracket to opening bracket such as "[ -> ]; { -> }"
     * @param json JSON blob to check on
     * @param index current bracket index
     * @param openingChar opening char (Todo: Automatically find closingChar via a mapping)
     * @param closingChar closing char to search for
     * @return index of closing char to work with
     */
    private static int findClosingBracketIndex(String json, int index, char openingChar, char closingChar) {
        int braceCount = 0;
        for (int i = index; i < json.length(); i++) {
            if (json.charAt(i) == openingChar) {
                braceCount++;
            } else if (json.charAt(i) == closingChar) {
                braceCount--;
                if (braceCount == 0) {
                    return i;
                }
            }
        }
        throw new RuntimeException("no " + closingChar + " found for " + openingChar);
    }

    /**
     * Parses an array represented by a JSON blob.
     * @param jsonArray Given JSON blob to work with.
     * @return The parsed array in UfoData's TypeValue structure
     */
    private static TypeValue parseArray(String jsonArray) {
        //not really necessary here because has been done before but for convenience reasons
        jsonArray = jsonArray.trim(); //remove empty spaces & line breaks from JSON (cleanup)

        //check if at least basic premises are met by input
        if (!(jsonArray.startsWith("[") || jsonArray.endsWith("]"))) throw new RuntimeException("No valid array!");
        //cut outer brackets
        jsonArray = jsonArray.substring(1, jsonArray.length() - 1).trim();

        final List<Object> array = new ArrayList<>(); //collects all of the arrays contents
        String type = ""; //searching for content type (problem: Can be assigned multiple times as of now)
        int length = jsonArray.length(); //length of the full JSON Array before we work with it
        int index = 0; //cursor

        //loop through the arrays contents
        while (index < length) {
            index = skipSpace(jsonArray, index);

            //check type of input by checking which char introduces next part
            switch (jsonArray.charAt(index)) {
                case '{' -> {
                    final int valueEnd = findClosingBracketIndex(jsonArray, index, '{', '}');
                    array.add(jsonArray.substring(index, valueEnd + 1)); //Todo: Test, Is this really working? There seems to be something missing here
                    type = "object";
                    index = valueEnd + 1;
                }
                case '[' -> {
                    final int valueEnd = findClosingBracketIndex(jsonArray, index, '[', ']');
                    final TypeValue typeValue = parseArray(jsonArray.substring(index, valueEnd + 1)); //inner array
                    array.add(typeValue.getValue());
                    type = typeValue.getType();
                    index = valueEnd + 1;
                }
                case '"' -> {
                    final int valueEnd = jsonArray.indexOf('"', index + 1);
                    array.add(jsonArray.substring(index + 1, valueEnd));
                    type = "string";
                    index = valueEnd + 1;
                }
                default -> {
                    final int valueEnd = findValueEnd(jsonArray, index);
                    final TypeValue typeValue = findTypeAndBundle(jsonArray.substring(index, valueEnd));
                    if (typeValue != null){
                        type = typeValue.getType();
                        array.add(typeValue.getValue());
                    }
                    index = valueEnd;
                }
            }

            //skip the comma at the end of the line
            index = skipSpace(jsonArray, index);
            if (index < length && jsonArray.charAt(index) == ',') {
                index++;
            }
        }

        //package results into TypeValue and return them
        return new TypeValue("list<" + type + ">", array);
    }

    /**
     * Skip to the end index of the current read value
     * @param json Json to check for value end
     * @param index Current cursor index
     * @return New index, paced forward to be the value's end index
     */
    private static int findValueEnd(final String json, int index) {
        while (index < json.length() && !Character.isWhitespace(json.charAt(index))
                && json.charAt(index) != ',' && json.charAt(index) != ']' && json.charAt(index) != '}') {
            index++;
        }
        return index;
    }

    /**
     * Checks the type of the given input. Can either be null, "null", a booleanish value, an integer or a double!
     * @param data Given input to check
     * @return A bundled TypeValue object containing the correct type & the data!
     */
    private static TypeValue findTypeAndBundle(final String data) {
        switch (data) {
            case "null"://empty value
                return null;
            case null:
                return null;
            case "true"://boolean true
                return new TypeValue("boolean", true);
            case "false"://boolean false
                return new TypeValue("boolean", false);
            default://try format as integer or double
                try {
                    return new TypeValue("int", Integer.parseInt(data)); //try integer
                } catch (final NumberFormatException exception) {
                    try {
                        return new TypeValue("double", Double.parseDouble(data)); //try double
                    } catch (final NumberFormatException exception2) {
                        throw new RuntimeException("Unknown type: '" + data + "'");
                    }
                }
        }
    }
}

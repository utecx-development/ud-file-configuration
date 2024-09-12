package de.ufomc.config.format;

import de.ufomc.config.io.Config;
import de.ufomc.config.pre.TypeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonFormatter {

    public static String toJson(Map<String, TypeValue> cache) {
        StringBuilder s = new StringBuilder();

        s.append("{");

        List<String> keys = new ArrayList<>(cache.keySet());
        List<TypeValue> typeValues = new ArrayList<>(cache.values());

        for (int i = 0; i != cache.size(); i++){

            String key = keys.get(i);
            TypeValue value = typeValues.get(i);

            s.append("\"")
                    .append(key)
                    .append("\"")
                    .append(":");

            switch (value.getType()){

                case "list" -> {
                    s.append("[");

                    System.out.println("ola");

                    s.append("]");
                }
                case "map" -> s.append("");
                case "object" -> s.append("");

                default -> s.append("\"")
                        .append(value.getValue())
                        .append("\"");


            }

            if (i != cache.size() - 1) {
                s.append(",");
            }

        }

        s.append("}");

        return s.toString();
    }




    //TODO
    public static Map<String, TypeValue> fromJson(String json) {
        StringBuilder s = new StringBuilder();


        //


        // case Int
        Pattern patternOfInt = Pattern.compile("\".*\": \\d+");

        //case String
        Pattern patternOfString = Pattern.compile("\".*\": \".*\"");

        //case Object
        Pattern patternOfObject = Pattern.compile("\"[^\"]+\":\\s*\\{[^{}]*\\}");

        //case List
        Pattern patternOfList = Pattern.compile("\".*\": [.*]");




        Matcher machter = patternOfObject.matcher(json);

        System.out.println(machter.group(1));

        return new HashMap<>();
    }



    // Test TODO DELETE
    public static void main(String[] args) throws Exception {

        String path = Config.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()
                .getPath()
                .substring(1);

        System.out.println(path);

        Config config = new Config(path, "config");

        config.put("hello", "world");
        config.put("ola", "olaaa");
        config.put("list", List.of(1));
        config.save();

        //System.out.println(config.toJson());





        // Test fromJson
        fromJson("""
                {
                  "hello": "world",
                  "list": ["hallo", "oder", "so"],
                  "map": {
                    "hallo": "ichbineinemap"
                  },
                  "object": {
                    "name": "hello",
                    "age": 13
                  }
                }
                """);
    }



}

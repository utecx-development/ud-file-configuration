package dev.ufo.etc;

public class FieldSerializer {

    public static Object serialize(String s) {

        if (s.startsWith("{") || s.startsWith("[")) {
            return s;
        }

        if (s.startsWith("\"")) {
            return s;
        }

        try {
            return Integer.parseInt(s);
        } catch (Exception a) {
            try {
                return Long.parseLong(s);
            } catch (Exception b) {
                try {
                    return Double.parseDouble(s);
                } catch (Exception ignored) {

                }
            }
        }

        try {
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            throw new RuntimeException("could not find a type for '" + s + "'", e);
        }

    }

}

package dev.ufo.etc;

public class FieldSerializer {

    public static Object serialize(String s) {

        if (s.startsWith("\"")) {
            return s.substring(1, s.length() - 1);
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

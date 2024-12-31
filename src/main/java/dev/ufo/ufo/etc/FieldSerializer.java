package dev.ufo.etc;

public class FieldSerializer {

    /**
     * Gets a data type from a string
     * @param string the string input
     * @return The data found
     */
    public static Object serialize(String string) {

        if (string.startsWith("{") || string.startsWith("[") || string.startsWith("\"")) {
            return string;
        }


        try {
            return Integer.parseInt(string);
        } catch (Exception a) {
            try {
                return Long.parseLong(string);
            } catch (Exception b) {
                try {
                    return Double.parseDouble(string);
                } catch (Exception ignored) {

                }
            }
        }

        try {
            return Boolean.parseBoolean(string);
        } catch (Exception e) {
            throw new RuntimeException("could not find a type for '" + string + "'", e);
        }

    }

}

package dev.ufo.ufodata.core.format;

import dev.ufo.ufodata.lib.TypeValue;

public class NumberSerializer {

    public static TypeValue serialize(final String data) {

        try {
            return new TypeValue("int", Integer.parseInt(data)); //try integer
        } catch (final NumberFormatException ignored) {}

        try {
            return new TypeValue("long", Long.parseLong(data));
        } catch (NumberFormatException ignored) {}

        try {
            return new TypeValue("double", Double.parseDouble(data)); //try double
        } catch (final NumberFormatException ignored) {}

        try {
            return new TypeValue("float", Float.parseFloat(data)); //try double
        } catch (final NumberFormatException ignored) {}

        throw new RuntimeException("Unknown type: '" + data + "'");

    }

}

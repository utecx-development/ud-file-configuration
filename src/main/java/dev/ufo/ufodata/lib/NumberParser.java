package dev.ufo.ufodata.lib;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class NumberParser {

    @NonNull
    public static TypeValue serialize(final String data) {
        try {
            return new TypeValue("int", Integer.parseInt(data)); //try integer
        } catch (final NumberFormatException ignored) {}

        try {
            return new TypeValue("long", Long.parseLong(data)); //try long
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

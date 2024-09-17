package de.ufomc.config.checks;

import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public final class CheckEquals {

    /**
     * Compare one string with a bunch of others.
     * @param source given string
     * @param compare an array of Strings to compare against
     * @return true if all given Strings are equal.
     */
    public static boolean allEqual(final String source, final String... compare) {
        return Arrays.stream(compare).allMatch(source::equals); //checks if all strings match
    }

    /**
     * There only has to be one equal to return true
     * @param source given string
     * @param equivalents an array of Strings to compare against
     * @return true if only one of all given Strings is equal to source
     */
    public static boolean oneEqual(final String source, final String... compare) {
        return Arrays.stream(compare).anyMatch(against -> against.equals(source)); //checks if more than 0 strings match
    }
}

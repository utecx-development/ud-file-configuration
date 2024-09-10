package de.ufomc.config.core;

public class CheckEquals {

    //all have to be equivalent to return true
    public static boolean allEqual(String s, String... equivalents) {

        for (String c : equivalents) {

            if (!(c.equals(s))){
                return false;
            }

        }

        return true;

    }

    //there has to be 1 equivalent to return true
    public static boolean oneEqual(String s, String... equivalents) {
        for (String c : equivalents) {

            if (c.equals(s)){
                return true;
            }

        }

        return false;

    }

}

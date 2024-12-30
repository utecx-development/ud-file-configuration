package dev.ufo.etc.listing;

import dev.ufo.etc.UDObject;

import java.util.List;

public class ListAdapter {

    public static String toString(List<?> list) {

        StringBuilder b = new StringBuilder("[");

        for (Object o : list) {

            b.append(UDObject.serializeObject(o));

            if (list.get(list.size() - 1) != o) {
                b.append(",");
            }

        }

        return b.append("]").toString();

    }

}

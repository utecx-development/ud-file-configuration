package dev.ufo.etc.listing;

import dev.ufo.etc.UDObject;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class MapAdapter {

    public static String toString(Map<?, ?> map) {

        StringBuilder b = new StringBuilder("<");

        AtomicInteger i = new AtomicInteger();
        map.forEach((key, value)->{
            i.getAndIncrement();

            b.append(UDObject.serializeObject(key));
            b.append("-");
            b.append(UDObject.serializeObject(value));

            if (i.get() != map.size()) b.append(",");

        });

        return b.append(">").toString();

    }

}

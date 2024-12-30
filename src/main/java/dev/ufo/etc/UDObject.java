package dev.ufo.etc;

import dev.ufo.etc.listing.ListAdapter;
import dev.ufo.etc.listing.MapAdapter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class UDObject {

    private final Map<String, Object> fields = new HashMap<>();

    public UDObject() {
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.getName().equals("fields")) continue;
                field.setAccessible(true);
                fields.put(field.getName(), field.get(this));
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse the object '" + this.toString() + "'", e);
        }

    }

    public void initFrom(Object o) {
        try {
            fields.clear();
            for (Field f : o.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                fields.put(f.getName(), f.get(o));
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not parse the object '" + this.toString() + "'", e);
        }
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("{");

        AtomicInteger i = new AtomicInteger();
        fields.forEach((key, value) ->{
            i.getAndIncrement();

            b.append(key);
            b.append("=");

            try {

                b.append(serializeObject(value));

            } catch (Exception e) {
                b.append("\"null\"");
                e.printStackTrace();
            }

            if (i.get() != fields.size()) {
                b.append(",");
            }

        });

        return b.append("}").toString();
    }

    private static final String space = "\"";

    public static String serializeObject(Object o) {

        if (TypeCheck.isPrimitive(o.getClass())) {
            if (o.getClass() == String.class) {
                if (o.toString().startsWith(space) && o.toString().endsWith(space)) {
                    return space + o + space;
                }
            }
            return o.toString();
        }

        if (List.class.isAssignableFrom(o.getClass())) {
            return ListAdapter.toString((List<?>) o);
        } else if (Map.class.isAssignableFrom(o.getClass())) {
            return MapAdapter.toString((Map<?, ?>) o);
        }

        try {
            UDObject ud = new UDObject();
            ud.initFrom(o);
            return ud.toString();
        } catch (Exception e) {
            throw new RuntimeException("The object '" + o + "' is not serializable");
        }

    }

}

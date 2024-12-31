package dev.ufo.etc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class JsonConverter {

    public static String toJson(Object o) {

        try {
            StringBuilder b = new StringBuilder();

            if (TypeCheck.isPrimitive(o.getClass())) {
                return toJsonElement(o);
            }

            if (TypeCheck.isListOrMap(o.getClass())) {

                if (o instanceof List<?> l) {

                    b.append("[");

                    for (int i = 0; i != l.size(); i++) {
                        Object obj = l.get(i);

                        b.append(toJsonElement(obj));

                        if (i != l.size() - 1) {
                            b.append(",");
                        }

                    }

                    b.append("]");

                    return b.toString();

                } else if (o instanceof Map<?, ?> map) {

                    b.append("{");

                    AtomicInteger i = new AtomicInteger();
                    map.forEach((key, value) -> {
                        i.getAndIncrement();

                        b.append(toJsonElement(key.toString()));
                        b.append(":");
                        b.append(toJsonElement(value));

                        if (i.get() != map.size()) {
                            b.append(",");
                        }

                    });

                    b.append("}");

                    return b.toString();

                } else {
                    //TODO
                    throw new RuntimeException("");
                }

            } else {

                b.append("{");

                Field[] fields = o.getClass().getDeclaredFields();

                for (int i = 0; i!=fields.length; i++) {

                    Field field = fields[i];

                    field.setAccessible(true);

                    b.append(toJsonElement(field.getName()));
                    b.append(":");
                    b.append(toJsonElement(field.get(o)));

                    if (i != fields.length - 1){
                        b.append(",");
                    }

                }

                b.append("}");


                return b.toString();

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toJsonElement(Object o) {

        if (TypeCheck.isPrimitiveExcludedString(o.getClass())) {
            return o.toString();
        } else if (o instanceof String s) {
            if (s.startsWith("[") || s.startsWith("{")) {
                return s;
            }
            return "\"" + s+ "\"";
        }

        return toJson(o);

    }

}

package de.ufomc.config.core;

import de.ufomc.config.checks.CheckType;
import lombok.NonNull;

import java.lang.reflect.Field;

public abstract class UDObject {

    /*
    * This class is used to fetch complex objects.
    * We accomplish this overwriting the toString method.
    */

    @Override
    @NonNull
    public String toString() {
        StringBuilder s = new StringBuilder();

        s.append("{");

        Field[] fields = this.getClass().getDeclaredFields();

        //loop through fields
        for (int i = 0; i != fields.length; i++) {
            Field field = fields[i];
            try {
                field.setAccessible(true);

                Object fieldValue = field.get(this);
                String fieldType = field.getType().getSimpleName().toLowerCase();

                String valueRepresentation;

                if (fieldValue != null && CheckType.isPrimitive(field.getType()) && !(fieldValue instanceof String)) {
                    valueRepresentation = fieldValue.toString();
                } else {
                    valueRepresentation = fieldValue != null ? fieldValue.toString() : "null";
                }

                s.append(fieldType)
                        .append(":")
                        .append(field.getName())
                        .append("=")
                        .append(valueRepresentation);

                //only append ',' if the current field is not the last one.
                if (fields.length - 1 != i){
                    s.append(",");
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        s.append("}");
        return s.toString();
    }
}

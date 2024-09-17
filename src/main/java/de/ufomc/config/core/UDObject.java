package de.ufomc.config.core;

import de.ufomc.config.checks.CheckType;
import lombok.NonNull;

import java.lang.reflect.Field;

public abstract class UDObject {

    /**
     * Format this UDObject to a representative String
     * @return object serialized as a String
     */
    @Override
    @NonNull
    public String toString() {
        final StringBuilder builder = new StringBuilder("{");
        final Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i != fields.length; i++) { //loop through fields
            final Field field = fields[i];
            try {
                field.setAccessible(true);

                final Object fieldValue = field.get(this);
                final String fieldType = field.getType().getSimpleName().toLowerCase();

                String valueRepresentation;
                if (fieldValue != null && CheckType.isPrimitive(field.getType()) && !(fieldValue instanceof String)) {
                    valueRepresentation = fieldValue.toString();
                } else {
                    valueRepresentation = fieldValue != null ? fieldValue.toString() : "null";
                }

                builder.append(fieldType).append(":")
                        .append(field.getName()).append("=")
                        .append(valueRepresentation);

                //only append ',' if the current field is not the last one.
                if (fields.length - 1 != i) {
                    builder.append(",");
                }
            } catch (final IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
        builder.append("}");
        return builder.toString();
    }

    /**
     *
     * @return
     */
    @NonNull
    public String toJson() {
        final StringBuilder builder = new StringBuilder("{");
        final Field[] fields = this.getClass().getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            try {
                field.setAccessible(true);

                Object fieldValue = field.get(this);

                builder.append("\"").append(field.getName()).append("\":");

                if (fieldValue == null) {
                    builder.append("null");
                } else if (fieldValue instanceof String) {
                    builder.append("\"").append(fieldValue).append("\"");
                } else if (CheckType.isPrimitive(field.getType()) || fieldValue instanceof Number || fieldValue instanceof Boolean) {
                    builder.append(fieldValue);
                } else {
                    builder.append("\"").append(fieldValue).append("\"");
                }

                if (i < fields.length - 1) {
                    builder.append(",");
                }

            } catch (final IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        builder.append("}");
        return builder.toString();
    }
}

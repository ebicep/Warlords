package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigUtils {

    public static <T> T getValue(Document document, List<String> namespaces, String key, Class<T> fieldType) {
        return getValue(document, namespaces, key, fieldType, defaultValue(fieldType));
    }

    public static <T> T getValue(Document document, List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        return getValue(document, namespaces, key, fieldType, defaultValue, false);
    }

    private static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type.equals(String.class) ||
                type.equals(Integer.class) ||
                type.equals(Float.class) ||
                type.equals(Double.class) ||
                type.equals(Boolean.class) ||
                type.equals(Long.class) ||
                type.equals(Short.class) ||
                type.equals(Byte.class);
    }

    public static <T> T getValue(Document document, List<String> namespaces, String key, Class<T> fieldType, boolean optionalField) {
        return getValue(document, namespaces, key, fieldType, defaultValue(fieldType), optionalField);
    }

    /**
     * Gets a value from the abilities configuration based on namespace and a dot-separated key path.
     *
     * @param namespace The namespace to look in (e.g., "pvp", "pve")
     * @param key       The dot-separated key path (e.g., "ArcaneShield.damageValues.strikeDamage")
     * @param fieldType The expected type of the value
     * @param <T>       The type parameter for the return value
     *
     * @return The value cast to the requested type, or a default value if not found
     */
    private static <T> Result<T> getValue(Document document, String namespace, String key, Class<T> fieldType) {
        // Get the namespace document
        Document namespaceDoc = document.get(namespace, Document.class);
        if (namespaceDoc == null) {
            return new Result<>(ValueResult.INVALID_NAMESPACE);
        }

        String[] keyParts = key.split("\\.");
        if (keyParts.length == 0) {
            return new Result<>(ValueResult.INVALID_KEY);
        }

        // Navigate to the parent object that contains our target value
        Document currentObject = namespaceDoc;

        // Navigate through all key parts except the last one
        for (int i = 0; i < keyParts.length - 1; i++) {
            String part = keyParts[i];
            currentObject = currentObject.get(part, Document.class);
            if (currentObject == null) {
                return new Result<>(ValueResult.INVALID_PATH);
            }
        }

        // Extract the final value using the last key part
        String finalKey = keyParts[keyParts.length - 1];
        if (!currentObject.containsKey(finalKey)) {
            return new Result<>(ValueResult.INVALID_FIELD);
        }

        Object value = currentObject.get(finalKey);

        if (fieldType.equals(List.class) && value instanceof List<?>) {
            return new Result<>((T) value, ValueResult.SUCCESS); // Raw list, no type-checking
        }
        // If the value is a document and we're expecting a complex type, build the object
        if (value instanceof Document && !isSimpleType(fieldType)) {
            return buildValueObject(fieldType, (Document) value);
        }

        return new Result<>(cast(value, fieldType), ValueResult.SUCCESS);
    }

    public static <T> T getValue(Document document, List<String> namespaces, String key, Class<T> fieldType, T defaultValue, boolean optionalField) {
        if (document == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return defaultValue;
        }
        Result<T> result = null;
        for (String namespace : namespaces) {
            result = getValue(document, namespace, key, fieldType);
            if (result.valueResult() == ValueResult.SUCCESS) {
                break;
            }
        }
        if (result == null || result.value() == null) {
            String debug = " (" + String.join(",", namespaces) + ") (" + key + ")" + " (" + fieldType.getName() + ")";
            if (result != null) {
                if (!optionalField) { // result.valueResult() != ValueResult.INVALID_FIELD ||
                    ChatUtils.MessageType.CONFIG.sendErrorMessage(result.valueResult() + debug);
                }
            } else {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("No Result" + debug);
            }
            return defaultValue;
        }
        return result.value();
    }

    private static <T> Result<T> buildValueObject(Class<T> type, Document doc) {
        try {
            T value = null;
            if (type.equals(Value.RangedValue.class)) {
                value = type.getConstructor(float.class, float.class)
                            .newInstance(
                                    getFloatValue(doc.get("min")),
                                    getFloatValue(doc.get("max"))
                            );
            } else if (type.equals(Value.RangedValueCritable.class)) {
                value = type.getConstructor(float.class, float.class, float.class, float.class)
                            .newInstance(
                                    getFloatValue(doc.get("min")),
                                    getFloatValue(doc.get("max")),
                                    getFloatValue(doc.get("critChance")),
                                    getFloatValue(doc.get("critMultiplier"))
                            );
            } else if (type.equals(Value.SetValue.class)) {
                value = type.getConstructor(float.class)
                            .newInstance(getFloatValue(doc.get("value")));
            } else {
                return new Result<>(ValueResult.INVALID_OBJECT);
            }
            return new Result<>(value, ValueResult.SUCCESS);
        } catch (Exception e) {
            return new Result<>(ValueResult.INVALID_OBJECT);
        }
    }

    public static <T> Map<String, T> getMapValue(
            Document document,
            List<String> namespaces,
            String key,
            Class<T> valueType
    ) {
        return getMapValue(document, namespaces, key, valueType, false);
    }

    private static float getFloatValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            return Float.parseFloat((String) value);
        } else {
            throw new IllegalArgumentException("Cannot convert " + value + " to float");
        }
    }

    public static <T> Map<String, T> getMapValue(
            Document document,
            List<String> namespaces,
            String key,
            Class<T> valueType,
            boolean optionalField
    ) {
        if (document == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return Map.of();
        }

        for (String namespace : namespaces) {
            Document namespaceDoc = document.get(namespace, Document.class);
            if (namespaceDoc == null) {
                continue;
            }

            String[] keyParts = key.split("\\.");
            Document current = namespaceDoc;

            for (String part : keyParts) {
                current = current.get(part, Document.class);
                if (current == null) {
                    break;
                }
            }

            if (current != null) {
                Map<String, T> result = new LinkedHashMap<>();
                for (Map.Entry<String, Object> entry : current.entrySet()) {
                    T casted = cast(entry.getValue(), valueType);
                    if (casted != null) {
                        result.put(entry.getKey(), casted);
                    }
                }
                return result;
            }
        }

        if (!optionalField) {
            String debug = " (" + String.join(",", namespaces) + ") (" + key + ")";
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Map not found" + debug);
        }

        return Map.of();
    }

    public static <T> List<T> getListValue(Document document, List<String> namespaces, String key, Class<T> itemType) {
        return getListValue(document, namespaces, key, itemType, false);
    }

    public static <T> List<T> getListValue(Document document, List<String> namespaces, String key, Class<T> itemType, boolean optionalField) {
        if (document == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return List.of();
        }

        for (String namespace : namespaces) {
            Document namespaceDoc = document.get(namespace, Document.class);
            if (namespaceDoc == null) {
                continue;
            }

            String[] keyParts = key.split("\\.");
            Document current = namespaceDoc;

            for (int i = 0; i < keyParts.length - 1; i++) {
                current = current.get(keyParts[i], Document.class);
                if (current == null) {
                    break;
                }
            }

            if (current != null && current.containsKey(keyParts[keyParts.length - 1])) {
                Object value = current.get(keyParts[keyParts.length - 1]);
                return castList(value, itemType);
            }
        }

        if (!optionalField) {
            String debug = " (" + String.join(",", namespaces) + ") (" + key + ")";
            ChatUtils.MessageType.CONFIG.sendErrorMessage("List not found" + debug);
        }
        return List.of();
    }

    private static <T> List<T> castList(Object value, Class<T> itemType) {
        if (!(value instanceof List<?> rawList)) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Expected a List but got " + value.getClass().getName());
            return List.of();
        }

        List<T> result = new ArrayList<>();
        for (Object item : rawList) {
            T castedItem = cast(item, itemType);
            if (castedItem != null) {
                result.add(castedItem);
            } else {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to cast list item: " + item + " to " + itemType.getName());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object value, Class<T> type) {
        if (value == null) {
            return defaultValue(type);
        }

        if (type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }

        if (type.equals(int.class) || type.equals(Integer.class)) {
            if (value instanceof Number) {
                return (T) Integer.valueOf(((Number) value).intValue());
            }
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            if (value instanceof Number) {
                return (T) Float.valueOf(((Number) value).floatValue());
            }
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            if (value instanceof Boolean) {
                return (T) value;
            }
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            if (value instanceof Number) {
                return (T) Short.valueOf(((Number) value).shortValue());
            }
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            if (value instanceof Number) {
                return (T) Byte.valueOf(((Number) value).byteValue());
            }
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            }
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
        } else if (type.isEnum()) {
            // Handle enum types
            if (value instanceof String) {
                try {
                    return (T) Enum.valueOf((Class<Enum>) type, (String) value);
                } catch (IllegalArgumentException e) {
                    ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to cast value to enum " + type.getName());
                }
            }
        }

        ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to cast value of type " + value.getClass().getName() + " to " + type.getName());
        return defaultValue(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultValue(Class<T> type) {
        if (type.equals(boolean.class)) {
            return (T) Boolean.FALSE;
        } else if (type.equals(int.class)) {
            return (T) Integer.valueOf(0);
        } else if (type.equals(float.class)) {
            return (T) Float.valueOf(0.0f);
        } else if (type.equals(double.class)) {
            return (T) Double.valueOf(0.0);
        } else if (type.equals(long.class)) {
            return (T) Long.valueOf(0L);
        } else if (type.equals(byte.class)) {
            return (T) Byte.valueOf((byte) 0);
        } else if (type.equals(short.class)) {
            return (T) Short.valueOf((short) 0);
        } else if (type.equals(char.class)) {
            return (T) Character.valueOf('\0');
        }

        if (type.equals(String.class)) {
            return (T) "UNKNOWN";
        }

        if (type.equals(Value.RangedValue.class)) {
            return (T) new Value.RangedValue(0, 0);
        } else if (type.equals(Value.RangedValueCritable.class)) {
            return (T) new Value.RangedValueCritable(0, 0, 0, 0);
        } else if (type.equals(Value.SetValue.class)) {
            return (T) new Value.SetValue(0);
        }

        if (type.equals(List.class)) {
            return (T) List.of();
        } else if (type.equals(Map.class)) {
            return (T) Map.of();
        }

        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            if (constants.length > 0) {
                return (T) constants[0];
            }
        }

        return null;
    }


}

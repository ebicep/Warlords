package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.lang.reflect.Field;
import java.util.List;

public class AbilitiesConfig implements ConfigManager.Config {

    public Document abilitiesConfig;
    // TODO cache

    @Override
    public String getName() {
        return "ABILITIES";
    }

    @Override
    public void load(Document doc) {
        this.abilitiesConfig = doc;
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        if (abilitiesConfig == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return defaultValue(fieldType);
        }
        Result<T> result = null;
        for (String namespace : namespaces) {
            result = getValue(namespace, key, fieldType);
            if (result.valueResult == ValueResult.SUCCESS) {
                break;
            }
        }
        if (result == null || result.value == null) {
            String debug = " (" + String.join(",", namespaces) + ") (" + key + ")" + " (" + fieldType.getName() + ")";
            if (result != null) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage(result.valueResult + debug);
            } else {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("No Result" + debug);
            }
            return defaultValue(fieldType);
        }
        return result.value;
    }

    /**
     * Gets a value from the abilities configuration based on namespace and a dot-separated key path.
     *
     * @param namespace The namespace to look in (e.g., "pvp", "pve")
     * @param key The dot-separated key path (e.g., "ArcaneShield.damageValues.strikeDamage")
     * @param fieldType The expected type of the value
     * @param <T> The type parameter for the return value
     * @return The value cast to the requested type, or a default value if not found
     */
    private <T> Result<T> getValue(String namespace, String key, Class<T> fieldType) {
        // Get the namespace document
        Document namespaceDoc = abilitiesConfig.get(namespace, Document.class);
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
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Field '" + finalKey + "' not found in path: " + key);
            return new Result<>(ValueResult.INVALID_FIELD);
        }

        Object value = currentObject.get(finalKey);

        // If the value is a document and we're expecting a complex type, build the object
        if (value instanceof Document && !isSimpleType(fieldType)) {
            return buildValueObject(fieldType, (Document) value);
        }

        return new Result<>(cast(value, fieldType), ValueResult.SUCCESS);

    }

    private boolean isSimpleType(Class<?> type) {
        return type.isPrimitive() ||
                type.equals(String.class) ||
                type.equals(Integer.class) ||
                type.equals(Float.class) ||
                type.equals(Double.class) ||
                type.equals(Boolean.class) ||
                type.equals(Long.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T defaultValue(Class<T> type) {
        if (!type.isPrimitive()) {
            return null;
        }

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

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T cast(Object value, Class<T> type) {
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
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            if (value instanceof Number) {
                return (T) Double.valueOf(((Number) value).doubleValue());
            }
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            if (value instanceof Number) {
                return (T) Long.valueOf(((Number) value).longValue());
            }
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            if (value instanceof Boolean) {
                return (T) value;
            }
        }

        ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to cast value of type " + value.getClass().getName() + " to " + type.getName());
        return defaultValue(type);
    }

    private <T> Result<T> buildValueObject(Class<T> type, Document doc) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();

            for (String fieldName : doc.keySet()) {
                try {
                    Field field = type.getDeclaredField(fieldName);
                    field.setAccessible(true);

                    Object value = doc.get(fieldName);
                    value = convertValueForField(value, field.getType());

                    field.set(instance, value);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to set field " + fieldName + " on " + type.getName() + ": " + e.getMessage());
                }
            }

            return new Result<>(instance, ValueResult.SUCCESS);
        } catch (Exception e) {
            return new Result<>(ValueResult.INVALID_OBJECT);
        }
    }

    private Object convertValueForField(Object value, Class<?> fieldType) {
        if (value == null) {
            return null;
        }

        if (fieldType.isAssignableFrom(value.getClass())) {
            return value;
        }

        // Handle numeric conversions
        if (value instanceof Number num) {
            if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                return num.intValue();
            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                return num.floatValue();
            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                return num.doubleValue();
            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                return num.longValue();
            }
        }

        // Handle document to complex object conversion
        if (value instanceof Document && !isSimpleType(fieldType)) {
            return buildValueObject(fieldType, (Document) value);
        }

        return value;
    }

    enum ValueResult {
        SUCCESS,
        INVALID_NAMESPACE,
        INVALID_KEY,
        INVALID_PATH,
        INVALID_FIELD,
        INVALID_OBJECT,
    }

    record Result<T>(T value, ValueResult valueResult) {

        public Result(ValueResult valueResult) {
            this(null, valueResult);
        }

    }

}

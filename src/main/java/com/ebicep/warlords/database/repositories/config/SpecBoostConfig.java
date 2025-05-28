package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.util.List;

public class SpecBoostConfig implements ConfigManager.Config {

    public Document specBoostConfig;

    @Override
    public String getName() {
        return "SPEC_BOOSTS";
    }

    @Override
    public void load(Document doc) {
        this.specBoostConfig = doc;
        SpecBoostManager.init();
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType) {
        return getValue(namespaces, key, fieldType, defaultValue(fieldType));
    }

    public <T> T getValue(List<String> namespaces, String key, Class<T> fieldType, T defaultValue) {
        if (specBoostConfig == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return defaultValue;
        }
        Result<T> result = null;
        for (String namespace : namespaces) {
            result = getValue(namespace, key, fieldType);
            if (result.valueResult() == ValueResult.SUCCESS) {
                break;
            }
        }
        if (result == null || result.value() == null) {
            String debug = " (" + String.join(",", namespaces) + ") (" + key + ")" + " (" + fieldType.getName() + ")";
            if (result != null) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage(result.valueResult() + debug);
            } else {
                ChatUtils.MessageType.CONFIG.sendErrorMessage("No Result" + debug);
            }
            return defaultValue;
        }
        return result.value();
    }

    @SuppressWarnings("unchecked")
    private <T> T defaultValue(Class<T> type) {
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

        return null;
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
    private <T> Result<T> getValue(String namespace, String key, Class<T> fieldType) {
        // Get the namespace document
        Document namespaceDoc = specBoostConfig.get(namespace, Document.class);
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

    private <T> Result<T> buildValueObject(Class<T> type, Document doc) {
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

    private float getFloatValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        } else if (value instanceof String) {
            return Float.parseFloat((String) value);
        } else {
            throw new IllegalArgumentException("Cannot convert " + value + " to float");
        }
    }

}

package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.util.chat.ChatUtils;
import org.bson.Document;

import java.util.List;

public class AbilitiesConfig implements ConfigManager.Config {

    public Document abilitiesConfig;

    @Override
    public String getName() {
        return "ABILITIES";
    }

    @Override
    public void load(Document doc) {
        this.abilitiesConfig = doc;
    }

    public <T> T getValue(String fieldName, Class<T> fieldType, Object contextInstance) {
        if (abilitiesConfig == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Config document not set");
            return defaultValue(fieldType);
        }

        Class<?> contextClass = contextInstance.getClass();
        Class<?> outerClass = contextClass.getEnclosingClass();
        String className = "AvengersStrike";//(outerClass != null ? outerClass : contextClass).getSimpleName();
        Document classDoc = abilitiesConfig.get(className, Document.class);
        if (classDoc == null) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("No document for class: " + className);
            return defaultValue(fieldType);
        }

        // Try reading directly for top-level fields (e.g., energySteal)
        if (classDoc.containsKey(fieldName)) {
            Object val = classDoc.get(fieldName);
            return cast(val, fieldType);
        }

        // Fallback: check if it's inside a value object (e.g., damageValues)
        List<Document> lists = classDoc.getList("damageValues", Document.class);
        if (lists != null) {
            for (Document entry : lists) {
                if (entry.containsKey(fieldName)) {
                    Document valueData = entry.get(fieldName, Document.class);
                    return buildValueObject(fieldType, valueData);
                }
            }
        }
        ChatUtils.MessageType.CONFIG.sendErrorMessage("Field '" + fieldName + "' not found in config for " + className);
        return defaultValue(fieldType);
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultValue(Class<T> clazz) {
        try {
            switch (clazz.getSimpleName()) {
                case "int", "Integer" -> {
                    return (T) Integer.valueOf(0);
                }
                case "float", "Float" -> {
                    return (T) Float.valueOf(0f);
                }
                case "double", "Double" -> {
                    return (T) Double.valueOf(0d);
                }
                case "long", "Long" -> {
                    return (T) Long.valueOf(0L);
                }
                case "boolean", "Boolean" -> {
                    return (T) Boolean.FALSE;
                }
                case "RangedValue" -> {
                    return clazz.getConstructor(float.class, float.class)
                                .newInstance(0f, 0f);
                }
                case "RangedValueCritable" -> {
                    return clazz.getConstructor(float.class, float.class, float.class, float.class)
                                .newInstance(0f, 0f, 0f, 0f);
                }
                case "SetValue" -> {
                    return clazz.getConstructor(float.class)
                                .newInstance(0f);
                }
            }
        } catch (Exception e) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Failed to create default for: " + clazz.getSimpleName());
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }

        return null;
    }

    private static <T> T cast(Object val, Class<T> targetType) {
        if (val == null) {
            return null;
        }
        if (targetType == int.class || targetType == Integer.class) {
            return targetType.cast(((Number) val).intValue());
        }
        if (targetType == float.class || targetType == Float.class) {
            return targetType.cast(((Number) val).floatValue());
        }
        if (targetType == double.class || targetType == Double.class) {
            return targetType.cast(((Number) val).doubleValue());
        }
        if (targetType == long.class || targetType == Long.class) {
            return targetType.cast(((Number) val).longValue());
        }
        return targetType.cast(val);
    }

    private static <T> T buildValueObject(Class<T> clazz, Document data) {
        try {
            switch (clazz.getSimpleName()) {
                case "RangedValue" -> {
                    return clazz.getConstructor(float.class, float.class)
                                .newInstance(
                                        getOrDefault(data, "min"),
                                        getOrDefault(data, "max")
                                );
                }
                case "RangedValueCritable" -> {
                    return clazz.getConstructor(float.class, float.class, float.class, float.class)
                                .newInstance(
                                        getOrDefault(data, "min"),
                                        getOrDefault(data, "max"),
                                        getOrDefault(data, "critChance"),
                                        getOrDefault(data, "critMultiplier")
                                );
                }
                case "SetValue" -> {
                    return clazz.getConstructor(float.class)
                                .newInstance(getOrDefault(data, "value"));
                }
            }
        } catch (Exception e) {
            ChatUtils.MessageType.CONFIG.sendErrorMessage("Error constructing value object: " + clazz.getSimpleName());
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
            return defaultValue(clazz);
        }

        ChatUtils.MessageType.CONFIG.sendErrorMessage("Unsupported value object type: " + clazz.getName());
        return defaultValue(clazz);
    }

    private static float getOrDefault(Document doc, String key) {
        return doc != null && doc.containsKey(key) ? Float.parseFloat(doc.get(key) + "") : 0f;
    }

}

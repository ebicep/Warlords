package com.ebicep.warlords.database.repositories.config;

import com.ebicep.warlords.abilities.internal.AbilityDescriptionBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface ConfigBased {

    default <T> T getValue(String fieldName, Class<T> clazz) {
        return getConfig().getValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + "." + fieldName, clazz);
    }

    ConfigManager.Config getConfig();

    List<String> getConfigNamespaces();

    default String getPrefix() {
        return "";
    }

    String getConfigFieldName();

    default <T> T getValue(String fieldName, Class<T> clazz, boolean optionalField) {
        return getConfig().getValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + "." + fieldName, clazz, optionalField);
    }

    default <T> List<T> getListValue(String fieldName, Class<T> clazz) {
        return getConfig().getListValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + "." + fieldName, clazz);
    }

    default <T> List<T> getListValue(String fieldName, Class<T> clazz, boolean optionalField) {
        return getConfig().getListValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + "." + fieldName, clazz, optionalField);
    }

    default <T> Map<String, T> getMapValue(String fieldName, Class<T> clazz) {
        return getConfig().getMapValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + "." + fieldName, clazz);
    }

    interface ConfigDescription extends ConfigBased {

        default List<Component> getDescriptionLore() {
            return WordWrap.wrap(getDescription(), getMaxDescriptionWidth());
        }

        default TextComponent getDescription() {
            return getTextDescription();
        }

        default int getMaxDescriptionWidth() {
            return 150;
        }

        default TextComponent getTextDescription() {
            try {
                Queue<Object> variables = new LinkedList<>(getVariables());
                String descriptionFormat = getConfig().getValue(getConfigNamespaces(), getPrefix() + getConfigFieldName() + ".description", String.class);
                AbilityDescriptionBuilder abilityDescriptionBuilder = AbilityDescriptionBuilder.create("", NamedTextColor.GRAY);
                String leadingSign = "";
                for (int i = 0; i < descriptionFormat.length(); i++) {
                    int nextCustomIndex = descriptionFormat.indexOf("{{");
                    if (nextCustomIndex == -1) {
                        abilityDescriptionBuilder.text(descriptionFormat);
                        break;
                    }
                    if (nextCustomIndex != 0) {
                        String text = descriptionFormat.substring(0, nextCustomIndex);
                        if (text.endsWith("+") || text.endsWith("-")) {
                            leadingSign = text.substring(text.length() - 1);
                            text = text.substring(0, text.length() - 1);
                        }
                        if (!text.isEmpty()) {
                            abilityDescriptionBuilder.text(text);
                        }
                        descriptionFormat = descriptionFormat.substring(nextCustomIndex);
                    } else {
                        int endIndex = descriptionFormat.indexOf("}}");
                        String customValue = descriptionFormat.substring(2, endIndex);
                        int prefixIndex = customValue.indexOf(";");
                        String prefix;
                        if (prefixIndex == -1) {
                            prefix = "";
                        } else {
                            prefix = customValue.substring(prefixIndex + 1);
                            customValue = customValue.substring(0, prefixIndex);
                        }
                        if (customValue.contains(":")) {
                            String type = customValue.substring(0, customValue.indexOf(":"));
                            String value = customValue.substring(customValue.indexOf(":") + 1);
                            // {{type:value;prefix}}
                            abilityDescriptionBuilder.autoFormat(type, prefix, value.isEmpty() ? variables.poll() : value, leadingSign);
                        } else {
                            abilityDescriptionBuilder.autoFormat(customValue, prefix, variables.poll(), leadingSign);
                        }
                        leadingSign = "";
                        descriptionFormat = descriptionFormat.substring(endIndex + 2);
                    }
                    i--;
                }
                return abilityDescriptionBuilder.build();
            } catch (Exception e) {
                ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
                return Component.text("ERROR", NamedTextColor.RED);
            }
        }

        List<Object> getVariables();

    }

}

package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.metadata.MetadataStoreBase;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@CommandAlias("memory")
@CommandPermission("group.administrator")
public class MemoryCommand extends BaseCommand {

    @Subcommand("metadata")
    @Description("Get metadata values - print only size")
    public void metaData(CommandIssuer issuer, MetadataType metadataType, MessageType messageType, @Optional String pluginFilter) {
        CraftServer server = (CraftServer) Bukkit.getServer();
        MetadataStoreBase<?> metadataStoreBase = switch (metadataType) {
            case ENTITY -> server.getEntityMetadata();
            case WORLD -> server.getWorldMetadata();
            case PLAYER -> server.getPlayerMetadata();
        };
        ChatChannels.sendDebugMessage(issuer, Component.text("MetadataMap for " + metadataType + " - " + messageType, NamedTextColor.AQUA));
        // use reflection to access metadata key values
        try {
            Field metadataMapField = MetadataStoreBase.class.getDeclaredField("metadataMap");
            metadataMapField.setAccessible(true);
            Map<String, Map<Plugin, MetadataValue>> metadataMap = (Map<String, Map<Plugin, MetadataValue>>) metadataMapField.get(metadataStoreBase);
            ChatChannels.sendDebugMessage(issuer, Component.text("MetadataMap size: " + metadataMap.size(), NamedTextColor.AQUA));
            if (pluginFilter != null) {
                Map<String, MetadataValue> filteredMap = new HashMap<>();
                metadataMap.forEach((s, pluginMetadataValueMap) -> {
                    pluginMetadataValueMap.forEach((plugin, metadataValue) -> {
                        if (plugin.getName().contains(pluginFilter)) {
                            filteredMap.put(s, metadataValue);
                        }
                    });
                });
                ChatChannels.sendDebugMessage(issuer, Component.text("Filtered MetadataMap size: " + filteredMap.size(), NamedTextColor.AQUA));
                if (messageType == MessageType.SIZE_ONLY) {
                    ChatChannels.sendDebugMessage(issuer, ComponentBuilder
                            .create("Filtered MetadataMap for " + pluginFilter, NamedTextColor.GREEN)
                            .text("->", NamedTextColor.GRAY)
                            .text(filteredMap.size(), NamedTextColor.GREEN)
                            .build()
                    );
                } else {
                    filteredMap.forEach((s, metadataValue) -> {
                        ChatChannels.sendDebugMessage(issuer, ComponentBuilder
                                .create(s, NamedTextColor.YELLOW)
                                .text("->", NamedTextColor.GRAY)
                                .text(metadataValue.asString(), NamedTextColor.GREEN)
                                .build()
                        );
                    });
                }
            } else {
                metadataMap.forEach((s, pluginMetadataValueMap) -> {
                    if (messageType == MessageType.SIZE_ONLY) {
                        ChatChannels.sendDebugMessage(issuer, ComponentBuilder
                                .create(s, NamedTextColor.YELLOW)
                                .text("->", NamedTextColor.GRAY)
                                .text(pluginMetadataValueMap.size(), NamedTextColor.GREEN)
                                .build()
                        );
                    } else {
                        pluginMetadataValueMap.forEach((plugin, metadataValue) -> {
                            ChatChannels.sendDebugMessage(issuer, ComponentBuilder
                                    .create(s, NamedTextColor.YELLOW)
                                    .text("->", NamedTextColor.GRAY)
                                    .text(plugin.getName(), NamedTextColor.AQUA)
                                    .text("->", NamedTextColor.GRAY)
                                    .text(metadataValue.asString(), NamedTextColor.GREEN)
                                    .build()
                            );
                        });
                    }
                });
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public enum MetadataType {
        ENTITY,
        WORLD,
        PLAYER
    }

    public enum MessageType {
        ALL,
        SIZE_ONLY
    }


}

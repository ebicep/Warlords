package com.ebicep.warlords.permissions;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import static com.ebicep.warlords.util.chat.ChatChannels.CHAT_ARROW;
import static com.ebicep.warlords.util.chat.ChatChannels.DEBUG;

public enum Permissions {

    ADMIN("ADMIN", NamedTextColor.DARK_AQUA, "group.administrator"),
    COORDINATOR("HGS", NamedTextColor.GOLD, "group.coordinator"),
    CONTENT_CREATOR("CT", NamedTextColor.LIGHT_PURPLE, "group.contentcreator"),
    GAME_STARTER("GS", NamedTextColor.YELLOW, "group.gamestarter"),
    GAME_TESTER("P", NamedTextColor.GREEN, "group.patreon"),
    DEFAULT("", NamedTextColor.AQUA, "group.default"),

    ;

    public static final Permissions[] VALUES = values();

    public static Component getPrefixWithColor(Player player) {
        for (Permissions value : VALUES) {
            if (player.hasPermission(value.permission)) {
                return value == DEFAULT ?
                       Component.empty().color(NamedTextColor.AQUA) :
                       Component.text("[" + value.prefix + "] ").color(value.prefixColor);
            }
        }
        return Component.empty().color(NamedTextColor.AQUA);
    }

    public static boolean isAdmin(Player player) {
        return player.hasPermission(ADMIN.permission);
    }

    public static boolean isCoordinator(Player player) {
        return player.hasPermission(COORDINATOR.permission);
    }

    public static boolean isContentCreator(Player player) {
        return player.hasPermission(CONTENT_CREATOR.permission);
    }

    public static boolean isGameStarter(Player player) {
        return player.hasPermission(GAME_STARTER.permission);
    }

    public static boolean isGameTester(Player player) {
        return player.hasPermission(GAME_TESTER.permission);
    }

    public static boolean isDefault(Player player) {
        return player.hasPermission(DEFAULT.permission);
    }

    public static void sendMessageToDebug(Player player, String message) {
        if (player.hasPermission("warlords.database.messagefeed")) {
            player.sendMessage(message);
        }
    }

    public static void sendMessageToDebug(WarlordsEntity player, String message) {
        if (player.getEntity().hasPermission("warlords.database.messagefeed")) {
            player.sendMessage(DEBUG.getColoredName() + CHAT_ARROW + message);
        }
    }

    public final String prefix;
    public final NamedTextColor prefixColor;
    public final String permission;

    Permissions(String prefix, NamedTextColor prefixColor, String permission) {
        this.prefix = prefix;
        this.prefixColor = prefixColor;
        this.permission = permission;
    }

}


package com.ebicep.warlords.permissions;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;

public class PermissionHandler {

    // Permission check whether a player has a specific rank.

    public static boolean isAdmin(Player player) {
        return player.hasPermission("group.administrator");
    }

    public static boolean isCoordinator(Player player) {
        return player.hasPermission("group.coordinator");
    }

    public static boolean isContentCreator(Player player) {
        return player.hasPermission("group.contentcreator");
    }

    public static boolean isGameStarter(Player player) {
        return player.hasPermission("group.gamestarter");
    }

    public static boolean isGameTester(Player player) {
        return player.hasPermission("group.gametester");
    }

    public static boolean isDefault(Player player) {
        return player.hasPermission("group.default");
    }

    public static void sendMessageToDebug(Player player, String message) {
        if (player.hasPermission("warlords.database.messagefeed")) {
            player.sendMessage(message);
        }
    }

    public static void sendMessageToDebug(WarlordsEntity player, String message) {
        if (player.getEntity().hasPermission("warlords.database.messagefeed")) {
            player.sendMessage(message);
        }
    }

}


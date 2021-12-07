package com.ebicep.warlords.perimissions;

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

    public static boolean isGamestarter(Player player) {
        return player.hasPermission("group.gamestarter");
    }

    public static boolean isCompszn(Player player) {
        return player.hasPermission("group.compszn");
    }

    public static boolean isDefault(Player player) {
        return player.hasPermission("group.default");
    }
}


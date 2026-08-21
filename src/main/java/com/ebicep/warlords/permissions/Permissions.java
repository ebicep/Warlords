package com.ebicep.warlords.permissions;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.honorifics.HonorificManager;
import com.ebicep.warlords.honorifics.HonorificProfile;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.event.user.UserDataRecalculateEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatChannels.CHAT_ARROW;
import static com.ebicep.warlords.util.chat.ChatChannels.DEBUG;

public enum Permissions {

    ADMIN("ADMIN", NamedTextColor.DARK_AQUA, "group.administrator"),
    COORDINATOR("HGS", NamedTextColor.BLUE, "group.coordinator"),
    CONTENT_CREATOR("CT", NamedTextColor.LIGHT_PURPLE, "group.contentcreator"),
    BUILDER("BUILDER", NamedTextColor.DARK_GREEN, "group.builder"),
    GAME_STARTER("GS", NamedTextColor.YELLOW, "group.gamestarter"),
    SUPPORTER("S", NamedTextColor.GOLD, "group.supporter"),
    STREAMER("", NamedTextColor.AQUA, "group.streamer"),
    DEFAULT("", NamedTextColor.AQUA, "group.default");

    public static final String LEGACY_PATREON_PERMISSION = "group.patreon";
    public static final String TEBEX_SUPPORTER_PERMISSION_PREFIX = "warlords.supporter.";
    public static final Permissions[] VALUES = values();

    public static void listenToNewPatreons(UserDataRecalculateEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                User user = event.getUser();
                List<String> permissions = user.getNodes().stream().map(Node::getKey).collect(Collectors.toList());
                permissions.remove("group.default");
                boolean supporter = isSupporterPermissionList(permissions);
                if (supporter && !permissions.contains(LEGACY_PATREON_PERMISSION)) {
                    permissions.add(LEGACY_PATREON_PERMISSION);
                }
                for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                    DatabasePlayer databasePlayer = DatabaseManager.getPlayer(user.getUniqueId(), activeCollection);
                    databasePlayer.setPermissions(permissions);
                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer, activeCollection);
                }
                validateHonorificSupporterAccess(user.getUniqueId(), supporter);
                CustomScoreboard.updateLobbyPlayerNames();
            }
        }.runTaskLater(Warlords.getInstance(), 60);
    }

    public static Component getPrefixWithColor(Player player, boolean includeName) {
        validateHonorificSupporterAccess(player.getUniqueId(), isSupporter(player));
        return createPlayerPrefix(getPermission(player), player.getUniqueId(), includeName ? player.getName() : "", includeName);
    }

    public static Component getPrefixWithColor(UUID uuid, boolean includeName) {
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(uuid);
        validateHonorificSupporterAccess(uuid, isSupporter(databasePlayer));
        return createPlayerPrefix(getPermission(databasePlayer), uuid, includeName ? databasePlayer.getName() : "", includeName);
    }

    private static Component createPlayerPrefix(Permissions permission, UUID uuid, String name, boolean includeName) {
        Component component = Component.empty().color(permission.prefixColor)
                .append(HonorificManager.getHonorificComponent(uuid));
        if (permission != DEFAULT && !permission.prefix.isEmpty()) {
            component = component.append(Component.text("[" + permission.prefix + "] ", permission.prefixColor));
        }
        if (includeName) {
            component = component.append(Component.text(name, permission.prefixColor));
        }
        return component;
    }

    private static Permissions getPermission(Player player) {
        for (Permissions value : VALUES) {
            if (value == SUPPORTER ? isSupporter(player) : player.hasPermission(value.permission)) {
                return value;
            }
        }
        return DEFAULT;
    }

    private static Permissions getPermission(DatabasePlayer databasePlayer) {
        for (Permissions value : VALUES) {
            if (value == SUPPORTER ? isSupporter(databasePlayer) : databasePlayer.getPermissions().contains(value.permission)) {
                return value;
            }
        }
        return DEFAULT;
    }

    public static NamedTextColor getColor(Player player) {
        return getPermission(player).prefixColor;
    }

    public static NamedTextColor getColor(DatabasePlayer databasePlayer) {
        return getPermission(databasePlayer).prefixColor;
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

    public static boolean isStreamer(Player player) {
        return player.hasPermission(STREAMER.permission);
    }

    public static boolean isGameStarter(Player player) {
        return player.hasPermission(GAME_STARTER.permission);
    }

    public static boolean isSupporter(Player player) {
        if (player.hasPermission(SUPPORTER.permission)
                || player.hasPermission(LEGACY_PATREON_PERMISSION)
                || player.hasPermission(CONTENT_CREATOR.permission)) {
            return true;
        }
        return player.getEffectivePermissions().stream()
                .anyMatch(permission -> permission.getValue()
                        && permission.getPermission().startsWith(TEBEX_SUPPORTER_PERMISSION_PREFIX));
    }

    public static boolean isSupporter(DatabasePlayer databasePlayer) {
        return isSupporterPermissionList(databasePlayer.getPermissions());
    }

    @Deprecated
    public static boolean isPatreon(Player player) {
        return isSupporter(player);
    }

    @Deprecated
    public static boolean isPatreon(DatabasePlayer databasePlayer) {
        return isSupporter(databasePlayer);
    }

    public static boolean isDefault(Player player) {
        return player.hasPermission(DEFAULT.permission);
    }

    private static boolean isSupporterPermissionList(List<String> permissions) {
        return permissions.contains(SUPPORTER.permission)
                || permissions.contains(LEGACY_PATREON_PERMISSION)
                || permissions.contains(CONTENT_CREATOR.permission)
                || permissions.stream().anyMatch(permission -> permission.startsWith(TEBEX_SUPPORTER_PERMISSION_PREFIX));
    }

    private static void validateHonorificSupporterAccess(UUID uuid, boolean hasSupporter) {
        HonorificProfile profile = HonorificManager.getProfile(uuid);
        if (!profile.validatePatreonAccess(hasSupporter)) {
            return;
        }
        HonorificManager.saveAsync(uuid);
        HonorificManager.refreshDisplays(Bukkit.getPlayer(uuid));
    }

    public static void sendMessageToDebug(WarlordsEntity player, Component message) {
        if (player.getEntity().hasPermission("warlords.database.messagefeed")) {
            player.getEntity().sendMessage(DEBUG.getColoredName().append(CHAT_ARROW).append(message));
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

    public boolean contains(Player player) {
        return this == SUPPORTER ? isSupporter(player) : player.hasPermission(permission);
    }
}

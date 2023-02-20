package com.ebicep.warlords.permissions;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.luckperms.api.event.user.track.UserTrackEvent;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionHandler {
    public static void listenToNewPatreons(UserTrackEvent event) {
        User user = event.getUser();
        List<String> permissions = user.getNodes().stream()
                                       .filter(NodeType.PERMISSION::matches)
                                       .map(NodeType.PERMISSION::cast)
                                       .map(PermissionNode::getPermission)
                                       .collect(Collectors.toList());
        DatabaseManager.getPlayer(user.getUniqueId(), databasePlayer -> checkForPatreon(databasePlayer, permissions.contains("group.patreon")));
    }

    public static void checkForPatreon(DatabasePlayer databasePlayer, boolean isPatreon) {
        if (isPatreon) {
            if (!databasePlayer.getPveStats().isCurrentlyPatreon()) {
                databasePlayer.getPveStats().setCurrentlyPatreon(true);
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
        } else {
            if (databasePlayer.getPveStats().isCurrentlyPatreon()) {
                databasePlayer.getPveStats().setCurrentlyPatreon(false);
                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
            }
        }
    }

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

    public static void sendMessageToDebug(WarlordsPlayer player, String message) {
        if (player.getEntity().hasPermission("warlords.database.messagefeed")) {
            player.sendMessage(message);
        }
    }

}


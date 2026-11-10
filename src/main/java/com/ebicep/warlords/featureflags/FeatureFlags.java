package com.ebicep.warlords.featureflags;

import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bson.Document;

import javax.annotation.Nullable;

public final class FeatureFlags {

    public static final String BYPASS_PERMISSION = "warlords.admin.bypassfeatureflags";

    public static final String HONORIFICS = "honorifics";
    public static final String HONORIFICS_DISPLAY = "honorificsDisplay";

    private FeatureFlags() {
    }

    public static boolean isGamemodeEnabled(GameMode mode, @Nullable CommandSender sender) {
        if (mode == GameMode.LOBBY) {
            return true;
        }
        return getFlag("gamemodes", mode.name(), sender);
    }

    public static boolean isAddonEnabled(GameAddon addon, @Nullable CommandSender sender) {
        return getFlag("addons", addon.name(), sender);
    }

    public static boolean isNpcEnabled(String npcName, @Nullable CommandSender sender) {
        return getFlag("npcs", npcName, sender);
    }

    public static boolean isFeatureEnabled(String featureKey, @Nullable CommandSender sender) {
        return getFlag("features", featureKey, sender);
    }

    public static void sendDisabledMessage(Player player) {
        player.sendMessage(Component.text("This feature is currently unavailable.", NamedTextColor.RED));
    }

    public static boolean getFlag(String section, String key, @Nullable CommandSender sender) {
        if (sender != null && sender.hasPermission(BYPASS_PERMISSION)) {
            return true;
        }
        Document data = ConfigManager.FEATURE_FLAGS_CONFIG.getConfigDocument();
        if (data == null) {
            return true;
        }
        Document sectionDoc = data.get(section, Document.class);
        if (sectionDoc == null) {
            return true;
        }
        return sectionDoc.getBoolean(key, true);
    }

}

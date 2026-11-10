package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.featureflags.FeatureFlags;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bson.Document;

import java.util.Map;
import java.util.TreeMap;

@CommandAlias("featureflags")
@CommandPermission("group.adminisrator")
public class FeatureFlagsCommand extends BaseCommand {

    @Subcommand("list")
    public void list(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer, Component.text("Feature flags (missing keys default to enabled):", NamedTextColor.GOLD));

        ChatChannels.sendDebugMessage(issuer, Component.text("Gamemodes:", NamedTextColor.YELLOW));
        for (GameMode gameMode : GameMode.values()) {
            boolean enabled = FeatureFlags.isGamemodeEnabled(gameMode, null);
            sendFlagLine(issuer, gameMode.name(), enabled);
        }

        ChatChannels.sendDebugMessage(issuer, Component.text("Addons:", NamedTextColor.YELLOW));
        for (GameAddon addon : GameAddon.values()) {
            boolean enabled = FeatureFlags.isAddonEnabled(addon, null);
            sendFlagLine(issuer, addon.name(), enabled);
        }

        Document data = ConfigManager.FEATURE_FLAGS_CONFIG.getConfigDocument();
        Document npcSection = data != null ? data.get("npcs", Document.class) : null;
        ChatChannels.sendDebugMessage(issuer, Component.text("NPCs (config + spawned):", NamedTextColor.YELLOW));
        Map<String, Boolean> npcFlags = new TreeMap<>();
        if (npcSection != null) {
            for (String key : npcSection.keySet()) {
                npcFlags.put(key, FeatureFlags.isNpcEnabled(key, null));
            }
        }
        for (NPC npc : NPCManager.NPC_REGISTRY) {
            npcFlags.putIfAbsent(npc.getName(), FeatureFlags.isNpcEnabled(npc.getName(), null));
        }
        if (npcFlags.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("  (none configured or spawned)", NamedTextColor.GRAY));
        } else {
            npcFlags.forEach((name, enabled) -> sendFlagLine(issuer, name, enabled));
        }

        Document featureSection = data != null ? data.get("features", Document.class) : null;
        ChatChannels.sendDebugMessage(issuer, Component.text("Features:", NamedTextColor.YELLOW));
        if (featureSection == null || featureSection.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("  (none configured)", NamedTextColor.GRAY));
        } else {
            for (String key : new TreeMap<String, Object>(featureSection).keySet()) {
                sendFlagLine(issuer, key, FeatureFlags.isFeatureEnabled(key, null));
            }
        }
    }

    private static void sendFlagLine(CommandIssuer issuer, String name, boolean enabled) {
        ChatChannels.sendDebugMessage(issuer, Component.text("  " + name + ": ", NamedTextColor.GRAY)
                .append(Component.text(enabled ? "enabled" : "disabled", enabled ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }

}

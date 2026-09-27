package com.ebicep.warlords.tablist;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListSkins;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Shared helpers for building player mirror rows on the custom tab list.
 */
public final class TabListPlayers {

    private TabListPlayers() {
    }

    @Nonnull
    public static List<TabEntrySource> toPlayerSources(@Nonnull List<Map.Entry<Player, Team>> players) {
        List<TabEntrySource> sources = new ArrayList<>(players.size());
        for (Map.Entry<Player, Team> entry : players) {
            TabEntry tabEntry = resolvePlayerEntry(entry.getKey(), entry.getValue());
            sources.add(viewer -> tabEntry);
        }
        return sources;
    }

    @Nonnull
    public static TabEntry resolvePlayerEntry(@Nonnull Player player, @Nonnull Team team) {
        UUID playerId = player.getUniqueId();
        Component displayName = Component.text(player.getName(), team.getTeamColor());
        TabEntry entry = TabEntry.of(displayName, player.getPing()).withLogicalId(playerId.toString());
        String[] skin = TabListSkins.textureAndSignature(player);
        if (skin != null) {
            entry = entry.withSkin(skin[0], skin[1]);
        }
        return entry;
    }
}

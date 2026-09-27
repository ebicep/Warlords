package com.ebicep.warlords.tablist;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListSkins;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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

    private static final TextComponent WHITE_FLAG = Component.text("⚑", NamedTextColor.WHITE);

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

    /**
     * Same format as {@code PlayingStateScoreboardUpdater.flushTabName}:
     * {@code [CLS] Name [LvXX]} with optional trailing white flag.
     */
    @Nonnull
    public static Component gameDisplayName(@Nonnull WarlordsPlayer warlordsPlayer) {
        TextComponent.Builder builder = Component
                .text()
                .append(classComponent(warlordsPlayer))
                .append(Component.text(warlordsPlayer.getName(), warlordsPlayer.getTeam().getTeamColor()))
                .append(levelComponent(warlordsPlayer.getUuid(), warlordsPlayer));
        if (warlordsPlayer.getCarriedFlag() != null) {
            builder.append(WHITE_FLAG);
        }
        return builder.build();
    }

    @Nonnull
    public static Component classComponent(@Nonnull WarlordsEntity entity) {
        return entity.getSpec().getClassNameShortWithBrackets(entity.getSpecClass().specType.getTextColor());
    }

    @Nonnull
    public static Component levelComponent(@Nonnull UUID uuid, @Nonnull WarlordsEntity entity) {
        return ExperienceManager.getLevelStringBracket(ExperienceManager.getLevelForSpec(uuid, entity.getSpecClass()));
    }

    @Nonnull
    public static TabEntry resolvePlayerEntry(@Nonnull Player player, @Nonnull Team team) {
        UUID playerId = player.getUniqueId();
        Component displayName;
        WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
        if (warlordsEntity instanceof WarlordsPlayer warlordsPlayer) {
            displayName = gameDisplayName(warlordsPlayer);
        } else {
            displayName = Component.text(player.getName(), team.getTeamColor());
        }
        TabEntry entry = TabEntry.of(displayName, player.getPing()).withLogicalId(playerId.toString());
        String[] skin = TabListSkins.textureAndSignature(player);
        if (skin != null) {
            entry = entry.withSkin(skin[0], skin[1]);
        }
        return entry;
    }
}

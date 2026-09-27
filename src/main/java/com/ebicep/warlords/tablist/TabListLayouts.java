package com.ebicep.warlords.tablist;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.util.bukkit.packets.tablist.TabListSkins;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Prefab {@link TabListLayout}s for common game modes.
 */
public final class TabListLayouts {

    private static final int TEAM_MAX_ENTRIES = 40;
    private static final int TEAM_MIN_COLUMNS = 1;
    private static final int TEAM_MAX_COLUMNS = 2;

    private TabListLayouts() {
    }

    /**
     * Blue then Red column bands (PvP). Each band reserves at least one padded column.
     */
    @Nonnull
    public static TabListLayout pvpBlueRed() {
        return new TeamBandsLayout(List.of(
                new TeamBand(Team.BLUE, "blue", 0),
                new TeamBand(Team.RED, "red", 1)
        ), false);
    }

    /**
     * Blue-only column band (PvE). All non-spectators appear under blue; band min-padded to one column.
     */
    @Nonnull
    public static TabListLayout pveBlue() {
        return new TeamBandsLayout(List.of(
                new TeamBand(Team.BLUE, "blue", 0)
        ), true);
    }

    private record TeamBand(@Nonnull Team team, @Nonnull String groupName, int priority) {
    }

    /**
     * One column band per team: header subgroup + players subgroup.
     * When {@code collapseAllToFirstBand}, every non-spectator is listed in the first band
     * (PvE); otherwise players are filtered by team assignment (PvP).
     */
    private static final class TeamBandsLayout implements TabListLayout {

        private final List<TeamBand> bands;
        private final boolean collapseAllToFirstBand;
        private final Map<String, TabSubgroup> playerSubgroups = new LinkedHashMap<>();
        private final Map<String, Set<UUID>> lastIds = new LinkedHashMap<>();
        private boolean applied;
        private boolean forcePlayerSync;

        private TeamBandsLayout(@Nonnull List<TeamBand> bands, boolean collapseAllToFirstBand) {
            this.bands = List.copyOf(bands);
            this.collapseAllToFirstBand = collapseAllToFirstBand;
        }

        @Override
        public void forcePlayerSync() {
            forcePlayerSync = true;
        }

        @Override
        public void apply(@Nonnull GameTabListManager manager, @Nonnull Game game) {
            if (applied) {
                return;
            }
            for (TeamBand band : bands) {
                TabGroup group = manager.group(
                        band.groupName(),
                        band.priority(),
                        TEAM_MIN_COLUMNS,
                        TEAM_MAX_COLUMNS,
                        TEAM_MAX_ENTRIES
                );
                group.addSubgroup(headerSubgroup(band.team(), 0));
                TabSubgroup players = new TabSubgroup(1);
                group.addSubgroup(players);
                playerSubgroups.put(band.groupName(), players);
                lastIds.put(band.groupName(), Set.of());
            }
            applied = true;
        }

        @Override
        public void sync(@Nonnull GameTabListManager manager, @Nonnull Game game) {
            apply(manager, game);
            if (collapseAllToFirstBand) {
                syncCollapsed(game);
                return;
            }
            syncByTeam(game);
        }

        private void syncCollapsed(@Nonnull Game game) {
            TeamBand band = bands.getFirst();
            List<Map.Entry<Player, Team>> players = game.onlinePlayersWithoutSpectators()
                    .sorted(Comparator.comparing(e -> e.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
                    .toList();
            Set<UUID> current = players.stream()
                    .map(e -> e.getKey().getUniqueId())
                    .collect(Collectors.toCollection(HashSet::new));
            if (!forcePlayerSync && current.equals(lastIds.get(band.groupName()))) {
                return;
            }
            forcePlayerSync = false;
            lastIds.put(band.groupName(), Set.copyOf(current));
            playerSubgroups.get(band.groupName()).setSources(TabListPlayers.toPlayerSources(players));
        }

        private void syncByTeam(@Nonnull Game game) {
            boolean anyChange = forcePlayerSync;
            Map<String, List<Map.Entry<Player, Team>>> byBand = new LinkedHashMap<>();
            Map<String, Set<UUID>> nextIds = new LinkedHashMap<>();
            for (TeamBand band : bands) {
                List<Map.Entry<Player, Team>> players = game.onlinePlayersWithoutSpectators()
                        .filter(e -> e.getValue() == band.team())
                        .sorted(Comparator.comparing(e -> e.getKey().getName(), String.CASE_INSENSITIVE_ORDER))
                        .toList();
                Set<UUID> ids = players.stream()
                        .map(e -> e.getKey().getUniqueId())
                        .collect(Collectors.toCollection(HashSet::new));
                byBand.put(band.groupName(), players);
                nextIds.put(band.groupName(), ids);
                if (!ids.equals(lastIds.get(band.groupName()))) {
                    anyChange = true;
                }
            }
            if (!anyChange) {
                return;
            }
            forcePlayerSync = false;
            for (TeamBand band : bands) {
                lastIds.put(band.groupName(), Set.copyOf(nextIds.get(band.groupName())));
                playerSubgroups.get(band.groupName())
                        .setSources(TabListPlayers.toPlayerSources(byBand.get(band.groupName())));
            }
        }

        @Nonnull
        private static TabSubgroup headerSubgroup(@Nonnull Team team, int priority) {
            TabSubgroup header = new TabSubgroup(priority);
            String title = team.prefix() + " TEAM";
            TabEntry entry = TabEntry.of(Component.text(title, team.getTeamColor(), TextDecoration.BOLD));
            String[] skin = TabListSkins.textureAndSignature(team.getColors());
            entry = entry.withSkin(skin[0], skin[1]);
            TabEntry headerEntry = entry;
            header.add(viewer -> headerEntry);
            return header;
        }
    }
}

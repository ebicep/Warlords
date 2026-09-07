package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.entity.Display;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class EventLeaderboard {

    private final Long eventTime;
    private final String title;
    private final Location location;
    private final HashMap<Long, List<DatabasePlayer>> sortedTimedPlayers = new HashMap<>();
    private final HashMap<Long, List<List<Hologram>>> sortedTimedHolograms = new HashMap<>();
    private final BiFunction<DatabasePlayer, Long, Number> valueFunction;
    private final BiFunction<DatabasePlayer, Long, String> stringFunction;
    private final Comparator<DatabasePlayer> comparator;
    private boolean hidden = false;
    private Predicate<DatabasePlayer> filter;


    public EventLeaderboard(
            String title,
            Location location,
            BiFunction<DatabasePlayer, Long, Number> valueFunction,
            BiFunction<DatabasePlayer, Long, String> stringFunction,
            Long eventTime,
            boolean hidden
    ) {
        this(eventTime, title, location, valueFunction, stringFunction);
        this.hidden = hidden;
    }

    public EventLeaderboard(
            Long eventTime,
            String title,
            Location location,
            BiFunction<DatabasePlayer, Long, Number> valueFunction,
            BiFunction<DatabasePlayer, Long, String> stringFunction
    ) {
        this(eventTime, title, location, valueFunction, stringFunction, null);
    }

    public EventLeaderboard(
            Long eventTime,
            String title,
            Location location,
            BiFunction<DatabasePlayer, Long, Number> valueFunction,
            BiFunction<DatabasePlayer, Long, String> stringFunction,
            Predicate<DatabasePlayer> filter
    ) {
        this.eventTime = eventTime;
        this.title = title;
        this.location = location;
        this.valueFunction = valueFunction;
        this.stringFunction = stringFunction;
        this.comparator = (o1, o2) -> {
            //if (o1.getUuid().equals(o2.getUuid())) return 0;
            BigDecimal value1 = new BigDecimal(valueFunction.apply(o1, eventTime).toString());
            BigDecimal value2 = new BigDecimal(valueFunction.apply(o2, eventTime).toString());
            return value2.compareTo(value1);
        };
        this.filter = filter;
    }

    public void resetHolograms(Predicate<DatabasePlayer> externalFilter, String categoryName, String subTitle) {
        if (!StatsLeaderboardManager.enabled) {
            return;
        }
        resetSortedPlayers(externalFilter);
        createLeaderboard(categoryName, subTitle);
    }

    public void resetSortedPlayers(Predicate<DatabasePlayer> externalFilter) {
        List<DatabasePlayer> databasePlayers = new ArrayList<>(DatabaseManager.CACHED_PLAYERS.get(PlayersCollections.LIFETIME).values());
        if (externalFilter != null) {
            databasePlayers.removeIf(externalFilter);
        }
        if (filter != null) {
            databasePlayers.removeIf(filter);
        }
        databasePlayers.sort(comparator);
        sortedTimedPlayers.put(eventTime, databasePlayers);
    }

    private void createLeaderboard(String eventType, String subTitle) {
        if (!StatsLeaderboardManager.enabled || hidden) {
            return;
        }
        //creating leaderboard
        List<Hologram> holograms = new ArrayList<>();
        List<HologramDataText> pageHologramData = new ArrayList<>();
        for (int i = 0; i < StatsLeaderboard.MAX_PAGES; i++) {
            pageHologramData.add(getPageHologramData(i, subTitle));
        }
        Hologram board = new Hologram.Builder(
                "event" + subTitle,
                location,
                p -> {
                    PlayerLeaderboardInfo playerInfo = StatsLeaderboardManager.getPlayerInfo(p);
                    int page = playerInfo.getPage();
                    return pageHologramData.get(Math.min(pageHologramData.size() - 1, page));
                }
        ).setVisibility(VisibilityType.ALL).build();
        List<DatabasePlayer> databasePlayers = getSortedPlayers();
        Hologram playerPosition = new Hologram.Builder(
                "eventPlayerPosition" + subTitle,
                location.clone().add(0, -0.5, 0),
                p -> {
                    for (int i = 0; i < databasePlayers.size(); i++) {
                        DatabasePlayer databasePlayer = databasePlayers.get(i);
                        if (!databasePlayer.getUuid().equals(p.getUniqueId())) {
                            continue;
                        }
                        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
                        Component guildTag = Component.empty();
                        if (guildPlayerPair != null) {
                            GuildTag tag = guildPlayerPair.getA().getTag();
                            if (tag != null) {
                                guildTag = tag.getTag(false);
                            }
                        }
                        return new HologramDataText.Builder<>(ComponentBuilder
                                .create((i + 1) + ". ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                .text(databasePlayer.getName(), Permissions.getColor(databasePlayer))
                                .space()
                                .append(guildTag)
                                .text(" - ", NamedTextColor.GRAY)
                                .text(stringFunction.apply(databasePlayer, eventTime))
                                .build()
                        )
                                .setBillboard(Display.Billboard.VERTICAL)
                                .build();
                    }
                    return StatsLeaderboard.LOADING;
                }
        ).setVisibility(VisibilityType.ALL).build();
        getSortedHolograms().stream().flatMap(Collection::stream).forEach(Hologram::deleteHologram);
        getSortedHolograms().clear();
        getSortedHolograms().add(holograms);
        holograms.add(board);
        holograms.add(playerPosition);
        HologramManager.addHologram(board);
        HologramManager.addHologram(playerPosition);
    }

    public HologramDataText getPageHologramData(int page, String subTitle) {
        List<DatabasePlayer> databasePlayers = getSortedPlayers();

        ComponentBuilder componentBuilder = ComponentBuilder
                .create(title, NamedTextColor.AQUA, TextDecoration.BOLD)
                .newLine(subTitle, NamedTextColor.GRAY);

        for (int i = page * StatsLeaderboard.PLAYERS_PER_PAGE; i < (page + 1) * StatsLeaderboard.PLAYERS_PER_PAGE && i < databasePlayers.size(); i++) {
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
            Component guildTag = Component.empty();
            if (guildPlayerPair != null) {
                GuildTag tag = guildPlayerPair.getA().getTag();
                if (tag != null) {
                    guildTag = tag.getTag(false);
                }
            }
            componentBuilder.newLine((i + 1) + ". ", NamedTextColor.YELLOW)
                            .text(databasePlayer.getName(), Permissions.getColor(databasePlayer))
                            .space()
                            .append(guildTag)
                            .text(" - ", NamedTextColor.GRAY)
                            .text(stringFunction.apply(databasePlayer, eventTime));
        }
        return new HologramDataText.Builder<>(componentBuilder.build())
                .setBillboard(Display.Billboard.VERTICAL)
                .build();
    }

    public List<List<Hologram>> getSortedHolograms() {
        return sortedTimedHolograms.computeIfAbsent(eventTime, k -> new ArrayList<>());
    }

    public List<DatabasePlayer> getSortedPlayers() {
        return sortedTimedPlayers.computeIfAbsent(eventTime, k -> new ArrayList<>());
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, location);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventLeaderboard that = (EventLeaderboard) o;
        return title.equals(that.title) && location.equals(that.location);
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public HashMap<Long, List<List<Hologram>>> getSortedTimedHolograms() {
        return sortedTimedHolograms;
    }

    public BiFunction<DatabasePlayer, Long, String> getStringFunction() {
        return stringFunction;
    }

    public Long getEventTime() {
        return eventTime;
    }

    public boolean isHidden() {
        return hidden;
    }
}

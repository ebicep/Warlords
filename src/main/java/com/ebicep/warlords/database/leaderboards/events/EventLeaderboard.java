package com.ebicep.warlords.database.leaderboards.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.java.Pair;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Location;

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
    private Predicate<DatabasePlayer> filter = null;


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
    }

    public void resetHolograms(Predicate<DatabasePlayer> externalFilter, String categoryName, String subTitle) {
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
        //skip hologram creation for hidden leaderboards
        if (hidden) {
            return;
        }
        //creating leaderboard
        List<Hologram> holograms = new ArrayList<>();
        for (int i = 0; i < StatsLeaderboard.MAX_PAGES; i++) {
            holograms.add(createHologram(i, subTitle));
        }
        getSortedHolograms().stream().flatMap(Collection::stream).forEach(Hologram::delete);
        getSortedHolograms().clear();
        getSortedHolograms().add(holograms);
    }

    public Hologram createHologram(int page, String subTitle) {
        List<DatabasePlayer> databasePlayers = getSortedPlayers();

        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
        HologramLines hologramLines = hologram.getLines();
        hologramLines.appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + title);
        hologramLines.appendText(ChatColor.GRAY + subTitle);
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
            hologramLines.appendText(LegacyComponentSerializer.legacySection().serialize(
                    Component.text((i + 1) + ". ", NamedTextColor.YELLOW)
                             .append(Component.text(databasePlayer.getName(), Permissions.getColor(databasePlayer)))
                             .append(guildTag)
                             .append(Component.text(" - ", NamedTextColor.GRAY))
                             .append(Component.text(stringFunction.apply(databasePlayer, eventTime)))
            ));
        }
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        return hologram;
    }

    public List<List<Hologram>> getSortedHolograms() {
        return sortedTimedHolograms.computeIfAbsent(eventTime, k -> new ArrayList<>());
    }

    public List<DatabasePlayer> getSortedPlayers() {
        return sortedTimedPlayers.computeIfAbsent(eventTime, k -> new ArrayList<>());
    }
    /*
    public void resetHolograms(Long eventTime, Predicate<DatabasePlayer> externalFilter, String categoryName, String subTitle) {
        resetSortedPlayers(eventTime, externalFilter);
        createLeaderboard(eventTime, categoryName, subTitle);
    }

    public void resetSortedPlayers(Long eventTime, Predicate<DatabasePlayer> externalFilter) {
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

    private void createLeaderboard(Long eventTime, String categoryName, String subTitle) {
        //skip hologram creation for hidden leaderboards
        if (hidden) {
            return;
        }
        //creating leaderboard
        List<Hologram> holograms = new ArrayList<>();
        for (int i = 0; i < StatsLeaderboard.MAX_PAGES; i++) {
            holograms.add(createHologram(eventTime, i, "TODO"));
        }
        getSortedHolograms(eventTime).stream().flatMap(Collection::stream).forEach(Hologram::delete);
        getSortedHolograms(eventTime).clear();
        getSortedHolograms(eventTime).add(holograms);
    }

    public Hologram createHologram(Long eventTime, int page, String subTitle) {
        List<DatabasePlayer> databasePlayers = getSortedPlayers(eventTime);

        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
        HologramLines hologramLines = hologram.getLines();
        hologramLines.appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "TODO - " + title);
        hologramLines.appendText(ChatColor.GRAY + subTitle);
        for (int i = page * StatsLeaderboard.PLAYERS_PER_PAGE; i < (page + 1) * StatsLeaderboard.PLAYERS_PER_PAGE && i < databasePlayers.size(); i++) {
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            hologramLines.appendText(ChatColor.YELLOW.toString() + (i + 1) + ". " +
                    ChatColor.AQUA + databasePlayer.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + stringFunction.apply(databasePlayer, time));
        }
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        return hologram;
    }

    public List<List<Hologram>> getSortedHolograms(Long eventTime) {
        return sortedTimedHolograms.get(eventTime);
    }

    public List<DatabasePlayer> getSortedPlayers(Long eventTime) {
        return sortedTimedPlayers.get(eventTime);
    }

     */

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

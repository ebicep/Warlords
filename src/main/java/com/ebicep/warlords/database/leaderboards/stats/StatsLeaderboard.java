package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.util.chat.ChatUtils;
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

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class StatsLeaderboard {

    public static final int MAX_PAGES = 3;
    public static final int PLAYERS_PER_PAGE = 10;
    private final String title;
    private final Location location;
    private final HashMap<PlayersCollections, List<DatabasePlayer>> sortedTimedPlayers = new HashMap<>();
    private final HashMap<PlayersCollections, List<List<Hologram>>> sortedTimedHolograms = new HashMap<>() {{
        for (PlayersCollections value : PlayersCollections.VALUES) {
            put(value, new ArrayList<>());
        }
    }};
    private final Function<DatabasePlayer, Number> valueFunction;
    private final Function<DatabasePlayer, String> stringFunction;
    private final Comparator<DatabasePlayer> comparator;
    private boolean hidden = false;
    private Predicate<DatabasePlayer> filter = null;

    public StatsLeaderboard(
            String title,
            Location location,
            Function<DatabasePlayer, Number> valueFunction,
            Function<DatabasePlayer, String> stringFunction,
            boolean hidden
    ) {
        this(title, location, valueFunction, stringFunction);
        this.hidden = hidden;
    }

    public StatsLeaderboard(String title, Location location, Function<DatabasePlayer, Number> valueFunction, Function<DatabasePlayer, String> stringFunction) {
        this.title = title;
        this.location = location;
        this.valueFunction = valueFunction;
        this.stringFunction = stringFunction;
        this.comparator = (o1, o2) -> {
            //if (o1.getUuid().equals(o2.getUuid())) return 0;
            BigDecimal value1 = new BigDecimal(valueFunction.apply(o1).toString());
            BigDecimal value2 = new BigDecimal(valueFunction.apply(o2).toString());
            return value2.compareTo(value1);
        };
        for (PlayersCollections value : PlayersCollections.VALUES) {
            sortedTimedPlayers.put(value, new ArrayList<>());
        }
    }

    public StatsLeaderboard(
            String title, Location location, Function<DatabasePlayer, Number> valueFunction, Function<DatabasePlayer, String> stringFunction,
            Predicate<DatabasePlayer> filter
    ) {
        this(title, location, valueFunction, stringFunction);
        this.filter = filter;
    }

    public StatsLeaderboard(
            String title, Location location, Function<DatabasePlayer, Number> valueFunction, Function<DatabasePlayer, String> stringFunction,
            Predicate<DatabasePlayer> filter, boolean hidden
    ) {
        this(title, location, valueFunction, stringFunction);
        this.filter = filter;
        this.hidden = hidden;
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
        StatsLeaderboard that = (StatsLeaderboard) o;
        return Objects.equals(title, that.title) && Objects.equals(location, that.location);
    }

    public void resetHolograms(PlayersCollections collection, Predicate<DatabasePlayer> externalFilter, String categoryName, String subTitle) {
        Warlords.newChain()
                .async(() -> resetSortedPlayers(collection, externalFilter))
                .sync(() -> createLeaderboard(collection, categoryName, subTitle))
                .execute();
    }

    public void resetSortedPlayers(PlayersCollections collections, Predicate<DatabasePlayer> externalFilter) {
        List<DatabasePlayer> databasePlayers = new ArrayList<>(DatabaseManager.CACHED_PLAYERS.get(collections).values());
        if (externalFilter != null) {
            databasePlayers.removeIf(externalFilter);
        }
        if (filter != null) {
            databasePlayers.removeIf(filter);
        }
        databasePlayers.sort(comparator);
        sortedTimedPlayers.put(collections, databasePlayers);
    }

    private void createLeaderboard(PlayersCollections collection, String categoryName, String subTitle) {
        if (location.getWorld() == null) {
            ChatUtils.MessageType.LEADERBOARDS.sendErrorMessage("Leaderboard " + title + " has invalid location - " + location);
            return;
        }
        //skip hologram creation for hidden leaderboards
        if (hidden) {
            return;
        }
        //creating leaderboard
        List<Hologram> holograms = new ArrayList<>();
        for (int i = 0; i < StatsLeaderboard.MAX_PAGES; i++) {
            holograms.add(createHologram(collection, i, subTitle + " - " + (categoryName.isEmpty() ? "" : categoryName + " - ") + collection.name));
        }
        getSortedHolograms(collection).stream().flatMap(Collection::stream).forEach(Hologram::delete);
        getSortedHolograms(collection).clear();
        getSortedHolograms(collection).add(holograms);
    }

    public Hologram createHologram(PlayersCollections collection, int page, String subTitle) {
        List<DatabasePlayer> databasePlayers = getSortedPlayers(collection);

        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
        HologramLines hologramLines = hologram.getLines();
        hologramLines.appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + collection.name + " " + title);
        hologramLines.appendText(ChatColor.GRAY + subTitle);
        for (int i = page * PLAYERS_PER_PAGE; i < (page + 1) * PLAYERS_PER_PAGE && i < databasePlayers.size(); i++) {
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
                             .append(Component.space())
                             .append(guildTag)
                             .append(Component.text(" - ", NamedTextColor.GRAY))
                             .append(Component.text(stringFunction.apply(databasePlayer)))
            ));
        }
        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);

        return hologram;
    }

    public List<List<Hologram>> getSortedHolograms(PlayersCollections collections) {
        return sortedTimedHolograms.get(collections);
    }

    public List<DatabasePlayer> getSortedPlayers(PlayersCollections collections) {
        return sortedTimedPlayers.get(collections);
    }

    public <T extends Number> T[] getTopThreeValues() {
        //current top value to compare to
        List<DatabasePlayer> sortedWeekly = sortedTimedPlayers.get(PlayersCollections.WEEKLY);
        Number topValue = valueFunction.apply(sortedWeekly.get(0));

        Class<T> clazz = (Class<T>) topValue.getClass();
        //ouput array of type clazz
        T[] output = (T[]) Array.newInstance(clazz, 3);
        //first top number is current top
        output[0] = (T) topValue;

        List<Number> topThree = new ArrayList<>();
        int counter = 0;
        //looping to get the next top two numbers
        //filtering out all players with 3 or less games from leaderboards if the top player has 10 or more (no one game olivers)
        boolean filter = sortedWeekly.get(0).getPlays() >= 10;
        List<DatabasePlayer> databasePlayers;
        if (filter) {
            databasePlayers = sortedWeekly.stream().filter(databasePlayer -> databasePlayer.getPlays() > 3).toList();
        } else {
            databasePlayers = new ArrayList<>(sortedWeekly);
        }

        for (DatabasePlayer databasePlayer : databasePlayers) {
            //must have more than 3 plays to get awarded
            if (databasePlayer.getPlays() <= 3) {
                continue;
            }

            Number currentTopValue = valueFunction.apply(databasePlayer);
            if (counter < 2) {
                if (compare(topValue, currentTopValue) > 0) {
                    topThree.add(currentTopValue);
                    topValue = currentTopValue;
                    counter++;
                }
            } else {
                break;
            }
        }

        //adding last two top numbers
        for (int i = 0; i < topThree.size(); i++) {
            output[i + 1] = (T) topThree.get(i);
        }

        return output;
    }

    public static int compare(Number a, Number b) {
        return new BigDecimal(a.toString()).compareTo(new BigDecimal(b.toString()));
    }

    public String[] getTopThreePlayerNames(Number[] numbers, Function<DatabasePlayer, String> function) {
        String[] topThreePlayers = new String[3];
        Arrays.fill(topThreePlayers, "");

        //matching top value with players
        for (int i = 0; i < numbers.length; i++) {
            Number topValue = numbers[i];
            for (DatabasePlayer databasePlayer : sortedTimedPlayers.get(PlayersCollections.WEEKLY)) {
                if (Objects.equals(valueFunction.apply(databasePlayer), topValue)) {
                    topThreePlayers[i] = topThreePlayers[i] + function.apply(databasePlayer) + ",";
                }
            }
            if (i == 2) {
                break;
            }
        }

        //removing end comma
        for (int i = 0; i < topThreePlayers.length; i++) {
            if (topThreePlayers[i].length() > 0) {
                topThreePlayers[i] = topThreePlayers[i].substring(0, topThreePlayers[i].length() - 1);
            }
        }
        return topThreePlayers;
    }

    public String getTitle() {
        return title;
    }

    public Location getLocation() {
        return location;
    }

    public HashMap<PlayersCollections, List<List<Hologram>>> getSortedTimedHolograms() {
        return sortedTimedHolograms;
    }

    public Function<DatabasePlayer, String> getStringFunction() {
        return stringFunction;
    }

    public boolean isHidden() {
        return hidden;
    }
}

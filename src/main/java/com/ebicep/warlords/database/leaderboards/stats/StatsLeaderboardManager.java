package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.StatsLeaderboardCTF;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.StatsLeaderboardGeneral;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.StatsLeaderboardPvE;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.chat.ChatUtils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StatsLeaderboardManager {

    public static final World MAIN_LOBBY = Bukkit.getWorld("MainLobby");
    public static final Location SPAWN_POINT = Bukkit.getWorlds().get(0).getSpawnLocation().clone();
    public static final HashMap<UUID, PlayerLeaderboardInfo> PLAYER_LEADERBOARD_INFOS = new HashMap<>();
    public static final HashMap<GameType, AbstractStatsLeaderboardGameType<?>> STATS_LEADERBOARDS = new HashMap<>() {{
        for (GameType value : GameType.values()) {
            put(value, value.createStatsLeaderboardGameType.get());
        }
    }};
    public static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> CACHED_PLAYERS = new ConcurrentHashMap<>();

    public static boolean enabled = true;
    public static boolean loaded = false;

    public static void validatePlayerHolograms(Player player) {
        if (!PLAYER_LEADERBOARD_INFOS.containsKey(player.getUniqueId()) || PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId()) == null) {
            PLAYER_LEADERBOARD_INFOS.put(player.getUniqueId(), new PlayerLeaderboardInfo());
        }
    }

    public static void addHologramLeaderboards(boolean init) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (!DatabaseManager.enabled) return;
        if (DatabaseManager.playerService == null || DatabaseManager.gameService == null) return;

        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> statsLeaderboardGameType.addLeaderboards());

        if (enabled) {
            loaded = false;
            ChatUtils.MessageTypes.LEADERBOARDS.sendMessage("Adding Holograms");

            //caching all sorted players
            AtomicInteger loadedBoards = new AtomicInteger();
            long startTime = System.nanoTime();
            for (PlayersCollections value : PlayersCollections.VALUES) {
                Warlords.newChain()
                        .asyncFirst(() -> DatabaseManager.playerService.findAll(value))
                        .syncLast((databasePlayers) -> {
                            CACHED_PLAYERS.computeIfAbsent(value, v -> new HashSet<>()).addAll(databasePlayers);
                            reloadLeaderboardsFromCache(value, true);
                            loadedBoards.getAndIncrement();
                        }).execute();
            }

            //depending on what player has selected, set visibility
            new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    if (loadedBoards.get() == 6) {
                        loaded = true;

                        ChatUtils.MessageTypes.LEADERBOARDS.sendMessage("Loaded leaderboards in " + ((System.nanoTime() - startTime) / 1000000) + "ms");

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            setLeaderboardHologramVisibility(player);
                            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                        });
                        ChatUtils.MessageTypes.LEADERBOARDS.sendMessage("Set Leaderboard Hologram Visibility");

                        if (init) {
                            DatabaseTiming.checkLeaderboardResets();
                            NPCManager.createGameJoinNPCs();
                        }
                        this.cancel();
                    } else if (counter++ > 2 * 300) { //holograms should all load within 5 minutes or ???
                        ChatUtils.MessageTypes.LEADERBOARDS.sendErrorMessage("Holograms did not load within 5 minutes");
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 20, 10);
        }
    }

    public static void reloadLeaderboardsFromCache(PlayersCollections playersCollections, boolean init) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (!DatabaseManager.enabled) return;
        if (DatabaseManager.playerService == null || DatabaseManager.gameService == null) return;

        Set<DatabasePlayer> databasePlayers = CACHED_PLAYERS.get(playersCollections);

        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> statsLeaderboardGameType.resetLeaderboards(playersCollections, databasePlayers));

        ChatUtils.MessageTypes.LEADERBOARDS.sendMessage("Loaded " + playersCollections.name + " leaderboards");

        if (playersCollections == PlayersCollections.SEASON_5 && init) {
            SRCalculator.databasePlayerCache = databasePlayers;
            SRCalculator.recalculateSR();
        }
    }

    public static StatsLeaderboardCategory<?> getLeaderboardCategoryFromPlayer(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return null;
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
        Category selectedCategory = playerLeaderboardInfo.getStatsCategory();

        return selectedCategory.getCategory.apply(STATS_LEADERBOARDS.get(selectedGameType));
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        int page = playerLeaderboardInfo.getPage();
        StatsLeaderboardCategory<?> statsLeaderboardCategory = getLeaderboardCategoryFromPlayer(player);

        getAllLeaderboardCategories().forEach(category -> {
            category.getAllHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        });
        if (statsLeaderboardCategory != null) {
            statsLeaderboardCategory.getCollectionHologramPaged(selectedTime).forEach(holograms -> holograms.get(page).getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        if (Warlords.playerScoreboards.containsKey(player.getUniqueId())) {
            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
        }

        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    public static void setLeaderboardHologramVisibilityToAll() {
        Bukkit.getOnlinePlayers().forEach(StatsLeaderboardManager::setLeaderboardHologramVisibility);
    }

    private static <T> void createLeaderboardSwitcherHologram(Player player, Location location, T selected, T before, T after, Function<T, String> getName, Consumer<T> set) {
        Hologram switchHologram = createSwitchHologram(location);
        if (selected != before) {
            switchHologram.getLines().appendText(ChatColor.GRAY + getName.apply(before)).setClickListener(p -> {
                set.accept(before);
                setLeaderboardHologramVisibility(player);
            });
        }
        switchHologram.getLines().appendText(ChatColor.GREEN + getName.apply(selected));
        if (selected != after) {
            switchHologram.getLines().appendText(ChatColor.GRAY + getName.apply(after)).setClickListener(p -> {
                set.accept(after);
                setLeaderboardHologramVisibility(player);
            });
        }

        switchHologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        switchHologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
    }

    private static void createLeaderboardSwitcherHologram(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        removePlayerSpecificHolograms(player);
        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());

        //GAME TYPE
        GameType selectedType = playerLeaderboardInfo.getStatsGameType();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_GAME_TYPE_SWITCH_LOCATION,
                selectedType,
                GameType.getBefore(selectedType),
                GameType.getAfter(selectedType),
                gameType -> gameType.name,
                playerLeaderboardInfo::setStatsGameType);
        //CATEGORY
        Category selectedCategory = playerLeaderboardInfo.getStatsCategory();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_CATEGORY_SWITCH_LOCATION,
                selectedCategory,
                selectedType == GameType.PVE ? selectedCategory : Category.getBefore(selectedCategory),
                selectedType == GameType.PVE ? selectedCategory : Category.getAfter(selectedCategory),
                category -> category.name,
                playerLeaderboardInfo::setStatsCategory);
        //TIME
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_TIME_SWITCH_LOCATION,
                selectedTime,
                PlayersCollections.getBeforeCollection(selectedTime),
                PlayersCollections.getAfterCollection(selectedTime),
                playersCollections -> playersCollections.name,
                playerLeaderboardInfo::setStatsTime);
        //PAGE
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_PAGE_SWITCH_LOCATION,
                playerLeaderboardInfo.getPage(),
                playerLeaderboardInfo.getPageBefore(),
                playerLeaderboardInfo.getPageAfter(),
                playerLeaderboardInfo::getPageRange,
                playerLeaderboardInfo::setPage);
    }

    private static Hologram createSwitchHologram(Location location) {
        Hologram switchHologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
        switchHologram.getLines().appendText(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
        switchHologram.getLines().appendText("");

        return switchHologram;
    }

    public static void addPlayerPositionLeaderboards(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (enabled) {
            //leaderboards
            removeLeaderboardPlayerSpecificHolograms(player);
            validatePlayerHolograms(player);
            PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
            PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
            StatsLeaderboardCategory<?> statsLeaderboardCategory = getLeaderboardCategoryFromPlayer(player);
            if (statsLeaderboardCategory == null) return;
            List<Hologram> playerHolograms = new ArrayList<>();
            for (StatsLeaderboard statsLeaderboard : statsLeaderboardCategory.getStatsLeaderboards()) {
                if (statsLeaderboard.isHidden()) continue;
                Location location = statsLeaderboard.getLocation().clone().add(0, -3.5, 0);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);

                List<DatabasePlayer> databasePlayers = statsLeaderboard.getSortedPlayers(selectedTime);
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId())) {
                        hologram.getLines().appendText(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.DARK_AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + statsLeaderboard.getStringFunction().apply(databasePlayer));
                        break;
                    }
                }

                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

                playerHolograms.add(hologram);
            }

            playerLeaderboardInfo.setHolograms(playerHolograms);
        }
    }

    public static void removePlayerSpecificHolograms(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        removeLeaderboardPlayerSpecificHolograms(player);
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) &&
                        (h.getPosition().toLocation().equals(DatabaseGameBase.GAME_SWITCH_LOCATION) ||
                                h.getPosition().toLocation().equals(StatsLeaderboardLocations.STATS_GAME_TYPE_SWITCH_LOCATION) ||
                                h.getPosition().toLocation().equals(StatsLeaderboardLocations.STATS_CATEGORY_SWITCH_LOCATION) ||
                                h.getPosition().toLocation().equals(StatsLeaderboardLocations.STATS_TIME_SWITCH_LOCATION) ||
                                h.getPosition().toLocation().equals(StatsLeaderboardLocations.STATS_PAGE_SWITCH_LOCATION)))
                .forEach(Hologram::delete);
    }

    private static void removeLeaderboardPlayerSpecificHolograms(Player player) {
        validatePlayerHolograms(player);
        PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId()).clearHolograms();
    }

    public static List<StatsLeaderboardCategory<?>> getAllLeaderboardCategories() {
        return STATS_LEADERBOARDS.values().stream()
                .flatMap(statsLeaderboardCategory -> statsLeaderboardCategory.getCategories().stream())
                .collect(Collectors.toList());
    }

    public enum GameType {
        ALL("All Modes", "", StatsLeaderboardGeneral::new),
        CTF("Capture The Flag", "CTF", StatsLeaderboardCTF::new),
        PVE("Player vs Environment", "PvE", StatsLeaderboardPvE::new);

        public final String name;
        public final String shortName;
        public final Supplier<AbstractStatsLeaderboardGameType<?>> createStatsLeaderboardGameType;

        GameType(String name, String shortName, Supplier<AbstractStatsLeaderboardGameType<?>> createStatsLeaderboardGameType) {
            this.name = name;
            this.shortName = shortName;
            this.createStatsLeaderboardGameType = createStatsLeaderboardGameType;
        }

        public static GameType getAfter(GameType gameType) {
            switch (gameType) {
                case ALL:
                    return CTF;
                case CTF:
                    return PVE;
                case PVE:
                    return ALL;
            }
            return ALL;
        }

        public static GameType getBefore(GameType gameType) {
            switch (gameType) {
                case ALL:
                    return PVE;
                case CTF:
                    return ALL;
                case PVE:
                    return CTF;
            }
            return ALL;
        }
    }

    public enum Category {
        ALL("All Queues", "", AbstractStatsLeaderboardGameType::getGeneral) {

        },
        COMPS("Competitive Queue", "Comps", AbstractStatsLeaderboardGameType::getComps) {

        },
        PUBS("Public Queue", "Pubs", AbstractStatsLeaderboardGameType::getPubs) {

        };

        public final String name;
        public final String shortName;
        public final Function<AbstractStatsLeaderboardGameType<?>, StatsLeaderboardCategory<?>> getCategory;

        Category(String name, String shortName, Function<AbstractStatsLeaderboardGameType<?>, StatsLeaderboardCategory<?>> getCategory) {
            this.name = name;
            this.shortName = shortName;
            this.getCategory = getCategory;
        }

        public static Category getAfter(Category category) {
            switch (category) {
                case ALL:
                    return COMPS;
                case COMPS:
                    return PUBS;
                case PUBS:
                    return ALL;
            }
            return ALL;
        }

        public static Category getBefore(Category category) {
            switch (category) {
                case ALL:
                    return PUBS;
                case COMPS:
                    return ALL;
                case PUBS:
                    return COMPS;
            }
            return ALL;
        }
    }


}
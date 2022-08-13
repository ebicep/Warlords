package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.stats.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.LeaderboardCTF;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.LeaderboardGeneral;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.sr.SRCalculator;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeaderboardManager {

    public static final World MAIN_LOBBY = Bukkit.getWorld("MainLobby");
    public static final Location SPAWN_POINT = Bukkit.getWorlds().get(0).getSpawnLocation().clone();
    public static final HashMap<UUID, PlayerLeaderboardInfo> PLAYER_LEADERBOARD_INFOS = new HashMap<>();
    public static final LeaderboardGeneral LEADERBOARD_GENERAL = new LeaderboardGeneral();
    public static final LeaderboardCTF LEADERBOARD_CTF = new LeaderboardCTF();
    public static final ConcurrentHashMap<PlayersCollections, Set<DatabasePlayer>> CACHED_PLAYERS = new ConcurrentHashMap<>();

    public static boolean enabled = true;
    public static boolean loaded = false;

    public static void validatePlayerHolograms(Player player) {
        if (!PLAYER_LEADERBOARD_INFOS.containsKey(player.getUniqueId()) || PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId()) == null) {
            PLAYER_LEADERBOARD_INFOS.put(player.getUniqueId(), new PlayerLeaderboardInfo());
        }
    }

    public static void putLeaderboards() {
        LEADERBOARD_GENERAL.addLeaderboards();
        LEADERBOARD_CTF.addLeaderboards();
    }

    public static void addHologramLeaderboards(boolean init) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (!DatabaseManager.enabled) return;
        if (DatabaseManager.playerService == null || DatabaseManager.gameService == null) return;

        putLeaderboards();

        if (enabled) {
            loaded = false;
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Leaderboards] Adding Holograms");

            //caching all sorted players
            AtomicInteger loadedBoards = new AtomicInteger();
            long startTime = System.nanoTime();
            for (PlayersCollections value : PlayersCollections.values()) {
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

                        long endTime = System.nanoTime();
                        long timeToLoad = (endTime - startTime) / 1000000;
                        System.out.println("[Leaderboards] Time it took for LB to load (ms): " + timeToLoad);

                        Bukkit.getOnlinePlayers().forEach(player -> {
                            setLeaderboardHologramVisibility(player);
                            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                        });
                        System.out.println("[Leaderboards] Set Leaderboard Hologram Visibility");

                        if (init) {
                            DatabaseTiming.checkTimings();
                            NPCManager.createGameJoinNPCs();
                        }
                        this.cancel();
                    } else if (counter++ > 2 * 300) { //holograms should all load within 5 minutes or ???
                        System.out.println("[Leaderboards] Holograms did not load within 5 minutes");
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

        getAllLeaderboardCategories().forEach(leaderboardCategory -> leaderboardCategory.deleteHolograms(playersCollections));
        Set<DatabasePlayer> databasePlayers = CACHED_PLAYERS.get(playersCollections);

        LEADERBOARD_GENERAL.resetLeaderboards(playersCollections, databasePlayers);
        LEADERBOARD_CTF.resetLeaderboards(playersCollections, databasePlayers);

        System.out.println("[Leaderboards] Loaded " + playersCollections.name + " leaderboards");

        if (playersCollections == PlayersCollections.SEASON_5 && init) {
            SRCalculator.databasePlayerCache = databasePlayers;
            SRCalculator.recalculateSR();
        }
    }

    public static LeaderboardCategory<?> getLeaderboardCategoryFromPlayer(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return null;
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
        Category selectedCategory = playerLeaderboardInfo.getStatsCategory();

        if (selectedGameType == GameType.ALL) {
            if (selectedCategory == Category.ALL) {
                return LEADERBOARD_GENERAL.getGeneral();
            } else if (selectedCategory == Category.COMPS) {
                return LEADERBOARD_GENERAL.getComps();
            } else {
                return LEADERBOARD_GENERAL.getPubs();
            }
        } else {
            if (selectedCategory == Category.ALL) {
                return LEADERBOARD_CTF.getGeneral();
            } else if (selectedCategory == Category.COMPS) {
                return LEADERBOARD_CTF.getComps();
            } else {
                return LEADERBOARD_CTF.getPubs();
            }
        }
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        int page = playerLeaderboardInfo.getPage();
        LeaderboardCategory<?> leaderboardCategory = getLeaderboardCategoryFromPlayer(player);

        getAllLeaderboardCategories().forEach(category -> {
            category.getAllHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        });
        if (leaderboardCategory != null) {
            leaderboardCategory.getCollectionHologramPaged(selectedTime).forEach(holograms -> holograms.get(page).getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        if (Warlords.playerScoreboards.containsKey(player.getUniqueId())) {
            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
        }

        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    public static void setLeaderboardHologramVisibilityToAll() {
        Bukkit.getOnlinePlayers().forEach(LeaderboardManager::setLeaderboardHologramVisibility);
    }

    private static void createLeaderboardSwitcherHologram(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        removePlayerSpecificHolograms(player);
        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());

        //GAME TYPE
        Hologram gameTypeSwitch = createSwitchHologram(StatsLeaderboardLocations.STATS_GAME_TYPE_SWITCH_LOCATION);
        //GameType beforeType = GameType.getBefore(playerLeaderboardGameType.get(uuid));
        GameType selectedType = playerLeaderboardInfo.getStatsGameType();
//        GameType afterType = GameType.getAfter(playerLeaderboardGameType.get(uuid));
//        gameTypeSwitch.getLines().appendText((selectedType == beforeType ? ChatColor.GREEN : ChatColor.GRAY) + beforeType.name).setClickListener(p -> {
//            playerLeaderboardGameType.put(uuid, beforeType);
//            setLeaderboardHologramVisibility(p.getPlayer());
//        });
        if (selectedType == GameType.ALL) {
            gameTypeSwitch.getLines().appendText(ChatColor.GREEN + GameType.ALL.name);
            gameTypeSwitch.getLines().appendText(ChatColor.GRAY + GameType.CTF.name).setClickListener(p -> {
                playerLeaderboardInfo.setStatsGameType(GameType.CTF);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        } else {
            gameTypeSwitch.getLines().appendText(ChatColor.GRAY + GameType.ALL.name).setClickListener(p -> {
                playerLeaderboardInfo.setStatsGameType(GameType.ALL);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
            gameTypeSwitch.getLines().appendText(ChatColor.GREEN + GameType.CTF.name);
        }
        gameTypeSwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        gameTypeSwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        //CATEGORY
        Hologram categorySwitch = createSwitchHologram(StatsLeaderboardLocations.STATS_CATEGORY_SWITCH_LOCATION);
        Category selectedCategory = playerLeaderboardInfo.getStatsCategory();
        Category beforeCategory = Category.getBefore(selectedCategory);
        Category afterCategory = Category.getAfter(selectedCategory);
        if (selectedCategory != beforeCategory) {
            categorySwitch.getLines().appendText(ChatColor.GRAY + beforeCategory.name).setClickListener(p -> {
                playerLeaderboardInfo.setStatsCategory(beforeCategory);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        }
        categorySwitch.getLines().appendText(ChatColor.GREEN + selectedCategory.name);
        if (selectedCategory != afterCategory) {
            categorySwitch.getLines().appendText(ChatColor.GRAY + afterCategory.name).setClickListener(p -> {
                playerLeaderboardInfo.setStatsCategory(afterCategory);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        }
        categorySwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        categorySwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        //TIME
        Hologram timeSwitch = createSwitchHologram(StatsLeaderboardLocations.STATS_TIME_SWITCH_LOCATION);
        PlayersCollections selectedCollection = playerLeaderboardInfo.getStatsTime();
        PlayersCollections beforeCollection = PlayersCollections.getBeforeCollection(selectedCollection);
        PlayersCollections afterCollection = PlayersCollections.getAfterCollection(selectedCollection);
        timeSwitch.getLines().appendText(ChatColor.GRAY + beforeCollection.name).setClickListener(p -> {
            playerLeaderboardInfo.setStatsTime(beforeCollection);
            setLeaderboardHologramVisibility(p.getPlayer());
        });

        timeSwitch.getLines().appendText(ChatColor.GREEN + selectedCollection.name);
        timeSwitch.getLines().appendText(ChatColor.GRAY + afterCollection.name).setClickListener(p -> {
            playerLeaderboardInfo.setStatsTime(afterCollection);
            setLeaderboardHologramVisibility(p.getPlayer());
        });
        timeSwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        timeSwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        //PAGE
        Hologram pageSelector = createSwitchHologram(StatsLeaderboardLocations.STATS_PAGE_SWITCH_LOCATION);
        int selectedPage = playerLeaderboardInfo.getPage();
        int beforePage = playerLeaderboardInfo.getPageBefore();
        int afterPage = playerLeaderboardInfo.getPageAfter();
        pageSelector.getLines().appendText(ChatColor.GRAY + playerLeaderboardInfo.getPageRange(beforePage)).setClickListener(p -> {
            playerLeaderboardInfo.setPage(beforePage);
            setLeaderboardHologramVisibility(p.getPlayer());
        });
        pageSelector.getLines().appendText(ChatColor.GREEN + playerLeaderboardInfo.getPageRange(selectedPage));
        pageSelector.getLines().appendText(ChatColor.GRAY + playerLeaderboardInfo.getPageRange(afterPage)).setClickListener(p -> {
            playerLeaderboardInfo.setPage(afterPage);
            setLeaderboardHologramVisibility(p.getPlayer());
        });
        pageSelector.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        pageSelector.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
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
            LeaderboardCategory<?> leaderboardCategory = getLeaderboardCategoryFromPlayer(player);
            if (leaderboardCategory == null) return;
            List<Hologram> playerHolograms = new ArrayList<>();
            for (Leaderboard leaderboard : leaderboardCategory.leaderboards) {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);

                List<DatabasePlayer> databasePlayers = new ArrayList<>(leaderboard.getSortedPlayers(selectedTime));
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId())) {
                        hologram.getLines().appendText(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.DARK_AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + leaderboard.getStringFunction().apply(databasePlayer));
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

    public static List<LeaderboardCategory<?>> getAllLeaderboardCategories() {
        return Stream.of(
                LEADERBOARD_GENERAL.getGeneral(),
                LEADERBOARD_GENERAL.getComps(),
                LEADERBOARD_GENERAL.getPubs(),
                LEADERBOARD_CTF.getGeneral(),
                LEADERBOARD_CTF.getComps(),
                LEADERBOARD_CTF.getPubs()
        ).collect(Collectors.toList());
    }

    public enum GameType {
        ALL("All Modes", ""),
        CTF("Capture The Flag", "CTF");

        public final String name;
        public final String shortName;

        GameType(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
        }

        public static GameType getAfter(GameType gameType) {
            switch (gameType) {
                case ALL:
                    return CTF;
                case CTF:
                    return ALL;
            }
            return ALL;
        }

        public static GameType getBefore(GameType gameType) {
            switch (gameType) {
                case ALL:
                    return CTF;
                case CTF:
                    return ALL;
            }
            return ALL;
        }
    }

    public enum Category {
        ALL("All Queues", "") {

        },
        COMPS("Competitive Queue", "Comps") {

        },
        PUBS("Public Queue", "Pubs") {

        };

        public final String name;
        public final String shortName;

        Category(String name, String shortName) {
            this.name = name;
            this.shortName = shortName;
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
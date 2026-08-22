package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.holograms.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.events.EventsLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.*;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.TriConsumer;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StatsLeaderboardManager {

    public static final World MAIN_LOBBY = Bukkit.getWorld("MainLobby");
    public static final Location MAIN_LOBBY_SPAWN = new Location(MAIN_LOBBY, 11.5, 81, 149.5, 0, 0);
    public static final Map<UUID, PlayerLeaderboardInfo> PLAYER_LEADERBOARD_INFOS = new ConcurrentHashMap<>();
    public static final Map<GameType, AbstractStatsLeaderboardGameType<?, ?, ?, ?>> STATS_LEADERBOARDS = new HashMap<>() {{
        for (GameType value : GameType.ACTIVE_LEADERBOARDS) {
            put(value, value.createStatsLeaderboardGameType.get());
        }
    }};

    public static final Map<PlayersCollections, Long> LAST_BOARD_RESETS = new HashMap<>();

    public static boolean enabled = true;
    public static boolean loaded = false;
    private static PlayerLeaderboardInfo leaderboardInfo;

    public static void addHologramLeaderboards(boolean init) {
        if (!Warlords.hologramsEnabled) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Not adding hologram leaderboards - holograms are disabled");
            return;
        }
        if (!DatabaseManager.enabled) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Not adding hologram leaderboards - database is disabled");
            return;
        }

        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> statsLeaderboardGameType.addLeaderboards());

        if (!enabled) {
            return;
        }
        loaded = false;
        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Adding Holograms");

        //caching all sorted players
        AtomicInteger loadedBoards = new AtomicInteger();
        long startTime = System.nanoTime();
        for (PlayersCollections value : PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS) {
            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.find(value.getQuery(), value))
                    .syncLast((databasePlayers) -> {
                        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Fetched " + databasePlayers.size() + " " + value.name + " players");
                        for (DatabasePlayer databasePlayer : databasePlayers) {
                            if (databasePlayer.getUuid() == null) {
                                ChatUtils.MessageType.LEADERBOARDS.sendErrorMessage(databasePlayer.getId() + " - " + databasePlayer.getName() + " has a null UUID");
                                continue;
                            }
                            String resolvedName = null;
                            if (databasePlayer.getName() == null) {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(databasePlayer.getUuid());
                                if (offlinePlayer.getName() != null) {
                                    resolvedName = offlinePlayer.getName();
                                }
                            }
                            DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
                            DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
                            boolean lessThan20Plays = databasePlayer.getPlays() + pveStats.getPlays() < 20;
                            EventMode eventMode = currentGameEvent == null ? null : currentGameEvent.getEvent().eventsStatsFunction.apply(pveStats.getEventStats())
                                                                                                                                   .get(currentGameEvent.getStartDateSecond());
                            boolean noCurrentEventPlays = currentGameEvent == null || eventMode != null && eventMode.getEventPlays() == 0;
                            if (value == PlayersCollections.LIFETIME && lessThan20Plays && noCurrentEventPlays) {
                                continue;
                            }
                            if (value == PlayersCollections.SEASON_12 && lessThan20Plays) {
                                continue;
                            }
                            DatabasePlayer cached = DatabaseManager.cachePlayer(value, databasePlayer);
                            if (resolvedName != null && !resolvedName.equals(cached.getName())) {
                                cached.setName(resolvedName);
                                ChatUtils.MessageType.LEADERBOARDS.sendMessage("Updated Name: " + cached.getName() + " - " + value);
                                DatabaseManager.queueUpdatePlayerAsync(cached, value);
                            }
                        }
                        resetLeaderboards(value, null);
                        loadedBoards.getAndIncrement();
                    }).execute();
        }

        //depending on what player has selected, set visibility
        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if (loadedBoards.get() >= PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS.size()) {
                    loaded = true;

                    ChatUtils.MessageType.LEADERBOARDS.sendMessage("Loaded leaderboards in " + ((System.nanoTime() - startTime) / 1000000) + "ms");

                    Bukkit.getOnlinePlayers().forEach(player -> {
                        setLeaderboardHologramVisibility(player);
                        CustomScoreboard.getPlayerScoreboard(player).giveMainLobbyScoreboard();
                    });
                    ChatUtils.MessageType.LEADERBOARDS.sendMessage("Set Leaderboard Hologram Visibility");

                    if (init) {
                        ChatUtils.MessageType.LEADERBOARDS.sendMessage("init Running");

                        DatabaseTiming.checkLeaderboardResets();
                        DatabaseGameEvent.startGameEvent();
                        SRCalculator.recalculateSR();

//                        createLeaderboardSwitcherHologram();
                    }
                    this.cancel();
                } else if (counter++ > 2 * 300) { //holograms should all load within 5 minutes or ???
                    ChatUtils.MessageType.LEADERBOARDS.sendErrorMessage("Holograms did not load within 5 minutes");
                    this.cancel();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 20, 10);
    }

    /**
     * All players in PLAYERS_TO_ADD become the new leaderboard players
     *
     * @param playersCollections The collection of players to reload
     * @param gameMode
     */
    public static void resetLeaderboards(PlayersCollections playersCollections, @Nullable GameMode gameMode) {
        if (!Warlords.hologramsEnabled) {
            return;
        }
        if (!DatabaseManager.enabled || !enabled) {
            return;
        }
        if (!PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS.contains(playersCollections)) {
            return;
        }
        // boards can only be reset every 5 minutes
        if (System.currentTimeMillis() - LAST_BOARD_RESETS.getOrDefault(playersCollections, 0L) < 1000 * 60 * 5) {
//            return;
        }
        LAST_BOARD_RESETS.put(playersCollections, System.currentTimeMillis());
        if (Warlords.getGameManager().getGames().stream().anyMatch(gameHolder -> gameHolder.getGame() != null && gameHolder.getMap() != GameMap.MAIN_LOBBY)) {
            return;
        }
        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Resetting leaderboards for " + playersCollections.name + " (" + gameMode + ")");
        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> {
            if (gameMode == null || gameType.shouldUpdateLeaderboard(gameMode)) {
                ChatUtils.MessageType.LEADERBOARDS.sendMessage("GameType: " + gameType.name + " - " + playersCollections.name);
                statsLeaderboardGameType.resetLeaderboards(playersCollections);
            }
        });
        createLeaderboardSwitcherHologram();
        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Loaded " + playersCollections.name +
                "(" + DatabaseManager.CACHED_PLAYERS.get(playersCollections).values().size() + ") leaderboards");
        if (playersCollections == PlayersCollections.LIFETIME) {
            DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
            if (currentGameEvent == null) {
                return;
            }
            EventsLeaderboardManager.EVENT_LEADERBOARDS.forEach((eventLeaderboard, s) -> eventLeaderboard.resetHolograms(null, "", s));
        }
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.hologramsEnabled) {
            return;
        }
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        int page = playerLeaderboardInfo.getPage();
        StatsLeaderboardCategory<?, ?, ?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(player.getUniqueId());

        getAllLeaderboardCategories().forEach(category -> {
            category.getAllHolograms()
                    .forEach(hologram -> hologram.getVisibilityManager().removeViewer(player.getUniqueId()));
        });
        if (statsLeaderboardCategory != null) {
            statsLeaderboardCategory.getCollectionHologramPaged(selectedTime)
                                    .forEach(holograms -> {
                                        Hologram hologram = holograms.get(page);
                                        hologram.getVisibilityManager().addViewer(player.getUniqueId());
                                    });
        }

        CustomScoreboard.getPlayerScoreboard(player).giveMainLobbyScoreboard();
    }

    private static void createLeaderboardSwitcherHologram() {
        if (!Warlords.hologramsEnabled) {
            return;
        }

        //PAGE
        List<Hologram> pageSwitcher = createLeaderboardSwitcherHologram(
                StatsLeaderboardLocations.STATS_PAGE_SWITCH_LOCATION,
                "Page",
                Math.min(3, StatsLeaderboard.MAX_PAGES),
                PlayerLeaderboardInfo::getPage,
                PlayerLeaderboardInfo::getPageBefore,
                PlayerLeaderboardInfo::getPageAfter,
                PlayerLeaderboardInfo::getPageRange,
                (player, playerLeaderboardInfo, integer) -> {
                    playerLeaderboardInfo.setPage(integer);
                    EventsLeaderboardManager.resetVisibility(player);
                    GuildLeaderboardManager.resetVisibility(player);
                }
        );

        List<Hologram> categorySwitcher = new ArrayList<>();
        //GAME TYPE
        createLeaderboardSwitcherHologram(
                StatsLeaderboardLocations.STATS_GAME_TYPE_SWITCH_LOCATION,
                "GameType",
                Math.min(3, GameType.ACTIVE_LEADERBOARDS.size()),
                PlayerLeaderboardInfo::getStatsGameType,
                info -> GameType.getBefore(info.getStatsGameType()),
                info -> GameType.getAfter(info.getStatsGameType()),
                (playerLeaderboardInfo, gameType) -> gameType.name,
                (p, playerLeaderboardInfo, gameType) -> {
                    playerLeaderboardInfo.setStatsGameType(gameType);
                    playerLeaderboardInfo.setStatsCategory(0);
                    categorySwitcher.forEach(hologram -> HologramManager.updateHologram(p, hologram));
                    CustomScoreboard.getPlayerScoreboard(p).giveMainLobbyScoreboard();
                }
        );
        //CATEGORY
        categorySwitcher.addAll(createLeaderboardSwitcherHologram(
                StatsLeaderboardLocations.STATS_CATEGORY_SWITCH_LOCATION,
                "Category",
                3, //Math.min(3, GameType.ACTIVE_LEADERBOARDS.size()),
                info -> {
                    GameType selectedType = info.getStatsGameType();
                    AbstractStatsLeaderboardGameType<?, ?, ?, ?> leaderboardGameType = STATS_LEADERBOARDS.get(selectedType);
                    if (leaderboardGameType == null) {
                        return null;
                    }
                    List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = leaderboardGameType.getCategories();
                    int selectedCategory = info.getStatsCategory();
                    if (selectedCategory < 0 || selectedCategory >= categories.size()) {
                        selectedCategory = 0;
                        info.setStatsCategory(selectedCategory);
                    }
                    return categories.get(selectedCategory);
                },
                info -> {
                    GameType selectedType = info.getStatsGameType();
                    AbstractStatsLeaderboardGameType<?, ?, ?, ?> leaderboardGameType = STATS_LEADERBOARDS.get(selectedType);
                    if (leaderboardGameType == null) {
                        return null;
                    }
                    List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = leaderboardGameType.getCategories();
                    if (categories.size() == 1) {
                        return null;
                    }
                    int selectedCategory = info.getStatsCategory();
                    return categories.get(selectedCategory == 0 ? categories.size() - 1 : selectedCategory - 1);
                },
                info -> {
                    GameType selectedType = info.getStatsGameType();
                    AbstractStatsLeaderboardGameType<?, ?, ?, ?> leaderboardGameType = STATS_LEADERBOARDS.get(selectedType);
                    if (leaderboardGameType == null) {
                        return null;
                    }
                    List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = leaderboardGameType.getCategories();
                    if (categories.size() == 1) {
                        return null;
                    }
                    int selectedCategory = info.getStatsCategory();
                    return categories.get(selectedCategory == categories.size() - 1 ? 0 : selectedCategory + 1);
                },
                (info, category) -> category.getCategoryName(),
                (p, info, category) -> {
                    GameType selectedType = info.getStatsGameType();
                    AbstractStatsLeaderboardGameType<?, ?, ?, ?> leaderboardGameType = STATS_LEADERBOARDS.get(selectedType);
                    List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = leaderboardGameType.getCategories();
                    info.setStatsCategory(categories.indexOf(category));
                    CustomScoreboard.getPlayerScoreboard(p).giveMainLobbyScoreboard();
                }
        ));
        //TIME
        List<Hologram> timeSwitcher = createLeaderboardSwitcherHologram(
                StatsLeaderboardLocations.STATS_TIME_SWITCH_LOCATION,
                "Time",
                Math.min(3, PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS.size()),
                PlayerLeaderboardInfo::getStatsTime,
                info -> PlayersCollections.getBeforeCollection(info.getStatsTime()),
                info -> PlayersCollections.getAfterCollection(info.getStatsTime()),
                (playerLeaderboardInfo, playersCollections) -> playersCollections.name,
                (p, playerLeaderboardInfo, playersCollections) -> {
                    playerLeaderboardInfo.setStatsTime(playersCollections);
                    CustomScoreboard.getPlayerScoreboard(p).giveMainLobbyScoreboard();
                }
        );
    }

    public static void validatePlayerHolograms(Player player) {
        validatePlayerHolograms(player.getUniqueId());
    }

    public static StatsLeaderboardCategory<?, ?, ?> getLeaderboardCategoryFromUUID(UUID uuid) {
        if (!Warlords.hologramsEnabled) {
            return null;
        }
        validatePlayerHolograms(uuid);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(uuid);
        GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
        int selectedCategory = playerLeaderboardInfo.getStatsCategory();

        AbstractStatsLeaderboardGameType<?, ?, ?, ?> leaderboardGameType = STATS_LEADERBOARDS.get(selectedGameType);
        if (leaderboardGameType == null) {
            return null;
        }
        List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = leaderboardGameType.getCategories();
        if (selectedCategory >= categories.size()) {
            selectedCategory = 0;
            playerLeaderboardInfo.setStatsCategory(selectedCategory);
        }

        return categories.get(selectedCategory);
    }

    public static List<StatsLeaderboardCategory<?, ?, ?>> getAllLeaderboardCategories() {
        return STATS_LEADERBOARDS.values().stream()
                                 .flatMap(statsLeaderboardCategory -> statsLeaderboardCategory.getCategories().stream())
                                 .collect(Collectors.toList());
    }

    private static <T> List<Hologram> createLeaderboardSwitcherHologram(
            Location location,
            String name,
            int max,
            Function<PlayerLeaderboardInfo, T> selected,
            Function<PlayerLeaderboardInfo, T> before,
            Function<PlayerLeaderboardInfo, T> after,
            BiFunction<PlayerLeaderboardInfo, T, String> getName,
            TriConsumer<Player, PlayerLeaderboardInfo, T> set
    ) {
        location = location.clone().add(0, -1.25, 0);
        List<Hologram> switcherHolograms = new ArrayList<>();
        InteractData interactData = new InteractData(2f, -1, true);
//        int max = 3;//selected == before && selected == after ? 1 : before == after ? 2 : 3;
        for (int i = 0; i < 3 && i < max; i++) {
            int finalI = i;
            Hologram.Builder builder = new Hologram.Builder("leaderboardSwitcher" + name + finalI,
                    location.clone(),
                    p -> {
                        PlayerLeaderboardInfo playerLeaderboardInfo = getPlayerInfo(p);
                        T mode;
                        if (finalI == 0) {
                            mode = before.apply(playerLeaderboardInfo);
                        } else if (finalI == 1) {
                            mode = selected.apply(playerLeaderboardInfo);
                        } else {
                            mode = after.apply(playerLeaderboardInfo);
                        }
                        if (mode == null) {
                            return null;
                        }
                        return new HologramDataText.Builder<>(ComponentBuilder
                                .create(getName.apply(playerLeaderboardInfo, mode), NamedTextColor.AQUA)
                                .build()
                        )
                                .setBillboard(Display.Billboard.VERTICAL)
                                .build();
                    }
            ).setVisibility(VisibilityType.ALL);
            if (finalI == 0 || finalI == 2 || finalI == 1 && max == 2) {
                builder.setInteract(p -> {
                            if (max == 1) {
                                return false;
                            }
                            PlayerLeaderboardInfo playerLeaderboardInfo = getPlayerInfo(p);
                            if (finalI == 0) {
                                set.accept(p, playerLeaderboardInfo, before.apply(playerLeaderboardInfo));
                            } else {
                                set.accept(p, playerLeaderboardInfo, after.apply(playerLeaderboardInfo));
                            }
                            switcherHolograms.forEach(hologram -> HologramManager.updateHologram(p, hologram));
                            return false;
                        }, player -> interactData
                );
            }

            Hologram gameSwitcherGame = builder.build();
            switcherHolograms.add(gameSwitcherGame);
            location.add(0, 0.4, 0);
        }
        switcherHolograms.forEach(HologramManager::addHologram);
        return switcherHolograms;
    }

    public static void validatePlayerHolograms(UUID uuid) {
        if (!PLAYER_LEADERBOARD_INFOS.containsKey(uuid) || PLAYER_LEADERBOARD_INFOS.get(uuid) == null) {
            PLAYER_LEADERBOARD_INFOS.put(uuid, new PlayerLeaderboardInfo());
        }
    }

    public static PlayerLeaderboardInfo getPlayerInfo(Player player) {
        UUID uuid = player.getUniqueId();
        if (!PLAYER_LEADERBOARD_INFOS.containsKey(uuid) || PLAYER_LEADERBOARD_INFOS.get(uuid) == null) {
            leaderboardInfo = new PlayerLeaderboardInfo();
            PLAYER_LEADERBOARD_INFOS.put(uuid, leaderboardInfo);
            return leaderboardInfo;
        }
        return PLAYER_LEADERBOARD_INFOS.get(uuid);
    }

//    private static Hologram createSwitchHologram(Location location) {
//        Hologram switchHologram = FancyHologramsPlugin.get().getHologramManager().create(hologramData);
//        hologramData.addLine(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
//        hologramData.addLine("");
//
//        return switchHologram;
//    }

    public static void setLeaderboardHologramVisibilityToAll() {
        Bukkit.getOnlinePlayers().forEach(StatsLeaderboardManager::setLeaderboardHologramVisibility);
    }

    private static void removeLeaderboardPlayerSpecificHolograms(Player player) {
        validatePlayerHolograms(player);
        PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId()).clearHolograms();
    }

    public enum GameType {
        ALL("All Modes (Excluding PvE)", "", StatsLeaderboardGeneral::new) {
            @Override
            public boolean shouldUpdateLeaderboard(GameMode gameMode) {
                return !GameMode.isPvE(gameMode);
            }
        },
        CTF("Capture The Flag", "CTF", StatsLeaderboardCTF::new) {
            @Override
            public boolean shouldUpdateLeaderboard(GameMode gameMode) {
                return gameMode == GameMode.CAPTURE_THE_FLAG;
            }
        },
        PVE("PvE", "PvE", StatsLeaderboardPvE::new) {
            @Override
            public boolean shouldUpdateLeaderboard(GameMode gameMode) {
                return GameMode.isPvE(gameMode);
            }
        },
        WAVE_DEFENSE("Wave Defense", "Wave Defense", StatsLeaderboardWaveDefense::new) {
            @Override
            public boolean shouldUpdateLeaderboard(GameMode gameMode) {
                return GameMode.isWaveDefense(gameMode);
            }
        },
        ONSLAUGHT("Onslaught", "Onslaught", StatsLeaderboardOnslaught::new) {
            @Override
            public boolean shouldUpdateLeaderboard(GameMode gameMode) {
                return gameMode == GameMode.ONSLAUGHT;
            }
        },

        ;

        public static final List<GameType> ACTIVE_LEADERBOARDS = Arrays.asList(ALL, CTF, PVE, WAVE_DEFENSE, ONSLAUGHT);

        public static boolean isPve(GameType gameType) {
            return gameType == PVE || gameType == WAVE_DEFENSE || gameType == ONSLAUGHT;
        }

        public static GameType getAfter(GameType gameType) {
            if (ACTIVE_LEADERBOARDS.size() <= 1) {
                return gameType;
            }
            int index = ACTIVE_LEADERBOARDS.indexOf(gameType);
            if (index == ACTIVE_LEADERBOARDS.size() - 1) {
                return ACTIVE_LEADERBOARDS.get(0);
            } else {
                return ACTIVE_LEADERBOARDS.get(index + 1);
            }
        }

        public static GameType getBefore(GameType gameType) {
            if (ACTIVE_LEADERBOARDS.size() <= 1) {
                return gameType;
            }
            int index = ACTIVE_LEADERBOARDS.indexOf(gameType);
            if (index == 0) {
                return ACTIVE_LEADERBOARDS.get(ACTIVE_LEADERBOARDS.size() - 1);
            } else {
                return ACTIVE_LEADERBOARDS.get(index - 1);
            }
        }

        public final String name;
        public final String shortName;
        public final Supplier<AbstractStatsLeaderboardGameType<?, ?, ?, ?>> createStatsLeaderboardGameType;

        GameType(String name, String shortName, Supplier<AbstractStatsLeaderboardGameType<?, ?, ?, ?>> createStatsLeaderboardGameType) {
            this.name = name;
            this.shortName = shortName;
            this.createStatsLeaderboardGameType = createStatsLeaderboardGameType;
        }

        public abstract boolean shouldUpdateLeaderboard(GameMode gameMode);
    }

}

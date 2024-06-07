package com.ebicep.warlords.database.leaderboards.stats;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.PlayerLeaderboardInfo;
import com.ebicep.warlords.database.leaderboards.events.EventLeaderboard;
import com.ebicep.warlords.database.leaderboards.events.EventsLeaderboardManager;
import com.ebicep.warlords.database.leaderboards.stats.sections.AbstractStatsLeaderboardGameType;
import com.ebicep.warlords.database.leaderboards.stats.sections.StatsLeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.stats.sections.leaderboardgametypes.*;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.database.repositories.timings.pojos.DatabaseTiming;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.GuildTag;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class StatsLeaderboardManager {

    public static final World MAIN_LOBBY = Bukkit.getWorld("MainLobby");
    public static final Location MAIN_LOBBY_SPAWN = new Location(MAIN_LOBBY, 11.5, 81, 149.5, 0, 0);
    public static final HashMap<UUID, PlayerLeaderboardInfo> PLAYER_LEADERBOARD_INFOS = new HashMap<>();
    public static final HashMap<GameType, AbstractStatsLeaderboardGameType<?, ?, ?, ?>> STATS_LEADERBOARDS = new HashMap<>() {{
        for (GameType value : GameType.values()) {
            put(value, value.createStatsLeaderboardGameType.get());
        }
    }};

    public static final Map<PlayersCollections, Long> LAST_BOARD_RESETS = new HashMap<>();

    public static boolean enabled = true;
    public static boolean loaded = false;

    public static void validatePlayerHolograms(Player player) {
        validatePlayerHolograms(player.getUniqueId());
    }

    public static void validatePlayerHolograms(UUID uuid) {
        if (!PLAYER_LEADERBOARD_INFOS.containsKey(uuid) || PLAYER_LEADERBOARD_INFOS.get(uuid) == null) {
            PLAYER_LEADERBOARD_INFOS.put(uuid, new PlayerLeaderboardInfo());
        }
    }

    public static void addHologramLeaderboards(boolean init) {
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
        if (!DatabaseManager.enabled) {
            return;
        }
        if (DatabaseManager.playerService == null || DatabaseManager.gameService == null) {
            return;
        }

        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> statsLeaderboardGameType.addLeaderboards());

        if (enabled) {
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
                            ConcurrentHashMap<UUID, DatabasePlayer> concurrentHashMap = DatabaseManager.CACHED_PLAYERS.computeIfAbsent(value,
                                    v -> new ConcurrentHashMap<>()
                            );
                            for (DatabasePlayer databasePlayer : databasePlayers) {
                                if (databasePlayer.getUuid() == null) {
                                    ChatUtils.MessageType.LEADERBOARDS.sendErrorMessage(databasePlayer.getId() + " - " + databasePlayer.getName() + " has a null UUID");
                                    continue;
                                }
                                if (databasePlayer.getName() == null) {
                                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(databasePlayer.getUuid());
                                    if (offlinePlayer.getName() != null) {
                                        databasePlayer.setName(offlinePlayer.getName());
                                        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Updated Name: " + databasePlayer.getName() + " - " + value);
                                        DatabaseManager.queueUpdatePlayerAsync(databasePlayer, value);
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
                                if (value == PlayersCollections.SEASON_9 && lessThan20Plays) {
                                    continue;
                                }
                                DatabasePlayer cachedPlayer = concurrentHashMap.get(databasePlayer.getUuid());
                                if (cachedPlayer == null || !cachedPlayer.getId().equals(databasePlayer.getId())) {
                                    concurrentHashMap.put(databasePlayer.getUuid(), databasePlayer);
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
                            DatabaseTiming.checkLeaderboardResets();
                            NPCManager.createGameJoinNPCs();
                            DatabaseGameEvent.startGameEvent();
                            SRCalculator.recalculateSR();
                        }
                        this.cancel();
                    } else if (counter++ > 2 * 300) { //holograms should all load within 5 minutes or ???
                        ChatUtils.MessageType.LEADERBOARDS.sendErrorMessage("Holograms did not load within 5 minutes");
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 20, 10);
        }
    }

    /**
     * All players in PLAYERS_TO_ADD become the new leaderboard players
     *
     * @param playersCollections The collection of players to reload
     * @param gameMode
     */
    public static void resetLeaderboards(PlayersCollections playersCollections, @Nullable GameMode gameMode) {
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
        if (!DatabaseManager.enabled || !enabled) {
            return;
        }
        if (DatabaseManager.playerService == null || DatabaseManager.gameService == null) {
            return;
        }
        if (!PlayersCollections.ACTIVE_LEADERBOARD_COLLECTIONS.contains(playersCollections)) {
            return;
        }
        // boards can only be reset every 5 minutes
        if (System.currentTimeMillis() - LAST_BOARD_RESETS.getOrDefault(playersCollections, 0L) < 1000 * 60 * 5) {
            return;
        }
        LAST_BOARD_RESETS.put(playersCollections, System.currentTimeMillis());
        ChatUtils.MessageType.LEADERBOARDS.sendMessage("Resetting leaderboards for " + playersCollections.name + " (" + gameMode + ")");
        STATS_LEADERBOARDS.forEach((gameType, statsLeaderboardGameType) -> {
            if (gameMode == null || gameType.shouldUpdateLeaderboard(gameMode)) {
                ChatUtils.MessageType.LEADERBOARDS.sendMessage("GameType: " + gameType.name + " - " + playersCollections.name);
                statsLeaderboardGameType.resetLeaderboards(playersCollections);
            }
        });
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

    public static StatsLeaderboardCategory<?, ?, ?> getLeaderboardCategoryFromUUID(UUID uuid) {
        if (!Warlords.holographicDisplaysEnabled) {
            return null;
        }
        validatePlayerHolograms(uuid);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(uuid);
        GameType selectedGameType = playerLeaderboardInfo.getStatsGameType();
        int selectedCategory = playerLeaderboardInfo.getStatsCategory();

        List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = STATS_LEADERBOARDS.get(selectedGameType).getCategories();
        if (selectedCategory >= categories.size()) {
            selectedCategory = 0;
            playerLeaderboardInfo.setStatsCategory(selectedCategory);
        }

        return categories.get(selectedCategory);
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
        validatePlayerHolograms(player);

        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        int page = playerLeaderboardInfo.getPage();
        StatsLeaderboardCategory<?, ?, ?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(player.getUniqueId());

        getAllLeaderboardCategories().forEach(category -> {
            category.getAllHolograms()
                    .forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        });
        if (statsLeaderboardCategory != null) {
            statsLeaderboardCategory.getCollectionHologramPaged(selectedTime)
                                    .forEach(holograms -> holograms.get(page)
                                                                   .getVisibilitySettings()
                                                                   .setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
        if (currentGameEvent != null) {
            EventsLeaderboardManager.EVENT_LEADERBOARDS
                    .keySet()
                    .forEach(eventLeaderboard -> eventLeaderboard
                            .getSortedHolograms()
                            .stream()
                            .flatMap(Collection::stream)
                            .forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN)));
            EventsLeaderboardManager.EVENT_LEADERBOARDS
                    .keySet()
                    .forEach(eventLeaderboard -> eventLeaderboard
                            .getSortedHolograms()
                            .get(0)
                            .get(page).getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        CustomScoreboard.getPlayerScoreboard(player).giveMainLobbyScoreboard();
        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    public static void setLeaderboardHologramVisibilityToAll() {
        Bukkit.getOnlinePlayers().forEach(StatsLeaderboardManager::setLeaderboardHologramVisibility);
    }

    private static <T> void createLeaderboardSwitcherHologram(
            Player player,
            Location location,
            T selected,
            T before,
            T after,
            Function<T, String> getName,
            Consumer<T> set
    ) {
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
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
        removePlayerSpecificHolograms(player);
        DatabaseGameBase.setGameHologramVisibility(player);
        PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());

        //GAME TYPE
        GameType selectedType = playerLeaderboardInfo.getStatsGameType();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_GAME_TYPE_SWITCH_LOCATION,
                selectedType,
                GameType.getBefore(selectedType),
                GameType.getAfter(selectedType),
                gameType -> gameType.name,
                playerLeaderboardInfo::setStatsGameType
        );
        //CATEGORY
        List<? extends StatsLeaderboardCategory<?, ?, ?>> categories = STATS_LEADERBOARDS.get(selectedType).getCategories();
        int selectedCategory = playerLeaderboardInfo.getStatsCategory();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_CATEGORY_SWITCH_LOCATION,
                categories.get(selectedCategory),
                categories.get(selectedCategory == 0 ? categories.size() - 1 : selectedCategory - 1),
                categories.get(selectedCategory == categories.size() - 1 ? 0 : selectedCategory + 1),
                StatsLeaderboardCategory::getCategoryName,
                category -> playerLeaderboardInfo.setStatsCategory(categories.indexOf(category))
        );
        //TIME
        PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_TIME_SWITCH_LOCATION,
                selectedTime,
                PlayersCollections.getBeforeCollection(selectedTime),
                PlayersCollections.getAfterCollection(selectedTime),
                playersCollections -> playersCollections.name,
                playerLeaderboardInfo::setStatsTime
        );
        //PAGE
        createLeaderboardSwitcherHologram(player,
                StatsLeaderboardLocations.STATS_PAGE_SWITCH_LOCATION,
                playerLeaderboardInfo.getPage(),
                playerLeaderboardInfo.getPageBefore(),
                playerLeaderboardInfo.getPageAfter(),
                playerLeaderboardInfo::getPageRange,
                playerLeaderboardInfo::setPage
        );
    }

    private static Hologram createSwitchHologram(Location location) {
        Hologram switchHologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
        switchHologram.getLines().appendText(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
        switchHologram.getLines().appendText("");

        return switchHologram;
    }

    public static void addPlayerPositionLeaderboards(Player player) {
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
        if (enabled) {
            //leaderboards
            removeLeaderboardPlayerSpecificHolograms(player);
            validatePlayerHolograms(player);
            PlayerLeaderboardInfo playerLeaderboardInfo = PLAYER_LEADERBOARD_INFOS.get(player.getUniqueId());
            PlayersCollections selectedTime = playerLeaderboardInfo.getStatsTime();
            StatsLeaderboardCategory<?, ?, ?> statsLeaderboardCategory = getLeaderboardCategoryFromUUID(player.getUniqueId());
            if (statsLeaderboardCategory == null) {
                return;
            }
            List<Hologram> playerHolograms = new ArrayList<>();
            for (StatsLeaderboard statsLeaderboard : statsLeaderboardCategory.getStatsLeaderboards()) {
                if (statsLeaderboard.isHidden()) {
                    continue;
                }
                Location location = statsLeaderboard.getLocation().clone().add(0, -3.5, 0);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);

                List<DatabasePlayer> databasePlayers = statsLeaderboard.getSortedPlayers(selectedTime);
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId())) {
                        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
                        Component guildTag = Component.empty();
                        if (guildPlayerPair != null) {
                            GuildTag tag = guildPlayerPair.getA().getTag();
                            if (tag != null) {
                                guildTag = tag.getTag(true);
                            }
                        }
                        hologram.getLines().appendText(LegacyComponentSerializer.legacySection().serialize(
                                Component.text((i + 1) + ". ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                         .append(Component.text(databasePlayer.getName(), Permissions.getColor(databasePlayer)))
                                         .append(Component.space())
                                         .append(guildTag)
                                         .append(Component.text(" - ", NamedTextColor.GRAY))
                                         .append(Component.text(statsLeaderboard.getStringFunction().apply(databasePlayer)))
                        ));
                        break;
                    }
                }

                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

                playerHolograms.add(hologram);
            }

            for (EventLeaderboard eventLeaderboard : EventsLeaderboardManager.EVENT_LEADERBOARDS.keySet()) {
                if (eventLeaderboard.isHidden()) {
                    continue;
                }
                Location location = eventLeaderboard.getLocation().clone().add(0, -3.5, 0);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);

                List<DatabasePlayer> databasePlayers = eventLeaderboard.getSortedPlayers();
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId())) {
                        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(databasePlayer.getUuid());
                        Component guildTag = Component.empty();
                        if (guildPlayerPair != null) {
                            GuildTag tag = guildPlayerPair.getA().getTag();
                            if (tag != null) {
                                guildTag = tag.getTag(true);
                            }
                        }
                        hologram.getLines().appendText(LegacyComponentSerializer.legacySection().serialize(
                                Component.text((i + 1) + ". ", NamedTextColor.YELLOW, TextDecoration.BOLD)
                                         .append(Component.text(databasePlayer.getName(), Permissions.getColor(databasePlayer)))
                                         .append(Component.space())
                                         .append(guildTag)
                                         .append(Component.text(" - ", NamedTextColor.GRAY))
                                         .append(Component.text(eventLeaderboard.getStringFunction().apply(databasePlayer, eventLeaderboard.getEventTime())))
                        ));
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
        if (!Warlords.holographicDisplaysEnabled) {
            return;
        }
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

    public static List<StatsLeaderboardCategory<?, ?, ?>> getAllLeaderboardCategories() {
        return STATS_LEADERBOARDS.values().stream()
                                 .flatMap(statsLeaderboardCategory -> statsLeaderboardCategory.getCategories().stream())
                                 .collect(Collectors.toList());
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

        public static final List<GameType> ACTIVE_LEADERBOARDS = Arrays.asList(ALL, CTF, PVE);

        public static boolean isPve(GameType gameType) {
            return gameType == PVE || gameType == WAVE_DEFENSE || gameType == ONSLAUGHT;
        }

        public static GameType getAfter(GameType gameType) {
            int index = ACTIVE_LEADERBOARDS.indexOf(gameType);
            if (index == ACTIVE_LEADERBOARDS.size() - 1) {
                return ACTIVE_LEADERBOARDS.get(0);
            } else {
                return ACTIVE_LEADERBOARDS.get(index + 1);
            }
        }

        public static GameType getBefore(GameType gameType) {
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
package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.sections.LeaderboardCategory;
import com.ebicep.warlords.database.leaderboards.sections.subsections.LeaderboardCTF;
import com.ebicep.warlords.database.leaderboards.sections.subsections.LeaderboardGeneral;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.LocationBuilder;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LeaderboardManager {

    public static final World world = Bukkit.getWorld("MainLobby");

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();

    public static final Location leaderboardGameTypeSwitchLocation = new Location(world, -2558.5, 53, 719.5);
    public static final Location leaderboardCategorySwitchLocation = new Location(world, -2552.5, 53, 719.5);
    public static final Location leaderboardTimeSwitchLocation = new Location(world, -2546.5, 53, 719.5);

    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
    public static final HashMap<UUID, GameType> playerLeaderboardGameType = new HashMap<>();
    public static final HashMap<UUID, Category> playerLeaderboardCategory = new HashMap<>();
    public static final HashMap<UUID, PlayersCollections> playerLeaderboardTime = new HashMap<>();

    public static final LeaderboardGeneral leaderboardGeneral = new LeaderboardGeneral();
    public static final LeaderboardCTF leaderboardCTF = new LeaderboardCTF();

    public static final HashMap<UUID, List<Hologram>> playerSpecificHolograms = new HashMap<>();

    private static final String[] weeklyIncludedLeaderboardsTitles = new String[]{"Plays", "Wins", "Kills", "DHP Per Game", "Flags Captured"};
    private static final String[] weeklyExperienceLeaderboards = new String[]{
            "Wins",
            "Losses",
            "Kills",
            "Assists",
            "Deaths",
            "DHP",
            "DHP Per Game",
            "Damage",
            "Healing",
            "Absorbed",
            "Flags Captured",
            "Flags Returned",
    };
    public static boolean enabled = true;

    public static void putLeaderboards() {
        leaderboardGeneral.addLeaderboards();
        leaderboardCTF.addLeaderboards();
    }

    public static void addHologramLeaderboards(String sharedChainName) {
        if (!Warlords.holographicDisplaysEnabled) return;
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().forEach(hologram -> {
            Location hologramLocation = hologram.getPosition().toLocation();
            if (!DatabaseGame.lastGameStatsLocation.equals(hologramLocation) &&
                    !DatabaseGame.topDamageLocation.equals(hologramLocation) &&
                    !DatabaseGame.topHealingLocation.equals(hologramLocation) &&
                    !DatabaseGame.topAbsorbedLocation.equals(hologramLocation) &&
                    !DatabaseGame.topDHPPerMinuteLocation.equals(hologramLocation) &&
                    !DatabaseGame.topDamageOnCarrierLocation.equals(hologramLocation) &&
                    !DatabaseGame.topHealingOnCarrierLocation.equals(hologramLocation)
            ) {
                hologram.delete();
            }
        });

        putLeaderboards();
        if (enabled) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");

            //caching all sorted players for each lifetime and weekly
            AtomicInteger loadedBoards = new AtomicInteger();
            long startTime = System.nanoTime();
            for (PlayersCollections value : PlayersCollections.values()) {
                Warlords.newChain()//newSharedChain(sharedChainName)
                        .asyncFirst(() -> DatabaseManager.playerService.findAll(value))
                        .syncLast((collection) -> {
                            addHologramsToGameType(value, collection, leaderboardGeneral.getGeneral(), "All Modes - " + value.name);
                            addHologramsToGameType(value, collection, leaderboardGeneral.getComps(), "All Modes - Comps - " + value.name);
                            addHologramsToGameType(value, collection, leaderboardGeneral.getPubs(), "All Modes - Pubs - " + value.name);
                            addHologramsToGameType(value, collection, leaderboardCTF.getGeneral(), "CTF - All Queues - " + value.name);
                            addHologramsToGameType(value, collection, leaderboardCTF.getComps(), "CTF - Comps - " + value.name);
                            addHologramsToGameType(value, collection, leaderboardCTF.getPubs(), "CTF - Pubs - " + value.name);
                            System.out.println("Loaded " + value.name + " leaderboards");
                            loadedBoards.getAndIncrement();

                            if (value == PlayersCollections.SEASON_5) {
                                SRCalculator.databasePlayerCache = collection;
                                SRCalculator.recalculateSR();
                            }
                        }).execute();
            }

            //depending on what player has selected, set visibility
            new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    if (loadedBoards.get() == 5) {
                        long endTime = System.nanoTime();
                        long timeToLoad = (endTime - startTime) / 1000000;
                        System.out.println("Time it took for LB to load (ms): " + timeToLoad);
//                        if (timeToLoad > 25000) {
//                            System.out.println("FART FART FART");
//                            System.out.println("HOLOGRAMS TOOK LONG ASS TIME TO LOAD!?!");
//                            System.out.println("FART FART FART");
//                        }
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            setLeaderboardHologramVisibility(player);
                            DatabaseGame.setGameHologramVisibility(player);
                            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
                        });
                        System.out.println("Set Hologram Visibility");
                        this.cancel();
                    } else if (counter++ > 60) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 20, 10);
//            Warlords.newChain()//newSharedChain(sharedChainName)
//                    .delay(10, TimeUnit.SECONDS)
//                    .sync(() -> {
//                        long endTime = System.nanoTime();
//                        long timeToLoad = (endTime - startTime) / 1000000;
////                        System.out.println("Time it took for LB to load (ms): " + timeToLoad);
////                        if (timeToLoad > 25000) {
////                            System.out.println("FART FART FART");
////                            System.out.println("HOLOGRAMS TOOK LONG ASS TIME TO LOAD!?!");
////                            System.out.println("FART FART FART");
////                        }
//                        System.out.println("Setting Hologram Visibility");
//                        Bukkit.getOnlinePlayers().forEach(player -> {
//                            setLeaderboardHologramVisibility(player);
//                            DatabaseGame.setGameHologramVisibility(player);
//                        });
//                        System.out.println("Set Hologram Visibility");
//                    }).execute();

        }


    }

    private static void addHologramsToGameType(PlayersCollections value, List<DatabasePlayer> collection, LeaderboardCategory<?> leaderboardCategory, String subTitle) {
        leaderboardCategory.getLeaderboards().forEach(leaderboard -> {
            //sorting values
            collection.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
            //resetting sort then adding new sorted values
            leaderboard.resetSortedPlayers(collection, value);
            //creating leaderboard
            value.function.apply(leaderboardCategory).add(addLeaderboard(leaderboard, value, ChatColor.AQUA + ChatColor.BOLD.toString() + value.name + " " + leaderboard.getTitle(), subTitle));
        });
    }

    public static LeaderboardCategory<?> getLeaderboardCategoryFromPlayer(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return null;
        if (!playerLeaderboardGameType.containsKey(player.getUniqueId()) || playerLeaderboardGameType.get(player.getUniqueId()) == null) {
            playerLeaderboardGameType.put(player.getUniqueId(), GameType.ALL);
        }
        if (!playerLeaderboardCategory.containsKey(player.getUniqueId()) || playerLeaderboardCategory.get(player.getUniqueId()) == null) {
            playerLeaderboardCategory.put(player.getUniqueId(), Category.ALL);
        }

        GameType selectedGameType = playerLeaderboardGameType.get(player.getUniqueId());
        Category selectedCategory = playerLeaderboardCategory.get(player.getUniqueId());

        if (selectedGameType == GameType.ALL) {
            if (selectedCategory == Category.ALL) {
                return leaderboardGeneral.getGeneral();
            } else if (selectedCategory == Category.COMPS) {
                return leaderboardGeneral.getComps();
            } else {
                return leaderboardGeneral.getPubs();
            }
        } else {
            if (selectedCategory == Category.ALL) {
                return leaderboardCTF.getGeneral();
            } else if (selectedCategory == Category.COMPS) {
                return leaderboardCTF.getComps();
            } else {
                return leaderboardCTF.getPubs();
            }
        }
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (!playerLeaderboardTime.containsKey(player.getUniqueId()) || playerLeaderboardTime.get(player.getUniqueId()) == null) {
            playerLeaderboardTime.put(player.getUniqueId(), PlayersCollections.LIFETIME);
        }
        PlayersCollections selectedTime = playerLeaderboardTime.get(player.getUniqueId());
        LeaderboardCategory<?> leaderboardCategory = getLeaderboardCategoryFromPlayer(player);

        getAllLeaderboardCategories().forEach(category -> {
            category.getAllHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        });

        assert leaderboardCategory != null;

        if (selectedTime == PlayersCollections.LIFETIME) {
            leaderboardCategory.getLifeTimeHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedTime == PlayersCollections.SEASON_5) {
            leaderboardCategory.getSeason5Holograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedTime == PlayersCollections.SEASON_4) {
            leaderboardCategory.getSeason4Holograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedTime == PlayersCollections.WEEKLY) {
            leaderboardCategory.getWeeklyHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else {
            leaderboardCategory.getDailyHolograms().forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        if (Warlords.playerScoreboards.containsKey(player.getUniqueId())) {
            Warlords.playerScoreboards.get(player.getUniqueId()).giveMainLobbyScoreboard();
        }

        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    private static void createLeaderboardSwitcherHologram(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) &&
                        (h.getPosition().toLocation().equals(leaderboardGameTypeSwitchLocation) ||
                                h.getPosition().toLocation().equals(leaderboardCategorySwitchLocation) ||
                                h.getPosition().toLocation().equals(leaderboardTimeSwitchLocation))
                ).forEach(Hologram::delete);

        UUID uuid = player.getUniqueId();

        //GAME TYPE
        Hologram gameTypeSwitch = createSwitchHologram(player, leaderboardGameTypeSwitchLocation);
        //GameType beforeType = GameType.getBefore(playerLeaderboardGameType.get(uuid));
        GameType selectedType = playerLeaderboardGameType.get(uuid);
//        GameType afterType = GameType.getAfter(playerLeaderboardGameType.get(uuid));
//        gameTypeSwitch.getLines().appendText((selectedType == beforeType ? ChatColor.GREEN : ChatColor.GRAY) + beforeType.name).setClickListener(p -> {
//            playerLeaderboardGameType.put(uuid, beforeType);
//            setLeaderboardHologramVisibility(p.getPlayer());
//        });
        if (selectedType == GameType.ALL) {
            gameTypeSwitch.getLines().appendText(ChatColor.GREEN + GameType.ALL.name);
            gameTypeSwitch.getLines().appendText(ChatColor.GRAY + GameType.CTF.name).setClickListener(p -> {
                playerLeaderboardGameType.put(uuid, GameType.CTF);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        } else {
            gameTypeSwitch.getLines().appendText(ChatColor.GRAY + GameType.ALL.name).setClickListener(p -> {
                playerLeaderboardGameType.put(uuid, GameType.ALL);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
            gameTypeSwitch.getLines().appendText(ChatColor.GREEN + GameType.CTF.name);
        }
        gameTypeSwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        gameTypeSwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        //CATEGORY
        Hologram categorySwitch = createSwitchHologram(player, leaderboardCategorySwitchLocation);
        Category beforeCategory = Category.getBefore(playerLeaderboardCategory.get(uuid));
        Category selectedCategory = playerLeaderboardCategory.get(uuid);
        Category afterCategory = Category.getAfter(playerLeaderboardCategory.get(uuid));
        if (selectedCategory != beforeCategory) {
            categorySwitch.getLines().appendText(ChatColor.GRAY + beforeCategory.name).setClickListener(p -> {
                playerLeaderboardCategory.put(uuid, beforeCategory);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        }
        categorySwitch.getLines().appendText(ChatColor.GREEN + selectedCategory.name);
        if (selectedCategory != afterCategory) {
            categorySwitch.getLines().appendText(ChatColor.GRAY + afterCategory.name).setClickListener(p -> {
                playerLeaderboardCategory.put(uuid, afterCategory);
                setLeaderboardHologramVisibility(p.getPlayer());
            });
        }
        categorySwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        categorySwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

        //TIME
        Hologram timeSwitch = createSwitchHologram(player, leaderboardTimeSwitchLocation);
        PlayersCollections beforeCollection = PlayersCollections.getBeforeCollection(playerLeaderboardTime.get(uuid));
        PlayersCollections selectedCollection = playerLeaderboardTime.get(uuid);
        PlayersCollections afterCollection = PlayersCollections.getAfterCollection(playerLeaderboardTime.get(uuid));
        timeSwitch.getLines().appendText(ChatColor.GRAY + beforeCollection.name).setClickListener(p -> {
            playerLeaderboardTime.put(uuid, beforeCollection);
            setLeaderboardHologramVisibility(p.getPlayer());
        });

        timeSwitch.getLines().appendText(ChatColor.GREEN + selectedCollection.name);
        timeSwitch.getLines().appendText(ChatColor.GRAY + afterCollection.name).setClickListener(p -> {
            playerLeaderboardTime.put(uuid, afterCollection);
            setLeaderboardHologramVisibility(p.getPlayer());
        });
        timeSwitch.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        timeSwitch.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
    }

    private static Hologram createSwitchHologram(Player player, Location location) {
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

            PlayersCollections selectedTime = playerLeaderboardTime.get(player.getUniqueId());
            LeaderboardCategory<?> leaderboardCategory = getLeaderboardCategoryFromPlayer(player);
            if (leaderboardCategory == null) return;
            List<Hologram> playerHolograms = new ArrayList<>();
            for (Leaderboard leaderboard : leaderboardCategory.leaderboards) {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);

                List<DatabasePlayer> databasePlayers;
                switch (selectedTime) {
                    case LIFETIME:
                        databasePlayers = leaderboard.getSortedAllTime();
                        break;
                    case SEASON_5:
                        databasePlayers = leaderboard.getSortedSeason5();
                        break;
                    case SEASON_4:
                        databasePlayers = leaderboard.getSortedSeason4();
                        break;
                    case WEEKLY:
                        databasePlayers = leaderboard.getSortedWeekly();
                        break;
                    case DAILY:
                        databasePlayers = leaderboard.getSortedDaily();
                        break;
                    default:
                        return;
                }

//                    if (Arrays.stream(weeklyIncludedLeaderboardsTitles).noneMatch(l -> l.equals(leaderboard.getTitle()))) {
//                        continue;
//                    }

                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId().toString())) {
                        hologram.getLines().appendText(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.DARK_AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + leaderboard.getStringFunction().apply(databasePlayer));
                        break;
                    }
                }

                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);

                playerHolograms.add(hologram);
            }

            playerSpecificHolograms.put(player.getUniqueId(), playerHolograms);
        }
    }

    public static void removePlayerSpecificHolograms(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        removeLeaderboardPlayerSpecificHolograms(player);
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) &&
                        (h.getPosition().toLocation().equals(DatabaseGame.gameSwitchLocation) ||
                                h.getPosition().toLocation().equals(leaderboardGameTypeSwitchLocation) ||
                                h.getPosition().toLocation().equals(leaderboardCategorySwitchLocation) ||
                                h.getPosition().toLocation().equals(leaderboardTimeSwitchLocation)))
                .forEach(Hologram::delete);
    }

    private static void removeLeaderboardPlayerSpecificHolograms(Player player) {
        playerSpecificHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>()).forEach(Hologram::delete);
        playerSpecificHolograms.getOrDefault(player.getUniqueId(), new ArrayList<>()).clear();
    }

    private static Hologram addLeaderboard(Leaderboard leaderboard, PlayersCollections collections, String title, String subTitle) {
        List<DatabasePlayer> databasePlayers = leaderboard.getSortedPlayers(collections);
        List<String> hologramLines = new ArrayList<>();
        hologramLines.add(ChatColor.GRAY + subTitle);
        for (int i = 0; i < 10 && i < databasePlayers.size(); i++) {
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + databasePlayer.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + leaderboard.getStringFunction().apply(databasePlayer));
        }
        return createLeaderboard(leaderboard, title, hologramLines);
    }

    private static Hologram createLeaderboard(Leaderboard leaderboard, String title, List<String> hologramLines) {
        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(leaderboard.getLocation());
        hologram.getLines().appendText(title);
        //hologram.getLines().appendText("");
        for (String line : hologramLines) {
            hologram.getLines().appendText(line);
        }

        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        return hologram;
    }

    public static Document getTopPlayersOnLeaderboard() {
        List<Leaderboard> leaderboards = leaderboardCTF.getComps().getLeaderboards();
        Document document = new Document("date", new Date()).append("total_players", leaderboards.get(0).getSortedWeekly().size());
        for (String title : weeklyExperienceLeaderboards) {
            leaderboards.stream().filter(leaderboard -> leaderboard.getTitle().equals(title)).findFirst().ifPresent(leaderboard -> {
                Number[] numbers = leaderboard.getTopThreeValues();
                String[] names = leaderboard.getTopThreePlayerNames(numbers, DatabasePlayer::getName);
                String[] uuids = leaderboard.getTopThreePlayerNames(numbers, DatabasePlayer::getUuid);
                List<Document> topList = new ArrayList<>();
                for (int i = 0; i < numbers.length; i++) {
                    topList.add(new Document("names", names[i]).append("uuids", uuids[i]).append("amount", numbers[i]));
                }
                Document totalDocument = new Document();
                if (numbers[0] instanceof Integer) {
                    totalDocument = new Document("total", Arrays.stream(numbers).mapToInt(Number::intValue).sum());
                } else if (numbers[0] instanceof Long) {
                    totalDocument = new Document("total", Arrays.stream(numbers).mapToLong(Number::longValue).sum());
                }
                document.append(title.toLowerCase().replace(" ", "_"), totalDocument.append("name", title).append("top", topList));
            });
        }
        return document;
    }

    public static List<LeaderboardCategory<?>> getAllLeaderboardCategories() {
        return Stream.of(
                leaderboardGeneral.getGeneral(),
                leaderboardGeneral.getComps(),
                leaderboardGeneral.getPubs(),
                leaderboardCTF.getGeneral(),
                leaderboardCTF.getComps(),
                leaderboardCTF.getPubs()
        ).collect(Collectors.toList());
    }

    public enum GameType {
        ALL("All Modes", ""),
        CTF("Capture The Flag", "CTF");

        public String name;
        public String shortName;

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
        ALL("All Queues", ""),
        COMPS("Competitive Queue", "Comps"),
        PUBS("Public Queue", "Pubs");

        public String name;
        public String shortName;

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
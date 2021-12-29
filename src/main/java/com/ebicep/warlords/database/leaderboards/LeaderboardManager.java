package com.ebicep.warlords.database.leaderboards;

import co.aikar.taskchain.TaskChain;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.NumberFormat;
import me.filoghost.holographicdisplays.api.beta.hologram.Hologram;
import me.filoghost.holographicdisplays.api.beta.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.beta.hologram.VisibilitySettings;
import me.filoghost.holographicdisplays.api.beta.hologram.line.ClickableHologramLine;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class LeaderboardManager {

    public static final World world = Bukkit.getWorld("MainLobby");

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();

    public static final Location leaderboardSwitchLocation = new Location(world, -2571.5, 54.5, 720.5);

    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final List<Leaderboard> leaderboards = new ArrayList<>();

    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
    public static final HashMap<UUID, Integer> playerLeaderboardHolograms = new HashMap<>();

    public static final List<Hologram> lifeTimeHolograms = new ArrayList<>();
    public static final List<Hologram> season5Holograms = new ArrayList<>();
    public static final List<Hologram> season4Holograms = new ArrayList<>();
    public static final List<Hologram> weeklyHolograms = new ArrayList<>();
    public static final List<Hologram> dailyHolograms = new ArrayList<>();
    public static final String[] hologramOrder = new String[]{"LifeTime", "Season 5", "Season 4", "Weekly", "Daily"};
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
        leaderboards.clear();

        leaderboards.add(new Leaderboard("Wins",
                new Location(world, -2558.5, 56, 712.5),
                (DatabasePlayer::getWins),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getWins())));
        leaderboards.add(new Leaderboard("Losses", new Location(world, -2608.5, 52, 728.5),
                (DatabasePlayer::getLosses),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getLosses())));
        leaderboards.add(new Leaderboard("Plays", new Location(world, -2564.5, 56, 712.5),
                (DatabasePlayer::getPlays),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getPlays())));
        leaderboards.add(new Leaderboard("Kills", new Location(world, -2552.5, 56, 712.5),
                (DatabasePlayer::getKills),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getKills())));
        leaderboards.add(new Leaderboard("Assists", new Location(world, -2616.5, 52, 733.5),
                (DatabasePlayer::getAssists),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getAssists())));
        leaderboards.add(new Leaderboard("Deaths", new Location(world, -2616.5, 52, 723.5),
                (DatabasePlayer::getDeaths),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getDeaths())));
        leaderboards.add(new Leaderboard("Damage", new Location(world, -2600.5, 52, 723.5),
                (DatabasePlayer::getDamage),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getDamage())));
        leaderboards.add(new Leaderboard("Healing", new Location(world, -2608.5, 52, 719.5),
                (DatabasePlayer::getHealing),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getHealing())));
        leaderboards.add(new Leaderboard("Absorbed", new Location(world, -2600.5, 52, 733.5),
                (DatabasePlayer::getAbsorbed),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getAbsorbed())));

        leaderboards.add(new Leaderboard("Flags Captured", new Location(world, -2540.5, 56, 712.5),
                (DatabasePlayer::getFlagsCaptured),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getFlagsCaptured())));
        leaderboards.add(new Leaderboard("Flags Returned", new Location(world, -2608.5, 52, 737.5),
                (DatabasePlayer::getFlagsReturned),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getFlagsReturned())));

        leaderboards.add(new Leaderboard("Avenger Wins", new Location(world, -2631.5, 52, 719.5),
                (o -> o.getPaladin().getAvenger().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getPaladin().getAvenger().getWins())));
        leaderboards.add(new Leaderboard("Crusader Wins", new Location(world, -2628.5, 52, 714.5),
                (o -> o.getPaladin().getCrusader().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getPaladin().getCrusader().getWins())));
        leaderboards.add(new Leaderboard("Protector Wins", new Location(world, -2623.5, 52, 711.5),
                (o -> o.getPaladin().getProtector().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getPaladin().getProtector().getWins())));
        leaderboards.add(new Leaderboard("Berserker Wins", new Location(world, -2623.5, 52, 745.5),
                (o -> o.getWarrior().getBerserker().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getWarrior().getBerserker().getWins())));
        leaderboards.add(new Leaderboard("Defender Wins", new Location(world, -2628.5, 52, 742.5),
                (o -> o.getWarrior().getDefender().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getWarrior().getDefender().getWins())));
        leaderboards.add(new Leaderboard("Revenant Wins", new Location(world, -2631.5, 52, 737.5),
                (o -> o.getWarrior().getRevenant().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getWarrior().getRevenant().getWins())));
        leaderboards.add(new Leaderboard("Pyromancer Wins", new Location(world, -2602.5, 53, 749.5),
                (o -> o.getMage().getPyromancer().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getMage().getPyromancer().getWins())));
        leaderboards.add(new Leaderboard("Cryomancer Wins", new Location(world, -2608.5, 53, 752.5),
                (o -> o.getMage().getCryomancer().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getMage().getCryomancer().getWins())));
        leaderboards.add(new Leaderboard("Aquamancer Wins", new Location(world, -2614.5, 53, 749.5),
                (o -> o.getMage().getAquamancer().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getMage().getAquamancer().getWins())));
        leaderboards.add(new Leaderboard("Thunderlord Wins", new Location(world, -2614.5, 53, 707.5),
                (o -> o.getShaman().getThunderlord().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getShaman().getThunderlord().getWins())));
        leaderboards.add(new Leaderboard("Spiritguard Wins", new Location(world, -2608.5, 53, 704.5),
                (o -> o.getShaman().getSpiritguard().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getShaman().getSpiritguard().getWins())));
        leaderboards.add(new Leaderboard("Earthwarden Wins", new Location(world, -2602.5, 53, 707.5),
                (o -> o.getShaman().getEarthwarden().getWins()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getShaman().getEarthwarden().getWins())));

        leaderboards.add(new Leaderboard("Experience", new Location(world, -2526.5, 57, 744.5),
                (DatabasePlayer::getExperience),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getExperience())));
        leaderboards.add(new Leaderboard("Mage Experience", new Location(world, -2520.5, 58, 735.5),
                (o -> o.getMage().getExperience()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getMage().getExperience())));
        leaderboards.add(new Leaderboard("Warrior Experience", new Location(world, -2519.5, 58, 741.5),
                (o -> o.getWarrior().getExperience()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getWarrior().getExperience())));
        leaderboards.add(new Leaderboard("Paladin Experience", new Location(world, -2519.5, 58, 747.5),
                (o -> o.getPaladin().getExperience()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getPaladin().getExperience())));
        leaderboards.add(new Leaderboard("Shaman Experience", new Location(world, -2520.5, 58, 753.5),
                (o -> o.getShaman().getExperience()),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getShaman().getExperience())));

        leaderboards.add(new Leaderboard("DHP", new Location(world, -2619.5, 66.5, 721.5),
                (DatabasePlayer::getDHP),
                databasePlayer -> NumberFormat.addCommaAndRound(databasePlayer.getDHP())));
        leaderboards.add(new Leaderboard("DHP Per Game", new Location(world, -2546.5, 56, 712.5),
                (DatabasePlayer::getDHPPerGame),
                databasePlayer -> NumberFormat.addCommaAndRound(Math.round((double) (databasePlayer.getDHPPerGame()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills Per Game", new Location(world, -2619.5, 66.5, 735.5),
                (DatabasePlayer::getKillsPerGame),
                databasePlayer -> String.valueOf(Math.round(databasePlayer.getKillsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Deaths Per Game", new Location(world, -2608.5, 67, 738.5),
                (DatabasePlayer::getDeathsPerGame),
                databasePlayer -> String.valueOf(Math.round(databasePlayer.getDeathsPerGame() * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills/Assists Per Game", new Location(world, -2608.5, 67, 719.5),
                (DatabasePlayer::getKillsAssistsPerGame),
                databasePlayer -> String.valueOf(Math.round(databasePlayer.getKillsAssistsPerGame() * 10) / 10d)));
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
        lifeTimeHolograms.clear();
        season5Holograms.clear();
        season4Holograms.clear();
        weeklyHolograms.clear();
        dailyHolograms.clear();

        putLeaderboards();
        if (enabled) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");

            //caching all sorted players for each lifetime and weekly
            long startTime = System.nanoTime();
            for (PlayersCollections value : PlayersCollections.values()) {
                Warlords.newChain()//newSharedChain(sharedChainName)
                        .asyncFirst(() -> DatabaseManager.playerService.findAll(value))
                        .syncLast((collection) -> {
                            leaderboards.forEach(leaderboard -> {
                                //sorting values
                                collection.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                //removing all players with 3 or less games from leaderboards if the top player has 10 or more (no one game olivers)
                                if (!collection.isEmpty() && collection.get(0).getPlays() >= 10) {
                                    collection.removeIf(databasePlayer -> databasePlayer.getPlays() <= 3);
                                }
                                //resetting sort then adding new sorted values
                                leaderboard.resetSortedPlayers(collection, value);
                                //creating leaderboard
                                value.leaderboardHolograms.add(addLeaderboard(leaderboard, value, ChatColor.AQUA + ChatColor.BOLD.toString() + value.name + " " + leaderboard.getTitle()));
                            });
                            System.out.println("Loaded " + value.name + " leaderboards");
                        }).execute();
            }

            //depending on what player has selected, set visibility
            Warlords.newChain()//newSharedChain(sharedChainName)
                    .delay(10, TimeUnit.SECONDS)
                    .sync(() -> {
                        long endTime = System.nanoTime();
                        long timeToLoad = (endTime - startTime) / 1000000;
//                        System.out.println("Time it took for LB to load (ms): " + timeToLoad);
//                        if (timeToLoad > 25000) {
//                            System.out.println("FART FART FART");
//                            System.out.println("HOLOGRAMS TOOK LONG ASS TIME TO LOAD!?!");
//                            System.out.println("FART FART FART");
//                        }
                        System.out.println("Setting Hologram Visibility");
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            setLeaderboardHologramVisibility(player);
                            DatabaseGame.setGameHologramVisibility(player);
                        });
                    }).execute();

        }
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (!playerLeaderboardHolograms.containsKey(player.getUniqueId()) || playerLeaderboardHolograms.get(player.getUniqueId()) == null) {
            playerLeaderboardHolograms.put(player.getUniqueId(), 0);
        }
        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());

        lifeTimeHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        season5Holograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        season4Holograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        weeklyHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));
        dailyHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.HIDDEN));

        if (selectedLeaderboard == 0) { //LIFETIME
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedLeaderboard == 1) { //SEASON 5
            season5Holograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedLeaderboard == 2) { //SEASON 4
            season4Holograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else if (selectedLeaderboard == 3) { //WEEKLY
            weeklyHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        } else { //DAILY
            dailyHolograms.forEach(hologram -> hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE));
        }

        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    private static void createLeaderboardSwitcherHologram(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) && h.getPosition().toLocation().equals(leaderboardSwitchLocation))
                .forEach(Hologram::delete);
        Hologram leaderboardSwitcher = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(leaderboardSwitchLocation);
        leaderboardSwitcher.getLines().appendText(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
        leaderboardSwitcher.getLines().appendText("");

        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
        int lbBefore = getLBBefore(selectedLeaderboard);
        int lbAfter = getLBAfter(selectedLeaderboard);
        ClickableHologramLine beforeLine = leaderboardSwitcher.getLines().appendText(ChatColor.GRAY + hologramOrder[lbBefore]);
        ClickableHologramLine selectedLine = leaderboardSwitcher.getLines().appendText(ChatColor.GREEN + hologramOrder[selectedLeaderboard]);
        ClickableHologramLine afterLine = leaderboardSwitcher.getLines().appendText(ChatColor.GRAY + hologramOrder[lbAfter]);
        beforeLine.setClickListener(p -> {
            playerLeaderboardHolograms.put(player.getUniqueId(), lbBefore);
            setLeaderboardHologramVisibility(p.getPlayer());
        });
        afterLine.setClickListener(p -> {
            playerLeaderboardHolograms.put(player.getUniqueId(), lbAfter);
            setLeaderboardHologramVisibility(p.getPlayer());
        });


        leaderboardSwitcher.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        leaderboardSwitcher.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
    }

    public static void addPlayerPositionLeaderboards(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        if (enabled) {
            //leaderboards
            for (Leaderboard leaderboard : leaderboards) {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
                HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                        .filter(hologram -> hologram.getPosition().toLocation().equals(location) && hologram.getVisibilitySettings().isVisibleTo(player))
                        .forEach(Hologram::delete);

                Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(location);
                int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
                List<DatabasePlayer> databasePlayers;
                if (selectedLeaderboard == 0) {
                    databasePlayers = leaderboard.getSortedAllTime();
                } else if (selectedLeaderboard == 1) {
                    databasePlayers = leaderboard.getSortedSeason5();
                } else if (selectedLeaderboard == 2) {
                    databasePlayers = leaderboard.getSortedSeason4();
                } else if (selectedLeaderboard == 3) {
                    if (Arrays.stream(weeklyIncludedLeaderboardsTitles).noneMatch(l -> l.equals(leaderboard.getTitle()))) {
                        continue;
                    }
                    databasePlayers = leaderboard.getSortedWeekly();
                } else if (selectedLeaderboard == 4) {
                    if (Arrays.stream(weeklyIncludedLeaderboardsTitles).noneMatch(l -> l.equals(leaderboard.getTitle()))) {
                        continue;
                    }
                    databasePlayers = leaderboard.getSortedDaily();
                } else {
                    return;
                }
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId().toString())) {
                        hologram.getLines().appendText(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + leaderboard.getStringFunction().apply(databasePlayer));
                        break;
                    }
                }
                hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
                hologram.getVisibilitySettings().setIndividualVisibility(player, VisibilitySettings.Visibility.VISIBLE);
            }
        }
    }

    public static void removePlayerSpecificHolograms(Player player) {
        if (!Warlords.holographicDisplaysEnabled) return;
        leaderboards.forEach(leaderboard -> {
            Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
            HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                    .filter(hologram -> hologram.getPosition().toLocation().equals(location) && hologram.getVisibilitySettings().isVisibleTo(player))
                    .forEach(Hologram::delete);
        });
        HolographicDisplaysAPI.get(Warlords.getInstance()).getHolograms().stream()
                .filter(h -> h.getVisibilitySettings().isVisibleTo(player) && (h.getPosition().toLocation().equals(DatabaseGame.gameSwitchLocation) || h.getPosition().toLocation().equals(leaderboardSwitchLocation)))
                .forEach(Hologram::delete);
    }

    private static Hologram addLeaderboard(Leaderboard leaderboard, PlayersCollections collections, String title) {
        List<DatabasePlayer> databasePlayers = leaderboard.getSortedPlayers(collections);
        List<String> hologramLines = new ArrayList<>();
        for (int i = 0; i < 10 && i < databasePlayers.size(); i++) {
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + databasePlayer.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + leaderboard.getStringFunction().apply(databasePlayer));
        }
        return createLeaderboard(leaderboard, title, hologramLines);
    }

    private static Hologram createLeaderboard(Leaderboard leaderboard, String title, List<String> hologramLines) {
        Hologram hologram = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(leaderboard.getLocation());
        hologram.getLines().appendText(title);
        hologram.getLines().appendText("");
        for (String line : hologramLines) {
            hologram.getLines().appendText(line);
        }

        hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        return hologram;
    }

    public static Document getTopPlayersOnLeaderboard() {
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

    public static int getLBBefore(int currentLB) {
        if (currentLB <= 0) {
            return 4;
        }
        return currentLB - 1;
    }

    private static int getLBAfter(int currentLB) {
        if (currentLB >= 4) {
            return 0;
        }
        return currentLB + 1;
    }
}
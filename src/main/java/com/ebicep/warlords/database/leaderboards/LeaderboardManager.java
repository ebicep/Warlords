package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.NumberFormat;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

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
        if (Warlords.holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(Warlords.getInstance()).forEach(hologram -> {
                Location hologramLocation = hologram.getLocation();
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
                leaderboards.forEach(leaderboard -> {
                    //LIFETIME
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.ALL_TIME);
                                databasePlayers.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                return databasePlayers;
                            })
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.ALL_TIME);
                                //creating leaderboard for lifetime
                                addLeaderboard(leaderboard, PlayersCollections.ALL_TIME, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(lifeTimeHolograms::add);
                            }).execute();
                    //SEASON 5
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5);
                                databasePlayers.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                return databasePlayers;
                            })
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.SEASON_5);
                                //creating leaderboard for season 5
                                addLeaderboard(leaderboard, PlayersCollections.SEASON_5, ChatColor.AQUA + ChatColor.BOLD.toString() + "Season 5 " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> !lifeTimeHolograms.contains(h) && h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(season5Holograms::add);
                            }).execute();
                    //SEASON 4
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_4);
                                databasePlayers.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                return databasePlayers;
                            })
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.SEASON_4);
                                //creating leaderboard for season 4
                                addLeaderboard(leaderboard, PlayersCollections.SEASON_4, ChatColor.AQUA + ChatColor.BOLD.toString() + "Season 4 " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> !lifeTimeHolograms.contains(h) && !season5Holograms.contains(h) && h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(season4Holograms::add);
                            }).execute();
                    //WEEKLY
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.WEEKLY);
                                databasePlayers.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                return databasePlayers;
                            })
                            .abortIfNull()
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.WEEKLY);
                                //creating leaderboard for weekly
                                if (Arrays.stream(weeklyIncludedLeaderboardsTitles).anyMatch(title -> title.equalsIgnoreCase(leaderboard.getTitle()))) {
                                    addLeaderboard(leaderboard, PlayersCollections.WEEKLY, ChatColor.AQUA + ChatColor.BOLD.toString() + "Weekly " + leaderboard.getTitle());
                                    HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                            .filter(h -> !lifeTimeHolograms.contains(h) && !season5Holograms.contains(h) && !season4Holograms.contains(h) && h.getLocation().equals(leaderboard.getLocation()))
                                            .forEach(weeklyHolograms::add);
                                }
                            }).execute();
                    //DAILY
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.DAILY);
                                databasePlayers.sort((o1, o2) -> Leaderboard.compare(leaderboard.getValueFunction().apply(o2), leaderboard.getValueFunction().apply(o1)));
                                return databasePlayers;
                            })
                            .abortIfNull()
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.DAILY);
                                //creating leaderboard for daily
                                if (Arrays.stream(weeklyIncludedLeaderboardsTitles).anyMatch(title -> title.equalsIgnoreCase(leaderboard.getTitle()))) {
                                    addLeaderboard(leaderboard, PlayersCollections.DAILY, ChatColor.AQUA + ChatColor.BOLD.toString() + "Daily " + leaderboard.getTitle());
                                    HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                            .filter(h -> !lifeTimeHolograms.contains(h) && !season5Holograms.contains(h) && !weeklyHolograms.contains(h) && h.getLocation().equals(leaderboard.getLocation()))
                                            .forEach(dailyHolograms::add);
                                }
                            }).execute();
                });

                //depending on what player has selected, set visibility
                Warlords.newSharedChain(sharedChainName).sync(() -> {
                    long endTime = System.nanoTime();
                    System.out.println((endTime - startTime) / 1000000);
                    System.out.println("Setting Hologram Visibility");
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        setLeaderboardHologramVisibility(player);
                        DatabaseGame.setGameHologramVisibility(player);
                    });
                }).execute();

            }
        }
    }

    public static void setLeaderboardHologramVisibility(Player player) {
        if (!playerLeaderboardHolograms.containsKey(player.getUniqueId()) || playerLeaderboardHolograms.get(player.getUniqueId()) == null) {
            playerLeaderboardHolograms.put(player.getUniqueId(), 0);
        }
        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
        if (selectedLeaderboard == 0) { //LIFETIME
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            season5Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season4Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            dailyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
        } else if (selectedLeaderboard == 1) { //SEASON 5
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season5Holograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            season4Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            dailyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
        } else if (selectedLeaderboard == 2) { //SEASON 4
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season5Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season4Holograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            dailyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
        } else if (selectedLeaderboard == 3) { //WEEKLY
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season5Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season4Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            dailyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
        } else { //DAILY
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season5Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            season4Holograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            dailyHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
        }

        createLeaderboardSwitcherHologram(player);
        addPlayerPositionLeaderboards(player);
    }

    private static void createLeaderboardSwitcherHologram(Player player) {
        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(h -> h.getVisibilityManager().isVisibleTo(player) && h.getLocation().equals(leaderboardSwitchLocation))
                .forEach(Hologram::delete);
        Hologram leaderboardSwitcher = HologramsAPI.createHologram(Warlords.getInstance(), leaderboardSwitchLocation);
        leaderboardSwitcher.appendTextLine(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
        leaderboardSwitcher.appendTextLine("");

        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
        int lbBefore = getLBBefore(selectedLeaderboard);
        int lbAfter = getLBAfter(selectedLeaderboard);
        TextLine beforeLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + hologramOrder[lbBefore]);
        TextLine selectedLine = leaderboardSwitcher.appendTextLine(ChatColor.GREEN + hologramOrder[selectedLeaderboard]);
        TextLine afterLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + hologramOrder[lbAfter]);
        beforeLine.setTouchHandler(p -> {
            playerLeaderboardHolograms.put(player.getUniqueId(), lbBefore);
            setLeaderboardHologramVisibility(p);
        });
        afterLine.setTouchHandler(p -> {
            playerLeaderboardHolograms.put(player.getUniqueId(), lbAfter);
            setLeaderboardHologramVisibility(p);
        });


        leaderboardSwitcher.getVisibilityManager().setVisibleByDefault(false);
        leaderboardSwitcher.getVisibilityManager().showTo(player);
    }

    public static void addPlayerPositionLeaderboards(Player player) {
        if (enabled) {
            //leaderboards
            for (Leaderboard leaderboard : leaderboards) {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                        .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
                        .forEach(Hologram::delete);

                Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
                List<DatabasePlayer> databasePlayers;
                if (playerLeaderboardHolograms.get(player.getUniqueId()) == 0) {
                    databasePlayers = leaderboard.getSortedAllTime();
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 1) {
                    databasePlayers = leaderboard.getSortedSeason5();
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 2) {
                    databasePlayers = leaderboard.getSortedSeason4();
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 3) {
                    if (Arrays.stream(weeklyIncludedLeaderboardsTitles).noneMatch(l -> l.equals(leaderboard.getTitle()))) {
                        continue;
                    }
                    databasePlayers = leaderboard.getSortedWeekly();
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 4) {
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
                        hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + leaderboard.getStringFunction().apply(databasePlayer));
                        break;
                    }
                }
                hologram.getVisibilityManager().setVisibleByDefault(false);
                hologram.getVisibilityManager().showTo(player);
            }
        }
    }

    public static void removePlayerSpecificHolograms(Player player) {
        leaderboards.forEach(leaderboard -> {
            Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
            HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                    .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
                    .forEach(Hologram::delete);
        });
        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(h -> h.getVisibilityManager().isVisibleTo(player) && (h.getLocation().equals(DatabaseGame.gameSwitchLocation) || h.getLocation().equals(leaderboardSwitchLocation)))
                .forEach(Hologram::delete);
    }

    private static void addLeaderboard(Leaderboard leaderboard, PlayersCollections collections, String title) {
        List<DatabasePlayer> databasePlayers = leaderboard.getSortedPlayers(collections);
        List<String> hologramLines = new ArrayList<>();
        for (int i = 0; i < 10 && i < databasePlayers.size(); i++) {
            DatabasePlayer databasePlayer = databasePlayers.get(i);
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + databasePlayer.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + leaderboard.getStringFunction().apply(databasePlayer));
        }
        createLeaderboard(leaderboard, title, hologramLines);
    }

    private static Hologram createLeaderboard(Leaderboard leaderboard, String title, List<String> hologramLines) {
        Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), leaderboard.getLocation());
        hologram.appendTextLine(title);
        hologram.appendTextLine("");
        for (String line : hologramLines) {
            hologram.appendTextLine(line);
        }

        hologram.getVisibilityManager().setVisibleByDefault(false);
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
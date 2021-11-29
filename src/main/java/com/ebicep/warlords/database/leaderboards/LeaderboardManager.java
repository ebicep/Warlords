package com.ebicep.warlords.database.leaderboards;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.DatabasePlayer;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;

import static com.mongodb.client.model.Sorts.descending;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class LeaderboardManager {

    public static final World world = Bukkit.getWorld("MainLobby");

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();

    public static final Location leaderboardSwitchLocation = new Location(world, -2552.5, 52.5, 719.5);

    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final List<Leaderboard> leaderboards = new ArrayList<>();
    
    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
    public static final HashMap<UUID, Integer> playerLeaderboardHolograms = new HashMap<>();

    public static final List<Hologram> lifeTimeHolograms = new ArrayList<>();
    public static final List<Hologram> weeklyHolograms = new ArrayList<>();

    public static boolean enabled = true;

    public static void init() {
        putLeaderboards();
    }

    public static void putLeaderboards() {
        leaderboards.clear();

        leaderboards.add(new Leaderboard("Wins",
                new Location(world, -2558.5, 56, 712.5),
                Aggregation.newAggregation(sort(DESC, "wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWins())));
        leaderboards.add(new Leaderboard("Losses", new Location(world, -2608.5, 52, 728.5),
                Aggregation.newAggregation(sort(DESC, "losses")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getLosses())));
        leaderboards.add(new Leaderboard("Kills", new Location(world, -2552.5, 56, 712.5),
                Aggregation.newAggregation(sort(DESC, "kills")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getKills())));
        leaderboards.add(new Leaderboard("Assists", new Location(world, -2616.5, 52, 733.5),
                Aggregation.newAggregation(sort(DESC, "assists")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getAssists())));
        leaderboards.add(new Leaderboard("Deaths", new Location(world, -2616.5, 52, 723.5),
                Aggregation.newAggregation(sort(DESC, "deaths")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getDeaths())));
        leaderboards.add(new Leaderboard("Damage", new Location(world, -2600.5, 52, 723.5),
                Aggregation.newAggregation(sort(DESC, "damage")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getDamage())));
        leaderboards.add(new Leaderboard("Healing", new Location(world, -2608.5, 52, 719.5),
                Aggregation.newAggregation(sort(DESC, "healing")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getHealing())));
        leaderboards.add(new Leaderboard("Absorbed", new Location(world, -2600.5, 52, 733.5),
                Aggregation.newAggregation(sort(DESC, "absorbed")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getAbsorbed())));

        leaderboards.add(new Leaderboard("Flags Captured", new Location(world, -2540.5, 56, 712.5),
                Aggregation.newAggregation(sort(DESC, "flags_captured")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getFlagsCaptured())));
        leaderboards.add(new Leaderboard("Flags Returned", new Location(world, -2608.5, 52, 737.5),
                Aggregation.newAggregation(sort(DESC, "flags_returned")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getFlagsReturned())));

        leaderboards.add(new Leaderboard("Avenger Wins", new Location(world, -2631.5, 52, 719.5),
                Aggregation.newAggregation(sort(DESC, "absorbed")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getPaladin().getAvenger().getWins())));
        leaderboards.add(new Leaderboard("Crusader Wins", new Location(world, -2628.5, 52, 714.5),
                Aggregation.newAggregation(sort(DESC, "paladin.crusader.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getPaladin().getCrusader().getWins())));
        leaderboards.add(new Leaderboard("Protector Wins", new Location(world, -2623.5, 52, 711.5),
                Aggregation.newAggregation(sort(DESC, "paladin.protector.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getPaladin().getProtector().getWins())));
        leaderboards.add(new Leaderboard("Berserker Wins", new Location(world, -2623.5, 52, 745.5),
                Aggregation.newAggregation(sort(DESC, "warrior.berserker.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWarrior().getBerserker().getWins())));
        leaderboards.add(new Leaderboard("Defender Wins", new Location(world, -2628.5, 52, 742.5),
                Aggregation.newAggregation(sort(DESC, "warrior.defender.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWarrior().getDefender().getWins())));
        leaderboards.add(new Leaderboard("Revenant Wins", new Location(world, -2631.5, 52, 737.5),
                Aggregation.newAggregation(sort(DESC, "warrior.revenant.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWarrior().getRevenant().getWins())));
        leaderboards.add(new Leaderboard("Pyromancer Wins", new Location(world, -2602.5, 53, 749.5),
                Aggregation.newAggregation(sort(DESC, "mage.pyromancer.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getMage().getPyromancer().getWins())));
        leaderboards.add(new Leaderboard("Cryomancer Wins", new Location(world, -2608.5, 53, 752.5),
                Aggregation.newAggregation(sort(DESC, "mage.cryomancer.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getMage().getCryomancer().getWins())));
        leaderboards.add(new Leaderboard("Aquamancer Wins", new Location(world, -2614.5, 53, 749.5),
                Aggregation.newAggregation(sort(DESC, "mage.aquamancer.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getMage().getAquamancer().getWins())));
        leaderboards.add(new Leaderboard("Thunderlord Wins", new Location(world, -2614.5, 52, 706.5),
                Aggregation.newAggregation(sort(DESC, "shaman.thunderlord.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getShaman().getThunderlord().getWins())));
        leaderboards.add(new Leaderboard("Spiritguard Wins", new Location(world, -2608.5, 52, 704.5),
                Aggregation.newAggregation(sort(DESC, "shaman.spiritguard.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getShaman().getSpiritguard().getWins())));
        leaderboards.add(new Leaderboard("Earthwarden Wins", new Location(world, -2602.5, 52, 706.5),
                Aggregation.newAggregation(sort(DESC, "shaman.earthwarden.wins")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getShaman().getEarthwarden().getWins())));

        leaderboards.add(new Leaderboard("Experience", new Location(world, -2526.5, 57, 744.5),
                Aggregation.newAggregation(sort(DESC, "experience")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getExperience())));
        leaderboards.add(new Leaderboard("Mage Experience", new Location(world, -2520.5, 58, 735.5),
                Aggregation.newAggregation(sort(DESC, "mage.experience")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getMage().getExperience())));
        leaderboards.add(new Leaderboard("Warrior Experience", new Location(world, -2519.5, 58, 741.5),
                Aggregation.newAggregation(sort(DESC, "warrior.experience")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWarrior().getExperience())));
        leaderboards.add(new Leaderboard("Paladin Experience", new Location(world, -2519.5, 58, 747.5),
                Aggregation.newAggregation(sort(DESC, "paladin.experience")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getPaladin().getExperience())));
        leaderboards.add(new Leaderboard("Shaman Experience", new Location(world, -2520.5, 58, 753.5),
                Aggregation.newAggregation(sort(DESC, "shaman.experience")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getShaman().getExperience())));

        leaderboards.add(new Leaderboard("Plays", new Location(world, -2564.5, 56, 712.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("plays", new Document("$add", Arrays.asList("$wins", "$losses")))),
                        sort(DESC, "plays")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getWins() + databasePlayer.getLosses())));
        leaderboards.add(new Leaderboard("DHP", new Location(world, -2619.5, 66.5, 721.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("dhp", new Document("$add", Arrays.asList("$damage", "$healing", "$absorbed")))),
                        sort(DESC, "dhp")),
                databasePlayer -> Utils.addCommaAndRound(databasePlayer.getDamage() + databasePlayer.getHealing() + databasePlayer.getAbsorbed())));
        leaderboards.add(new Leaderboard("DHP Per Game", new Location(world, -2546.5, 56, 712.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("plays", new Document("$add", Arrays.asList("$wins", "$losses")))),
                        match(Criteria.where("plays").gt(5)),
                        aoc -> new Document("$addFields", new Document("dhp", new Document("$add", Arrays.asList("$damage", "$healing", "$absorbed")))),
                        aoc -> new Document("$addFields", new Document("dhp_per_game", new Document("$divide", Arrays.asList("$dhp", "$plays")))),
                        sort(DESC, "dhp_per_game")),
                databasePlayer -> Utils.addCommaAndRound(Math.round((double) (databasePlayer.getDamage() + databasePlayer.getHealing() + databasePlayer.getAbsorbed()) / (databasePlayer.getWins() + databasePlayer.getLosses()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills Per Game", new Location(world, -2619.5, 66.5, 735.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("plays", new Document("$add", Arrays.asList("$wins", "$losses")))),
                        match(Criteria.where("plays").gt(5)),
                        aoc -> new Document("$addFields", new Document("kills_per_game", new Document("$divide", Arrays.asList("$kills", "$plays")))),
                        sort(DESC, "kills_per_game")),
                databasePlayer -> String.valueOf(Math.round((double) databasePlayer.getKills() / (databasePlayer.getWins() + databasePlayer.getLosses()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Deaths Per Game", new Location(world, -2608.5, 67, 738.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("plays", new Document("$add", Arrays.asList("$wins", "$losses")))),
                        match(Criteria.where("plays").gt(5)),
                        aoc -> new Document("$addFields", new Document("deaths_per_game", new Document("$divide", Arrays.asList("$deaths", "$plays")))),
                        sort(DESC, "deaths_per_game")),
                databasePlayer -> String.valueOf(Math.round((double) databasePlayer.getDeaths() / (databasePlayer.getWins() + databasePlayer.getLosses()) * 10) / 10d)));
        leaderboards.add(new Leaderboard("Kills/Assists Per Game", new Location(world, -2608.5, 67, 719.5),
                Aggregation.newAggregation(
                        aoc -> new Document("$addFields", new Document("plays", new Document("$add", Arrays.asList("$wins", "$losses")))),
                        match(Criteria.where("plays").gt(5)),
                        aoc -> new Document("$addFields", new Document("ka", new Document("$add", Arrays.asList("$kills", "$assists")))),
                        aoc -> new Document("$addFields", new Document("ka_per_game", new Document("$divide", Arrays.asList("$ka", "$plays")))),
                        sort(DESC, "ka_per_game")),
                databasePlayer -> String.valueOf(Math.round((double) (databasePlayer.getKills() + databasePlayer.getAssists()) / (databasePlayer.getWins() + databasePlayer.getLosses()) * 10) / 10d)));
    }

    private static final String[] weeklyExcludedLeaderboardsTitles = new String[] {"Plays", "Wins", "Kills", "DHP Per Game", "Flags Captured"};

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
            weeklyHolograms.clear();

            putLeaderboards();
            if (enabled) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");

                //caching all sorted players for each lifetime and weekly
                leaderboards.forEach(leaderboard -> {
                    //LIFETIME
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> DatabaseManager.playerService.getPlayersSorted(leaderboard.getAggregation(), PlayersCollections.ALL_TIME))
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.ALL_TIME);
                                //creating leaderboard for lifetime
                                addLeaderboard(leaderboard, PlayersCollections.ALL_TIME, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(lifeTimeHolograms::add);
                            }).execute();
                    //WEEKLY
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                if(Arrays.stream(weeklyExcludedLeaderboardsTitles).noneMatch(title -> title.equalsIgnoreCase(leaderboard.getTitle()))) {
                                    return null;
                                }
                                return DatabaseManager.playerService.getPlayersSorted(leaderboard.getAggregation(), PlayersCollections.WEEKLY);
                            })
                            .abortIfNull()
                            .syncLast((sortedInformation) -> {
                                leaderboard.resetSortedPlayers(sortedInformation, PlayersCollections.WEEKLY);
                                //creating leaderboard for lifetime
                                addLeaderboard(leaderboard, PlayersCollections.WEEKLY, ChatColor.AQUA + ChatColor.BOLD.toString() + "Weekly " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> !lifeTimeHolograms.contains(h) && h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(weeklyHolograms::add);
                            }).execute();
                });

                //depending on what player has selected, set visibility
                Warlords.newSharedChain(sharedChainName).sync(() -> {
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
        if (selectedLeaderboard == 0) {
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
        } else {
            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
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
        if (selectedLeaderboard == 0) {
            leaderboardSwitcher.appendTextLine(ChatColor.GREEN + "LifeTime");
            TextLine weeklyLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + "Weekly");

            weeklyLine.setTouchHandler(p -> {
                playerLeaderboardHolograms.put(player.getUniqueId(), 1);
                setLeaderboardHologramVisibility(p);
            });
        } else {
            TextLine lifeTimeLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + "LifeTime");
            leaderboardSwitcher.appendTextLine(ChatColor.GREEN + "Weekly");

            lifeTimeLine.setTouchHandler(p -> {
                playerLeaderboardHolograms.put(player.getUniqueId(), 0);
                setLeaderboardHologramVisibility(p);
            });
        }

        leaderboardSwitcher.getVisibilityManager().setVisibleByDefault(false);
        leaderboardSwitcher.getVisibilityManager().showTo(player);
    }

    public static void addPlayerPositionLeaderboards(Player player) {
        if (enabled) {
            //leaderboards
            leaderboards.forEach(leaderboard -> {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                        .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
                        .forEach(Hologram::delete);

                Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
                List<DatabasePlayer> databasePlayers;
                if (playerLeaderboardHolograms.get(player.getUniqueId()) == 0) {
                    databasePlayers = leaderboard.getSortedAllTime();
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 1) {
                    databasePlayers = leaderboard.getSortedWeekly();
                } else {
                    return;
                }
                for (int i = 0; i < databasePlayers.size(); i++) {
                    DatabasePlayer databasePlayer = databasePlayers.get(i);
                    if (databasePlayer.getUuid().equals(player.getUniqueId().toString())) {
                        hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + databasePlayer.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + leaderboard.getValueFunction().apply(databasePlayer));
                        break;
                    }
                }
                hologram.getVisibilityManager().setVisibleByDefault(false);
                hologram.getVisibilityManager().showTo(player);
            });
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
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + databasePlayer.getName() + ChatColor.GRAY + " - " + ChatColor.YELLOW + leaderboard.getValueFunction().apply(databasePlayer));
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

    //    public static Document getTopPlayersOnLeaderboard() {
//        Document document = new Document("date", new Date()).append("total_players", cachedSortedPlayersWeekly.get("wins").size());
//        //leaderboards.keySet().forEach(key -> appendTop(document, key));
//        appendTop(document, "plays");
//        appendTop(document, "dhp");
//        return document;
//    }
//    public static void appendTop(Document document, String key) {
//        Object[] highest = new Object[3];
//        Object total;
//        Object classType = cachedSortedPlayersWeekly.get(key).get(0).get(key);
//        if (classType instanceof Integer || classType instanceof Long) {
//            if (classType instanceof Integer) {
//                int[] highestThreeInt = getHighestThreeInt(key);
//                highest[0] = highestThreeInt[0];
//                highest[1] = highestThreeInt[1];
//                highest[2] = highestThreeInt[2];
//                total = cachedSortedPlayersWeekly.get(key).stream().mapToInt(d -> (Integer) d.getOrDefault(key, 0)).sum();
//            } else {
//                long[] highestThreeLong = getHighestThreeLong(key);
//                highest[0] = highestThreeLong[0];
//                highest[1] = highestThreeLong[1];
//                highest[2] = highestThreeLong[2];
//                total = cachedSortedPlayersWeekly.get(key).stream().mapToLong(d -> (Long) d.getOrDefault(key, 0L)).sum();
//            }
//            List<Document> documentList = new ArrayList<>();
//            String[] highest1 = getHighestPlayers(key, highest[0], cachedSortedPlayersWeekly.get(key));
//            String[] highest2 = getHighestPlayers(key, highest[1], cachedSortedPlayersWeekly.get(key));
//            String[] highest3 = getHighestPlayers(key, highest[2], cachedSortedPlayersWeekly.get(key));
//            documentList.add(new Document("names", highest1[0]).append("uuids", highest1[1]).append("amount", highest[0]));
//            documentList.add(new Document("names", highest2[0]).append("uuids", highest2[1]).append("amount", highest[1]));
//            documentList.add(new Document("names", highest3[0]).append("uuids", highest3[1]).append("amount", highest[2]));
//            //document.append(key, new Document("total", total).append("name", leaderboards.get(key).getTitle()).append("top", documentList));
//        }
//    }
//
//    public static int[] getHighestThreeInt(String key) {
//        return findThreeLargestInt(cachedSortedPlayersWeekly.get(key).stream()
//                .sorted((d1, d2) -> ((Integer) d2.getOrDefault(key, 0)).compareTo(((Integer) d1.getOrDefault(key, 0))))
//                .mapToInt(d -> (Integer) d.getOrDefault(key, 0))
//                .toArray());
//    }
//
//    public static long[] getHighestThreeLong(String key) {
//        return findThreeLargestLong(cachedSortedPlayersWeekly.get(key).stream()
//                .sorted((d1, d2) -> ((Long) d2.getOrDefault(key, 0L)).compareTo(((Long) d1.getOrDefault(key, 0L))))
//                .mapToLong(d -> (Long) d.getOrDefault(key, 0L))
//                .toArray());
//    }
//
//    private static int[] findThreeLargestInt(int[] arr) {
//        int[] output = new int[3];
//
//        Arrays.sort(arr);
//        int n = arr.length;
//        int check = 0, count = 1;
//
//        for (int i = 1; i <= n; i++) {
//            if (count < 4) {
//                if (check != arr[n - i]) {
//                    output[count - 1] = arr[n - i];
//                    check = arr[n - i];
//                    count++;
//                }
//            } else {
//                break;
//            }
//        }
//        return output;
//    }
//
//    private static long[] findThreeLargestLong(long[] arr) {
//        long[] output = new long[3];
//
//        Arrays.sort(arr);
//        int n = arr.length;
//        long check = 0;
//        int count = 1;
//
//        for (int i = 1; i <= n; i++) {
//            if (count < 4) {
//                if (check != arr[n - i]) {
//                    output[count - 1] = arr[n - i];
//                    check = arr[n - i];
//                    count++;
//                }
//            } else {
//                break;
//            }
//        }
//        return output;
//    }
//
//    private static String[] getHighestPlayers(String key, Object highest, List<Document> documentList) {
//        StringBuilder topPlayersName = new StringBuilder();
//        StringBuilder topPlayersUUID = new StringBuilder();
//        for (Document document : documentList) {
//            if (document.get(key) != null && document.get(key).equals(highest)) {
//                topPlayersName.append(document.get("name")).append(",");
//                topPlayersUUID.append(document.get("uuid")).append(",");
//            }
//        }
//        if (topPlayersName.length() > 0) {
//            topPlayersName.setLength(topPlayersName.length() - 1);
//        }
//        if (topPlayersUUID.length() > 0) {
//            topPlayersUUID.setLength(topPlayersUUID.length() - 1);
//        }
//        return new String[]{topPlayersName.toString(), topPlayersUUID.toString()};
//    }

}
package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.descending;

public class LeaderboardRanking {

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();
    public static final Location lifeTimeWinsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(4.5f).addY(3.5).get();
    public static final Location lifeTimeKillsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(1f).addY(3.5).get();
    public static final Location srLB = new LocationBuilder(spawnPoint.clone()).backward(27).left(5f).addY(3.5).get();
    public static final Location srLBMage = new LocationBuilder(spawnPoint.clone()).backward(27).left(9.5f).addY(3.5).get();
    public static final Location srLBWarrior = new LocationBuilder(spawnPoint.clone()).backward(27).left(14f).addY(3.5).get();
    public static final Location srLBPaladin = new LocationBuilder(spawnPoint.clone()).backward(27).left(18.5f).addY(3.5).get();
    public static final Location srLBShaman = new LocationBuilder(spawnPoint.clone()).backward(27).left(23f).addY(3.5).get();
    public static final Location lastGameLocation = new LocationBuilder(spawnPoint.clone()).forward(29).left(16).addY(3.5).get();
    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final HashMap<String, Location> leaderboardLocations = new HashMap<>();
    public static final HashMap<String, List<Document>> cachedSortedPlayers = new HashMap<>();
    public static final HashMap<String, HashMap<Document, Integer>> cachedSR = new HashMap<>();

    public LeaderboardRanking() {
        leaderboardLocations.put("wins", lifeTimeWinsLB);
        leaderboardLocations.put("kills", lifeTimeKillsLB);
        leaderboardLocations.put("", srLB);
        leaderboardLocations.put("mage", srLBMage);
        leaderboardLocations.put("warrior", srLBWarrior);
        leaderboardLocations.put("paladin", srLBPaladin);
        leaderboardLocations.put("shaman", srLBShaman);
    }

    public static void addHologramLeaderboards() {
        if (DatabaseManager.connected && Warlords.holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(Warlords.getInstance()).forEach(Hologram::delete);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");

            addLeaderboard("wins", lifeTimeWinsLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime Wins");
            addLeaderboard("kills", lifeTimeKillsLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime Kills");

            addLeaderboardSR("", srLB, ChatColor.AQUA + ChatColor.BOLD.toString() + "SR Ranking");
            addLeaderboardSR("mage", srLBMage, ChatColor.AQUA + ChatColor.BOLD.toString() + "Mage SR Ranking");
            addLeaderboardSR("warrior", srLBWarrior, ChatColor.AQUA + ChatColor.BOLD.toString() + "Warrior SR Ranking");
            addLeaderboardSR("paladin", srLBPaladin, ChatColor.AQUA + ChatColor.BOLD.toString() + "Paladin SR Ranking");
            addLeaderboardSR("shaman", srLBShaman, ChatColor.AQUA + ChatColor.BOLD.toString() + "Shaman SR Ranking");

            DatabaseManager.addLastGameHologram(lastGameLocation);

            new BukkitRunnable() {

                @Override
                public void run() {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding player leaderboards");
                    addPlayerLeaderboardsToAll();
                }
            }.runTaskLater(Warlords.getInstance(), 20 * 13);
        }
    }

    public static void addPlayerLeaderboards(Player player) {
        leaderboardLocations.forEach((key, loc) -> {
            Location location = loc.clone().add(0, -3.5, 0);
            HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                    .filter(hologram -> hologram.getLocation().equals(location))
                    .filter(hologram -> hologram.getVisibilityManager().isVisibleTo(player))
                    .forEach(Hologram::delete);
            if(key.equals("wins") || key.equals("kills")) {
                List<Document> documents = cachedSortedPlayers.get(key);
                Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
                for (int i = 0; i < documents.size(); i++) {
                    Document document = documents.get(i);
                    if (document.get("uuid").equals(player.getUniqueId().toString())) {
                        hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound((Integer) document.get(key))));
                        break;
                    }
                }
                hologram.getVisibilityManager().showTo(player);
                hologram.getVisibilityManager().setVisibleByDefault(false);
            } else {
                HashMap<Document, Integer> documentIntegerHashMap = cachedSR.get(key);
                List<Document> top = getDocumentInSortedList(documentIntegerHashMap);
                Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
                for (int i = 0; i < top.size(); i++) {
                    Document document = top.get(i);
                    if(document.get("uuid").equals(player.getUniqueId().toString())) {
                        hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound(documentIntegerHashMap.get(document))));
                        break;
                    }
                }
                hologram.getVisibilityManager().showTo(player);
                hologram.getVisibilityManager().setVisibleByDefault(false);
            }
        });
    }

    public static void addPlayerLeaderboardsToAll() {
        Bukkit.getOnlinePlayers().forEach(LeaderboardRanking::addPlayerLeaderboards);
    }

    private static void addLeaderboard(String key, Location location, String title) {
        Warlords.newChain()
                .asyncFirst(() -> getPlayersSortedByKey(key))
                .abortIfNull()
                .syncLast((top) -> {
                    cachedSortedPlayers.put(key, top);
                    List<String> hologramLines = new ArrayList<>();
                    for (int i = 0; i < 10 && i < top.size(); i++) {
                        Document player = top.get(i);
                        hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + player.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Integer) player.get(key))));
                    }
                    createLeaderboard(location, title, hologramLines);
                })
                .execute();
    }

    private static void addLeaderboardSR(String key, Location location, String title) {
        Warlords.newChain()
                .asyncFirst(() -> getPlayersSortedBySR(key))
                .abortIfNull()
                .syncLast((top) -> {
                    cachedSR.put(key, top);
                    createLeaderboard(location, title, getHologramLines(top));
                })
                .execute();
    }

    private static List<String> getHologramLines(HashMap<Document, Integer> players) {
        List<Document> sorted = getDocumentInSortedList(players);
        List<String> hologramLines = new ArrayList<>();
        for (int i = 0; i < 10 && i < sorted.size(); i++) {
            Document player = sorted.get(i);
            hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + player.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound(players.get(player))));
        }
        return hologramLines;
    }

    private static List<Document> getDocumentInSortedList(HashMap<Document, Integer> map) {
        List<Document> sorted = new ArrayList<>();
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(documentIntegerEntry -> sorted.add(documentIntegerEntry.getKey()));
        Collections.reverse(sorted);
        return sorted;
    }

    public static Hologram createLeaderboard(Location location, String title, List<String> lines) {
        Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
        hologram.appendTextLine(title);
        hologram.appendTextLine("");
        for (String line : lines) {
            hologram.appendTextLine(line);
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Created Hologram - " + title);
        return hologram;
    }

    public static int getSR(Player player) {
        return getSR(player.getUniqueId());
    }

    public static int getSR(UUID uuid) {
        double dhp = averageAdjustedDHP(uuid, "") * 2000;
        double wl = averageAdjustedWL(uuid, "") * 2000;
        double kda = averageAdjustedKDA(uuid, "") * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    public static int getSRClass(Player player, String optionalClass) {
        return getSRClass(player.getUniqueId(), optionalClass);
    }

    public static int getSRClass(UUID uuid, String optionalClass) {
        double dhp = averageAdjustedDHP(uuid, optionalClass) * 2000;
        double wl = averageAdjustedWL(uuid, optionalClass) * 2000;
        double kda = averageAdjustedKDA(uuid, optionalClass) * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    private static double averageAdjusted(long playerAverage, long total) {
        double average = playerAverage / ((total / (double) DatabaseManager.playersInformation.countDocuments()));
        if (average >= 5) return 1;
        if (average <= 0) return 0;
        return 1.00699 + (-1.02107 / (1.01398 + Math.pow(average, 3.09248)));
    }

    private static double averageAdjustedDHP(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerDHP = (Long) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "damage") + (Long) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "healing") + (Long) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "absorbed");
        long totalDHP = DatabaseManager.getPlayerTotalKey(optionalClass + "damage") + DatabaseManager.getPlayerTotalKey(optionalClass + "healing") + DatabaseManager.getPlayerTotalKey(optionalClass + "absorbed");
        return averageAdjusted(playerDHP, totalDHP);
    }

    private static double averageAdjustedWL(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerWL = (Integer) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "wins") / Math.max((Integer) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "losses"), 1);
        long totalWL = DatabaseManager.getPlayerTotalKey(optionalClass + "wins") / Math.max(DatabaseManager.getPlayerTotalKey(optionalClass + "losses"), 1);
        return averageAdjusted(playerWL, totalWL);
    }

    private static double averageAdjustedKDA(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerDHP = ((Integer) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "kills") + (Integer) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "assists")) / Math.max((Integer) DatabaseManager.getPlayerInfoWithDotNotation(uuid, optionalClass + "deaths"), 1);
        long totalDHP = (DatabaseManager.getPlayerTotalKey(optionalClass + "kills") + DatabaseManager.getPlayerTotalKey(optionalClass + "assists")) / Math.max(DatabaseManager.getPlayerTotalKey(optionalClass + "deaths"), 1);
        return averageAdjusted(playerDHP, totalDHP);
    }

    public static HashMap<Document, Integer> getPlayersSortedBySR(String optionalClass) {
        if (!DatabaseManager.connected) return null;
        try {
            HashMap<Document, Integer> playersSr = new HashMap<>();
            for (Document document : DatabaseManager.playersInformation.find()) {
                playersSr.put(document, getSRClass(UUID.fromString((String) document.get("uuid")), optionalClass));
            }
            return playersSr;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Problem getting players sorted by sr");
            return null;
        }
    }

    public static List<Document> getPlayersSortedByKey(String key) {
        if (!DatabaseManager.connected) return null;
        try {
            return Lists.newArrayList(DatabaseManager.playersInformation.aggregate(Collections.singletonList(sort(descending(key)))));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Problem getting " + key);
            return null;
        }
    }

}

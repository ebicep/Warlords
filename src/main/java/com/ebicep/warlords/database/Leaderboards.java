package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ebicep.warlords.database.DatabaseManager.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

public class Leaderboards {

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();

    public static final Location winsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(4.5f).addY(3.5).get();
    public static final Location lossesLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(.5f).addY(3.5).get();

    public static final Location killsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-3.5f).addY(3.5).get();
    public static final Location assistsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-7.5f).addY(3.5).get();
    public static final Location deathsLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-11.5f).addY(3.5).get();

    public static final Location damageLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-16f).addY(3.5).get();
    public static final Location healingLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-20.5f).addY(3.5).get();
    public static final Location absorbedLB = new LocationBuilder(spawnPoint.clone()).backward(27).right(-25f).addY(3.5).get();

    public static final Location leaderboardSwitchLocation = new LocationBuilder(spawnPoint.clone()).backward(23).right(-28f).addY(.5f).get();

    public static final Location lastGameLocation = new LocationBuilder(spawnPoint.clone()).forward(28.5f).left(16.5f).addY(3.5).get();
    public static final Location gameSwitchLocation = new LocationBuilder(lastGameLocation.clone()).backward(3).left(9.5f).addY(-2).get();
    public static final List<Location> gameHologramLocations = new ArrayList<>();

    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final HashMap<String, Location> leaderboardLocations = new HashMap<>();
    public static final HashMap<String, List<Document>> cachedSortedPlayersLifeTime = new HashMap<>();
    public static final HashMap<String, List<Document>> cachedSortedPlayersWeekly = new HashMap<>();

    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
    public static final HashMap<UUID, Integer> playerLeaderboardHolograms = new HashMap<>();

    public static final List<Hologram> lifeTimeHolograms = new ArrayList<>();
    public static final List<Hologram> weeklyHolograms = new ArrayList<>();

    public static boolean enabled = true;

    public static void init() {
        leaderboardLocations.put("wins", winsLB);
        leaderboardLocations.put("losses", lossesLB);
        leaderboardLocations.put("kills", killsLB);
        leaderboardLocations.put("assists", assistsLB);
        leaderboardLocations.put("deaths", deathsLB);
        leaderboardLocations.put("damage", damageLB);
        leaderboardLocations.put("healing", healingLB);
        leaderboardLocations.put("absorbed", absorbedLB);

        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).left(5).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(.5f).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(4).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(7.5f).get());

    }

    public static Document getTopPlayersOnLeaderboard() {
        Document document = new Document("date", new Date()).append("total_players", cachedSortedPlayersWeekly.get("wins").size());
        leaderboardLocations.keySet().forEach(key -> appendTop(document, key));
        appendTop(document, "plays");
        appendTop(document, "dhp");
        return document;
    }

    private static void addCalculatedStatsToWeeklyCache() {
        List<Document> plays = new ArrayList<>();
        for (Document d : cachedSortedPlayersWeekly.get("wins")) {
            int currentTop = 0;
            currentTop += d.getInteger("wins");
            currentTop += d.getInteger("losses");
            plays.add(new Document("name", d.get("name")).append("plays", currentTop));
        }

        List<Document> dhp = new ArrayList<>();
        for (Document d : cachedSortedPlayersWeekly.get("wins")) {
            long currentTop = 0;
            currentTop += d.getLong("damage");
            currentTop += d.getLong("healing");
            currentTop += d.getLong("absorbed");
            dhp.add(new Document("name", d.get("name")).append("dhp", currentTop));
        }

        cachedSortedPlayersWeekly.put("plays", plays);
        cachedSortedPlayersWeekly.put("dhp", dhp);
    }

    public static void appendTop(Document document, String key) {
        Object[] highest = new Object[3];
        Object total;
        if (key.equals("wins") || key.equals("losses") || key.equals("kills") || key.equals("assists") || key.equals("deaths") || key.equals("plays")) {
            int[] highestThreeInt = getHighestThreeInt(key);
            highest[0] = highestThreeInt[0];
            highest[1] = highestThreeInt[1];
            highest[2] = highestThreeInt[2];
            total = cachedSortedPlayersWeekly.get(key).stream().mapToInt(d -> d.getInteger(key)).sum();
        } else {
            long[] highestThreeLong = getHighestThreeLong(key);
            highest[0] = highestThreeLong[0];
            highest[1] = highestThreeLong[1];
            highest[2] = highestThreeLong[2];
            total = cachedSortedPlayersWeekly.get(key).stream().mapToLong(d -> d.getLong(key)).sum();
        }
        List<Document> documentList = new ArrayList<>();
        documentList.add(new Document("players", getHighestPlayers(key, highest[0], cachedSortedPlayersWeekly.get(key))).append("amount", highest[0]));
        documentList.add(new Document("players", getHighestPlayers(key, highest[1], cachedSortedPlayersWeekly.get(key))).append("amount", highest[1]));
        documentList.add(new Document("players", getHighestPlayers(key, highest[2], cachedSortedPlayersWeekly.get(key))).append("amount", highest[2]));
        document.append(key, new Document("total", total).append("top", documentList));
    }

    public static int[] getHighestThreeInt(String key) {
        return findThreeLargestInt(cachedSortedPlayersWeekly.get(key).stream()
                .sorted((d1, d2) -> d2.getInteger(key).compareTo(d1.getInteger(key)))
                .mapToInt(d -> d.getInteger(key))
                .toArray());
    }

    public static long[] getHighestThreeLong(String key) {
        return findThreeLargestLong(cachedSortedPlayersWeekly.get(key).stream()
                .sorted((d1, d2) -> d2.getLong(key).compareTo(d1.getLong(key)))
                .mapToLong(d -> d.getLong(key))
                .toArray());
    }

    private static int[] findThreeLargestInt(int[] arr) {
        int[] output = new int[3];

        Arrays.sort(arr);
        int n = arr.length;
        int check = 0, count = 1;

        for (int i = 1; i <= n; i++) {
            if (count < 4) {
                if (check != arr[n - i]) {
                    output[count - 1] = arr[n - i];
                    check = arr[n - i];
                    count++;
                }
            } else {
                break;
            }
        }
        return output;
    }

    private static long[] findThreeLargestLong(long[] arr) {
        long[] output = new long[3];

        Arrays.sort(arr);
        int n = arr.length;
        long check = 0;
        int count = 1;

        for (int i = 1; i <= n; i++) {
            if (count < 4) {
                if (check != arr[n - i]) {
                    output[count - 1] = arr[n - i];
                    check = arr[n - i];
                    count++;
                }
            } else {
                break;
            }
        }
        return output;
    }

    private static String getHighestPlayers(String key, Object highest, List<Document> documentList) {
        StringBuilder topPlayers = new StringBuilder();
        for (Document document : documentList) {
            if (document.get(key).equals(highest)) {
                topPlayers.append(document.get("name")).append(",");
            }
        }
        if (topPlayers.length() > 0) {
            topPlayers.setLength(topPlayers.length() - 1);
        }
        return topPlayers.toString();
    }

    public static void addHologramLeaderboards(String sharedChainName) {
        if (DatabaseManager.connected && Warlords.holographicDisplaysEnabled) {
            HologramsAPI.getHolograms(Warlords.getInstance()).forEach(hologram -> {
                if (!gameHologramLocations.contains(hologram.getLocation())) {
                    hologram.delete();
                }
            });
            lifeTimeHolograms.clear();
            weeklyHolograms.clear();
            if (enabled) {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");
                //caching all sorted players for each lifetime and weekly
                leaderboardLocations.forEach((s, location) -> {
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> getPlayersSortedByKey(s, 0))
                            .syncLast((sortedInformation) -> {
                                cachedSortedPlayersLifeTime.put(s, sortedInformation);

                                //creating leaderboard for lifetime
                                addLeaderboard(cachedSortedPlayersLifeTime.get(s), s, location, ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime " + s.substring(0, 1).toUpperCase() + s.substring(1));                //default making lifetime leaderboard visible to everyone
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(location))
                                        .forEach(lifeTimeHolograms::add);
                            }).execute();
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> getPlayersSortedByKey(s, 1))
                            .syncLast((sortedInformation) -> {
                                cachedSortedPlayersWeekly.put(s, sortedInformation);

                                //creating leaderboard for weekly
                                addLeaderboard(cachedSortedPlayersWeekly.get(s), s, location, ChatColor.AQUA + ChatColor.BOLD.toString() + "Weekly " + s.substring(0, 1).toUpperCase() + s.substring(1));
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(location) && !lifeTimeHolograms.contains(h))
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

                Warlords.newSharedChain(sharedChainName).sync(Leaderboards::addCalculatedStatsToWeeklyCache).execute();
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
            leaderboardLocations.forEach((key, loc) -> {
                Location location = loc.clone().add(0, -3.5, 0);
                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                        .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
                        .forEach(Hologram::delete);

                List<Document> documents;
                if (playerLeaderboardHolograms.get(player.getUniqueId()) == 0 && cachedSortedPlayersLifeTime.containsKey(key)) {
                    documents = cachedSortedPlayersLifeTime.get(key);
                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 1 && cachedSortedPlayersWeekly.containsKey(key)) {
                    documents = cachedSortedPlayersWeekly.get(key);
                } else {
                    return;
                }

                Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
                for (int i = 0; i < documents.size(); i++) {
                    Document document = documents.get(i);
                    if (document.get("uuid").equals(player.getUniqueId().toString())) {
                        Object docKey = document.get(key);
                        if (docKey instanceof Integer) {
                            hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound((Integer) docKey)));
                        } else if (docKey instanceof Long) {
                            hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound((Long) docKey)));
                        }
                        break;
                    }
                }
                hologram.getVisibilityManager().setVisibleByDefault(false);
                hologram.getVisibilityManager().showTo(player);
            });
        }
    }

    private static void addLeaderboard(List<Document> top, String key, Location location, String title) {
        List<String> hologramLines = new ArrayList<>();
        //formats top 10
        for (int i = 0; i < 10 && i < top.size(); i++) {
            Document p = top.get(i);
            Object playerKey = p.get(key);
            if (playerKey instanceof Integer) {
                hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + p.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Integer) playerKey)));
            } else if (playerKey instanceof Long) {
                hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + p.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Long) playerKey)));
            }
        }
        //creates actual leaderboard
        createLeaderboard(location, title, hologramLines);
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

        hologram.getVisibilityManager().setVisibleByDefault(false);
        return hologram;
    }

    public static List<Document> getPlayersSortedByKey(String key, int database) {
        if (!DatabaseManager.connected) return null;
        try {
            if (database == 0) {
                return Lists.newArrayList(DatabaseManager.playersInformation.aggregate(Collections.singletonList(sort(descending(key)))));
            } else {
                return Lists.newArrayList(playersInformationWeekly.aggregate(Collections.singletonList(sort(descending(key)))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Problem getting " + key);
            return null;
        }
    }

}
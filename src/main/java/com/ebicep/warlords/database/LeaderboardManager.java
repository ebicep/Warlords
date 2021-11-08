package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.LocationBuilder;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

import static com.ebicep.warlords.database.DatabaseManager.*;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Sorts.descending;

public class LeaderboardManager {

    public static final World world = Bukkit.getWorld("MainLobby");

    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();

    public static final Location leaderboardSwitchLocation = new Location(world, -2535, 52.5, 721);

    public static final Location lastGameLocation = new Location(world, -2546.5, 55.5, 772.5);
    public static final Location gameSwitchLocation = new Location(world, -2537, 53.5, 769.5);
    public static final List<Location> gameHologramLocations = new ArrayList<>();

    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();

    public static final HashMap<String, Leaderboard> leaderboards = new HashMap<>();
    public static final HashMap<String, List<Document>> cachedSortedPlayersLifeTime = new HashMap<>();
    public static final HashMap<String, List<Document>> cachedSortedPlayersWeekly = new HashMap<>();

    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
    public static final HashMap<UUID, Integer> playerLeaderboardHolograms = new HashMap<>();

    public static final List<Hologram> lifeTimeHolograms = new ArrayList<>();
    public static final List<Hologram> weeklyHolograms = new ArrayList<>();

    public static boolean enabled = true;

    public static void init() {
        leaderboards.put("wins", new Leaderboard("Wins", new Location(world, -2567, 55.5, 717)));
        leaderboards.put("losses", new Leaderboard("Losses", new Location(world, -2563, 55.5, 717)));
        leaderboards.put("kills", new Leaderboard("Kills", new Location(world, -2559, 55.5, 717)));
        leaderboards.put("assists", new Leaderboard("Assists", new Location(world, -2555, 55.5, 717)));
        leaderboards.put("deaths", new Leaderboard("Deaths", new Location(world, -2551, 55.5, 717)));
        leaderboards.put("damage", new Leaderboard("Damage", new Location(world, -2547, 55.5, 717)));
        leaderboards.put("healing", new Leaderboard("Healing", new Location(world, -2543, 55.5, 717)));
        leaderboards.put("absorbed", new Leaderboard("Absorbed", new Location(world, -2539, 55.5, 717)));

        leaderboards.put("flags_captured", new Leaderboard("Flags Captured", new Location(world, -2567, 60.5, 717)));
        leaderboards.put("flags_returned", new Leaderboard("Flags Returned", new Location(world, -2563, 60.5, 717)));

        leaderboards.put("paladin.avenger.wins", new Leaderboard("Avenger Wins", new Location(world, -2551, 60.5, 717)));
        leaderboards.put("paladin.crusader.wins", new Leaderboard("Crusader Wins", new Location(world, -2547, 60.5, 717)));
        leaderboards.put("paladin.protector.wins", new Leaderboard("Protector Wins", new Location(world, -2543, 60.5, 717)));
        leaderboards.put("warrior.berserker.wins", new Leaderboard("Berserker Wins", new Location(world, -2539, 60.5, 717)));
        leaderboards.put("warrior.defender.wins", new Leaderboard("Defender Wins", new Location(world, -2567, 65.5, 717)));
        leaderboards.put("warrior.revenant.wins", new Leaderboard("Revenant Wins", new Location(world, -2563, 65.5, 717)));
        leaderboards.put("mage.pyromancer.wins", new Leaderboard("Pyromancer Wins", new Location(world, -2559, 65.5, 717)));
        leaderboards.put("mage.cryomancer.wins", new Leaderboard("Cryomancer Wins", new Location(world, -2555, 65.5, 717)));
        leaderboards.put("mage.aquamancer.wins", new Leaderboard("Aquamancer Wins", new Location(world, -2551, 65.5, 717)));
        leaderboards.put("shaman.thunderlord.wins", new Leaderboard("Thunderlord Wins", new Location(world, -2547, 65.5, 717)));
        leaderboards.put("shaman.spiritguard.wins", new Leaderboard("Spiritguard Wins", new Location(world, -2543, 65.5, 717)));
        leaderboards.put("shaman.earthwarden.wins", new Leaderboard("Earthwarden Wins", new Location(world, -2539, 65.5, 717)));

        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).left(5).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(.5f).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(4).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(7.5f).get());
        gameHologramLocations.add(new LocationBuilder(lastGameLocation.clone()).addY(2).right(11f).get());

    }

    public static Document getTopPlayersOnLeaderboard() {
        Document document = new Document("date", new Date()).append("total_players", cachedSortedPlayersWeekly.get("wins").size());
        leaderboards.keySet().forEach(key -> appendTop(document, key));
        appendTop(document, "plays");
        appendTop(document, "dhp");
        return document;
    }

    private static void addCalculatedStatsToCache() {
        //weekly
        addCalculatedStats(playersInformationWeekly, cachedSortedPlayersWeekly);

        //lifetime
        addCalculatedStats(playersInformation, cachedSortedPlayersLifeTime);
        //leaderboard position
        leaderboards.put("plays", new Leaderboard("Plays", new Location(world, -2559, 60.5, 717)));
        leaderboards.put("dhp", new Leaderboard("DHP", new Location(world, -2555, 60.5, 717)));
        leaderboards.put("dhp_per_game", new Leaderboard("DHP per Game", new Location(world, -2555, 70.5, 717)));

    }

    private static void addCalculatedStats(MongoCollection<Document> collection, HashMap<String, List<Document>> cache) {
        List<Document> plays = new ArrayList<>();
        List<Document> dhp = new ArrayList<>();
        List<Document> dhpPerGame = new ArrayList<>();
        for (Document d : collection.find()) {
            //plays
            int currentTopWL = 0;
            currentTopWL += d.getInteger("wins");
            currentTopWL += d.getInteger("losses");
            plays.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("plays", currentTopWL));
            //dhp
            long currentTopDHP = 0;
            currentTopDHP += d.getLong("damage");
            currentTopDHP += d.getLong("healing");
            currentTopDHP += d.getLong("absorbed");
            dhp.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("dhp", currentTopDHP));
            //dhp per game
            dhpPerGame.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("dhp_per_game", currentTopWL == 0 ? 0L : currentTopDHP / currentTopWL));
        }

        plays.sort((o1, o2) -> o2.getInteger("plays").compareTo(o1.getInteger("plays")));
        dhp.sort((o1, o2) -> o2.getLong("dhp").compareTo(o1.getLong("dhp")));
        dhpPerGame.sort((o1, o2) -> o2.getLong("dhp_per_game").compareTo(o1.getLong("dhp_per_game")));

        cache.put("plays", plays);
        cache.put("dhp", dhp);
        cache.put("dhp_per_game", dhpPerGame);
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

                //caching all calculated stats
                Warlords.newSharedChain(sharedChainName).sync(LeaderboardManager::addCalculatedStatsToCache).execute();

                //caching all sorted players for each lifetime and weekly
                leaderboards.forEach((s, leaderboard) -> {
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                //excluding calculated stats
                                if (s.equals("plays") || s.equals("dhp") || s.equals("dhp_per_game")) {
                                    return null;
                                }
                                return getPlayersSortedByKey(s, 0);
                            })
                            .syncLast((sortedInformation) -> {
                                if (sortedInformation != null) {
                                    cachedSortedPlayersLifeTime.put(s, sortedInformation);
                                }

                                //creating leaderboard for lifetime
                                addLeaderboard(cachedSortedPlayersLifeTime.get(s), s, leaderboard.getLocation(), ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(leaderboard.getLocation()))
                                        .forEach(lifeTimeHolograms::add);
                            }).execute();
                    Warlords.newSharedChain(sharedChainName)
                            .asyncFirst(() -> {
                                //excluding calculated stats
                                if (s.equals("plays") || s.equals("dhp") || s.equals("dhp_per_game")) {
                                    return null;
                                }
                                return getPlayersSortedByKey(s, 1);
                            })
                            .syncLast((sortedInformation) -> {
                                if (sortedInformation != null) {
                                    cachedSortedPlayersWeekly.put(s, sortedInformation);
                                }

                                //creating leaderboard for weekly
                                addLeaderboard(cachedSortedPlayersWeekly.get(s), s, leaderboard.getLocation(), ChatColor.AQUA + ChatColor.BOLD.toString() + "Weekly " + leaderboard.getTitle());
                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                                        .filter(h -> h.getLocation().equals(leaderboard.getLocation()) && !lifeTimeHolograms.contains(h))
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
            leaderboards.forEach((key, leaderboard) -> {
                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
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
                        Object docKey = document.getEmbedded(Arrays.asList(key.split("\\.")), Object.class);
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
            Object playerKey = p.getEmbedded(Arrays.asList(key.split("\\.")), Object.class);
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
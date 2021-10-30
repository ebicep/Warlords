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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static com.ebicep.warlords.database.DatabaseManager.*;
import static com.mongodb.client.model.Aggregates.sort;
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
    }

    public static Document getTopPlayersOnLeaderboard() {
        Document document = new Document("date", new Date());
        leaderboardLocations.keySet().forEach(key -> {
            appendTop(document, key);
        });
        appendTop(document, "plays", Integer.class, "wins", "losses");
        appendTop(document, "dhp", Long.class, "damage", "healing", "absorbed");
        return document;
    }

    private static void appendTop(Document document, String key, Class subKeyClass, String... subKeys) {
        long top = 0;
        List<Document> topDocuments = new ArrayList<>();
        for (Document d : cachedSortedPlayersWeekly.get("wins")) {
            long currentTop = 0;
            for (String subKey : subKeys) {
                if (d.get(subKey) instanceof Integer) {
                    currentTop += (int) d.get(subKey);
                } else if (d.get(subKey) instanceof Long) {
                    currentTop += (Long) d.get(subKey);
                }
            }
            if (currentTop > top) {
                top = currentTop;
            }

            if (subKeyClass == Integer.class) {
                topDocuments.add(new Document("name", d.get("name")).append(key, (int) currentTop));
            } else {
                topDocuments.add(new Document("name", d.get("name")).append(key, currentTop));
            }
        }

        if (subKeyClass == Integer.class) {
            document.append(key, new Document("amount", (int) top).append("players", getHighestPlayers(key, (int) top, topDocuments)));
        } else {
            document.append(key, new Document("amount", top).append("players", getHighestPlayers(key, top, topDocuments)));
        }
    }

    private static void appendTop(Document document, String key) {
        Object highest = getHighest(key);
        if (highest != null) {
            document.append(key, new Document("amount", highest).append("players", getHighestPlayers(key, highest, cachedSortedPlayersWeekly.get(key))));
        }
    }

    private static Object getHighest(String key) {
        if (cachedSortedPlayersWeekly.get(key).stream().findFirst().isPresent()) {
            return cachedSortedPlayersWeekly.get(key).stream().findFirst().get().get(key);
        } else {
            return null;
        }
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
            HologramsAPI.getHolograms(Warlords.getInstance()).forEach(Hologram::delete);
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
                    Bukkit.getOnlinePlayers().forEach(Leaderboards::setHologramVisibility);
                }).execute();
            }
        }
    }

    public static void setHologramVisibility(Player player) {
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
                setHologramVisibility(p);
            });
        } else {
            TextLine lifeTimeLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + "LifeTime");
            leaderboardSwitcher.appendTextLine(ChatColor.GREEN + "Weekly");

            lifeTimeLine.setTouchHandler(p -> {
                playerLeaderboardHolograms.put(player.getUniqueId(), 0);
                setHologramVisibility(p);
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
            //game
            addGameHologram(lastGameLocation, player);
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

    public static final String[] specsOrdered = {"Pyromancer", "Cryomancer", "Aquamancer", "Berserker", "Defender", "Revenant", "Avenger", "Crusader", "Protector", "Thunderlord", "Spiritguard", "Earthwarden"};

    public static void addGameHologram(Location location, Player player) {
        List<Hologram> holograms = new ArrayList<>();
        List<Location> hologramLocations = new ArrayList<>();
        hologramLocations.add(new LocationBuilder(location.clone()).left(5).get());
        hologramLocations.add(new LocationBuilder(location.clone()).addY(2).right(.5f).get());
        hologramLocations.add(new LocationBuilder(location.clone()).addY(2).right(4).get());
        hologramLocations.add(new LocationBuilder(location.clone()).addY(2).right(7.5f).get());
        //toggle game location
        hologramLocations.add(new LocationBuilder(location.clone()).backward(3).left(9.5f).addY(-2).get());

        if (!playerGameHolograms.containsKey(player.getUniqueId()) || playerGameHolograms.get(player.getUniqueId()) == null) {
            playerGameHolograms.put(player.getUniqueId(), previousGames.size() - 1);
        }
        int selectedGame = playerGameHolograms.get(player.getUniqueId());
        DatabaseGame databaseGame = previousGames.get(selectedGame);

        //removing old game holograms
        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
                .filter(hologram -> hologram.getVisibilityManager().isVisibleTo(player) && hologramLocations.contains(hologram.getLocation()))
                .forEach(Hologram::delete);

        //readding game holograms
        Hologram gameInfo = HologramsAPI.createHologram(Warlords.getInstance(), hologramLocations.get(0));
        holograms.add(gameInfo);
        gameInfo.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last Game Stats");

        Hologram topDamage = HologramsAPI.createHologram(Warlords.getInstance(), hologramLocations.get(1));
        holograms.add(topDamage);
        topDamage.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HologramsAPI.createHologram(Warlords.getInstance(), hologramLocations.get(2));
        holograms.add(topHealing);
        topHealing.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HologramsAPI.createHologram(Warlords.getInstance(), hologramLocations.get(3));
        holograms.add(topAbsorbed);
        topAbsorbed.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        //adding switch game hologram
        Hologram toggleGame = HologramsAPI.createHologram(Warlords.getInstance(), hologramLocations.get(4));
        holograms.add(toggleGame);
        toggleGame.appendTextLine(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Last " + previousGames.size() + " Games");
        toggleGame.appendTextLine("");

        //last game stats
        int timeLeft = databaseGame.getTimeLeft();
        gameInfo.appendTextLine(ChatColor.GRAY + databaseGame.getDate());
        gameInfo.appendTextLine(ChatColor.GREEN + databaseGame.getMap() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        gameInfo.appendTextLine(ChatColor.BLUE.toString() + databaseGame.getBluePoints() + ChatColor.GRAY + "  -  " + ChatColor.RED + databaseGame.getRedPoints());

        List<DatabaseGamePlayer> databaseGamePlayers = databaseGame.getDatabasePlayers();
        List<String> players = new ArrayList<>();

        for (String s : specsOrdered) {
            StringBuilder playerSpecs = new StringBuilder(ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            databaseGamePlayers.stream().filter(o -> o.getSpec().equals(s)).forEach(p -> {
                playerSpecs.append(p.getColoredName()).append(p.getKDA()).append(ChatColor.GRAY).append(", ");
                add[0] = true;
            });
            if (add[0]) {
                playerSpecs.setLength(playerSpecs.length() - 2);
                players.add(playerSpecs.toString());
            }
        }
        players.forEach(gameInfo::appendTextLine);

        //top dmg/healing/absorbed
        List<String> topDamagePlayers = new ArrayList<>();
        List<String> topHealingPlayers = new ArrayList<>();
        List<String> topAbsorbedPlayers = new ArrayList<>();

        databaseGamePlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayer::getTotalDamage).reversed()).forEach(databaseGamePlayer -> {
            topDamagePlayers.add(databaseGamePlayer.getColoredName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
        });

        databaseGamePlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayer::getTotalHealing).reversed()).forEach(databaseGamePlayer -> {
            topHealingPlayers.add(databaseGamePlayer.getColoredName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
        });

        databaseGamePlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayer::getTotalAbsorbed).reversed()).forEach(databaseGamePlayer -> {
            topAbsorbedPlayers.add(databaseGamePlayer.getColoredName() + ": " + ChatColor.YELLOW + Utils.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()));
        });

        topDamagePlayers.forEach(topDamage::appendTextLine);
        topHealingPlayers.forEach(topHealing::appendTextLine);
        topAbsorbedPlayers.forEach(topAbsorbed::appendTextLine);

        //switch game
        int gameBefore = getGameBefore(selectedGame);
        int gameAfter = getGameAfter(selectedGame);
        TextLine beforeLine;
        TextLine afterLine;
        if (gameBefore == previousGames.size() - 1) {
            beforeLine = toggleGame.appendTextLine(ChatColor.GRAY + "Latest Game");
        } else {
            beforeLine = toggleGame.appendTextLine(ChatColor.GRAY.toString() + (gameBefore + 1) + ". " + previousGames.get(gameBefore).getDate());
        }
        if (selectedGame == previousGames.size() - 1) {
            toggleGame.appendTextLine(ChatColor.GREEN + "Latest Game");
        } else {
            toggleGame.appendTextLine(ChatColor.GREEN.toString() + (selectedGame + 1) + ". " + databaseGame.getDate());
        }

        if (gameAfter == previousGames.size() - 1) {
            afterLine = toggleGame.appendTextLine(ChatColor.GRAY + "Latest Game");
        } else {
            afterLine = toggleGame.appendTextLine(ChatColor.GRAY.toString() + (gameAfter + 1) + ". " + previousGames.get(gameAfter).getDate());
        }

        beforeLine.setTouchHandler((clicker) -> {
            playerGameHolograms.put(player.getUniqueId(), gameBefore);
            addGameHologram(location, player);
        });

        afterLine.setTouchHandler((clicker) -> {
            playerGameHolograms.put(player.getUniqueId(), gameAfter);
            addGameHologram(location, player);
        });

        //only showing hologram to player
        holograms.forEach(hologram -> {
            hologram.getVisibilityManager().setVisibleByDefault(false);
            hologram.getVisibilityManager().showTo(player);
        });

    }

    private static int getGameBefore(int currentGame) {
        if (currentGame == 0) {
            return previousGames.size() - 1;
        }
        return currentGame - 1;
    }

    private static int getGameAfter(int currentGame) {
        if (currentGame == previousGames.size() - 1) {
            return 0;
        }
        return currentGame + 1;
    }

}
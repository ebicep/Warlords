//package com.ebicep.warlords.database;
//
//import com.ebicep.warlords.Warlords;
//import com.ebicep.warlords.util.LocationBuilder;
//import com.ebicep.warlords.util.Utils;
//import com.gmail.filoghost.holographicdisplays.api.Hologram;
//import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
//import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
//import com.google.common.collect.Lists;
//import com.mongodb.client.MongoCollection;
//import org.bson.Document;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
//import org.bukkit.World;
//import org.bukkit.entity.Player;
//
//import java.util.*;
//
//import static com.ebicep.warlords.database.DatabaseManager.*;
//import static com.mongodb.client.model.Aggregates.*;
//import static com.mongodb.client.model.Sorts.descending;
//
//public class LeaderboardManager {
//
//    public static final World world = Bukkit.getWorld("MainLobby");
//
//    public static final Location spawnPoint = Bukkit.getWorlds().get(0).getSpawnLocation().clone();
//
//    public static final Location leaderboardSwitchLocation = new Location(world, -2552.5, 52.5, 719.5);
//
//    public static final Location center = new LocationBuilder(spawnPoint.clone()).forward(.5f).left(21).addY(2).get();
//
//    public static final HashMap<String, Leaderboard> leaderboards = new HashMap<>();
//    public static final HashMap<String, List<Document>> cachedSortedPlayersLifeTime = new HashMap<>();
//    public static final HashMap<String, List<Document>> cachedSortedPlayersWeekly = new HashMap<>();
//
//    public static final HashMap<UUID, Integer> playerGameHolograms = new HashMap<>();
//    public static final HashMap<UUID, Integer> playerLeaderboardHolograms = new HashMap<>();
//
//    public static final List<Hologram> lifeTimeHolograms = new ArrayList<>();
//    public static final List<Hologram> weeklyHolograms = new ArrayList<>();
//
//    public static boolean enabled = true;
//
//    public static void init() {
//        putLeaderboards();
//    }
//
//    public static void putLeaderboards() {
//        leaderboards.put("wins", new Leaderboard("Wins", new Location(world, -2558.5, 56, 712.5)));
//        leaderboards.put("losses", new Leaderboard("Losses", new Location(world, -2608.5, 52, 728.5)));
//        leaderboards.put("kills", new Leaderboard("Kills", new Location(world, -2552.5, 56, 712.5)));
//        leaderboards.put("assists", new Leaderboard("Assists", new Location(world, -2616.5, 52, 733.5)));
//        leaderboards.put("deaths", new Leaderboard("Deaths", new Location(world, -2616.5, 52, 723.5)));
//        leaderboards.put("damage", new Leaderboard("Damage", new Location(world, -2600.5, 52, 723.5)));
//        leaderboards.put("healing", new Leaderboard("Healing", new Location(world, -2608.5, 52, 719.5)));
//        leaderboards.put("absorbed", new Leaderboard("Absorbed", new Location(world, -2600.5, 52, 733.5)));
//
//        leaderboards.put("flags_captured", new Leaderboard("Flags Captured", new Location(world, -2540.5, 56, 712.5)));
//        leaderboards.put("flags_returned", new Leaderboard("Flags Returned", new Location(world, -2608.5, 52, 737.5)));
//
//        String statType = "Wins";
//        leaderboards.put("paladin.avenger." + statType.toLowerCase(), new Leaderboard("Avenger " + statType, new Location(world, -2631.5, 52, 719.5)));
//        leaderboards.put("paladin.crusader." + statType.toLowerCase(), new Leaderboard("Crusader " + statType, new Location(world, -2628.5, 52, 714.5)));
//        leaderboards.put("paladin.protector." + statType.toLowerCase(), new Leaderboard("Protector " + statType, new Location(world, -2623.5, 52, 711.5)));
//        leaderboards.put("warrior.berserker." + statType.toLowerCase(), new Leaderboard("Berserker " + statType, new Location(world, -2623.5, 52, 745.5)));
//        leaderboards.put("warrior.defender." + statType.toLowerCase(), new Leaderboard("Defender " + statType, new Location(world, -2628.5, 52, 742.5)));
//        leaderboards.put("warrior.revenant." + statType.toLowerCase(), new Leaderboard("Revenant " + statType, new Location(world, -2631.5, 52, 737.5)));
//        leaderboards.put("mage.pyromancer." + statType.toLowerCase(), new Leaderboard("Pyromancer " + statType, new Location(world, -2602.5, 53, 749.5)));
//        leaderboards.put("mage.cryomancer." + statType.toLowerCase(), new Leaderboard("Cryomancer " + statType, new Location(world, -2608.5, 53, 752.5)));
//        leaderboards.put("mage.aquamancer." + statType.toLowerCase(), new Leaderboard("Aquamancer " + statType, new Location(world, -2614.5, 53, 749.5)));
//        leaderboards.put("shaman.thunderlord." + statType.toLowerCase(), new Leaderboard("Thunderlord " + statType, new Location(world, -2614.5, 52, 706.5)));
//        leaderboards.put("shaman.spiritguard." + statType.toLowerCase(), new Leaderboard("Spiritguard " + statType, new Location(world, -2608.5, 52, 704.5)));
//        leaderboards.put("shaman.earthwarden." + statType.toLowerCase(), new Leaderboard("Earthwarden " + statType, new Location(world, -2602.5, 52, 706.5)));
//
//        leaderboards.put("experience", new Leaderboard("Experience", new Location(world, -2526.5, 57, 744.5)));
//        leaderboards.put("mage.experience", new Leaderboard("Mage Experience", new Location(world, -2520.5, 58, 735.5)));
//        leaderboards.put("warrior.experience", new Leaderboard("Warrior Experience", new Location(world, -2519.5, 58, 741.5)));
//        leaderboards.put("paladin.experience", new Leaderboard("Paladin Experience", new Location(world, -2519.5, 58, 747.5)));
//        leaderboards.put("shaman.experience", new Leaderboard("Shaman Experience", new Location(world, -2520.5, 58, 753.5)));
//
//    }
//
//    public static Document getTopPlayersOnLeaderboard() {
//        Document document = new Document("date", new Date()).append("total_players", cachedSortedPlayersWeekly.get("wins").size());
//        leaderboards.keySet().forEach(key -> appendTop(document, key));
//        appendTop(document, "plays");
//        appendTop(document, "dhp");
//        return document;
//    }
//
//    private static void addCalculatedStatsToCache() {
//        //weekly
//        addCalculatedStats(playersInformationWeekly, cachedSortedPlayersWeekly);
//
//        //lifetime
//        addCalculatedStats(playersInformation, cachedSortedPlayersLifeTime);
//        //leaderboard position
//        leaderboards.put("plays", new Leaderboard("Plays", new Location(world, -2564.5, 56, 712.5)));
//        leaderboards.put("dhp", new Leaderboard("DHP", new Location(world, -2555, 6000.5, 717)));
//        leaderboards.put("dhp_per_game", new Leaderboard("DHP per Game", new Location(world, -2546.5, 56, 712.5)));
////        leaderboards.put("kills_per_game", new Leaderboard("Kills per Game", new Location(world, -2567, 75.5, 717)));
//        leaderboards.put("deaths_per_game", new Leaderboard("Deaths per Game", new Location(world, -2608.5, 62, 737.5)));
//        leaderboards.put("ka_per_game", new Leaderboard("Kills/Assists per Game", new Location(world, -2608.5, 62, 719.5)));
//    }
//
//    private static void addCalculatedStats(MongoCollection<Document> collection, HashMap<String, List<Document>> cache) {
//        List<Document> plays = new ArrayList<>();
//        List<Document> dhp = new ArrayList<>();
//        List<Document> dhpPerGame = new ArrayList<>();
//        List<Document> killsPerGame = new ArrayList<>();
//        List<Document> deathsPerGame = new ArrayList<>();
//        List<Document> killAssistPerGame = new ArrayList<>();
//        for (Document d : collection.find()) {
//            //plays
//            int currentTopWL = 0;
//            currentTopWL += d.getInteger("wins");
//            currentTopWL += d.getInteger("losses");
//            if (currentTopWL <= 8) {
//                continue;
//            }
//
//            plays.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("plays", currentTopWL));
//            //dhp
//            long currentTopDHP = 0;
//            currentTopDHP += d.getLong("damage");
//            currentTopDHP += d.getLong("healing");
//            currentTopDHP += d.getLong("absorbed");
//            dhp.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("dhp", currentTopDHP));
//            //dhp per game
//            dhpPerGame.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("dhp_per_game", currentTopDHP / currentTopWL));
//            //kills/deaths per game
//            int kills = d.getInteger("kills");
//            int assist = d.getInteger("assists");
//            int deaths = d.getInteger("deaths");
//            killsPerGame.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("kills_per_game", kills == 0 ? 0d : Math.round(((double) kills / currentTopWL) * 10) / 10d));
//            deathsPerGame.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("deaths_per_game", deaths == 0 ? 0d : Math.round(((double) deaths / currentTopWL) * 10) / 10d));
//            killAssistPerGame.add(new Document("name", d.getString("name")).append("uuid", d.getString("uuid")).append("ka_per_game", (kills + assist) == 0 ? 0d : Math.round(((double) (kills + assist) / currentTopWL) * 10) / 10d));
//        }
//
//        plays.sort((o1, o2) -> o2.getInteger("plays").compareTo(o1.getInteger("plays")));
//        dhp.sort((o1, o2) -> o2.getLong("dhp").compareTo(o1.getLong("dhp")));
//        dhpPerGame.sort((o1, o2) -> o2.getLong("dhp_per_game").compareTo(o1.getLong("dhp_per_game")));
//        killsPerGame.sort((o1, o2) -> o2.getDouble("kills_per_game").compareTo(o1.getDouble("kills_per_game")));
//        deathsPerGame.sort((o1, o2) -> o2.getDouble("deaths_per_game").compareTo(o1.getDouble("deaths_per_game")));
//        killAssistPerGame.sort((o1, o2) -> o2.getDouble("ka_per_game").compareTo(o1.getDouble("ka_per_game")));
//
//        cache.put("plays", plays);
//        cache.put("dhp", dhp);
//        cache.put("dhp_per_game", dhpPerGame);
//        cache.put("kills_per_game", killsPerGame);
//        cache.put("deaths_per_game", deathsPerGame);
//        cache.put("ka_per_game", killAssistPerGame);
//    }
//
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
//            document.append(key, new Document("total", total).append("name", leaderboards.get(key).getTitle()).append("top", documentList));
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
//
//    public static void addHologramLeaderboards(String sharedChainName) {
//        if (DatabaseManager.connected && Warlords.holographicDisplaysEnabled) {
//            HologramsAPI.getHolograms(Warlords.getInstance()).forEach(hologram -> {
//                Location hologramLocation = hologram.getLocation();
//                if (!DatabaseGame.lastGameStatsLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topDamageLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topHealingLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topAbsorbedLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topDHPPerMinuteLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topDamageOnCarrierLocation.equals(hologramLocation) &&
//                        !DatabaseGame.topHealingOnCarrierLocation.equals(hologramLocation)
//                ) {
//                    hologram.delete();
//                }
//            });
//            lifeTimeHolograms.clear();
//            weeklyHolograms.clear();
//
//            putLeaderboards();
//            if (enabled) {
//                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Adding Holograms");
//
//                //caching all calculated stats
//                Warlords.newSharedChain(sharedChainName).sync(LeaderboardManager::addCalculatedStatsToCache).execute();
//
//                //caching all sorted players for each lifetime and weekly
//                leaderboards.forEach((s, leaderboard) -> {
//                    //LIFETIME
//                    Warlords.newSharedChain(sharedChainName)
//                            .asyncFirst(() -> {
//                                //excluding calculated stats
//                                if (s.equals("plays") || s.equals("dhp") || s.equals("dhp_per_game") || s.equals("kills_per_game") || s.equals("deaths_per_game") || s.equals("ka_per_game")) {
//                                    return null;
//                                }
//                                return getPlayersSortedByKey(s, 0);
//                            })
//                            .syncLast((sortedInformation) -> {
//                                if (sortedInformation != null) {
//                                    cachedSortedPlayersLifeTime.put(s, sortedInformation);
//                                }
//
//                                //creating leaderboard for lifetime
//                                addLeaderboard(cachedSortedPlayersLifeTime.get(s), s, leaderboard.getLocation(), ChatColor.AQUA + ChatColor.BOLD.toString() + "Lifetime " + leaderboard.getTitle());
//                                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                                        .filter(h -> h.getLocation().equals(leaderboard.getLocation()))
//                                        .forEach(lifeTimeHolograms::add);
//                            }).execute();
//                    //WEEKLY
//                    Warlords.newSharedChain(sharedChainName)
//                            .asyncFirst(() -> {
//                                //excluding calculated stats
//                                if (s.equals("plays") || s.equals("dhp") || s.equals("dhp_per_game") || s.equals("kills_per_game") || s.equals("deaths_per_game") || s.equals("ka_per_game")) {
//                                    return null;
//                                }
//                                return getPlayersSortedByKey(s, 1);
//                            })
//                            .syncLast((sortedInformation) -> {
//                                if (sortedInformation != null) {
//                                    cachedSortedPlayersWeekly.put(s, sortedInformation);
//                                }
//
//                                //creating leaderboard for weekly
//                                if(s.equals("plays") || s.equals("wins") || s.equals("kills") || s.equals("dhp_per_game") || s.equals("flags_captured")) {
//                                    addLeaderboard(cachedSortedPlayersWeekly.get(s), s, leaderboard.getLocation(), ChatColor.AQUA + ChatColor.BOLD.toString() + "Weekly " + leaderboard.getTitle());
//                                    HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                                            .filter(h -> h.getLocation().equals(leaderboard.getLocation()) && !lifeTimeHolograms.contains(h))
//                                            .forEach(weeklyHolograms::add);
//                                }
//                            }).execute();
//                });
//
//                //depending on what player has selected, set visibility
//                Warlords.newSharedChain(sharedChainName).sync(() -> {
//                    System.out.println("Setting Hologram Visibility");
//                    Bukkit.getOnlinePlayers().forEach(player -> {
//                        setLeaderboardHologramVisibility(player);
//                        DatabaseGame.setGameHologramVisibility(player);
//                    });
//                }).execute();
//
//            }
//        }
//    }
//
//    public static void setLeaderboardHologramVisibility(Player player) {
//        if (!playerLeaderboardHolograms.containsKey(player.getUniqueId()) || playerLeaderboardHolograms.get(player.getUniqueId()) == null) {
//            playerLeaderboardHolograms.put(player.getUniqueId(), 0);
//        }
//        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
//        if (selectedLeaderboard == 0) {
//            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
//            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
//        } else {
//            lifeTimeHolograms.forEach(hologram -> hologram.getVisibilityManager().hideTo(player));
//            weeklyHolograms.forEach(hologram -> hologram.getVisibilityManager().showTo(player));
//        }
//
//        createLeaderboardSwitcherHologram(player);
//        addPlayerPositionLeaderboards(player);
//    }
//
//    private static void createLeaderboardSwitcherHologram(Player player) {
//        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                .filter(h -> h.getVisibilityManager().isVisibleTo(player) && h.getLocation().equals(leaderboardSwitchLocation))
//                .forEach(Hologram::delete);
//        Hologram leaderboardSwitcher = HologramsAPI.createHologram(Warlords.getInstance(), leaderboardSwitchLocation);
//        leaderboardSwitcher.appendTextLine(ChatColor.AQUA.toString() + ChatColor.UNDERLINE + "Click to Toggle");
//        leaderboardSwitcher.appendTextLine("");
//
//        int selectedLeaderboard = playerLeaderboardHolograms.get(player.getUniqueId());
//        if (selectedLeaderboard == 0) {
//            leaderboardSwitcher.appendTextLine(ChatColor.GREEN + "LifeTime");
//            TextLine weeklyLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + "Weekly");
//
//            weeklyLine.setTouchHandler(p -> {
//                playerLeaderboardHolograms.put(player.getUniqueId(), 1);
//                setLeaderboardHologramVisibility(p);
//            });
//        } else {
//            TextLine lifeTimeLine = leaderboardSwitcher.appendTextLine(ChatColor.GRAY + "LifeTime");
//            leaderboardSwitcher.appendTextLine(ChatColor.GREEN + "Weekly");
//
//            lifeTimeLine.setTouchHandler(p -> {
//                playerLeaderboardHolograms.put(player.getUniqueId(), 0);
//                setLeaderboardHologramVisibility(p);
//            });
//        }
//
//        leaderboardSwitcher.getVisibilityManager().setVisibleByDefault(false);
//        leaderboardSwitcher.getVisibilityManager().showTo(player);
//    }
//
//    public static void addPlayerPositionLeaderboards(Player player) {
//        if (enabled) {
//            //leaderboards
//            leaderboards.forEach((key, leaderboard) -> {
//                Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
//                HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                        .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
//                        .forEach(Hologram::delete);
//
//                List<Document> documents;
//                boolean weekly = false;
//                if (playerLeaderboardHolograms.get(player.getUniqueId()) == 0 && cachedSortedPlayersLifeTime.containsKey(key)) {
//                    documents = cachedSortedPlayersLifeTime.get(key);
//                } else if (playerLeaderboardHolograms.get(player.getUniqueId()) == 1 && cachedSortedPlayersWeekly.containsKey(key)) {
//                    documents = cachedSortedPlayersWeekly.get(key);
//                    weekly = true;
//                } else {
//                    return;
//                }
//
//                if(!weekly || (key.equals("plays") || key.equals("wins") || key.equals("kills") || key.equals("dhp_per_game") || key.equals("flags_captured"))) {
//                    Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
//                    for (int i = 0; i < documents.size(); i++) {
//                        Document document = documents.get(i);
//                        if (document.get("uuid").equals(player.getUniqueId().toString())) {
//                            Object docKey = document.getEmbedded(Arrays.asList(key.split("\\.")), Object.class);
//                            if (docKey instanceof Integer) {
//                                hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound((Integer) docKey)));
//                            } else if (docKey instanceof Long) {
//                                hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + (Utils.addCommaAndRound((Long) docKey)));
//                            } else if (docKey instanceof Double) {
//                                hologram.appendTextLine(ChatColor.YELLOW.toString() + ChatColor.BOLD + (i + 1) + ". " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GRAY + ChatColor.BOLD + " - " + ChatColor.YELLOW + ChatColor.BOLD + docKey);
//                            }
//                            break;
//                        }
//                    }
//                    hologram.getVisibilityManager().setVisibleByDefault(false);
//                    hologram.getVisibilityManager().showTo(player);
//                }
//            });
//        }
//    }
//
//    public static void removePlayerSpecificHolograms(Player player) {
//        leaderboards.forEach((key, leaderboard) -> {
//            Location location = leaderboard.getLocation().clone().add(0, -3.5, 0);
//            HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                    .filter(hologram -> hologram.getLocation().equals(location) && hologram.getVisibilityManager().isVisibleTo(player))
//                    .forEach(Hologram::delete);
//        });
//        HologramsAPI.getHolograms(Warlords.getInstance()).stream()
//                .filter(h -> h.getVisibilityManager().isVisibleTo(player) && (h.getLocation().equals(DatabaseGame.gameSwitchLocation) || h.getLocation().equals(leaderboardSwitchLocation)))
//                .forEach(Hologram::delete);
//    }
//
//    private static void addLeaderboard(List<Document> top, String key, Location location, String title) {
//        List<String> hologramLines = new ArrayList<>();
//        //formats top 10
//        for (int i = 0; i < 10 && i < top.size(); i++) {
//            Document p = top.get(i);
//            Object playerKey = p.getEmbedded(Arrays.asList(key.split("\\.")), Object.class);
//            if (playerKey instanceof Integer) {
//                hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + p.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Integer) playerKey)));
//            } else if (playerKey instanceof Long) {
//                hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + p.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + (Utils.addCommaAndRound((Long) playerKey)));
//            } else if (playerKey instanceof Double) {
//                hologramLines.add(ChatColor.YELLOW.toString() + (i + 1) + ". " + ChatColor.AQUA + p.get("name") + ChatColor.GRAY + " - " + ChatColor.YELLOW + playerKey);
//            }
//        }
//        //creates actual leaderboard
//        createLeaderboard(location, title, hologramLines);
//    }
//
//    private static List<Document> getDocumentInSortedList(HashMap<Document, Integer> map) {
//        List<Document> sorted = new ArrayList<>();
//        map.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue())
//                .forEach(documentIntegerEntry -> sorted.add(documentIntegerEntry.getKey()));
//        Collections.reverse(sorted);
//        return sorted;
//    }
//
//    public static Hologram createLeaderboard(Location location, String title, List<String> lines) {
//        Hologram hologram = HologramsAPI.createHologram(Warlords.getInstance(), location);
//        hologram.appendTextLine(title);
//        hologram.appendTextLine("");
//        for (String line : lines) {
//            hologram.appendTextLine(line);
//        }
//
//        hologram.getVisibilityManager().setVisibleByDefault(false);
//        return hologram;
//    }
//
//    public static List<Document> getPlayersSortedByKey(String key, int database) {
//        if (!DatabaseManager.connected) return null;
//        try {
//            if (database == 0) {
//                return Lists.newArrayList(DatabaseManager.playersInformation.aggregate(Collections.singletonList(sort(descending(key)))));
//            } else {
//                return Lists.newArrayList(playersInformationWeekly.aggregate(Collections.singletonList(sort(descending(key)))));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(ChatColor.GREEN + "[Warlords] Problem getting " + key);
//            return null;
//        }
//    }
//
//}
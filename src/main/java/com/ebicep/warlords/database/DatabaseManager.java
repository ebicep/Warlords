package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.PlayerFilter;
import com.google.common.collect.Lists;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class DatabaseManager {

    private boolean connected;
    private MongoClient mongoClient;
    private MongoDatabase warlordsPlayersDatabase;
    private MongoDatabase warlordsGamesDatabase;
    private MongoCollection<Document> playersInformation;
    private MongoCollection<Document> gamesInformation;
    private HashMap<UUID, Document> cachedPlayerInfo = new HashMap<>();
    private HashMap<String, Long> cachedTotalKeyValues = new HashMap<>();

    public boolean isConnected() {
        return connected;
    }

    public DatabaseManager() {
        try {
            System.out.println(System.getProperty("user.dir"));
            File myObj = new File(System.getProperty("user.dir") + "/plugins/Warlords/database_key.TXT");
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                mongoClient = MongoClients.create(data);
                warlordsPlayersDatabase = mongoClient.getDatabase("Warlords_Players");
                warlordsGamesDatabase = mongoClient.getDatabase("Warlords_Games");
                playersInformation = warlordsPlayersDatabase.getCollection("Players_Information");
                gamesInformation = warlordsGamesDatabase.getCollection("Games_Information");
                //List<UUID> uuids = new ArrayList<>();
                playersInformation.find().forEach((Consumer<? super Document>) document -> {
                    //UUID uuid = UUID.fromString((String) document.get("uuid"));
                    //uuids.add(uuid);
                    cachedPlayerInfo.put(UUID.fromString((String) document.get("uuid")), document);
                });
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    loadPlayer(onlinePlayer);
                }
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords]: Database Connected");
                connected = true;
//                playersInformation.deleteMany(new Document());
//                for (UUID uuid : uuids) {
//                    addPlayer(uuid);
//                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            connected = false;
        }
    }

    public boolean hasPlayer(UUID uuid) {
        if (!connected) return false;
        if (cachedPlayerInfo.containsKey(uuid)) return true;
        try {
            Document document = playersInformation.find(eq("uuid", uuid.toString())).first();
            return document != null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Some error while trying to find document");
            return false;
        }
    }

    public void loadPlayer(Player player) {
        if (!connected) return;
        try {
            if (hasPlayer(player.getUniqueId())) {
                Document playerInfo = playersInformation.find(eq("uuid", player.getUniqueId().toString())).first();
                //todo update name
                cachedPlayerInfo.put(player.getUniqueId(), playerInfo);
                Classes.setSelected(player, Classes.getClass((String) getPlayerInfoWithDotNotation(player, "last_spec")));
                Weapons.setSelected(player, Weapons.getWeapon((String) getPlayerInfoWithDotNotation(player, "last_weapon")));
                ArmorManager.Helmets.setSelectedMage(player, ArmorManager.Helmets.getMageHelmet((String) getPlayerInfoWithDotNotation(player, "mage_helm")));
                ArmorManager.ArmorSets.setSelectedMage(player, ArmorManager.ArmorSets.getMageArmor((String) getPlayerInfoWithDotNotation(player, "mage_armor")));
                ArmorManager.Helmets.setSelectedWarrior(player, ArmorManager.Helmets.getWarriorHelmet((String) getPlayerInfoWithDotNotation(player, "warrior_helm")));
                ArmorManager.ArmorSets.setSelectedWarrior(player, ArmorManager.ArmorSets.getWarriorArmor((String) getPlayerInfoWithDotNotation(player, "warrior_armor")));
                ArmorManager.Helmets.setSelectedPaladin(player, ArmorManager.Helmets.getPaladinHelmet((String) getPlayerInfoWithDotNotation(player, "paladin_helm")));
                ArmorManager.ArmorSets.setSelectedPaladin(player, ArmorManager.ArmorSets.getPaladinArmor((String) getPlayerInfoWithDotNotation(player, "paladin_armor")));
                ArmorManager.Helmets.setSelectedShaman(player, ArmorManager.Helmets.getShamanHelmet((String) getPlayerInfoWithDotNotation(player, "shaman_helm")));
                ArmorManager.ArmorSets.setSelectedShaman(player, ArmorManager.ArmorSets.getShamanArmor((String) getPlayerInfoWithDotNotation(player, "shaman_armor")));
                Settings.Powerup.setSelected(player, Settings.Powerup.getPowerup((String) getPlayerInfoWithDotNotation(player, "powerup")));
                Settings.HotkeyMode.setSelected(player, Settings.HotkeyMode.getHotkeyMode((String) getPlayerInfoWithDotNotation(player, "hotkeymode")));
                System.out.println("Loaded player " + player.getName());
            } else {
                addPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR loading player - " + player.getName());
        }
    }

    public void updatePlayerInformation(Player player, String key, String newValue) {
        if (!connected) return;
        try {
            if (hasPlayer(player.getUniqueId())) {
                playersInformation.updateOne(
                        eq("uuid", player.getUniqueId().toString()),
                        combine(set(key, newValue))
                );
                cachedPlayerInfo.remove(player.getUniqueId());
                loadPlayer(player);

                cachedTotalKeyValues.clear();
                System.out.println(player.getUniqueId() + " - " + player.getName() + " - " + key + " was updated to " + newValue);
            } else {
                System.out.println("Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to update information of player " + player.getName());
        }
    }

    public void updatePlayerInformation(Player player, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        if (!connected) return;
        try {
            if (hasPlayer(player.getUniqueId())) {
                Document history = new Document();
                for (String s : newInfo.keySet()) {
                    history.append(s, newInfo.get(s));
                }
                Document update = new Document(operator.operator, history);
                playersInformation.updateOne(eq("uuid", player.getUniqueId().toString()), update);
                cachedPlayerInfo.remove(player.getUniqueId());
                loadPlayer(player);

                cachedTotalKeyValues.clear();
                System.out.println(player.getUniqueId() + " - " + player.getName() + " was updated");
            } else {
                System.out.println("Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (Exception e) {
            System.out.println("There was an error trying to update information of player " + player.getName());
        }
    }

    public void updatePlayerInformation(OfflinePlayer player, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        if (!connected) return;
        try {
            if (hasPlayer(player.getUniqueId())) {
                Document history = new Document();
                for (String s : newInfo.keySet()) {
                    history.append(s, newInfo.get(s));
                }
                Document update = new Document(operator.operator, history);
                playersInformation.updateOne(eq("uuid", player.getUniqueId().toString()), update);
                cachedPlayerInfo.remove(player.getUniqueId());
                loadPlayer(player.getPlayer());

                cachedTotalKeyValues.clear();
                System.out.println(player.getUniqueId() + " - " + player.getName() + " was updated");
            } else {
                System.out.println("Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (Exception e) {
            System.out.println("There was an error trying to update information of player " + player.getName());
        }
    }

    public Object getPlayerInfoWithDotNotation(Player player, String dots) {
        return getPlayerInfoWithDotNotation(player.getUniqueId(), dots);
    }

    public Object getPlayerInfoWithDotNotation(UUID uuid, String dots) throws MongoException {
        if (!connected) return null;

        Document doc;
        if (cachedPlayerInfo.containsKey(uuid)) {
            doc = cachedPlayerInfo.get(uuid);
        } else if (hasPlayer(uuid)) {
            doc = playersInformation.find(eq("uuid", uuid.toString())).first();
        } else {
            System.out.println("Couldn't get player " + uuid + " - Not in the database!");
            return null;
        }

        if (doc == null) {
            return null;
        }

        return getPlayerInfoWithDotNotation(doc, dots);
    }

    public Object getPlayerInfoWithDotNotation(Document document, String dots) throws MongoException {
        if (!connected) return null;

        String[] keys = dots.split("\\.");
        Document doc = document;

        for (int i = 0; i < keys.length - 1; i++) {
            Object o = doc.get(keys[i]);
            if (!(o instanceof Document)) {
                throw new MongoException(String.format("Field '%s' does not exist or s not a Document", keys[i]));
            }
            doc = (Document) o;
        }

        return doc.get(keys[keys.length - 1]);
    }

    public long getPlayerTotalKey(String key) {
        if (cachedTotalKeyValues.containsKey(key)) return cachedTotalKeyValues.get(key);
        try {
            long total = 0;
            for (Map.Entry<UUID, Document> uuidDocumentEntry : cachedPlayerInfo.entrySet()) {
                total += (Integer) getPlayerInfoWithDotNotation(uuidDocumentEntry.getValue(), key);
            }
            cachedTotalKeyValues.put(key, total);
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was an error trying to total of " + key);
            return 0L;
        }
    }

    public int getSR(Player player) {
        return getSR(player.getUniqueId());
    }

    public int getSR(UUID uuid) {
        double dhp = averageAdjustedDHP(uuid, "") * 2000;
        double wl = averageAdjustedWL(uuid, "") * 2000;
        double kda = averageAdjustedKDA(uuid, "") * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    public int getSRClass(Player player, String optionalClass) {
        return getSRClass(player.getUniqueId(), optionalClass);
    }

    public int getSRClass(UUID uuid, String optionalClass) {
        double dhp = averageAdjustedDHP(uuid, optionalClass) * 2000;
        double wl = averageAdjustedWL(uuid, optionalClass) * 2000;
        double kda = averageAdjustedKDA(uuid, optionalClass) * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    private double averageAdjusted(long playerAverage, long total) {
        double average = playerAverage / ((total / (double) playersInformation.countDocuments()));
        if (average >= 5) return 1;
        if (average <= 0) return 0;
        return 1.00699 + (-1.02107 / (1.01398 + Math.pow(average, 3.09248)));
    }

    private double averageAdjustedDHP(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerDHP = (Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "damage") + (Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "healing") + (Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "absorbed");
        long totalDHP = getPlayerTotalKey(optionalClass + "damage") + getPlayerTotalKey(optionalClass + "healing") + getPlayerTotalKey(optionalClass + "absorbed");
        return averageAdjusted(playerDHP, totalDHP);
    }

    private double averageAdjustedWL(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerWL = (Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "wins") / Math.max((Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "losses"), 1);
        long totalWL = getPlayerTotalKey(optionalClass + "wins") / Math.max(getPlayerTotalKey(optionalClass + "losses"), 1);
        return averageAdjusted(playerWL, totalWL);
    }

    private double averageAdjustedKDA(UUID uuid, String optionalClass) {
        if (!optionalClass.isEmpty()) {
            optionalClass += ".";
        }
        long playerDHP = ((Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "kills") + (Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "assists")) / Math.max((Integer) getPlayerInfoWithDotNotation(uuid, optionalClass + "deaths"), 1);
        long totalDHP = (getPlayerTotalKey(optionalClass + "kills") + getPlayerTotalKey(optionalClass + "assists")) / Math.max(getPlayerTotalKey(optionalClass + "deaths"), 1);
        return averageAdjusted(playerDHP, totalDHP);
    }

    public HashMap<Document, Integer> getPlayersSortedBySR(String optionalClass) {
        if (!connected) return null;
        try {
            HashMap<Document, Integer> playersSr = new HashMap<>();
            for (Document document : playersInformation.find()) {
                playersSr.put(document, getSRClass(UUID.fromString((String) document.get("uuid")), optionalClass));
            }
            return playersSr;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem getting players sorted by sr");
            return null;
        }
    }

    public List<Document> getPlayersSortedByKey(String key) {
        if (!connected) return null;
        try {
            return Lists.newArrayList(playersInformation.aggregate(Collections.singletonList(sort(descending(key)))));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Problem getting " + key);
            return null;
        }
    }

    public void clearPlayerCache() {
        cachedPlayerInfo.clear();
    }

    private Document getBaseStatDocument() {
        return new Document("kills", 0)
                .append("assists", 0)
                .append("deaths", 0)
                .append("wins", 0)
                .append("losses", 0)
                .append("flags_captured", 0)
                .append("flags_returned", 0)
                .append("damage", 0)
                .append("healing", 0)
                .append("absorbed", 0);
    }

    public void addPlayer(Player player) {
        if (!connected) return;
        try {
            if (!hasPlayer(player.getUniqueId())) {
                Document newPlayerDocument = new Document("uuid", player.getUniqueId().toString())
                        .append("name", player.getName())
                        .append("kills", 0)
                        .append("assists", 0)
                        .append("deaths", 0)
                        .append("wins", 0)
                        .append("losses", 0)
                        .append("flags_captured", 0)
                        .append("flags_returned", 0)
                        .append("damage", 0)
                        .append("healing", 0)
                        .append("absorbed", 0)
                        .append("mage", getBaseStatDocument()
                                .append("pyromancer", getBaseStatDocument())
                                .append("cryomancer", getBaseStatDocument())
                                .append("aquamancer", getBaseStatDocument())
                        )
                        .append("warrior", getBaseStatDocument()
                                .append("berserker", getBaseStatDocument())
                                .append("defender", getBaseStatDocument())
                                .append("revenant", getBaseStatDocument())
                        )
                        .append("paladin", getBaseStatDocument()
                                .append("avenger", getBaseStatDocument())
                                .append("crusader", getBaseStatDocument())
                                .append("protector", getBaseStatDocument())
                        )
                        .append("shaman", getBaseStatDocument()
                                .append("thunderlord", getBaseStatDocument())
                                .append("spiritguard", getBaseStatDocument())
                                .append("earthwarden", getBaseStatDocument())
                        );
                playersInformation.insertOne(newPlayerDocument);
                System.out.println(player.getUniqueId() + " - " + player.getName() + " was added to the player database");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to add player " + player.getName());
        }
    }

    public void addPlayer(UUID uuid) {
        if (!connected) return;
        try {
            if (!hasPlayer(uuid)) {
                Document newPlayerDocument = new Document("uuid", uuid.toString())
                        .append("name", Bukkit.getServer().getOfflinePlayer(uuid).getName())
                        .append("kills", 0)
                        .append("assists", 0)
                        .append("deaths", 0)
                        .append("wins", 0)
                        .append("losses", 0)
                        .append("flags_captured", 0)
                        .append("flags_returned", 0)
                        .append("damage", 0)
                        .append("healing", 0)
                        .append("absorbed", 0)
                        .append("mage", getBaseStatDocument()
                                .append("pyromancer", getBaseStatDocument())
                                .append("cryomancer", getBaseStatDocument())
                                .append("aquamancer", getBaseStatDocument())
                        )
                        .append("warrior", getBaseStatDocument()
                                .append("berserker", getBaseStatDocument())
                                .append("defender", getBaseStatDocument())
                                .append("revenant", getBaseStatDocument())
                        )
                        .append("paladin", getBaseStatDocument()
                                .append("avenger", getBaseStatDocument())
                                .append("crusader", getBaseStatDocument())
                                .append("protector", getBaseStatDocument())
                        )
                        .append("shaman", getBaseStatDocument()
                                .append("thunderlord", getBaseStatDocument())
                                .append("spiritguard", getBaseStatDocument())
                                .append("earthwarden", getBaseStatDocument())
                        );
                playersInformation.insertOne(newPlayerDocument);
                System.out.println(uuid + " - " + Bukkit.getServer().getOfflinePlayer(uuid).getName() + " was added to the player database");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to add player " + Bukkit.getServer().getOfflinePlayer(uuid).getName());
        }
    }


    public void addGame(PlayingState gameState) {
        if (!connected) return;
        List<Document> blue = new ArrayList<>();
        List<Document> red = new ArrayList<>();
        for (WarlordsPlayer value : PlayerFilter.playingGame(gameState.getGame())) {
            int totalKills = value.getTotalKills();
            int totalAssists = value.getTotalAssists();
            int totalDeaths = value.getTotalDeaths();
            boolean won = !gameState.isForceEnd() && gameState.getStats(value.getTeam()).points() > gameState.getStats(value.getTeam().enemy()).points();
            int flagsCaptured = value.getFlagsCaptured();
            int flagsReturned = value.getFlagsReturned();
            int damage = (int) value.getTotalDamage();
            int healing = (int) value.getTotalHealing();
            int absorbed = (int) value.getTotalAbsorbed();
            String className = value.getSpec().getClassName().toLowerCase();
            String specName = value.getSpecClass().name.toLowerCase();
            HashMap<String, Object> playerInfo = new HashMap<>();
            playerInfo.put("kills", totalKills);
            playerInfo.put("assists", totalAssists);
            playerInfo.put("deaths", totalDeaths);
            playerInfo.put("wins", won ? 1 : 0);
            playerInfo.put("losses", won ? 0 : 1);
            playerInfo.put("flags_captured", flagsCaptured);
            playerInfo.put("flags_returned", flagsReturned);
            playerInfo.put("damage", damage);
            playerInfo.put("healing", healing);
            playerInfo.put("absorbed", absorbed);
            playerInfo.put(className + ".kills", totalKills);
            playerInfo.put(className + ".assists", totalAssists);
            playerInfo.put(className + ".deaths", totalDeaths);
            playerInfo.put(className + ".wins", won ? 1 : 0);
            playerInfo.put(className + ".losses", won ? 0 : 1);
            playerInfo.put(className + ".flags_captured", flagsCaptured);
            playerInfo.put(className + ".flags_returned", flagsCaptured);
            playerInfo.put(className + ".damage", damage);
            playerInfo.put(className + ".healing", healing);
            playerInfo.put(className + ".absorbed", absorbed);
            playerInfo.put(className + "." + specName + ".kills", totalKills);
            playerInfo.put(className + "." + specName + ".assists", totalAssists);
            playerInfo.put(className + "." + specName + ".deaths", totalDeaths);
            playerInfo.put(className + "." + specName + ".wins", won ? 1 : 0);
            playerInfo.put(className + "." + specName + ".losses", won ? 0 : 1);
            playerInfo.put(className + "." + specName + ".flags_captured", flagsCaptured);
            playerInfo.put(className + "." + specName + ".flags_returned", flagsReturned);
            playerInfo.put(className + "." + specName + ".damage", damage);
            playerInfo.put(className + "." + specName + ".healing", healing);
            playerInfo.put(className + "." + specName + ".absorbed", absorbed);
            if (value.getEntity() instanceof Player) {
                updatePlayerInformation((Player) value.getEntity(), playerInfo, FieldUpdateOperators.INCREMENT);
            } else if (value.getEntity() instanceof OfflinePlayer) {
                updatePlayerInformation((OfflinePlayer) value.getEntity(), playerInfo, FieldUpdateOperators.INCREMENT);
            }
            if (value.getTeam() == Team.BLUE) {
                gameAddPlayerStats(blue, value);
            } else {
                gameAddPlayerStats(red, value);
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Team winner = gameState.calculateWinnerByPoints();
        Document document = new Document("date", dateFormat.format(new Date()))
                .append("map", gameState.getGame().getMap().getMapName())
                .append("time_left", gameState.getTimerInSeconds())
                .append("winner", gameState.isForceEnd() || winner == null ? "DRAW" : winner.name.toUpperCase(Locale.ROOT))
                .append("blue_points", gameState.getStats(Team.BLUE).points())
                .append("red_points", gameState.getStats(Team.RED).points())
                .append("players", new Document("blue", blue).append("red", red))
                .append("stat_info", getWarlordsPlusEndGameStats(gameState));
        try {
            gamesInformation.insertOne(document);
            System.out.println("Added game");
        } catch (MongoWriteException e) {
            e.printStackTrace();
            System.out.println("Error trying to insert game stats");
        }
    }

    public String getWarlordsPlusEndGameStats(PlayingState gameState) {
        StringBuilder output = new StringBuilder("Winners:");
        int bluePoints = gameState.getStats(Team.RED).points();
        int redPoints = gameState.getStats(Team.BLUE).points();
        if (bluePoints > redPoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        } else if (redPoints > bluePoints) {
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            output.setLength(output.length() - 1);
            output.append("Losers:");
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        } else {
            output.setLength(0);
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.BLUE)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
            for (WarlordsPlayer player : PlayerFilter.playingGame(gameState.getGame()).matchingTeam(Team.RED)) {
                output.append(player.getUuid().toString().replace("-", "")).append("[").append(player.getTotalKills()).append(":").append(player.getTotalDeaths()).append("],");
            }
        }
        output.setLength(output.length() - 1);
        return output.toString();
    }

    public void gameAddPlayerStats(List<Document> list, WarlordsPlayer warlordsPlayer) {
        list.add(new Document(warlordsPlayer.getUuid().toString(), new Document("name", warlordsPlayer.getName())
                .append("spec", Warlords.getPlayerSettings(warlordsPlayer.getUuid()).selectedClass().name)
                .append("blocks_travelled", warlordsPlayer.getBlocksTravelledCM() / 100)
                .append("seconds_in_combat", warlordsPlayer.getTimeInCombat())
                .append("seconds_in_respawn", warlordsPlayer.getRespawnTimeSpent())
                .append("kills", new BsonArray(Arrays.stream(warlordsPlayer.getKills()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("deaths", new BsonArray(Arrays.stream(warlordsPlayer.getDeaths()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("assists", new BsonArray(Arrays.stream(warlordsPlayer.getAssists()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("damage", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getDamage().length).mapToDouble(i -> warlordsPlayer.getDamage()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("healing", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getHealing().length).mapToDouble(i -> warlordsPlayer.getHealing()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("absorbed", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getAbsorbed().length).mapToDouble(i -> warlordsPlayer.getAbsorbed()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("flag_captures", new BsonInt32(warlordsPlayer.getFlagsCaptured()))
                .append("flag_returns", new BsonInt32(warlordsPlayer.getFlagsReturned()))));
    }

}

package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.player.*;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class DatabaseManager {

    private MongoClient mongoClient;
    private MongoDatabase warlordsPlayersDatabase;
    private MongoDatabase warlordsGamesDatabase;
    private MongoCollection<Document> playersInformation;
    private MongoCollection<Document> gamesInformation;
    private HashMap<UUID, Document> cachedPlayerInfo;

    public DatabaseManager() {
        mongoClient = MongoClients.create("mongodb+srv://warlords_user:ngPnQ076v9MRFAou@warlords.xphds.mongodb.net/myFirstDatabase?retryWrites=true&w=majority");
        warlordsPlayersDatabase = mongoClient.getDatabase("Warlords_Players");
        warlordsGamesDatabase = mongoClient.getDatabase("Warlords_Games");
        playersInformation = warlordsPlayersDatabase.getCollection("Players_Information");
        gamesInformation = warlordsGamesDatabase.getCollection("Games_Information");
        cachedPlayerInfo = new HashMap<>();
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            loadPlayer(onlinePlayer);
        }
    }

    public boolean hasPlayer(Player player) {
        if (cachedPlayerInfo.containsKey(player.getUniqueId())) return true;
        try {
            Document document = playersInformation.find(eq("uuid", player.getUniqueId().toString())).first();
            return document != null;
        } catch (MongoException e) {
            e.printStackTrace();
            System.out.println("Some error while trying to find document");
            return false;
        }
    }

    public void loadPlayer(Player player) {
        try {
            if (hasPlayer(player)) {
                Document playerInfo = playersInformation.find(eq("uuid", player.getUniqueId().toString())).first();
                cachedPlayerInfo.put(player.getUniqueId(), playerInfo);
                Classes.setSelected(player, Classes.getClass((String) getPlayerInformation(player, "last_spec")));
                Weapons.setSelected(player, Weapons.getWeapon((String) getPlayerInformation(player, "last_weapon")));
                ArmorManager.Helmets.setSelectedMage(player, ArmorManager.Helmets.getMageHelmet((String) getPlayerInformation(player, "mage_helm")));
                ArmorManager.ArmorSets.setSelectedMage(player, ArmorManager.ArmorSets.getMageArmor((String) getPlayerInformation(player, "mage_armor")));
                ArmorManager.Helmets.setSelectedWarrior(player, ArmorManager.Helmets.getWarriorHelmet((String) getPlayerInformation(player, "warrior_helm")));
                ArmorManager.ArmorSets.setSelectedWarrior(player, ArmorManager.ArmorSets.getWarriorArmor((String) getPlayerInformation(player, "warrior_armor")));
                ArmorManager.Helmets.setSelectedPaladin(player, ArmorManager.Helmets.getPaladinHelmet((String) getPlayerInformation(player, "paladin_helm")));
                ArmorManager.ArmorSets.setSelectedPaladin(player, ArmorManager.ArmorSets.getPaladinArmor((String) getPlayerInformation(player, "paladin_armor")));
                ArmorManager.Helmets.setSelectedShaman(player, ArmorManager.Helmets.getShamanHelmet((String) getPlayerInformation(player, "shaman_helm")));
                ArmorManager.ArmorSets.setSelectedShaman(player, ArmorManager.ArmorSets.getShamanArmor((String) getPlayerInformation(player, "shaman_armor")));
                Settings.Powerup.setSelected(player, Settings.Powerup.getPowerup((String) getPlayerInformation(player, "powerup")));
                Settings.HotkeyMode.setSelected(player, Settings.HotkeyMode.getHotkeyMode((String) getPlayerInformation(player, "hotkeymode")));
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
        try {
            if (hasPlayer(player)) {
                playersInformation.updateOne(
                        eq("uuid", player.getUniqueId().toString()),
                        combine(set(key, newValue))
                );
                cachedPlayerInfo.remove(player.getUniqueId());
                loadPlayer(player);
                System.out.println(player.getUniqueId() + " - " + player.getName() + " - " + key + " was updated to " + newValue);
            } else {
                System.out.println("Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to update information of player " + player.getName());
        }
    }

    public void updatePlayerInformation(Player player, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        try {
            if (hasPlayer(player)) {
                Document history = new Document();
                for (String s : newInfo.keySet()) {
                    history.append(s, newInfo.get(s));
                }
                Document update = new Document(operator.operator, history);
                playersInformation.updateOne(eq("uuid", player.getUniqueId().toString()), update);
                cachedPlayerInfo.remove(player.getUniqueId());
                loadPlayer(player);
                System.out.println(player.getUniqueId() + " - " + player.getName() + " was updated");
            } else {
                System.out.println("Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to update information of player " + player.getName());
        }
    }

    public Object getPlayerInformation(Player player, String key) {
        try {
            if (cachedPlayerInfo.containsKey(player.getUniqueId())) {
                return cachedPlayerInfo.get(player.getUniqueId()).get(key);
            } else if (hasPlayer(player)) {
                Document playerInfo = playersInformation.find(eq("uuid", player.getUniqueId().toString())).first();
                cachedPlayerInfo.put(player.getUniqueId(), playerInfo);
                assert playerInfo != null;
                return playerInfo.get(key);
            } else {
                System.out.println("Couldn't get player " + player.getName() + " - Not in the database!");
                return null;
            }
        } catch (MongoWriteException e) {
            e.printStackTrace();
            System.out.println("There was an error trying to get player " + player.getName());
            return null;
        }
    }


    public void clearPlayerCache() {
        cachedPlayerInfo.clear();
    }

    public void addPlayer(Player player) {
        try {
            if (!hasPlayer(player)) {
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
                        .append("mage",
                                new Document("kills", 0)
                                        .append("assists", 0)
                                        .append("deaths", 0)
                                        .append("wins", 0)
                                        .append("losses", 0)
                                        .append("flags_captured", 0)
                                        .append("flags_returned", 0)
                                        .append("damage", 0)
                                        .append("healing", 0)
                                        .append("absorbed", 0)
                                        .append("pyromancer",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("cryomancer",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("aquamancer",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                        )
                        .append("warrior",
                                new Document("kills", 0)
                                        .append("assists", 0)
                                        .append("deaths", 0)
                                        .append("wins", 0)
                                        .append("losses", 0)
                                        .append("flags_captured", 0)
                                        .append("flags_returned", 0)
                                        .append("damage", 0)
                                        .append("healing", 0)
                                        .append("absorbed", 0)
                                        .append("berserker",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("defender",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("revenant",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                        )
                        .append("paladin",
                                new Document("kills", 0)
                                        .append("assists", 0)
                                        .append("deaths", 0)
                                        .append("wins", 0)
                                        .append("losses", 0)
                                        .append("flags_captured", 0)
                                        .append("flags_returned", 0)
                                        .append("damage", 0)
                                        .append("healing", 0)
                                        .append("absorbed", 0)
                                        .append("avenger",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("crusader",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("protector",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                        )
                        .append("shaman",
                                new Document("kills", 0)
                                        .append("assists", 0)
                                        .append("deaths", 0)
                                        .append("wins", 0)
                                        .append("losses", 0)
                                        .append("flags_captured", 0)
                                        .append("flags_returned", 0)
                                        .append("damage", 0)
                                        .append("healing", 0)
                                        .append("absorbed", 0)
                                        .append("thunderlord",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("spiritguard",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                                        .append("earthwarden",
                                                new Document("kills", 0)
                                                        .append("assists", 0)
                                                        .append("deaths", 0)
                                                        .append("wins", 0)
                                                        .append("losses", 0)
                                                        .append("flags_captured", 0)
                                                        .append("flags_returned", 0)
                                                        .append("damage", 0)
                                                        .append("healing", 0)
                                                        .append("absorbed", 0))
                        );
                playersInformation.insertOne(newPlayerDocument);
                System.out.println(player.getUniqueId() + " - " + player.getName() + " was added to the player database");
            }
        } catch (MongoWriteException e) {
            System.out.println("There was an error trying to add player " + player.getName());
        }
    }


    public void addGame(Game game) {
        List<Document> blue = new ArrayList<>();
        List<Document> red = new ArrayList<>();
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            int totalKills = value.getTotalKills();
            int totalAssists = value.getTotalAssists();
            int totalDeaths = value.getTotalDeaths();
            boolean won = game.isBlueTeam(value.getPlayer()) && game.getBluePoints() > game.getRedPoints() || game.isRedTeam(value.getPlayer()) && game.getRedPoints() > game.getBluePoints();
            int flagsCaptured = value.getFlagsCaptured();
            int flagsReturned = value.getFlagsReturned();
            int damage = (int) value.getTotalDamage();
            int healing = (int) value.getTotalHealing();
            int absorbed = (int) value.getTotalAbsorbed();
            String className = value.getSpec().getClassName().toLowerCase();
            String specName = Classes.getSelected(value.getPlayer()).name.toLowerCase();
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
            updatePlayerInformation(value.getPlayer(), playerInfo, FieldUpdateOperators.INCREMENT);
            if (game.isBlueTeam(value.getPlayer())) {
                gameAddPlayerStats(blue, value);
            } else {
                gameAddPlayerStats(red, value);
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        Document document = new Document("date", dateFormat.format(new Date()))
                .append("time_left", game.getScoreboardTime())
                .append("winner", game.isForceEnd() ? "DRAW" : game.getBluePoints() > game.getRedPoints() ? "BLUE" : "RED")
                .append("blue_points", game.getBluePoints())
                .append("red_points", game.getRedPoints())
                .append("players", new Document("blue", blue).append("red", red))
                .append("stat_info", game.getWarlordsPlusEndGameStats());
        try {
            gamesInformation.insertOne(document);
        } catch (MongoWriteException e) {
            e.printStackTrace();
            System.out.println("Error trying to insert game stats");
        }
    }

    public void gameAddPlayerStats(List<Document> list, WarlordsPlayer warlordsPlayer) {
        list.add(new Document(warlordsPlayer.getUuid().toString(), new Document("name", warlordsPlayer.getName())
                .append("spec", Classes.getSelected(warlordsPlayer.getPlayer()).name)
                .append("kills", new BsonArray(Arrays.stream(warlordsPlayer.getKills()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("deaths", new BsonArray(Arrays.stream(warlordsPlayer.getDeaths()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("assists", new BsonArray(Arrays.stream(warlordsPlayer.getAssists()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("damage", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getDamage().length).mapToDouble(i -> warlordsPlayer.getDamage()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("healing", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getHealing().length).mapToDouble(i -> warlordsPlayer.getHealing()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("absorbed", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getAbsorbed().length).mapToDouble(i -> warlordsPlayer.getAbsorbed()[i]).toArray()).mapToObj(BsonDouble::new).collect(Collectors.toList())))
                .append("flag_captures", new BsonInt64(warlordsPlayer.getFlagsCaptured()))
                .append("flag_returns", new BsonInt64(warlordsPlayer.getFlagsReturned()))));
    }

}

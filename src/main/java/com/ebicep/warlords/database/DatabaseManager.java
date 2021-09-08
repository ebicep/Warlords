package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.PlayerFilter;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.google.common.collect.Lists;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonArray;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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

    public static boolean connected;
    public static MongoClient mongoClient;
    public static MongoDatabase warlordsPlayersDatabase;
    public static MongoDatabase warlordsGamesDatabase;
    public static MongoCollection<Document> playersInformation;
    public static MongoCollection<Document> gamesInformation;
    public static HashMap<UUID, Document> cachedPlayerInfo = new HashMap<>();
    public static HashMap<String, Long> cachedTotalKeyValues = new HashMap<>();

    public static boolean connect() {
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
//                List<UUID> uuids = new ArrayList<>();
                playersInformation.find().forEach((Consumer<? super Document>) document -> {
//                    UUID uuid = UUID.fromString((String) document.get("uuid"));
//                    uuids.add(uuid);
                    cachedPlayerInfo.put(UUID.fromString((String) document.get("uuid")), document);
                });

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Database Connected");
                connected = true;
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    loadPlayer(onlinePlayer);
                }
//                playersInformation.deleteMany(new Document());
//                for (UUID uuid : uuids) {
//                    addPlayer(uuid);
//                }
                return true;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            connected = false;
        }
        return false;
    }

    public static boolean hasPlayer(UUID uuid) {
        if (!connected) return false;
        if (cachedPlayerInfo.containsKey(uuid)) return true;
        try {
            Document document = playersInformation.find(eq("uuid", uuid.toString())).first();
            return document != null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Some error while trying to find document");
            return false;
        }
    }

    public static void loadPlayer(UUID uuid) {
        if (!connected) return;
        Player player = Bukkit.getPlayer(uuid);
        try {
            if (hasPlayer(uuid)) {
                Document playerInfo = playersInformation.find(eq("uuid", uuid.toString())).first();
                //todo update name
                cachedPlayerInfo.put(uuid, playerInfo);
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
                Settings.ParticleQuality.setSelected(player, Settings.ParticleQuality.getParticleQuality((String) getPlayerInfoWithDotNotation(player, "particle_quality")));
                System.out.println(ChatColor.GREEN + "[Warlords] Loaded player " + player.getName());
            } else {
                addPlayer(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] ERROR loading player - " + player.getName());
        }
    }

    public static void loadPlayer(Player player) {
        loadPlayer(player.getUniqueId());
    }

    public static void updatePlayerInformation(Player player, String key, String newValue) {
        if (!connected) return;
        try {
            if (hasPlayer(player.getUniqueId())) {
                playersInformation.updateOne(
                        eq("uuid", player.getUniqueId().toString()),
                        combine(set(key, newValue))
                );
                cachedPlayerInfo.remove(player.getUniqueId());
                cachedTotalKeyValues.clear();

                System.out.println(ChatColor.GREEN + "[Warlords] " + player.getUniqueId() + " - " + player.getName() + " - " + key + " was updated to " + newValue);
                loadPlayer(player);
            } else {
                System.out.println(ChatColor.GREEN + "[Warlords] Could not update player " + player.getName() + " - Not in the database!");
            }
        } catch (MongoWriteException e) {
            System.out.println(ChatColor.GREEN + "[Warlords] There was an error trying to update information of player " + player.getName());
        }
    }

    public static void updatePlayerInformation(UUID uuid, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        if (!connected) return;
        String name = Bukkit.getPlayer(uuid).getName();
        try {
            if (hasPlayer(uuid)) {
                Document history = new Document();
                for (String s : newInfo.keySet()) {
                    history.append(s, newInfo.get(s));
                }
                Document update = new Document(operator.operator, history);
                playersInformation.updateOne(eq("uuid", uuid.toString()), update);
                cachedPlayerInfo.remove(uuid);
                cachedTotalKeyValues.clear();

                System.out.println(ChatColor.GREEN + "[Warlords] " + uuid + " - " + name + " was updated");
                loadPlayer(uuid);
            } else {
                System.out.println(ChatColor.GREEN + "[Warlords] Could not update player " + name + " - Not in the database!");
            }
        } catch (Exception e) {
            System.out.println(ChatColor.GREEN + "[Warlords] There was an error trying to update information of player " + name);
        }
    }

    public static void updatePlayerInformation(Player player, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        updatePlayerInformation(player.getUniqueId(), newInfo, operator);
    }

    public static void updatePlayerInformation(OfflinePlayer player, HashMap<String, Object> newInfo, FieldUpdateOperators operator) {
        updatePlayerInformation(player.getUniqueId(), newInfo, operator);
    }

    public static Object getPlayerInfoWithDotNotation(Player player, String dots) {
        return getPlayerInfoWithDotNotation(player.getUniqueId(), dots);
    }

    public static Object getPlayerInfoWithDotNotation(UUID uuid, String dots) throws MongoException {
        if (!connected) return null;

        Document doc;
        if (cachedPlayerInfo.containsKey(uuid)) {
            doc = cachedPlayerInfo.get(uuid);
        } else if (hasPlayer(uuid)) {
            doc = playersInformation.find(eq("uuid", uuid.toString())).first();
        } else {
            System.out.println(ChatColor.GREEN + "[Warlords] Couldn't get player " + uuid + " - Not in the database!");
            return null;
        }

        if (doc == null) {
            return null;
        }

        return getDocumentInfoWithDotNotation(doc, dots);
    }

    public static Object getDocumentInfoWithDotNotation(Document document, String dots) throws MongoException {
        if (!connected) return null;

        String[] keys = dots.split("\\.");
        Document doc = document;

        for (int i = 0; i < keys.length - 1; i++) {
            Object o = doc.get(keys[i]);
            if (!(o instanceof Document)) {
                throw new MongoException(String.format(ChatColor.GREEN + "[Warlords] Field '%s' does not exist or s not a Document", keys[i]));
            }
            doc = (Document) o;
        }

        return doc.get(keys[keys.length - 1]);
    }

    public static long getPlayerTotalKey(String key) {
        if (cachedTotalKeyValues.containsKey(key)) return cachedTotalKeyValues.get(key);
        try {
            long total = 0;
            for (Map.Entry<UUID, Document> uuidDocumentEntry : cachedPlayerInfo.entrySet()) {
                Object info = getDocumentInfoWithDotNotation(uuidDocumentEntry.getValue(), key);
                if (info instanceof Integer) {
                    total += (Integer) info;
                } else if (info instanceof Long) {
                    total += (Long) info;
                }
            }
            cachedTotalKeyValues.put(key, total);
            return total;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] There was an error trying to total of " + key);
            return 0L;
        }
    }

    public static void clearPlayerCache() {
        cachedPlayerInfo.clear();
    }

    private static Document getBaseStatDocument() {
        return new Document("kills", 0)
                .append("assists", 0)
                .append("deaths", 0)
                .append("wins", 0)
                .append("losses", 0)
                .append("flags_captured", 0)
                .append("flags_returned", 0)
                .append("damage", 0L)
                .append("healing", 0L)
                .append("absorbed", 0L);
    }

    public static void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
    }

    public static void addPlayer(UUID uuid) {
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
                        .append("damage", 0L)
                        .append("healing", 0L)
                        .append("absorbed", 0L)
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
                System.out.println(ChatColor.GREEN + "[Warlords] " + uuid + " - " + Bukkit.getServer().getOfflinePlayer(uuid).getName() + " was added to the player database");
            }
        } catch (MongoWriteException e) {
            System.out.println(ChatColor.GREEN + "[Warlords] There was an error trying to add player " + Bukkit.getServer().getOfflinePlayer(uuid).getName());
        }
    }

    public static Document getLastGame() {
        Document document = null;
        Iterator<Document> games = gamesInformation.find().iterator();
        while (games.hasNext()) {
            document = games.next();
        }
        return document;
    }

    public static final String[] specsOrdered = {"Pyromancer", "Cryomancer", "Aquamancer", "Berserker", "Defender", "Revenant", "Avenger", "Crusader", "Protector", "Thunderlord", "Spiritguard", "Earthwarden"};

    public static void addLastGameHologram(Location location) {
        Hologram gameInfo = HologramsAPI.createHologram(Warlords.getInstance(), location);
        gameInfo.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last Game Stats");

        Document lastGame = getLastGame();
        int timeLeft = (int) getDocumentInfoWithDotNotation(lastGame, "time_left");
        gameInfo.appendTextLine(ChatColor.GRAY.toString() + getDocumentInfoWithDotNotation(lastGame, "date"));
        gameInfo.appendTextLine(ChatColor.GREEN.toString() + getDocumentInfoWithDotNotation(lastGame, "map") + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        gameInfo.appendTextLine(ChatColor.BLUE.toString() + getDocumentInfoWithDotNotation(lastGame, "blue_points") + ChatColor.GRAY + "  -  " + ChatColor.RED.toString() + getDocumentInfoWithDotNotation(lastGame, "red_points"));

        List<String> players = new ArrayList<>();
        List<DatabasePlayer> databasePlayers = new ArrayList<>();

        for (Document o : ((ArrayList<Document>) getDocumentInfoWithDotNotation(lastGame, "players.blue"))) {
            databasePlayers.add(new DatabasePlayer((String) o.get("name"), ChatColor.BLUE, (String) o.get("spec"), (ArrayList<Integer>) o.get("kills"), (ArrayList<Integer>) o.get("assists"), (ArrayList<Integer>) o.get("deaths")));
        }
        for (Document o : ((ArrayList<Document>) getDocumentInfoWithDotNotation(lastGame, "players.red"))) {
            databasePlayers.add(new DatabasePlayer((String) o.get("name"), ChatColor.RED, (String) o.get("spec"), (ArrayList<Integer>) o.get("kills"), (ArrayList<Integer>) o.get("assists"), (ArrayList<Integer>) o.get("deaths")));
        }

        for (String s : specsOrdered) {
            StringBuilder playerSpecs = new StringBuilder(ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            databasePlayers.stream().filter(o -> o.getSpec().equals(s)).forEach(p -> {
                playerSpecs.append(p.getColoredName()).append(p.getKDA()).append(ChatColor.GRAY).append(", ");
                add[0] = true;
            });
            if (add[0]) {
                playerSpecs.setLength(playerSpecs.length() - 2);
                players.add(playerSpecs.toString());
            }
        }

        players.forEach(gameInfo::appendTextLine);

    }

    static class DatabasePlayer {
        private String name;
        private ChatColor teamColor;
        private String spec;
        private ArrayList<Integer> kills;
        private ArrayList<Integer> assists;
        private ArrayList<Integer> deaths;

        public DatabasePlayer(String name, ChatColor teamColor, String spec, ArrayList<Integer> kills, ArrayList<Integer> assists, ArrayList<Integer> deaths) {
            this.name = name;
            this.teamColor = teamColor;
            this.spec = spec;
            this.kills = kills;
            this.assists = assists;
            this.deaths = deaths;
        }

        public String getColoredName() {
            return this.teamColor + this.name;
        }

        public String getSpec() {
            return spec;
        }

        public String getKDA() {
            return ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + getTotalKills() + ChatColor.GRAY + ":" + ChatColor.GOLD + getTotalAssists() + ChatColor.GRAY + ":" + ChatColor.RED + getTotalDeaths() + ChatColor.DARK_GRAY + "]";
        }

        public int getTotalKills() {
            return kills.stream().reduce(0, Integer::sum);
        }

        public int getTotalAssists() {
            return assists.stream().reduce(0, Integer::sum);
        }

        public int getTotalDeaths() {
            return deaths.stream().reduce(0, Integer::sum);
        }

    }

    public static void addGame(PlayingState gameState) {
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
            long damage = (int) value.getTotalDamage();
            long healing = (int) value.getTotalHealing();
            long absorbed = (int) value.getTotalAbsorbed();
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

            updatePlayerInformation(value.getUuid(), playerInfo, FieldUpdateOperators.INCREMENT);

            if (value.getTeam() == Team.BLUE) {
                gameAddPlayerStats(blue, value);
            } else {
                gameAddPlayerStats(red, value);
            }
        }
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
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
            System.out.println(ChatColor.GREEN + "[Warlords] Added game");
        } catch (MongoWriteException e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Error trying to insert game stats");
        }
    }

    public static String getWarlordsPlusEndGameStats(PlayingState gameState) {
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

    public static void gameAddPlayerStats(List<Document> list, WarlordsPlayer warlordsPlayer) {
        StringBuilder xLocations = new StringBuilder();
        StringBuilder zLocations = new StringBuilder();
        for (Location location : warlordsPlayer.getLocations()) {
            xLocations.append((int) location.getX()).append(",");
            zLocations.append((int) location.getZ()).append(",");
        }
        list.add(new Document()
                .append("uuid", warlordsPlayer.getUuid().toString())
                .append("name", warlordsPlayer.getName())
                .append("spec", Warlords.getPlayerSettings(warlordsPlayer.getUuid()).getSelectedClass().name)
                .append("blocks_travelled", warlordsPlayer.getBlocksTravelledCM() / 100)
                .append("seconds_in_combat", warlordsPlayer.getTimeInCombat())
                .append("seconds_in_respawn", warlordsPlayer.getRespawnTimeSpent())
                .append("x_locations", xLocations.toString())
                .append("z_locations", zLocations.toString())
                .append("kills", new BsonArray(Arrays.stream(warlordsPlayer.getKills()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("deaths", new BsonArray(Arrays.stream(warlordsPlayer.getDeaths()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("assists", new BsonArray(Arrays.stream(warlordsPlayer.getAssists()).mapToObj(BsonInt32::new).collect(Collectors.toList())))
                .append("damage", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getDamage().length).mapToLong(i -> (long) warlordsPlayer.getDamage()[i]).toArray()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("healing", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getHealing().length).mapToLong(i -> (long) warlordsPlayer.getHealing()[i]).toArray()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("absorbed", new BsonArray(Arrays.stream(IntStream.range(0, warlordsPlayer.getAbsorbed().length).mapToLong(i -> (long) warlordsPlayer.getAbsorbed()[i]).toArray()).mapToObj(BsonInt64::new).collect(Collectors.toList())))
                .append("flag_captures", new BsonInt32(warlordsPlayer.getFlagsCaptured()))
                .append("flag_returns", new BsonInt32(warlordsPlayer.getFlagsReturned())));
    }

    public static MongoCollection<Document> getGamesInformation() {
        return gamesInformation;
    }
}

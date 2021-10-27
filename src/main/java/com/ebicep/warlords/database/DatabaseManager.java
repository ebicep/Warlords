package com.ebicep.warlords.database;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.state.PlayingState;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.PlayerFilter;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Sorts;
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

import static com.mongodb.client.model.Filters.and;
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
    public static MongoCollection<Document> playersInformationWeekly;
    public static MongoCollection<Document> gamesInformation;
    public static HashMap<UUID, Document> cachedPlayerInfo = new HashMap<>();
    public static HashMap<String, Long> cachedTotalKeyValues = new HashMap<>();
    public static String lastWarlordsPlusString = "";

    public static final List<DatabaseGame> previousGames = new ArrayList<>();

    public static void connect() {
        try {
            Bukkit.getServer().getConsoleSender().sendMessage(System.getProperty("user.dir"));
            File myObj = new File(System.getProperty("user.dir") + "/plugins/Warlords/database_key.TXT");
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                mongoClient = MongoClients.create(data);

                warlordsPlayersDatabase = mongoClient.getDatabase("Warlords_Players");
                warlordsGamesDatabase = mongoClient.getDatabase("Warlords_Games");

                playersInformation = warlordsPlayersDatabase.getCollection("Players_Information");
                playersInformationWeekly = warlordsPlayersDatabase.getCollection("Players_Information_Weekly");
                gamesInformation = warlordsGamesDatabase.getCollection("Games_Information");

                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Database Connected");
                connected = true;

                //caching all players
                playersInformation.find().forEach((Consumer<? super Document>) document -> {
                    cachedPlayerInfo.put(UUID.fromString((String) document.get("uuid")), document);
                });

                //caching all games
                Warlords.newChain()
                        .asyncFirst(() -> {
                            List<DatabaseGame> tempPreviousGames = new ArrayList<>();
                            gamesInformation.find()
                                    .skip((int) (gamesInformation.countDocuments() - 20))
                                    .forEach((Consumer<? super Document>) game -> {

                                        //player information
                                        List<DatabaseGamePlayer> databaseGamePlayersBlue = new ArrayList<>();
                                        List<DatabaseGamePlayer> databaseGamePlayersRed = new ArrayList<>();

                                        HashMap<UUID, HashMap<String, Object>> newPlayerInfo = new HashMap<>();
                                        ArrayList<Document> players = new ArrayList<>();
                                        ArrayList<Document> playersBlue = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(game, "players.blue"));
                                        ArrayList<Document> playersRed = new ArrayList<>((ArrayList<Document>) getDocumentInfoWithDotNotation(game, "players.red"));
                                        players.addAll(playersBlue);
                                        players.addAll(playersRed);

                                        for (Document document : players) {
                                            DatabaseGamePlayer databaseGamePlayer = new DatabaseGamePlayer(document, playersBlue.contains(document) ? ChatColor.BLUE : ChatColor.RED);
                                            int totalKills = databaseGamePlayer.getTotalKills();
                                            int totalAssists = databaseGamePlayer.getTotalAssists();
                                            int totalDeaths = databaseGamePlayer.getTotalDeaths();
                                            boolean won = game.get("winner") == "BLUE" && playersBlue.contains(document) || game.get("winner") == "RED" && playersRed.contains(document);
                                            int flagsCaptured = databaseGamePlayer.getFlagCaptures();
                                            int flagsReturned = databaseGamePlayer.getFlagReturns();
                                            long damage = databaseGamePlayer.getTotalDamage();
                                            long healing = databaseGamePlayer.getTotalHealing();
                                            long absorbed = databaseGamePlayer.getTotalAbsorbed();
                                            String className = Classes.getClassesGroup(databaseGamePlayer.getSpec()).name.toLowerCase();
                                            String specName = databaseGamePlayer.getSpec().toLowerCase();
                                            HashMap<String, Object> playerInfo = new HashMap<>();
                                            playerInfo.put("kills", databaseGamePlayer.getTotalKills());
                                            playerInfo.put("assists", databaseGamePlayer.getTotalAssists());
                                            playerInfo.put("deaths", databaseGamePlayer.getTotalDeaths());
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

                                            if (databaseGamePlayer.getTeamColor() == ChatColor.BLUE) {
                                                databaseGamePlayersBlue.add(databaseGamePlayer);
                                            } else {
                                                databaseGamePlayersRed.add(databaseGamePlayer);
                                            }
                                            newPlayerInfo.put(UUID.fromString(databaseGamePlayer.getUuid()), playerInfo);
                                        }

                                        tempPreviousGames.add(new DatabaseGame(game, databaseGamePlayersBlue, databaseGamePlayersRed, newPlayerInfo, (boolean) game.get("counted")));
                                    });
                            return tempPreviousGames;
                        }).syncLast(previousGames::addAll)
                        .execute();

                //loading all players
                loadAllPlayers();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            connected = false;
        }
    }

    /**
     * This method must be used in an async context.
     *
     * @param uuid UUID of the player to search
     * @return {@code Document} of the player in the database; {@code null} if the player is not in the database
     */
    public static Document getPlayer(UUID uuid) {
        Document document = playersInformation.find(eq("uuid", uuid.toString())).first();
        if (cachedPlayerInfo.containsKey(uuid)) {
            return cachedPlayerInfo.get(uuid);
        }
        if (document == null) {
            addPlayer(uuid);
            return null;
        }
        return document;
    }

    public static Document getPlayer(OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * This method must be used in a sync context.
     * Loads all online players.
     */
    public static void loadAllPlayers() {
        if (!connected) return;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        try {
            Warlords.newChain()
                    .asyncFirst(() -> { //async loading all player information
                        HashMap<UUID, Document> playerInfo = new HashMap<>();
                        players.forEach(player -> {
                            Document document = getPlayer(player);
                            updateName(document, player);
                            playerInfo.put(player.getUniqueId(), document);
                        });
                        return playerInfo;
                    }).syncLast(playerInfo -> { //sync updating all player information on the server
                        playerInfo.forEach((uuid, document) -> {
                            if (document != null) {
                                cachedPlayerInfo.put(uuid, document);
                                Player player = Bukkit.getPlayer(uuid);
                                if (player != null) {
                                    loadPlayerInfo(player);
                                }
                            }
                        });
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Loaded all players");
                    }).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * This method must be used in a sync context.
     *
     * @param document {@code Document} of the players information
     * @param uuid     {@code UUID} of the player
     */
    private static void loadPlayer(Document document, UUID uuid) {
        updateName(document, Bukkit.getPlayer(uuid));
        if (document == null) {
            addPlayer(uuid);
        } else {
            Player player = Bukkit.getPlayer(uuid);
            cachedPlayerInfo.put(uuid, document);
            loadPlayerInfo(player);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Loaded player " + player.getName());
        }
    }

    /**
     * @param uuid     {@code UUID} of the player to load
     * @param runAsync {@code true} if this method is to be run async; {@code false} if this method is to be run sync or to block the current thread to ensure this method runs before any code after
     */
    public static void loadPlayer(UUID uuid, boolean runAsync) {
        if (!connected) return;
        Player player = Bukkit.getPlayer(uuid);
        try {
            if (runAsync) {
                Warlords.newChain()
                        .asyncFirst(() -> getPlayer(uuid))
                        .syncLast((document) -> {
                            loadPlayer(document, uuid);
                        }).execute();
            } else {
                Document document = getPlayer(uuid);
                loadPlayer(document, uuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] ERROR loading player - " + player.getName());
        }
    }

    public static void loadPlayer(OfflinePlayer player, boolean runAsync) {
        loadPlayer(player.getUniqueId(), runAsync);
    }

    /**
     * This method must be used in a sync context.
     * Updates player information (Class, Armor, Weapons, Settings).
     *
     * @param player Player to update their information
     */
    private static void loadPlayerInfo(Player player) {
        Classes.setSelected(player, Classes.getClass((String) getPlayerInfoWithDotNotation(player, "last_spec")));
        ArmorManager.Helmets.setSelectedMage(player, ArmorManager.Helmets.getMageHelmet((String) getPlayerInfoWithDotNotation(player, "mage.helm")));
        ArmorManager.ArmorSets.setSelectedMage(player, ArmorManager.ArmorSets.getMageArmor((String) getPlayerInfoWithDotNotation(player, "mage.armor")));
        ArmorManager.Helmets.setSelectedWarrior(player, ArmorManager.Helmets.getWarriorHelmet((String) getPlayerInfoWithDotNotation(player, "warrior.helm")));
        ArmorManager.ArmorSets.setSelectedWarrior(player, ArmorManager.ArmorSets.getWarriorArmor((String) getPlayerInfoWithDotNotation(player, "warrior.armor")));
        ArmorManager.Helmets.setSelectedPaladin(player, ArmorManager.Helmets.getPaladinHelmet((String) getPlayerInfoWithDotNotation(player, "paladin.helm")));
        ArmorManager.ArmorSets.setSelectedPaladin(player, ArmorManager.ArmorSets.getPaladinArmor((String) getPlayerInfoWithDotNotation(player, "paladin.armor")));
        ArmorManager.Helmets.setSelectedShaman(player, ArmorManager.Helmets.getShamanHelmet((String) getPlayerInfoWithDotNotation(player, "shaman.helm")));
        ArmorManager.ArmorSets.setSelectedShaman(player, ArmorManager.ArmorSets.getShamanArmor((String) getPlayerInfoWithDotNotation(player, "shaman.armor")));
        HashMap<Classes, Weapons> weaponSkins = new HashMap<>();
        for (Classes value : Classes.values()) {
            weaponSkins.put(value, Weapons.getWeapon(
                    (String) getPlayerInfoWithDotNotation(player, Classes.getClassesGroup(value).name.toLowerCase() + "." + value.name.toLowerCase() + ".weapon")));
        }
        Weapons.setSelected(player, weaponSkins);
        Settings.HotkeyMode.setSelected(player, Settings.HotkeyMode.getHotkeyMode((String) getPlayerInfoWithDotNotation(player, "hotkeymode")));
        Settings.ParticleQuality.setSelected(player, Settings.ParticleQuality.getParticleQuality((String) getPlayerInfoWithDotNotation(player, "particle_quality")));
    }

    /**
     * Updates a players name on the database
     *
     * @param document {@code Document} of player info
     * @param player   {@code Player} to be updated
     */
    private static void updateName(Document document, Player player) {
        if (document != null && !((String) document.get("name")).equalsIgnoreCase(player.getName())) {
            Warlords.newChain()
                    .async(() -> {
                        playersInformation.updateOne(
                                eq("uuid", player.getUniqueId().toString()),
                                combine(set("name", player.getName()))
                        );
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Updated player name " + player.getName());
                    }).execute();
        }
    }

    /**
     * This method must be used in a sync context and if the updated information is not being used shortly after.
     * Updates the key with a new value of a player on the database.
     *
     * @param player   {@code Player} to be updated on the database
     * @param key      The key to be updated
     * @param newValue The new value of the key
     */
    public static void updatePlayerInformation(Player player, String key, String newValue) {
        if (!connected) return;
        try {
            Warlords.newChain()
                    .asyncFirst(() -> {
                        Document document = getPlayer(player.getUniqueId());
                        if (document == null) {
                            return null;
                        }
                        playersInformation.updateOne(
                                eq("uuid", player.getUniqueId().toString()),
                                combine(set(key, newValue))
                        );
                        return true;
                    })
                    .abortIfNull()
                    .sync(() -> {
                        cachedPlayerInfo.remove(player.getUniqueId());
                        cachedTotalKeyValues.clear();

                        loadPlayer(player.getUniqueId(), true);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] " + player.getUniqueId() + " - " + player.getName() + " - " + key + " was updated to " + newValue);
                    }).execute();
        } catch (MongoWriteException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] There was an error trying to update information of player " + player.getName());
        }
    }

    /**
     * @param uuid     {@code UUID} of the player
     * @param newInfo  {@code HashMap<String, Object>} of the new information
     * @param operator {@code FieldUpdateOperators} operator to apply the new information
     * @param runAsync {@code true} if this method is to be run async; {@code false} if this method is to be run sync or to block the current thread to ensure this method runs before any code after
     */
    public static void updatePlayerInformation(UUID uuid, HashMap<String, Object> newInfo, FieldUpdateOperators operator, boolean runAsync) {
        if (!connected) return;
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        try {
            if (runAsync) {
                Warlords.newChain()
                        .asyncFirst(() -> getPlayer(uuid))
                        .abortIfNull()
                        .async(() -> {
                            Document history = new Document();
                            for (String s : newInfo.keySet()) {
                                history.append(s, newInfo.get(s));
                            }
                            Document update = new Document(operator.operator, history);
                            playersInformation.updateOne(eq("uuid", uuid.toString()), update);
                        }).sync(() -> {
                            cachedPlayerInfo.remove(uuid);
                            cachedTotalKeyValues.clear();

                            loadPlayer(uuid, true);
                            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] " + uuid + " - " + name + " was updated");
                        }).execute();
            } else {
                if (getPlayer(uuid) == null) {
                    return;
                }
                Document history = new Document();
                for (String s : newInfo.keySet()) {
                    history.append(s, newInfo.get(s));
                }
                Document update = new Document(operator.operator, history);
                playersInformation.updateOne(eq("uuid", uuid.toString()), update);
                cachedPlayerInfo.remove(uuid);
                cachedTotalKeyValues.clear();

                loadPlayer(uuid, true);
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] " + uuid + " - " + name + " was updated");
            }
        } catch (Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] There was an error trying to update information of player " + name);
        }
    }

    public static void updatePlayerInformation(Player player, HashMap<String, Object> newInfo, FieldUpdateOperators operator, boolean runAsync) {
        updatePlayerInformation(player.getUniqueId(), newInfo, operator, runAsync);
    }

    public static void updatePlayerInformation(OfflinePlayer player, HashMap<String, Object> newInfo, FieldUpdateOperators operator, boolean runAsync) {
        updatePlayerInformation(player.getUniqueId(), newInfo, operator, runAsync);
    }

    public static Object getPlayerInfoWithDotNotation(Player player, String dots) {
        return getPlayerInfoWithDotNotation(player.getUniqueId(), dots);
    }

    /**
     * Precondition: cachedPlayerInfo must contain the {@code UUID} player
     *
     * @param uuid {@code UUID} of the player
     * @param dots String of the targeted information (e.g. = mage.damage)
     * @return An {@code Object} of the targeted information in the database
     * @throws MongoException If the targeted information does not exist or is not a document
     */
    public static Object getPlayerInfoWithDotNotation(UUID uuid, String dots) throws MongoException {
        if (!connected) return null;

        Document doc = cachedPlayerInfo.get(uuid);

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
                throw new MongoException(String.format(ChatColor.GREEN + "[Warlords] Field '%s' does not exist or is not a Document", keys[i]));
            }
            doc = (Document) o;
        }

        return doc.get(keys[keys.length - 1]);
    }

    /**
     * @param key The targeted information
     * @return The total values of the key from all players
     */
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] There was an error trying to total of " + key);
            return 0L;
        }
    }

    private static Document getNewPlayerDocument(UUID uuid) {
        return new Document("uuid", uuid.toString())
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

    public static void addPlayer(UUID uuid) {
        if (!connected) return;
        try {
            Warlords.newChain()
                    .asyncFirst(() -> playersInformation.find(eq("uuid", uuid.toString())).first())
                    .abortIf(Objects::nonNull)
                    .asyncFirst(() -> {
                        Document newPlayerDocument = getNewPlayerDocument(uuid);
                        playersInformation.insertOne(newPlayerDocument);
                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] " + uuid + " - " + Bukkit.getServer().getOfflinePlayer(uuid).getName() + " was added to the player database");
                        return newPlayerDocument;
                    }).syncLast(doc -> {
                        cachedPlayerInfo.put(uuid, doc);
                    }).execute();
        } catch (MongoWriteException e) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] There was an error trying to add player " + Bukkit.getServer().getOfflinePlayer(uuid).getName());
        }
    }

    public static void addPlayer(Player player) {
        addPlayer(player.getUniqueId());
    }

    public static Document getLastGame() {
        return gamesInformation.find().sort(Sorts.descending("_id")).limit(1).first();
    }

    public static void removeGameFromDatabase(DatabaseGame gameInformation) {
        try {
            Warlords.newChain()
                    .async(() -> {
                        if (gameInformation.isUpdatePlayerStats()) {
                            //updating all players, blocks this async thread, so leaderboard updated after
                            gameInformation.getPlayerInfoNegative().forEach((uuid, stringObjectHashMap) -> {
                                updatePlayerInformation(uuid, stringObjectHashMap, FieldUpdateOperators.INCREMENT, false);
                            });
                        }
                        //removing the game from the database
                        gamesInformation.deleteOne(and(
                                eq("date", gameInformation.getGameInfo().get("date")),
                                eq("time_left", gameInformation.getGameInfo().get("time_left"))
                        ));
                        //reloading leaderboards
                        Leaderboards.playerGameHolograms.forEach((uuid, integer) -> {
                            Leaderboards.playerGameHolograms.put(uuid, previousGames.size() - 1);
                        });
                        Leaderboards.addHologramLeaderboards(UUID.randomUUID().toString());
                    }).execute();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Error trying to remove game stats");
        }
    }


    public static void addGameToDatabase(DatabaseGame gameInformation) {
        try {
            Warlords.newChain()
                    .async(() -> {
                        if (gameInformation.isUpdatePlayerStats()) {
                            //updating all players, blocks this async thread, so leaderboard updated after
                            gameInformation.getPlayerInfo().forEach((uuid, stringObjectHashMap) -> {
                                updatePlayerInformation(uuid, stringObjectHashMap, FieldUpdateOperators.INCREMENT, false);
                            });
                        }
                        //inserting the game to the database
                        gamesInformation.insertOne(gameInformation.getGameInfo());
                        //reloading leaderboards
                        Leaderboards.playerGameHolograms.forEach((uuid, integer) -> {
                            Leaderboards.playerGameHolograms.put(uuid, previousGames.size() - 1);
                        });
                        Leaderboards.addHologramLeaderboards(UUID.randomUUID().toString());
                    }).execute();
            System.out.println(ChatColor.GREEN + "[Warlords] Added game");
        } catch (MongoWriteException e) {
            e.printStackTrace();
            System.out.println(ChatColor.GREEN + "[Warlords] Error trying to insert game stats");
        }
    }

    public static void addGame(PlayingState gameState, boolean updatePlayerStats) {
        if (!connected) return;
        List<Document> blue = new ArrayList<>();
        List<Document> red = new ArrayList<>();
        for (WarlordsPlayer value : PlayerFilter.playingGame(gameState.getGame())) {
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
                .append("stat_info", getWarlordsPlusEndGameStats(gameState))
                .append("counted", updatePlayerStats);
        try {
            HashMap<UUID, HashMap<String, Object>> newPlayerInfo = getNewPlayerInfo(gameState);
            DatabaseGame gameInformation = new DatabaseGame(document,
                    blue.stream().map(d -> new DatabaseGamePlayer(d, ChatColor.BLUE)).collect(Collectors.toList()),
                    red.stream().map(d -> new DatabaseGamePlayer(d, ChatColor.RED)).collect(Collectors.toList()),
                    newPlayerInfo,
                    updatePlayerStats
            );
            previousGames.add(gameInformation);
            addGameToDatabase(gameInformation);

            //sending message if player information remained the same
            for (WarlordsPlayer value : PlayerFilter.playingGame(gameState.getGame())) {
                if (value.getEntity().isOp()) {
                    if (updatePlayerStats) {
                        value.sendMessage(ChatColor.GREEN + "This game was added to the database and player information was updated");
                    } else {
                        value.sendMessage(ChatColor.GREEN + "This game was added to the database but player information remained the same");
                    }
                }
            }
        } catch (MongoWriteException e) {
            e.printStackTrace();
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Error trying to insert game stats");
        }
    }

    private static HashMap<UUID, HashMap<String, Object>> getNewPlayerInfo(PlayingState gameState) {
        HashMap<UUID, HashMap<String, Object>> newPlayerInfo = new HashMap<>();
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

            newPlayerInfo.put(value.getUuid(), playerInfo);
        }
        return newPlayerInfo;
    }

    public static String getWarlordsPlusEndGameStats(PlayingState gameState) {
        StringBuilder output = new StringBuilder("Winners:");
        int bluePoints = gameState.getStats(Team.BLUE).points();
        int redPoints = gameState.getStats(Team.RED).points();
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
        BotManager.getTextChannelByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage(output.toString()).queue());
        lastWarlordsPlusString = output.toString();
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
                .append("seconds_in_respawn", Math.round(warlordsPlayer.getRespawnTimeSpent()))
                .append("x_locations", xLocations.toString())
                .append("z_locations", zLocations.toString())
                .append("kills", Arrays.stream(warlordsPlayer.getKills()).boxed().collect(Collectors.toList()))
                .append("deaths", Arrays.stream(warlordsPlayer.getDeaths()).boxed().collect(Collectors.toList()))
                .append("assists", Arrays.stream(warlordsPlayer.getAssists()).boxed().collect(Collectors.toList()))
                .append("damage", IntStream.range(0, warlordsPlayer.getDamage().length).mapToLong(i -> (long) warlordsPlayer.getDamage()[i]).boxed().collect(Collectors.toList()))
                .append("healing", IntStream.range(0, warlordsPlayer.getHealing().length).mapToLong(i -> (long) warlordsPlayer.getHealing()[i]).boxed().collect(Collectors.toList()))
                .append("absorbed", IntStream.range(0, warlordsPlayer.getAbsorbed().length).mapToLong(i -> (long) warlordsPlayer.getAbsorbed()[i]).boxed().collect(Collectors.toList()))
                .append("flag_captures",warlordsPlayer.getFlagsCaptured())
                .append("flag_returns", warlordsPlayer.getFlagsReturned())
        );
    }
}

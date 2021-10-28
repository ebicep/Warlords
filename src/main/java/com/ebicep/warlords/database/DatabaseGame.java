package com.ebicep.warlords.database;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DatabaseGame {

    private final Document gameInfo;
    private final List<DatabaseGamePlayer> bluePlayers;
    private final List<DatabaseGamePlayer> redPlayers;
    private final HashMap<UUID, HashMap<String, Object>> playerInfo;
    private final HashMap<UUID, HashMap<String, Object>> playerInfoNegative = new HashMap<>();
    private final boolean updatePlayerStats;

    public DatabaseGame(Document gameInfo, List<DatabaseGamePlayer> bluePlayers, List<DatabaseGamePlayer> redPlayers, HashMap<UUID, HashMap<String, Object>> playerInfo, boolean updatePlayerStats) {
        this.gameInfo = gameInfo;
        this.playerInfo = playerInfo;
        this.bluePlayers = bluePlayers;
        this.redPlayers = redPlayers;
        playerInfo.forEach((uuid, stringObjectHashMap) -> {
            HashMap<String, Object> newHashMap = new HashMap<>();
            stringObjectHashMap.forEach((s, o) -> {
                if (o instanceof Integer) {
                    newHashMap.put(s, -((Integer) o));
                } else if (o instanceof Long) {
                    newHashMap.put(s, -((Long) o));
                }
            });
            this.playerInfoNegative.put(uuid, newHashMap);
        });
        this.updatePlayerStats = updatePlayerStats;
    }



    public List<DatabaseGamePlayer> getDatabasePlayers() {
        List<DatabaseGamePlayer> databaseGamePlayers = new ArrayList<>();
        databaseGamePlayers.addAll(bluePlayers);
        databaseGamePlayers.addAll(redPlayers);
        return databaseGamePlayers;
    }

    public List<DatabaseGamePlayer> getBluePlayers() {
        return bluePlayers;
    }

    public List<DatabaseGamePlayer> getRedPlayers() {
        return redPlayers;
    }

    public Document getGameInfo() {
        return gameInfo;
    }

    public String getDate() {
        return (String) gameInfo.get("date");
    }

    public String getMap() {
        return (String) gameInfo.get("map");
    }

    public int getTimeLeft() {
        return (int) gameInfo.get("time_left");
    }

    public String getWinner() {
        return (String) gameInfo.get("winner");
    }

    public int getBluePoints() {
        return (int) gameInfo.get("blue_points");
    }

    public int getRedPoints() {
        return (int) gameInfo.get("red_points");
    }

    public HashMap<UUID, HashMap<String, Object>> getPlayerInfo() {
        return playerInfo;
    }

    public HashMap<UUID, HashMap<String, Object>> getPlayerInfoNegative() {
        return playerInfoNegative;
    }

    public HashMap<String, Object> getPlayer(UUID uuid) {
        return playerInfo.get(uuid);
    }

    public String getGameLabel() {
        return ChatColor.GRAY.toString() + gameInfo.get("date") + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + gameInfo.get("map") + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + gameInfo.get("blue_points") + ChatColor.GRAY + ":" + ChatColor.RED + gameInfo.get("red_points") + ChatColor.GRAY + ")";
    }

    public boolean isUpdatePlayerStats() {
        return updatePlayerStats;
    }

}
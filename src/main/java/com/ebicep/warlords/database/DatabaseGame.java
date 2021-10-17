package com.ebicep.warlords.database;

import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.UUID;

public class DatabaseGame {

    private final Document gameInfo;
    private final HashMap<UUID, HashMap<String, Object>> playerInfo;
    private final HashMap<UUID, HashMap<String, Object>> playerInfoNegative = new HashMap<>();

    public DatabaseGame(Document gameInfo, HashMap<UUID, HashMap<String, Object>> playerInfo) {
        this.gameInfo = gameInfo;
        this.playerInfo = playerInfo;
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
    }

    public Document getGameInfo() {
        return gameInfo;
    }

    public HashMap<UUID, HashMap<String, Object>> getPlayerInfo() {
        return playerInfo;
    }

    public HashMap<UUID, HashMap<String, Object>> getPlayerInfoNegative() {
        return playerInfoNegative;
    }

    public String getGameLabel() {
        return ChatColor.GRAY.toString() + gameInfo.get("date") + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + gameInfo.get("map") + ChatColor.DARK_GRAY + " - " +
                ChatColor.GRAY + "(" + ChatColor.BLUE + gameInfo.get("blue_points") + ChatColor.GRAY + ":" + ChatColor.RED + gameInfo.get("red_points") + ChatColor.GRAY + ")";
    }
}
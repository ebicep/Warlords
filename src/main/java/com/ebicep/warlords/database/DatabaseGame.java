package com.ebicep.warlords.database;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.Utils;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.*;

public class DatabaseGame {

    private final Document gameInfo;
    private final List<DatabaseGamePlayer> bluePlayers;
    private final List<DatabaseGamePlayer> redPlayers;
    private final HashMap<UUID, HashMap<String, Object>> playerInfo;
    private final HashMap<UUID, HashMap<String, Object>> playerInfoNegative = new HashMap<>();
    private final boolean updatePlayerStats;

    private List<Hologram> holograms = new ArrayList<>();

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

    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        Hologram lastGameStats = HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.gameHologramLocations.get(0));
        holograms.add(lastGameStats);
        lastGameStats.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last Game Stats");

        Hologram topDamage = HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.gameHologramLocations.get(1));
        holograms.add(topDamage);
        topDamage.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.gameHologramLocations.get(2));
        holograms.add(topHealing);
        topHealing.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HologramsAPI.createHologram(Warlords.getInstance(), Leaderboards.gameHologramLocations.get(3));
        holograms.add(topAbsorbed);
        topAbsorbed.appendTextLine(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        //last game stats
        int timeLeft = getTimeLeft();
        lastGameStats.appendTextLine(ChatColor.GRAY + getDate());
        lastGameStats.appendTextLine(ChatColor.GREEN + getMap() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + timeLeft / 60 + ":" + timeLeft % 60 + (timeLeft % 60 < 10 ? "0" : ""));
        lastGameStats.appendTextLine(ChatColor.BLUE.toString() + getBluePoints() + ChatColor.GRAY + "  -  " + ChatColor.RED + getRedPoints());

        List<DatabaseGamePlayer> databaseGamePlayers = getDatabasePlayers();
        List<String> players = new ArrayList<>();

        for (String s : Leaderboards.specsOrdered) {
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
        players.forEach(lastGameStats::appendTextLine);

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

        //setting visibility to none
        holograms.forEach(hologram -> {
            hologram.getVisibilityManager().setVisibleByDefault(false);
        });

        this.holograms = holograms;
    }

    public List<Hologram> getHolograms() {
        if(holograms.isEmpty()) {
            createHolograms();
        }
        return holograms;
    }

    public void deleteHologram() {
        holograms.forEach(Hologram::delete);
    }

}
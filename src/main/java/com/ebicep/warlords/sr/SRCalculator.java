package com.ebicep.warlords.sr;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.Stats;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SRCalculator {

    public static final HashMap<Function<DatabasePlayer, Double>, Double> TOTAL_VALUES = new HashMap<>();
    public static final HashMap<DatabasePlayer, Integer> PLAYERS_SR = new HashMap<>();
    public static int numberOfActualPlayers = 20;

    public static void recalculateSR() {
        TOTAL_VALUES.clear();
        PLAYERS_SR.clear();
        numberOfActualPlayers = 20;
        ChatUtils.MessageType.WARLORDS.sendMessage("Recalculating player SR PUBS");
        Collection<DatabasePlayer> players = DatabaseManager.CACHED_PLAYERS.getOrDefault(PlayersCollections.SEASON_8, new ConcurrentHashMap<>()).values();
        numberOfActualPlayers = (int) players
                .stream()
                .filter(databasePlayer -> databasePlayer.getPubStats().getPlays() > 5)
                .count();
        for (DatabasePlayer databasePlayer : players) {
            if (databasePlayer.getPubStats().getPlays() > 5) {
                PLAYERS_SR.put(databasePlayer, SRCalculator.getSR(databasePlayer, DatabasePlayer::getPubStats));
            } else {
                PLAYERS_SR.put(databasePlayer, 500);
            }
        }
//                    SRCalculator.playersSR.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(databasePlayerIntegerEntry -> {
//                        System.out.println(databasePlayerIntegerEntry.getKey().getName() + " - " + databasePlayerIntegerEntry.getValue());
//                    });
        ChatUtils.MessageType.WARLORDS.sendMessage("Number of actual players = " + numberOfActualPlayers);
        ChatUtils.MessageType.WARLORDS.sendMessage("Recalculated player SR PUBS");
    }

    public static int getSR(DatabasePlayer databasePlayer, Function<DatabasePlayer, Stats> getStatInformation) {
        double dhp = averageAdjustedDHP(databasePlayer, getStatInformation) * 2000;
        double wl = averageAdjustedWL(databasePlayer, getStatInformation) * 2000;
        double kda = averageAdjustedKDA(databasePlayer, getStatInformation) * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    private static double averageAdjustedDHP(DatabasePlayer databasePlayer, Function<DatabasePlayer, Stats> getStatInformation) {
        double playerDHP = getStatInformation.apply(databasePlayer).getDHPPerGame();
        double totalDHP = getPlayerTotal(db -> (double) getStatInformation.apply(db).getDHPPerGame());
        return averageAdjusted(playerDHP, totalDHP);
    }

    private static double averageAdjustedWL(DatabasePlayer databasePlayer, Function<DatabasePlayer, Stats> getStatInformation) {
        double playerWL = getStatInformation.apply(databasePlayer).getWL();
        double totalWL = getPlayerTotal(db -> getStatInformation.apply(db).getWL());
        return averageAdjusted(playerWL, totalWL);
    }

    private static double averageAdjustedKDA(DatabasePlayer databasePlayer, Function<DatabasePlayer, Stats> getStatInformation) {
        double playerKDA = getStatInformation.apply(databasePlayer).getKDA();
        double totalKDA = getPlayerTotal(db -> getStatInformation.apply(db).getKDA());
        return averageAdjusted(playerKDA, totalKDA);
    }

    private static double getPlayerTotal(Function<DatabasePlayer, Double> function) {
        if (TOTAL_VALUES.containsKey(function)) {
            return TOTAL_VALUES.get(function);
        }
        double total = DatabaseManager.CACHED_PLAYERS.getOrDefault(PlayersCollections.SEASON_8, new ConcurrentHashMap<>()).values()
                                                     .stream()
                                                     .mapToDouble(function::apply)
                                                     .sum();
        TOTAL_VALUES.put(function, total);
        return total;
    }

    private static double averageAdjusted(double playerAverage, double total) {
        double average = playerAverage / ((total / numberOfActualPlayers));
        if (average >= 5) {
            return 1;
        }
        if (average <= 0) {
            return 0;
        }
        return 1.00699 + (-1.02107 / (1.01398 + Math.pow(average, 3.09248)));
    }
}

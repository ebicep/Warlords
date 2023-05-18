package com.ebicep.warlords.sr;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class SRCalculator {

    public static final HashMap<Function<DatabasePlayer, Double>, Double> TOTAL_VALUES = new HashMap<>();
    public static final HashMap<DatabasePlayer, Integer> PLAYERS_SR = new HashMap<>();
    public static final Set<DatabasePlayer> DATABASE_PLAYER_CACHE = new HashSet<>();
    public static int numberOfActualPlayers = 20;

    public static void recalculateSR() {
        TOTAL_VALUES.clear();
        PLAYERS_SR.clear();
        numberOfActualPlayers = 40;
        ChatUtils.MessageTypes.WARLORDS.sendMessage("Recalculating player SR PUBS");
        Warlords.newChain()
                .async(() -> {
                    numberOfActualPlayers = (int) DATABASE_PLAYER_CACHE.stream().filter(databasePlayer -> databasePlayer.getPubStats().getPlays() > 5).count();
                    for (DatabasePlayer databasePlayer : DATABASE_PLAYER_CACHE) {
                        if (databasePlayer.getPubStats().getPlays() > 5) {
                            PLAYERS_SR.put(databasePlayer, SRCalculator.getSR(databasePlayer, DatabasePlayer::getPubStats));
                        } else {
                            PLAYERS_SR.put(databasePlayer, 500);
                        }
                    }
//                    SRCalculator.playersSR.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(databasePlayerIntegerEntry -> {
//                        System.out.println(databasePlayerIntegerEntry.getKey().getName() + " - " + databasePlayerIntegerEntry.getValue());
//                    });
                    ChatUtils.MessageTypes.WARLORDS.sendMessage("Number of actual players = " + numberOfActualPlayers);
                    ChatUtils.MessageTypes.WARLORDS.sendMessage("Recalculated player SR PUBS");
                })
                .execute();
    }

    public static int getSR(DatabasePlayer databasePlayer, Function<DatabasePlayer, AbstractDatabaseStatInformation> getStatInformation) {
        double dhp = averageAdjustedDHP(databasePlayer, getStatInformation) * 2000;
        double wl = averageAdjustedWL(databasePlayer, getStatInformation) * 2000;
        double kda = averageAdjustedKDA(databasePlayer, getStatInformation) * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    private static double averageAdjusted(double playerAverage, double total) {
        double average = playerAverage / ((total / numberOfActualPlayers));
        if (average >= 5) return 1;
        if (average <= 0) return 0;
        return 1.00699 + (-1.02107 / (1.01398 + Math.pow(average, 3.09248)));
    }

    private static double getPlayerTotal(Function<DatabasePlayer, Double> function) {
        if (TOTAL_VALUES.containsKey(function)) {
            return TOTAL_VALUES.get(function);
        }
        double total = DATABASE_PLAYER_CACHE.stream().mapToDouble(function::apply).sum();
        TOTAL_VALUES.put(function, total);
        return total;
    }

    private static double averageAdjustedDHP(DatabasePlayer databasePlayer, Function<DatabasePlayer, AbstractDatabaseStatInformation> getStatInformation) {
        double playerDHP = getStatInformation.apply(databasePlayer).getDHPPerGame();
        double totalDHP = getPlayerTotal(db -> (double) getStatInformation.apply(db).getDHPPerGame());
        return averageAdjusted(playerDHP, totalDHP);
    }

    private static double averageAdjustedWL(DatabasePlayer databasePlayer, Function<DatabasePlayer, AbstractDatabaseStatInformation> getStatInformation) {
        double playerWL = getStatInformation.apply(databasePlayer).getWL();
        double totalWL = getPlayerTotal(db -> getStatInformation.apply(db).getWL());
        return averageAdjusted(playerWL, totalWL);
    }

    private static double averageAdjustedKDA(DatabasePlayer databasePlayer, Function<DatabasePlayer, AbstractDatabaseStatInformation> getStatInformation) {
        double playerKDA = getStatInformation.apply(databasePlayer).getKDA();
        double totalKDA = getPlayerTotal(db -> getStatInformation.apply(db).getKDA());
        return averageAdjusted(playerKDA, totalKDA);
    }
}

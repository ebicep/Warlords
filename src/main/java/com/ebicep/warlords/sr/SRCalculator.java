package com.ebicep.warlords.sr;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class SRCalculator {

    public static final HashMap<Function<DatabasePlayer, Double>, Double> totalValues = new HashMap<>();

    public static int getSR(DatabasePlayer databasePlayer) {
        double dhp = averageAdjustedDHP(databasePlayer) * 2000;
        double wl = averageAdjustedWL(databasePlayer) * 2000;
        double kda = averageAdjustedKDA(databasePlayer) * 1000;
        return (int) Math.round(dhp + wl + kda);
    }

    private static double averageAdjusted(double playerAverage, double total) {
        double average = playerAverage / ((total / 48d));
        if (average >= 5) return 1;
        if (average <= 0) return 0;
        return average;//1.00699 + (-1.02107 / (1.01398 + Math.pow(average, 3.09248)));
    }

    private static double getPlayerTotal(Function<DatabasePlayer, Double> function) {
        if (totalValues.containsKey(function)) return totalValues.get(function);
        List<DatabasePlayer> databasePlayers = DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5);
        double total = databasePlayers.stream().mapToDouble(function::apply).sum();
        totalValues.put(function, total);
        return total;
    }

    private static double averageAdjustedDHP(DatabasePlayer databasePlayer) {
        double playerDHP = databasePlayer.getDHPPerGame();
        double totalDHP = getPlayerTotal(db -> (double) db.getDHPPerGame());
        return averageAdjusted(playerDHP, totalDHP);
    }

    private static double averageAdjustedWL(DatabasePlayer databasePlayer) {
        double playerWL = databasePlayer.getWL();
        double totalWL = getPlayerTotal(AbstractDatabaseStatInformation::getWL);
        return averageAdjusted(playerWL, totalWL);
    }

    private static double averageAdjustedKDA(DatabasePlayer databasePlayer) {
        double playerKDA = databasePlayer.getKDA();
        double totalKDA = getPlayerTotal(AbstractDatabaseStatInformation::getKDA);
        return averageAdjusted(playerKDA, totalKDA);
    }
}

package com.ebicep.warlords.sr;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SRCalculator {

    public static final HashMap<Function<DatabasePlayer, Double>, Double> totalValues = new HashMap<>();
    public static final HashMap<DatabasePlayer, Integer> playersSR = new HashMap<>();
    public static List<DatabasePlayer> databasePlayerCache = new ArrayList<>();
    public static int numberOfActualPlayers = 40;

    public static void recalculateSR() {
        totalValues.clear();
        playersSR.clear();
        numberOfActualPlayers = 40;
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Recalculating player SR PUBS");
        Warlords.newChain()
                .async(() -> {
                    numberOfActualPlayers = (int) databasePlayerCache.stream().filter(databasePlayer -> databasePlayer.getPubStats().getPlays() > 5).count();
                    for (DatabasePlayer databasePlayer : databasePlayerCache) {
                        if (databasePlayer.getPubStats().getPlays() > 5) {
                            playersSR.put(databasePlayer, SRCalculator.getSR(databasePlayer, DatabasePlayer::getPubStats));
                        } else {
                            playersSR.put(databasePlayer, 500);
                        }
                    }
//                    SRCalculator.playersSR.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(databasePlayerIntegerEntry -> {
//                        System.out.println(databasePlayerIntegerEntry.getKey().getName() + " - " + databasePlayerIntegerEntry.getValue());
//                    });
                    System.out.println("Number of actual players = " + numberOfActualPlayers);
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords] Recalculated player SR PUBS");
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
        if (totalValues.containsKey(function)) return totalValues.get(function);
        double total = databasePlayerCache.stream().mapToDouble(function::apply).sum();
        totalValues.put(function, total);
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

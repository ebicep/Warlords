package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public interface WeaponScore {

    static double getAverageValue(double min, double max, double current) {
        return (current - min) / (max - min);
    }

    static void testWeaponScore(Player player) {
        double minScore = 100;
        double maxScore = 0;
        int weaponsToGenerate = 500000;
        for (int i = 0; i < weaponsToGenerate; i++) {
            CommonWeapon commonWeapon = new CommonWeapon(player.getUniqueId());
            double score = commonWeapon.getWeaponScore();
            if (score < minScore) {
                minScore = score;
            } else if (score > maxScore) {
                maxScore = score;
            }
        }
        System.out.println("Weapons Generated: " + weaponsToGenerate);
        System.out.println("Min Score: " + minScore);
        System.out.println("Max Score: " + maxScore);
    }

    default double getWeaponScore() {
        List<Double> averageScores = getWeaponScoreAverageValues();
        double sum = 0;
        for (Double d : averageScores) {
            sum += d;
        }
        return Math.round(sum / averageScores.size() * 10000) / 100.0;
    }

    default String getWeaponScoreString() {
        return ChatColor.GRAY + "Score: " + ChatColor.YELLOW + NumberFormat.formatOptionalHundredths(getWeaponScore()) + ChatColor.GRAY + "/" + ChatColor.GREEN + "100";
    }

    List<Double> getWeaponScoreAverageValues();


}

package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.pve.weapons.weapontypes.CommonWeapon;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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

    default float getWeaponScore() {
        List<Double> averageScores = getWeaponScoreAverageValues();
        double sum = 0;
        for (Double d : averageScores) {
            sum += d;
        }
        return Math.round(sum / averageScores.size() * 10000) / 100f;
    }

    default Component getWeaponScoreString() {
        return Component.text("Score: ", NamedTextColor.GRAY)
                        .append(Component.text(NumberFormat.formatOptionalHundredths(getWeaponScore()), NamedTextColor.YELLOW))
                        .append(Component.text("/"))
                        .append(Component.text("100", NamedTextColor.GREEN));
    }

    List<Double> getWeaponScoreAverageValues();


}

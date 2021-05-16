package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class HammerOfLight extends AbstractAbility {
    public HammerOfLight() {
        super("Hammer of Light", -119, -158, 60 + 11, 30, 20, 175,
                "§7Throw down a Hammer of Light on\n" +
                        "§7the ground, dealing §c119 §7-\n" +
                        "§c158 §7damage every second to\n" +
                        "§7nearby enemies and healing nearby\n" +
                        "§7allies for §a160 §7- §a216 §7every\n" +
                        "§7second. Your Protector Strike pierces\n" +
                        "§7shields and defenses of enemies standing\n" +
                        "§7on top of the Hammer of Light. §7Lasts §68\n" +
                        "§7seconds.");
    }

    @Override
    public void onActivate(Player player) {
        DamageHealCircle damageHealCircle = new DamageHealCircle(player, player.getTargetBlock((HashSet<Byte>) null, 15).getLocation().add(1, 0, 1), 5, 8, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        damageHealCircle.spawnHammer();
        Warlords.getPlayer(player).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 1, 1);
        }
    }
}
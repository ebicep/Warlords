package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class HammerOfLight extends AbstractAbility {
    public HammerOfLight() {
        super("Hammer of Light", 119, 158, 60 + 11, 30, 20, 175,
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
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        DamageHealCircle damageHealCircle = new DamageHealCircle(e.getPlayer(), e.getPlayer().getTargetBlock((HashSet<Byte>) null, 15).getLocation(), 5, 8, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawn();
        Warlords.getPlayer(e.getPlayer()).subtractEnergy(energyCost);
        Warlords.damageHealCircles.add(damageHealCircle);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 1, 1);
        }
    }
}
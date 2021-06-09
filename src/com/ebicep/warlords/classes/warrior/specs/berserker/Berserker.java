package com.ebicep.warlords.classes.warrior.specs.berserker;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Berserker extends AbstractWarrior {
    public Berserker(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Wounding Strike", -497, -632, 0, 100, 20, 175,
                        "§7Strike the targeted enemy player,\n" +
                                "§7causing §c497 §7- §c632 §7damage\n" +
                                "§7and §cwounding §7them for §63 §7seconds.\n" +
                                "§7A wounded player receives §c35% §7less\n" +
                                "§7healing for the duration of the effect."),

                new SeismicWave("Seismic Wave", -557, -753, 12, 60, 25, 200,
                        "§7Send a wave of incredible force forward\n" +
                                "§7that deals §c557 §7- §c753§7 damage\n" +
                                "§7to all enemies hit and knocks them back\n" +
                                "§7slightly.", player),

                new GroundSlam("Ground Slam", -449, -606, 10, 60, 15, 200,
                        "§7Slam the ground, creating a shockwave\n" +
                                "§7around you that deals §c449 §7- §c606\n" +
                                "§7damage and knocks enemies back slightly.", player),
                new BloodLust(),
                new Berserk());
    }
}

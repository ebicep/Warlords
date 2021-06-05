package com.ebicep.warlords.classes.mage.specs.cryomancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Cryomancer extends AbstractMage {
    public Cryomancer(Player player) {
        super(player, 6135, 305, 20, 14, 10,
                new Projectile("Frostbolt", -323, -415, 0, 70, 20, 175,
                        "§7Shoot a frostbolt that will shatter\n" +
                        "§7for §c323 §7- §c415 §7damage and slow\n" +
                        "§7by §e20% §7for §62 §7seconds. A\n" +
                        "§7direct hit will cause the enemy\n" +
                        "§7to take an additional §c30% §7extra\n" +
                        "§7damage. Has an optimal range of §e30\n" +
                        "§7blocks.", 30),

                new Breath("Freezing Breath", -422, -585, 7, 60, 20, 175,
                        "§7Breathe cold air in a cone in front\n" +
                                "§7of you, dealing §c422 §7- §c585 §7damage\n" +
                                "§7to all enemies hit and slowing them by\n" +
                                "§e35% §7for §64 §7seconds."),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier());
    }
}

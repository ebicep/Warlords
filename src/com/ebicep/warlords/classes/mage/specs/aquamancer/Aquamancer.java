package com.ebicep.warlords.classes.mage.specs.aquamancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;

public class Aquamancer extends AbstractMage {
    public Aquamancer() {
        super(5200, 355, 20, 14, 0,
                new Projectile("Water Bolt", 394, 542, 0, 85, 20, 175,
                        "§7Shoot a bolt of water that will burst\n" +
                        "§7for §c%dynamic.value% §7- §c%dynamic.value% §7damage and restore\n" +
                        "§a%value% §7- §a%value% §7health to allies. A\n" +
                        "§7direct hit will cause §a15% §7increased\n" +
                        "§7damage or healing for the target hit.\n" +
                        "§7Has an optimal range of §e40 §7blocks.", 40),

                new Breath("Water Breath", 556, 752, 13, 60, 25, 175,
                        "§7Breathe water in a cone in front of you,\n" +
                                "§7Knocking back enemies and restoring §a%dynamic.value%\n" +
                                "§7- §a%dynamic.value% §7health to yourself and all\n" +
                                "§7allies hit."),

                new TimeWarp(),
                new ArcaneShield(),
                new HealingRain());
    }
}

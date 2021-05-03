package com.ebicep.warlords.classes.mage.specs.aquamancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Aquamancer extends AbstractMage {
    public Aquamancer(Player player) {
        super(player, 5200, 355, 20, 14, 0,
                new Projectile("Water Bolt", 328, 452, 0, 85, 20, 175, "waterbolt description", 40),
                new Breath("Water Breath", 668, 905, 1, 60, 25, 175, "water breath description"),
                new TimeWarp(),
                new ArcaneShield(),
                new HealingRain());
    }
}

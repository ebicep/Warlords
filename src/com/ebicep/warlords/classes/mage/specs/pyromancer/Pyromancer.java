package com.ebicep.warlords.classes.mage.specs.pyromancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Pyromancer extends AbstractMage {
    public Pyromancer(Player player) {
        super(player, 5200, 305, 20, 14, 0,
                new Projectile("Fireball", -401, -520, 0, 70, 20, 175, "fireball description", 50),
                new Projectile("Flame Burst", -557, -753, 10, 60, 25, 185, "flameburst description", 50),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno());
    }
}

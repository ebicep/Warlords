package com.ebicep.warlords.classes.mage.specs.pyromancer;

import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.Inferno;
import com.ebicep.warlords.classes.abilties.Projectile;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Pyromancer extends AbstractMage {
    public Pyromancer(Player player) {
        super(player, 5200, 305, 20, 14, 0,
                new Projectile("Fireball", -334.4f, -433.4f, 0, 70, 20, 175, 50),

                new Projectile("Flame Burst", -557, -753, 9.4f, 60, 25, 185, 50),
                new TimeWarp(),
                new ArcaneShield(),
                new Inferno());
    }
}

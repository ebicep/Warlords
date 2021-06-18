package com.ebicep.warlords.classes.mage.specs.cryomancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Cryomancer extends AbstractMage {
    public Cryomancer(Player player) {
        super(player, 6135, 305, 20, 14, 10,
                new Projectile("Frostbolt", -268.8f, -345.45f, 0, 70, 20, 175, 30),
                new Breath("Freezing Breath", -422, -585, 6.3f, 60, 20, 175),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier());
    }
}

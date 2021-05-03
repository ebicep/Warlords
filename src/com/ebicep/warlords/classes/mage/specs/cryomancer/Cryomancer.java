package com.ebicep.warlords.classes.mage.specs.cryomancer;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Cryomancer extends AbstractMage {
    public Cryomancer(Player player) {
        super(player, 6200, 305, 20, 14, 10,
                new Projectile("Frostbolt", -323, -415, 0, 70, 20, 175, "frostbolt description", 30),
                new Breath("Freezing Breath", -422, -585, 6, 60, 20, 175, "freezing breath description"),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier());
    }
}

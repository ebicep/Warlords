package com.ebicep.warlords.classes.warrior.specs.berserker;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Berserker extends AbstractWarrior {
    public Berserker(Player player) {
        super(player, 6300, 305, 0,
                new Strike("Wounding Strike", -496.65f, -632.1f, 0, 100, 20, 175),
                new SeismicWave("Seismic Wave", -557, -753, 12, 60, 25, 200, player),
                new GroundSlam("Ground Slam", -448.8f, -606.1f, 10, 60, 15, 200, player),
                new BloodLust(),
                new Berserk());
    }
}

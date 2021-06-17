package com.ebicep.warlords.classes.warrior.specs.defender;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import org.bukkit.entity.Player;

public class Defender extends AbstractWarrior {

    public Defender(Player player) {
        super(player, 7400, 305, 10,
                new Strike("Wounding Strike", -415.8f, -556.5f, 0, 100, 20, 200),
                new SeismicWave("Seismic Wave", -506, -685, 12, 60, 25, 200, player),
                new GroundSlam("Ground Slam", -326, -441, 7, 0, 15, 200, player),
                new Intervene(),
                new LastStand());
    }

}

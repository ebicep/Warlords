package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.pve.mobs.bosses.bossminions.EggSac;
import org.bukkit.Location;

public class AnomalyRelic extends EggSac {

    private final int objectiveIndex;

    public AnomalyRelic(Location location, int objectiveIndex, int maxHealth) {
        super(location, "Anomaly Relic " + (objectiveIndex + 1), maxHealth, 0, 0, 0, 0);
        this.objectiveIndex = objectiveIndex;
    }

    public int getObjectiveIndex() {
        return objectiveIndex;
    }
}

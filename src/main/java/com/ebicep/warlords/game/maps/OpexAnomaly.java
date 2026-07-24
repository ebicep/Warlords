package com.ebicep.warlords.game.maps;

import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyObjectiveMarker;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyOption;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalySpawnMarker;
import com.ebicep.warlords.game.option.pve.anomaly.OpexCurrencyOption;
import com.ebicep.warlords.util.bukkit.LocationFactory;

import java.util.List;

public class OpexAnomaly extends AbstractAnomalyMap {

    private static final double[][] OBJECTIVE_LOCATIONS = {
            {-15.5, 12, 52.5},
            {-236.5, 67, 109.5},
            {-110.5, 156, -556.5}
    };
    private static final double[][][] ENEMY_SPAWN_LOCATIONS = {
            {{-15.5, 12, 70.5}, {-1.5, 12, 38.5}, {-29.5, 12, 38.5}},
            {{-235.5, 66, 32.5}, {-250.5, 66, 27.5}, {-221.5, 66, 94.5}},
            {{-124.5, 158, -542.5}, {-124.5, 158, -570.5}, {-96.5, 158, -570.5}}
    };

    public OpexAnomaly() {
        super("Opex Anomaly", "OpexAnomaly", new double[]{-15.5, 12, 52.5});
    }

    @Override
    protected void addAnomalyOptions(List<Option> options, LocationFactory loc) {
        for (int objectiveIndex = 0; objectiveIndex < OBJECTIVE_LOCATIONS.length; objectiveIndex++) {
            options.add(AnomalyObjectiveMarker.create(objectiveIndex, location(loc, OBJECTIVE_LOCATIONS[objectiveIndex])).asOption());
            for (double[] spawn : ENEMY_SPAWN_LOCATIONS[objectiveIndex]) {
                options.add(AnomalySpawnMarker.create(objectiveIndex, location(loc, spawn)).asOption());
            }
        }
        options.add(new OpexCurrencyOption());
        options.add(new AnomalyOption());
    }
}

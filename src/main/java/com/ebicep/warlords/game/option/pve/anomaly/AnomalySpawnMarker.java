package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface AnomalySpawnMarker extends LocationMarker {

    int getObjectiveIndex();

    static AnomalySpawnMarker create(int objectiveIndex, Location location) {
        return new AnomalySpawnMarker() {
            @Override
            public int getObjectiveIndex() {
                return objectiveIndex;
            }

            @Override
            public Location getLocation() {
                return location;
            }
        };
    }

    default Option asOption() {
        return new MarkerOption(this);
    }
}

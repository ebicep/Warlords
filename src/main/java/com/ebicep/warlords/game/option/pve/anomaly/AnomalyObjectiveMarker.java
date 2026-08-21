package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface AnomalyObjectiveMarker extends LocationMarker {

    int getObjectiveIndex();

    static AnomalyObjectiveMarker create(int objectiveIndex, Location location) {
        return new AnomalyObjectiveMarker() {
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

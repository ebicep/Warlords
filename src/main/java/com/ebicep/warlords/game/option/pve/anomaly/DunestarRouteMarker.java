package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface DunestarRouteMarker extends LocationMarker {

    int getRouteIndex();

    static DunestarRouteMarker create(int routeIndex, Location location) {
        return new DunestarRouteMarker() {
            @Override
            public int getRouteIndex() {
                return routeIndex;
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

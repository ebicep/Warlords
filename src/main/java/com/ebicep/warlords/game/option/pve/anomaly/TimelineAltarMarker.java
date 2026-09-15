package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface TimelineAltarMarker extends LocationMarker {

    static TimelineAltarMarker create(Location location) {
        return new TimelineAltarMarker() {
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

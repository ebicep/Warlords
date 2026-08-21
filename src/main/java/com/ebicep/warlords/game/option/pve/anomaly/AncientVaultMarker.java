package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface AncientVaultMarker extends LocationMarker {

    int getVaultIndex();

    static AncientVaultMarker create(int vaultIndex, Location location) {
        return new AncientVaultMarker() {
            @Override
            public int getVaultIndex() {
                return vaultIndex;
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

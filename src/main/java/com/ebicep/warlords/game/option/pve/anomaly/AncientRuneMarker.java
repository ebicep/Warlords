package com.ebicep.warlords.game.option.pve.anomaly;

import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import org.bukkit.Location;

public interface AncientRuneMarker extends LocationMarker {

    int getVaultIndex();

    AncientRune getRune();

    static AncientRuneMarker create(int vaultIndex, AncientRune rune, Location location) {
        return new AncientRuneMarker() {
            @Override
            public int getVaultIndex() {
                return vaultIndex;
            }

            @Override
            public AncientRune getRune() {
                return rune;
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

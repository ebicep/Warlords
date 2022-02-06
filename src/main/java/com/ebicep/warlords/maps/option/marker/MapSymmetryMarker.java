package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.MapSymmetry;
import com.ebicep.warlords.maps.option.MarkerOption;
import com.ebicep.warlords.maps.option.Option;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface MapSymmetryMarker extends GameMarker {
    
    @Nonnull
    MapSymmetry getSymmetry();
    
    @Nonnull
    static MapSymmetry getSymmetry(Game game) {
        for(MapSymmetryMarker marker : game.getMarkers(MapSymmetryMarker.class)) {
            return marker.getSymmetry();
        }
        return MapSymmetry.NONE;
    }
    
    public default Option asOption() {
        return new MarkerOption(this);
    }
}

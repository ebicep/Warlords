package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.MapSymmetry;
import javax.annotation.Nonnull;

@FunctionalInterface
public interface MapSymmetryMarker extends GameMarker {
    
    @Nonnull
    public MapSymmetry getSymmetry();
    
    @Nonnull
    public static MapSymmetry getSymmetry(Game game) {
        for(MapSymmetryMarker marker : game.getMarkers(MapSymmetryMarker.class)) {
            return marker.getSymmetry();
        }
        return MapSymmetry.NONE;
    }
}

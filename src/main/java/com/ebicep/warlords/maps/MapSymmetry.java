package com.ebicep.warlords.maps;

import com.ebicep.warlords.maps.option.marker.MapSymmetryMarker;

public enum MapSymmetry {
    NONE, SPIN, MIRROR;

    public MapSymmetryMarker asMarker() {
        return new MapSymmetryMarker() {
            @Override
            public MapSymmetry getSymmetry() {
                return MapSymmetry.this;
            }

            @Override
            public String toString() {
                return MapSymmetry.this.toString();
            }
        };
    }
	
}

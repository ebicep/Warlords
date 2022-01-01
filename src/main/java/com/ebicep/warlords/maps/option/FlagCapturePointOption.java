package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.FlagCaptureMarker;
import static com.ebicep.warlords.maps.option.marker.FlagCaptureMarker.DEFAULT_CAPTURE_RADIUS;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.bukkit.Location;

public class FlagCapturePointOption extends MarkerOption {

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnull Team... toIgnore) {
        this(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... toIgnore) {
        this(FlagCaptureMarker.aroundLocation(loc, radius, toIgnore));
    }

    public FlagCapturePointOption(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... toIgnore) {
        this(FlagCaptureMarker.zonedCapture(a, b, toIgnore));
    }

    protected FlagCapturePointOption(FlagCaptureMarker marker) {
        super(marker);
    }    
}

package com.ebicep.warlords.maps.option;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.marker.FlagCaptureMarker;
import static com.ebicep.warlords.maps.option.marker.FlagCaptureMarker.DEFAULT_CAPTURE_RADIUS;
import com.ebicep.warlords.maps.option.marker.SimpleDebugLocationMarker;
import java.util.Arrays;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.bukkit.Location;

public class FlagCapturePointOption extends MarkerOption {

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnull Team... toIgnore) {
        this(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... toIgnore) {
        super(
            FlagCaptureMarker.aroundLocation(loc, radius, toIgnore),
            new SimpleDebugLocationMarker(FlagCapturePointOption.class, "Capture zone (middle): " + Arrays.toString(toIgnore), loc)
        );
    }

    public FlagCapturePointOption(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... toIgnore) {
        super(
            FlagCaptureMarker.zonedCapture(a, b, toIgnore),
            new SimpleDebugLocationMarker(FlagCapturePointOption.class, "Capture zone (rectangle P1): " + Arrays.toString(toIgnore), a),
            new SimpleDebugLocationMarker(FlagCapturePointOption.class, "Capture zone (rectangle P2): " + Arrays.toString(toIgnore), b)
        );
    }
}

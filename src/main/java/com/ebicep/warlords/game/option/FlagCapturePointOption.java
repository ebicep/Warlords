package com.ebicep.warlords.game.option;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.FlagCaptureMarker;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;

import static com.ebicep.warlords.game.option.marker.FlagCaptureMarker.DEFAULT_CAPTURE_RADIUS;

import java.util.Arrays;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import org.bukkit.Location;
import org.bukkit.Material;

public class FlagCapturePointOption extends MarkerOption {

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnull Team... toIgnore) {
        this(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... forTeams) {
        super(
                FlagCaptureMarker.aroundLocation(loc, radius, forTeams),
                DebugLocationMarker.create(Material.CARPET, 0, FlagCapturePointOption.class,
                        "Capture zone",
                        loc,
                        () -> Arrays.asList(
                                "Ignoring teams: " + Arrays.toString(forTeams),
                                "Shape: POINT",
                                "Radius: " + radius
                        )
                )
        );
    }

    public FlagCapturePointOption(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... forTeams) {
        super(
                FlagCaptureMarker.zonedCapture(a, b, forTeams),
                DebugLocationMarker.create(Material.CARPET, 0, FlagCapturePointOption.class,
                        "Capture zone",
                        new Location(
                                a.getWorld(),
                                (a.getX() + b.getX()) / 2,
                                (a.getY() + b.getY()) / 2,
                                (a.getZ() + b.getZ()) / 2
                        ),
                        () -> Arrays.asList(
                                "Ignoring teams: " + Arrays.toString(forTeams),
                                "Shape: RECTANGLE",
                                "A: " + a.getX() + ", " + a.getY() + ", " + a.getZ(),
                                "B: " + b.getX() + ", " + b.getY() + ", " + b.getZ()
                        )
                )
        );
    }
}

package com.ebicep.warlords.game.option.pvp;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.MarkerOption;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.FlagCaptureMarker;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Arrays;

import static com.ebicep.warlords.game.option.marker.FlagCaptureMarker.DEFAULT_CAPTURE_RADIUS;

public class FlagCapturePointOption extends MarkerOption {

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnull Team... toIgnore) {
        this(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public FlagCapturePointOption(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... forTeams) {
        super(
                FlagCaptureMarker.aroundLocation(loc, radius, forTeams),
                DebugLocationMarker.create(Material.BLACK_CARPET, 0, FlagCapturePointOption.class,
                        Component.text("Capture zone"),
                        loc,
                        () -> Arrays.asList(
                                Component.text("Ignoring teams: " + Arrays.toString(forTeams)),
                                Component.text("Shape: POINT"),
                                Component.text("Radius: " + radius)
                        )
                )
        );
    }

    public FlagCapturePointOption(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... forTeams) {
        super(
                FlagCaptureMarker.zonedCapture(a, b, forTeams),
                DebugLocationMarker.create(Material.BLACK_CARPET, 0, FlagCapturePointOption.class,
                        Component.text("Capture zone"),
                        new Location(
                                a.getWorld(),
                                (a.getX() + b.getX()) / 2,
                                (a.getY() + b.getY()) / 2,
                                (a.getZ() + b.getZ()) / 2
                        ),
                        () -> Arrays.asList(
                                Component.text("Ignoring teams: " + Arrays.toString(forTeams)),
                                Component.text("Shape: RECTANGLE"),
                                Component.text("A: " + a.getX() + ", " + a.getY() + ", " + a.getZ()),
                                Component.text("B: " + b.getX() + ", " + b.getY() + ", " + b.getZ())
                        )
                )
        );
    }
}

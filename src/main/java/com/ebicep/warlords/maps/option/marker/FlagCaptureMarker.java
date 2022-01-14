package com.ebicep.warlords.maps.option.marker;

import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.flags.PlayerFlagLocation;
import static com.ebicep.warlords.util.Utils.isInCircleRadiusFast;
import java.util.EnumSet;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import org.bukkit.Location;

/**
 * Marks a flag capture zone, for the gamemodes who have captureable flags
 */
public interface FlagCaptureMarker extends GameMarker {

    /**
     * Standard radius if not further specified
     */
    public static final double DEFAULT_CAPTURE_RADIUS = 4;

    /**
     * Checked every flag tick for every flag carrier. This needs to return true
     * if the capture should be counted
     *
     * @param flag The carried flag to check for
     * @return true if the flag should be marked as captured, false if not
     */
    public boolean shouldCountAsCapture(PlayerFlagLocation flag);

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull Team... toIgnore) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull Iterable<Team> toIgnore) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull EnumSet<Team> toIgnore) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, toIgnore);
    }

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... toIgnore) {
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team ignore : toIgnore) {
            asSet.add(ignore);
        }
        return aroundLocation(loc, radius, asSet);
    }

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Iterable<Team> toIgnore) {
        if (toIgnore instanceof EnumSet<?>) {
            return aroundLocation(loc, radius, (EnumSet<Team>) toIgnore);
        }
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team ignore : toIgnore) {
            asSet.add(ignore);
        }
        return aroundLocation(loc, radius, asSet);
    }

    public static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull EnumSet<Team> toIgnore) {
        return flag -> !toIgnore.contains(flag.getPlayer().getTeam())
                && isInCircleRadiusFast(loc, flag.getLocation(), radius);
    }

    public static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... toIgnore) {
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team ignore : toIgnore) {
            asSet.add(ignore);
        }
        return zonedCapture(a, b, asSet);
    }

    public static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull Iterable<Team> toIgnore) {
        if (toIgnore instanceof EnumSet<?>) {
            return zonedCapture(a, b, (EnumSet<Team>) toIgnore);
        }
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team ignore : toIgnore) {
            asSet.add(ignore);
        }
        return zonedCapture(a, b, asSet);
    }

    public static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull EnumSet<Team> toIgnore) {
        double xMin = Math.min(a.getX(), b.getX());
        double xMax = Math.max(a.getX(), b.getX());
        double yMin = Math.min(a.getY(), b.getY());
        double yMax = Math.max(a.getY(), b.getY());
        double zMin = Math.min(a.getZ(), b.getZ());
        double zMax = Math.max(a.getZ(), b.getZ());
        return flag -> {
            if (toIgnore.contains(flag.getPlayer().getTeam())) {
                return false;
            }
            Location loc = flag.getLocation();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            return x >= xMin && x <= xMax &&
                    y >= yMin && y <= yMax &&
                    z >= zMin && z <= zMax;
        };
    }
}

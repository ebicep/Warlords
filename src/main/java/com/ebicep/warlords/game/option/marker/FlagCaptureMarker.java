package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import org.bukkit.Location;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.EnumSet;

import static com.ebicep.warlords.util.warlords.Utils.isInCircleRadiusFast;

/**
 * Marks a flag capture zone, for the gamemodes who have captureable flags
 */
public interface FlagCaptureMarker extends GameMarker {

    /**
     * Standard radius if not further specified
     */
    double DEFAULT_CAPTURE_RADIUS = 4;

    /**
     * Checked every flag tick for every flag carrier. This needs to return true
     * if the capture should be counted
     *
     * @param flag The carried flag to check for
     * @return true if the flag should be marked as captured, false if not
     */
    boolean shouldCountAsCapture(PlayerFlagLocation flag);

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull Team... forTeams) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, forTeams);
    }

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull Iterable<Team> forTeams) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, forTeams);
    }

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnull EnumSet<Team> forTeams) {
        return aroundLocation(loc, DEFAULT_CAPTURE_RADIUS, forTeams);
    }

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Team... forTeams) {
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team team : forTeams) asSet.add(team);
        return aroundLocation(loc, radius, asSet);
    }

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull Iterable<Team> forTeams) {
        if (forTeams instanceof EnumSet<?>) {
            return aroundLocation(loc, radius, (EnumSet<Team>) forTeams);
        }
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team team : forTeams) asSet.add(team);
        return aroundLocation(loc, radius, asSet);
    }

    static FlagCaptureMarker aroundLocation(@Nonnull Location loc, @Nonnegative double radius, @Nonnull EnumSet<Team> forTeams) {
        return flag -> forTeams.contains(flag.getPlayer().getTeam())
                && isInCircleRadiusFast(loc, flag.getLocation(), radius);
    }

    static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull Team... forTeams) {
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team team : forTeams) asSet.add(team);
        return zonedCapture(a, b, asSet);
    }

    static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull Iterable<Team> forTeams) {
        if (forTeams instanceof EnumSet<?>) {
            return zonedCapture(a, b, (EnumSet<Team>) forTeams);
        }
        EnumSet<Team> asSet = EnumSet.noneOf(Team.class);
        for (Team team : forTeams) asSet.add(team);
        return zonedCapture(a, b, asSet);
    }

    static FlagCaptureMarker zonedCapture(@Nonnull Location a, @Nonnull Location b, @Nonnull EnumSet<Team> forTeams) {
        double xMin = Math.min(a.getX(), b.getX());
        double xMax = Math.max(a.getX(), b.getX());
        double yMin = Math.min(a.getY(), b.getY());
        double yMax = Math.max(a.getY(), b.getY());
        double zMin = Math.min(a.getZ(), b.getZ());
        double zMax = Math.max(a.getZ(), b.getZ());
        return flag -> {
            Location loc = flag.getLocation();
            double x = loc.getX();
            double y = loc.getY();
            double z = loc.getZ();
            return forTeams.contains(flag.getPlayer().getTeam())
                    && loc.getWorld() == a.getWorld()
                    && x >= xMin && x <= xMax
                    && y >= yMin && y <= yMax
                    && z >= zMin && z <= zMax;
        };
    }
}

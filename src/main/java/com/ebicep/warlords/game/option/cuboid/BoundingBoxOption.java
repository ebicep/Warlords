package com.ebicep.warlords.game.option.cuboid;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nonnull;

public class BoundingBoxOption extends AbstractCuboidOption implements Option {

    private static final Location REUSEABLE_LOCATION_OBJECT = new Location(null, 0, 0, 0);

    public BoundingBoxOption(World world) {
        super(world);
    }

    public BoundingBoxOption(World world, int maxSize) {
        super(world, maxSize);
    }

    public BoundingBoxOption(Location a, Location b) {
        super(a, b);
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                game.forEachOnlinePlayerWithoutSpectators((p, t) -> {
                    Location loc = p.getLocation(REUSEABLE_LOCATION_OBJECT);
                    if (
                            loc.getWorld() != min.getWorld() ||
                                    loc.getX() < min.getX() || loc.getX() > max.getX() ||
                                    loc.getY() < min.getY() || loc.getY() > max.getY() ||
                                    loc.getZ() < min.getZ() || loc.getZ() > max.getZ()
                    ) {
                        p.sendMessage("Do not leave the playing area!");
                        REUSEABLE_LOCATION_OBJECT.setWorld(min.getWorld());
                        REUSEABLE_LOCATION_OBJECT.setX((min.getX() + max.getX()) / 2);
                        REUSEABLE_LOCATION_OBJECT.setY((min.getY() + max.getY()) / 2);
                        REUSEABLE_LOCATION_OBJECT.setZ((min.getZ() + max.getZ()) / 2);
                        p.teleport(REUSEABLE_LOCATION_OBJECT);
                    }
                });
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
    }

    public Location getCenter() {
        return new Location(min.getWorld(),
                (min.getX() + max.getX()) / 2,
                (min.getY() + max.getY()) / 2,
                (min.getZ() + max.getZ()) / 2
        );
    }

    
}

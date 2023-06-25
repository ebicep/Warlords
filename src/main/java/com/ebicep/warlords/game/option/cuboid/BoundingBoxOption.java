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
                            loc.getWorld() != getMin().getWorld() ||
                                    loc.getX() < getMin().getX() || loc.getX() > getMax().getX() ||
                                    loc.getY() < getMin().getY() || loc.getY() > getMax().getY() ||
                                    loc.getZ() < getMin().getZ() || loc.getZ() > getMax().getZ()
                    ) {
                        p.sendMessage("Do not leave the playing area!");
                        REUSEABLE_LOCATION_OBJECT.setWorld(getMin().getWorld());
                        REUSEABLE_LOCATION_OBJECT.setX((getMin().getX() + getMax().getX()) / 2);
                        REUSEABLE_LOCATION_OBJECT.setY((getMin().getY() + getMax().getY()) / 2);
                        REUSEABLE_LOCATION_OBJECT.setZ((getMin().getZ() + getMax().getZ()) / 2);
                        p.teleport(REUSEABLE_LOCATION_OBJECT);
                    }
                });
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
    }

    public Location getCenter() {
        return new Location(getMin().getWorld(),
                (getMin().getX() + getMax().getX()) / 2,
                (getMin().getY() + getMax().getY()) / 2,
                (getMin().getZ() + getMax().getZ()) / 2
        );
    }


}

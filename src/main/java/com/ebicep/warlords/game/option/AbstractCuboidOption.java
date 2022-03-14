
package com.ebicep.warlords.game.option;

import com.ebicep.warlords.util.bukkit.LocationFactory;
import org.bukkit.Location;
import org.bukkit.World;

public abstract class AbstractCuboidOption implements Option {
    public static final int MAX_WORLD_SIZE = 30000000;

    protected final Location min;
    protected final Location max;

    public AbstractCuboidOption(World world) {
        this(
                new Location(world, -MAX_WORLD_SIZE, -256, -MAX_WORLD_SIZE),
                new Location(world, MAX_WORLD_SIZE, 512, MAX_WORLD_SIZE)
        );
    }

    public AbstractCuboidOption(Location a, Location b) {
        if (a.getWorld() != b.getWorld()) {
            throw new IllegalArgumentException("The locations provided have different worlds");
        }
        this.min = new Location(a.getWorld(), Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()), a.getYaw(), a.getPitch());
        this.max = new Location(a.getWorld(), Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), b.getYaw(), b.getPitch());
    }
    
    public AbstractCuboidOption(LocationFactory loc, double x1, double y1, double z1, double x2, double y2, double z2) {
        this.min = new Location(
                loc.getWorld(),
                loc.getX() + Math.min(x1, x2),
                loc.getY() + Math.min(y1, y2),
                loc.getZ() + Math.min(z1, z2)
        );
        this.max = new Location(
                loc.getWorld(),
                loc.getX() + Math.max(x1, x2),
                loc.getY() + Math.max(y1, y2),
                loc.getZ() + Math.max(z1, z2)
        );
    }
    
    public AbstractCuboidOption setLocation(Location a, Location b) {
        if (a.getWorld() != b.getWorld()) {
            throw new IllegalArgumentException("The locations provided have different worlds");
        }
        this.min.setWorld(a.getWorld());
        this.min.setX(Math.min(a.getX(), b.getX()));
        this.min.setY(Math.min(a.getY(), b.getY()));
        this.min.setZ(Math.min(a.getZ(), b.getZ()));
        this.min.setYaw(a.getYaw());
        this.min.setPitch(a.getPitch());
        this.max.setWorld(b.getWorld());
        this.max.setX(Math.min(a.getX(), b.getX()));
        this.max.setY(Math.min(a.getY(), b.getY()));
        this.max.setZ(Math.min(a.getZ(), b.getZ()));
        this.max.setYaw(b.getYaw());
        this.max.setPitch(b.getPitch());
        return this;
    }
    

}

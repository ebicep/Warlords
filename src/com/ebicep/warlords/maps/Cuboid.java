package com.ebicep.warlords.maps;

import org.bukkit.World;

public class Cuboid {
    private final World world;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public Cuboid(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (minX > maxX) {
            throw new IllegalArgumentException("minX > maxX");
        }
        if (minY > maxY) {
            throw new IllegalArgumentException("minY > maxY");
        }
        if (minZ > maxZ) {
            throw new IllegalArgumentException("minZ > maxZ");
        }
        this.world = world;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public World getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    @Override
    public String toString() {
        return "Cuboid{" + "minX=" + minX + ", minY=" + minY + ", minZ=" + minZ + ", maxX=" + maxX + ", maxY=" + maxY + ", maxZ=" + maxZ + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + this.minX;
        hash = 89 * hash + this.minY;
        hash = 89 * hash + this.minZ;
        hash = 89 * hash + this.maxX;
        hash = 89 * hash + this.maxY;
        hash = 89 * hash + this.maxZ;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Cuboid other = (Cuboid) obj;
        if (this.minX != other.minX) {
            return false;
        }
        if (this.minY != other.minY) {
            return false;
        }
        if (this.minZ != other.minZ) {
            return false;
        }
        if (this.maxX != other.maxX) {
            return false;
        }
        if (this.maxY != other.maxY) {
            return false;
        }
        return this.maxZ == other.maxZ;
    }

}
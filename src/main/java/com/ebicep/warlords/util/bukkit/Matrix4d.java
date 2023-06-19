package com.ebicep.warlords.util.bukkit;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.Arrays;

/**
 * Class for automatically converting a Vector to a Location based on another Location
 */
public final class Matrix4d {
    private final double[] matrix;

    public Matrix4d() {
        this.matrix = new double[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1,
        };
    }

    public Matrix4d(Location loc) {
        this();
        this.updateFromLocation(loc);
    }

    public void updateFromLocation(Location loc) {
        this.setTranslation(
                loc.getX(),
                loc.getY(),
                loc.getZ()
        );

        this.setRotation(
                0,
                Math.toRadians(-loc.getYaw() - 90),
                Math.toRadians(-loc.getPitch())
        );
    }

    public void setTranslation(double x, double y, double z) {
        this.matrix[12] = x;
        this.matrix[13] = y;
        this.matrix[14] = z;
    }

    public void setRotation(double x, double y, double z) {
        double a = Math.cos(x), b = Math.sin(x);
        double c = Math.cos(y), d = Math.sin(y);
        double e = Math.cos(z), f = Math.sin(z);

        double ac = a * c, ad = a * d, bc = b * c, bd = b * d;

        this.matrix[0] = c * e;
        this.matrix[4] = bd - ac * f;
        this.matrix[8] = bc * f + ad;

        this.matrix[1] = f;
        this.matrix[5] = a * e;
        this.matrix[9] = -b * e;

        this.matrix[2] = -d * e;
        this.matrix[6] = ad * f + bc;
        this.matrix[10] = ac - bd * f;
    }

    public void scale(double v) {
        this.scale(v, v, v);
    }

    public void scale(double x, double y, double z) {
        this.matrix[0] *= x;
        this.matrix[4] *= y;
        this.matrix[8] *= z;
        this.matrix[1] *= x;
        this.matrix[5] *= y;
        this.matrix[9] *= z;
        this.matrix[2] *= x;
        this.matrix[6] *= y;
        this.matrix[10] *= z;
        this.matrix[3] *= x;
        this.matrix[7] *= y;
        this.matrix[11] *= z;
    }


    public Vector translateVector(double x, double y, double z) {
        double w = 1;
        return new Vector(
                this.matrix[0] * x + this.matrix[4] * y + this.matrix[8] * z + this.matrix[12] * w,
                this.matrix[1] * x + this.matrix[5] * y + this.matrix[9] * z + this.matrix[13] * w,
                this.matrix[2] * x + this.matrix[6] * y + this.matrix[10] * z + this.matrix[14] * w
        );
    }

    public Location translateVector(World world, double x, double y, double z) {
        return translateVector(x, y, z).toLocation(world);
    }

    @Override
    public String toString() {
        return "Matrix4d{" + Arrays.toString(matrix) + '}';
    }

    public static void main(String... args) {
        // Stupid manual test:
        Matrix4d matrix4d = new Matrix4d(new Location(null, 128, 64, 0, 0, 90));
        System.out.println(matrix4d);
        System.out.println("0,0,0 => " + matrix4d.translateVector(0, 0, 0));
        System.out.println("1,0,0 => " + matrix4d.translateVector(1, 0, 0));
        System.out.println("0,1,0 => " + matrix4d.translateVector(0, 1, 0));
        System.out.println("0,0,1 => " + matrix4d.translateVector(0, 0, 1));
        System.out.println("1,1,1 => " + matrix4d.translateVector(1, 1, 1));
        System.out.println("-1,-1,-1 => " + matrix4d.translateVector(-1, -1, -1));
    }
}
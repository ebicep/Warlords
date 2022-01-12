package com.ebicep.warlords.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EffectUtils {

    /**
     * @param player what player should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red is the RGB assigned color for the particles.
     * @param green is the RGB assigned color for the particles.
     * @param blue is the RGB assigned color for the particles.
     */
    public static void playSphereAnimation(Player player, double sphereRadius, int red, int green, int blue) {
        Location particleLoc = player.getLocation();
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = Math.cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), particleLoc, 500);
                particleLoc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param player what player should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param effect which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    public static void playSphereAnimation(Player player, double sphereRadius, ParticleEffect effect, int particleCount) {
        Location particleLoc = player.getLocation();
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = Math.cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a+= Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                effect.display(0, 0, 0, 0, particleCount, particleLoc, 500);
                particleLoc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param player what player should the sphere be around.
     * @param helixRadius is how big the helix should be.
     * @param red is the RGB assigned color for the particles.
     * @param green is the RGB assigned color for the particles.
     * @param blue is the RGB assigned color for the particles.
     */
    public static void playHelixAnimation(Player player, double helixRadius, int red, int green, int blue) {
        double rotation = Math.PI / 4;
        int particles = 40;
        int strands = 8;
        int curve = 10;
        Location location = player.getLocation();
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = Math.cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                location.add(x, 0, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), location, 500);
                location.subtract(x, 0, z);
            }
        }
    }

    /**
     * @param player what player should the sphere be around.
     * @param helixRadius is how big the helix should be.
     * @param effect which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    public static void playHelixAnimation(Player player, double helixRadius, ParticleEffect effect, int particleCount) {
        double rotation = Math.PI / 4;
        int particles = 40;
        int strands = 8;
        int curve = 10;
        Location location = player.getLocation();
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = Math.cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                location.add(x, 0, z);
                effect.display(0, 0, 0, 0, particleCount, location, 500);
                location.subtract(x, 0, z);
            }
        }
    }

    /**
     * @param player what player should the sphere be around.
     * @param cylinderRadius is how big the helix should be.
     * @param red which particle effect should be displayed.
     * @param green the amount of particles that should be displayed.
     * @param blue the amount of particles that should be displayed.
     */
    public static void playCylinderAnimation(Player player, double cylinderRadius, int red, int green, int blue) {
        Location playerLoc = player.getLocation();
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(playerLoc.getY() + i / 5D);
                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * cylinderRadius);

                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), particleLoc, 500);
            }
        }
    }

    /**
     * @param player what player should the sphere be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    public static void playCylinderAnimation(Player player, double cylinderRadius, ParticleEffect effect, int particleCount) {
        Location playerLoc = player.getLocation();
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(playerLoc.getY() + i / 5D);
                particleLoc.setZ(playerLoc.getZ() + Math.cos(angle) * cylinderRadius);

                effect.display(0, 0, 0, 0, particleCount, particleLoc, 500);
            }
        }
    }
}

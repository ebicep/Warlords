package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EffectUtils {

    /**
     * @param player       what player should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red          is the RGB assigned color for the particles.
     * @param green        is the RGB assigned color for the particles.
     * @param blue         is the RGB assigned color for the particles.
     */
    @Deprecated
    public static void playSphereAnimation(Player player, double sphereRadius, int red, int green, int blue) {
        playSphereAnimation(player.getLocation(), sphereRadius, red, green, blue);
    }

    public static void playSphereAnimation(Location particleLoc, double sphereRadius, int red, int green, int blue) {
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                Particle.DustOptions data = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                particleLoc.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, data, true);
                particleLoc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param player        what player should the sphere be around.
     * @param sphereRadius  is how big the sphere should be.
     * @param effect        which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    @Deprecated
    public static void playSphereAnimation(Player player, double sphereRadius, Particle effect, int particleCount) {
        playSphereAnimation(player.getLocation(), sphereRadius, effect, particleCount);
    }

    public static void playSphereAnimation(Location particleLoc, double sphereRadius, Particle effect, int particleCount) {
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                particleLoc.getWorld().spawnParticle(effect, particleLoc, particleCount, 0, 0, 0, 0, null, true);
                particleLoc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param player      what player should the helix be around.
     * @param helixRadius is how big the helix should be.
     * @param red         is the RGB assigned color for the particles.
     * @param green       is the RGB assigned color for the particles.
     * @param blue        is the RGB assigned color for the particles.
     */
    @Deprecated
    public static void playHelixAnimation(Player player, double helixRadius, int red, int green, int blue) {
        playHelixAnimation(player.getLocation(), helixRadius, red, green, blue);
    }

    public static void playHelixAnimation(Location location, double helixRadius, int red, int green, int blue) {
        double rotation = Math.PI / 4;
        int particles = 40;
        int strands = 8;
        int curve = 10;
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                location.add(x, 0, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                location.getWorld().spawnParticle(Particle.REDSTONE, location, 1, 0, 0, 0, 0, dustOptions, true);
                location.subtract(x, 0, z);
            }
        }
    }


    /**
     * @param player        what player should the helix be around.
     * @param helixRadius   is how big the helix should be.
     * @param effect        which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    @Deprecated
    public static void playHelixAnimation(Player player, double helixRadius, Particle effect, int particleCount, int helixDots) {
        playHelixAnimation(player.getLocation(), helixRadius, effect, particleCount, helixDots);
    }

    public static void playHelixAnimation(Location location, double helixRadius, Particle effect, int particleCount, int helixDots) {
        double rotation = Math.PI / 4;
        int strands = 8;
        int curve = 10;
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= helixDots; j++) {
                float ratio = (float) j / helixDots;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                location.add(x, 0, z);
                location.getWorld().spawnParticle(effect, location, particleCount, 0, 0, 0, 0, null, true);
                location.subtract(x, 0, z);
            }
        }
    }

    /**
     * @param player         what player should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param red            which particle effect should be displayed.
     * @param green          the amount of particles that should be displayed.
     * @param blue           the amount of particles that should be displayed.
     */
    @Deprecated
    public static void playCylinderAnimation(Player player, double cylinderRadius, int red, int green, int blue) {
        playCylinderAnimation(player.getLocation(), cylinderRadius, red, green, blue);
    }

    public static void playCylinderAnimation(Location location, double cylinderRadius, int red, int green, int blue) {
        Location particleLoc = location.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(location.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(location.getY() + i / 5D);
                particleLoc.setZ(location.getZ() + cos(angle) * cylinderRadius);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dustOptions, true);
            }
        }
    }

    public static void playCylinderAnimation(Location location, double cylinderRadius, int red, int green, int blue, int cylinderDots, int cylinderHeight) {
        Location particleLoc = location.clone();
        for (int i = 0; i < cylinderHeight; i++) {
            for (int j = 0; j < cylinderDots; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(location.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(location.getY() + i / 5D);
                particleLoc.setZ(location.getZ() + cos(angle) * cylinderRadius);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                location.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dustOptions, true);
            }
        }
    }

    /**
     * @param player         what player should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect         which particle effect should be displayed.
     * @param particleCount  the amount of particles that should be displayed.
     */
    @Deprecated
    public static void playCylinderAnimation(Player player, double cylinderRadius, Particle effect, int particleCount) {
        Location playerLoc = player.getLocation();
        Location particleLoc = playerLoc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(playerLoc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(playerLoc.getY() + i / 5D);
                particleLoc.setZ(playerLoc.getZ() + cos(angle) * cylinderRadius);
                particleLoc.getWorld().spawnParticle(effect, particleLoc, particleCount, 0, 0, 0, 0, null, true);
            }
        }
    }

    /**
     * @param location       what location should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect         which particle effect should be displayed.
     * @param particleCount  the amount of particles that should be displayed.
     */

    public static void playCylinderAnimation(Location location, double cylinderRadius, Particle effect, int particleCount) {
        Location particleLoc = location.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(location.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(location.getY() + i / 5D);
                particleLoc.setZ(location.getZ() + cos(angle) * cylinderRadius);
                location.getWorld().spawnParticle(effect, location, particleCount, 0, 0, 0, 0, null, true);
            }
        }
    }

    /**
     * @param player     what player should the star be around.
     * @param starRadius is how big the star should be.
     * @param effect     which particle effect should be displayed.
     */
    @Deprecated
    public static void playStarAnimation(Player player, float starRadius, Particle effect) {
        playStarAnimation(player.getLocation(), starRadius, effect);
    }

    /**
     * @param location   what location should the star be around.
     * @param starRadius is how big the star should be.
     * @param effect     which particle effect should be displayed.
     */
    public static void playStarAnimation(Location location, float starRadius, Particle effect) {
        int spikesHalf = 3;
        float spikeHeight = 3.5f;
        int particles = 30;
        float radius = 3 * starRadius / 1.73205f;
        for (int i = 0; i < spikesHalf * 2; i++) {
            double xRotation = i * Math.PI / spikesHalf;
            for (int x = 0; x < particles; x++) {
                double angle = 2 * Math.PI * x / particles;
                final Random random = new Random(System.nanoTime());
                float height = random.nextFloat() * spikeHeight;
                Vector v = new Vector(cos(angle), 0, Math.sin(angle));
                v.multiply((spikeHeight - height) * radius / spikeHeight);
                v.setY(starRadius + height);
                EffectUtils.rotateAroundAxisY(v, xRotation);
                location.add(v);
                location.getWorld().spawnParticle(effect, location, 1, 0, 0, 0, 0, null, true);
                location.subtract(v);
            }
        }
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static void playChainAnimation(Player player1, Player player2, ItemStack item, int ticksLived) {
        playChainAnimation(player1.getLocation(), player2.getLocation(), item, ticksLived);
    }

    /**
     * @param location1  point A
     * @param location2  point B
     * @param item       which item should the chain hold
     * @param ticksLived how long should the chain last
     */
    public static void playChainAnimation(Location location1, Location location2, ItemStack item, int ticksLived) {
        Location from = location1.clone().add(0, -0.6, 0);
        Location to = location2.clone().add(0, -0.6, 0);
        from.setDirection(from.toVector().subtract(to.toVector()).multiply(-1));
        List<ArmorStand> chains = new ArrayList<>();
        int maxDistance = (int) Math.round(to.distance(from));
        for (int i = 0; i < maxDistance; i++) {
            ArmorStand chain = Utils.spawnArmorStand(from, armorStand -> {
                armorStand.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
                armorStand.setMarker(true);
                armorStand.getEquipment().setHelmet(item);
            });
            from.add(from.getDirection().multiply(1.1));
            chains.add(chain);
            if (to.distanceSquared(from) < .3) {
                break;
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.size() == 0) {
                    this.cancel();
                }

                for (int i = 0; i < chains.size(); i++) {
                    ArmorStand armorStand = chains.get(i);
                    if (armorStand.getTicksLived() > ticksLived) {
                        armorStand.remove();
                        chains.remove(i);
                        i--;
                    }
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);
    }

    public static void playChainAnimation(WarlordsEntity player1, WarlordsEntity player2, ItemStack item, int ticksLived) {
        playChainAnimation(player1.getLocation(), player2.getLocation(), item, ticksLived);
    }

    public static void playParticleLinkAnimation(Location to, Location from, Particle effect) {
        to = to.clone();
        from = from.clone();
        Location lineLocation = to.add(0, 1, 0).clone();
        lineLocation.setDirection(lineLocation.toVector().subtract(from.add(0, 1, 0).toVector()).multiply(-1));
        for (int i = 0; i < Math.floor(to.distance(from)) * 2; i++) {
            lineLocation.getWorld().spawnParticle(effect, lineLocation, 1, 0, 0, 0, 0, null, true);
            lineLocation.add(lineLocation.getDirection().multiply(.5));
        }
    }

    public static void playParticleLinkAnimation(Location to, Location from, int red, int green, int blue, int amount) {
        to = to.clone();
        from = from.clone();
        Location lineLocation = to.add(0, 1, 0).clone();
        lineLocation.setDirection(lineLocation.toVector().subtract(from.add(0, 1, 0).toVector()).multiply(-1));
        for (int i = 0; i < Math.floor(to.distance(from)) * 2; i++) {
            for (int i1 = 0; i1 < amount; i1++) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                lineLocation.getWorld().spawnParticle(Particle.REDSTONE, lineLocation, amount, 0, 0, 0, 0, dustOptions, true);
            }
            lineLocation.add(lineLocation.getDirection().multiply(.5));
        }
    }

    public static void playRandomHitEffect(Location loc, int red, int green, int blue, int amount) {
        for (int i = 0; i < amount; i++) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
            loc.getWorld()
               .spawnParticle(Particle.REDSTONE,
                       loc.clone().add((Math.random() * 2) - 1, 1.2 + (Math.random() * 2) - 1, (Math.random() * 2) - 1),
                       amount,
                       0,
                       0,
                       0,
                       0,
                       dustOptions,
                       true
               );
        }
    }

    public static void strikeLightning(Location location, boolean isSilent, int amount) {
        for (int i = 0; i < amount; i++) {
            strikeLightning(location, isSilent);
        }
    }

    public static void strikeLightning(Location location, boolean isSilent) {
        location.getWorld().spigot().strikeLightningEffect(location, isSilent);
    }

    public static void strikeLightningInCylinder(Location location, double cylinderRadius, boolean isSilent, int ticksDelay, Game game) {
        new GameRunnable(game) {
            @Override
            public void run() {
                strikeLightningInCylinder(location, cylinderRadius, isSilent);
            }
        }.runTaskLater(ticksDelay);
    }

    public static void strikeLightningInCylinder(Location location, double cylinderRadius, boolean isSilent) {
        Location particleLoc = location.clone();
        for (int j = 0; j < 10; j++) {
            double angle = j / 10D * Math.PI * 2;
            particleLoc.setX(location.getX() + Math.sin(angle) * cylinderRadius);
            particleLoc.setZ(location.getZ() + cos(angle) * cylinderRadius);

            strikeLightning(particleLoc, isSilent);
        }
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static void playCircularEffectAround(WarlordsEntity we, Particle effect, int particleCount) {
        Location loc = we.getLocation().clone();
        new GameRunnable(we.getGame()) {
            double t = 0;
            @Override
            public void run() {
                t++;
                double r = 2;
                t = t + Math.PI / 16;
                double x = r * cos(t);
                double y = 0.25 * t;
                double z = r * sin(t);
                loc.add(x, y ,z);
                loc.getWorld().spawnParticle(effect, loc, particleCount, 0, 0, 0, 0, null, true);
                loc.subtract(x, y, z);

                if (t > Math.PI * 8) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 2);
    }

    public static void playCircularEffectAround(
            Game game,
            Location location,
            Particle effect,
            int particleCount,
            double radius,
            double yAxisElevation,
            int interval,
            int delayBetweenParticles,
            int amountOfSwirls
    ) {
        Location loc = location.clone();
        new GameRunnable(game) {
            double t = 0;
            @Override
            public void run() {
                t++;
                t = t + Math.PI / interval;
                double x = radius * cos(t);
                double y = yAxisElevation * t;
                double z = radius * sin(t);
                loc.add(x, y ,z);
                loc.getWorld().spawnParticle(effect, loc, particleCount, 0, 0, 0, 0, null, true);
                loc.subtract(x, y, z);

                if (t > Math.PI * amountOfSwirls) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delayBetweenParticles);
    }

    public static void playRadialWaveAnimation(WarlordsEntity we) {

    }

    public static void playCircularShieldAnimation(Location location, Particle particle, int amountOfCircles, double circleRadius, double distance) {
        Location loc = location.clone();
        loc.setPitch(0);
        loc.setYaw(0);
        loc.add(0, 1, 0);
        Matrix4d matrix = new Matrix4d();
        for (int i = 0; i < amountOfCircles; i++) {
            loc.setYaw(loc.getYaw() + 360F / amountOfCircles);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 20; c++) {
                double angle = c / 20D * Math.PI * 2;
                displayParticle(
                        particle,
                        matrix.translateVector(loc.getWorld(), distance, Math.sin(angle) * circleRadius, Math.cos(angle) * circleRadius),
                        1,
                        0,
                        0,
                        0,
                        0
                );
            }
        }
    }

    /**
     * @param location
     * @param particle particle effect of outer circle
     * @param innerParticle particle effect of inner circle
     * @param amountOfCircles amount of circles to spawn
     * @param circleRadius how big the circle has to be
     * @param innerCricleRadius how big the inner circle has to be
     * @param distance how far away from the location the circles have to be
     */
    public static void playCircularShieldAnimationWithInnerCircle(
            Location location,
            Particle particle,
            Particle innerParticle,
            int amountOfCircles,
            double circleRadius,
            double innerCricleRadius,
            double distance
    ) {
        Location loc = location.clone();
        loc.setPitch(0);
        loc.setYaw(0);
        Matrix4d matrix = new Matrix4d();
        for (int i = 0; i < amountOfCircles; i++) {
            loc.setYaw(loc.getYaw() + 360F / 3F);
            matrix.updateFromLocation(loc);
            for (int c = 0; c < 20; c++) {
                double angle = c / 20D * Math.PI * 2;
                loc.getWorld().spawnParticle(
                        particle,
                        matrix.translateVector(loc.getWorld(), distance, Math.sin(angle) * circleRadius, Math.cos(angle) * circleRadius),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }

            for (int c = 0; c < 10; c++) {
                double angle = c / 10D * Math.PI * 2;

                loc.getWorld().spawnParticle(
                        innerParticle,
                        matrix.translateVector(loc.getWorld(), distance, Math.sin(angle) * innerCricleRadius, Math.cos(angle) * innerCricleRadius),
                        1,
                        0,
                        0,
                        0,
                        0,
                        null,
                        true
                );
            }
        }
    }

    /**
     * @param particle which particle to display
     * @param loc location of the particle
     * @param count particle count
     * @param offsetX particle X axis offset
     * @param offsetY particle Y axis offset
     * @param offsetZ particle Z axis offset
     * @param speed speed of the particle animation
     */
    public static void displayParticle(
            Particle particle,
            Location loc,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed
    ) {
        loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, null, true);
    }
}
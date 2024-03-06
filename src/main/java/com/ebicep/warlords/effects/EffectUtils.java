package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EffectUtils {

    /**
     * @param loc          what location should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red          is the RGB assigned color for the particles.
     * @param green        is the RGB assigned color for the particles.
     * @param blue         is the RGB assigned color for the particles.
     */
    public static void playSphereAnimation(Location loc, double sphereRadius, int red, int green, int blue) {
        loc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = cos(a) * radius;
                double z = Math.sin(a) * radius;

                loc.add(x, y, z);
                Particle.DustOptions data = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                displayParticle(Particle.REDSTONE, loc, 1, data);
                loc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param particle which particle to display
     * @param loc      location of the particle
     * @param count    particle count
     * @param data     optional extra data for the particle (e.g. DustOptions)
     */
    public static <T> void displayParticle(
            Particle particle,
            Location loc,
            int count,
            T data
    ) {
        loc.getWorld().spawnParticle(particle, loc, count, 0, 0, 0, 0, data, true);
    }

    /**
     * @param loc           what location should the sphere be around.
     * @param sphereRadius  is how big the sphere should be.
     * @param effect        which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    public static void playSphereAnimation(Location loc, double sphereRadius, Particle effect, int particleCount) {
        playSphereAnimation(loc, sphereRadius, effect, particleCount, 1);
    }

    public static void playSphereAnimation(Location loc, double sphereRadius, Particle effect, int particleCount, float density) {
        float dens = 10 * density;
        loc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / dens) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / dens) {
                double x = cos(a) * radius;
                double z = Math.sin(a) * radius;
                loc.add(x, y, z);
                displayParticle(effect, loc, particleCount);
                loc.subtract(x, y, z);
            }
        }
    }

    /**
     * @param particle which particle to display
     * @param loc      location of the particle
     * @param count    particle count
     */
    public static void displayParticle(
            Particle particle,
            Location loc,
            int count
    ) {
        loc.getWorld().spawnParticle(particle, loc, count, 0, 0, 0, 0, null, true);
    }

    /**
     * @param loc         what location should the helix be around.
     * @param helixRadius is how big the helix should be.
     * @param red         is the RGB assigned color for the particles.
     * @param green       is the RGB assigned color for the particles.
     * @param blue        is the RGB assigned color for the particles.
     */
    public static void playHelixAnimation(Location loc, double helixRadius, int red, int green, int blue) {
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
                loc.add(x, 0, z);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                displayParticle(Particle.REDSTONE, loc, 1, dustOptions);
                loc.subtract(x, 0, z);
            }
        }
    }

    /**
     * @param loc           what location should the helix be around.
     * @param helixRadius   is how big the helix should be.
     * @param effect        which particle effect should be displayed.
     * @param particleCount the amount of particles that should be displayed.
     */
    public static void playHelixAnimation(Location loc, double helixRadius, Particle effect, int particleCount, int helixDots) {
        double rotation = Math.PI / 4;
        int strands = 8;
        int curve = 10;
        for (int i = 1; i <= strands; i++) {
            for (int j = 1; j <= helixDots; j++) {
                float ratio = (float) j / helixDots;
                double angle = curve * ratio * 2 * Math.PI / strands + (2 * Math.PI * i / strands) + rotation;
                double x = cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                loc.add(x, 0, z);
                displayParticle(effect, loc, particleCount);
                loc.subtract(x, 0, z);
            }
        }
    }

    /**
     * @param loc            what location should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param red            which particle effect should be displayed.
     * @param green          the amount of particles that should be displayed.
     * @param blue           the amount of particles that should be displayed.
     */
    public static void playCylinderAnimation(Location loc, double cylinderRadius, int red, int green, int blue) {
        Location particleLoc = loc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(loc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(loc.getY() + i / 5D);
                particleLoc.setZ(loc.getZ() + cos(angle) * cylinderRadius);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                displayParticle(Particle.REDSTONE, particleLoc, 1, dustOptions);
            }
        }
    }

    public static void playCylinderAnimation(Location loc, double cylinderRadius, int red, int green, int blue, int cylinderDots, int cylinderHeight) {
        Location particleLoc = loc.clone();
        for (int i = 0; i < cylinderHeight; i++) {
            for (int j = 0; j < cylinderDots; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(loc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(loc.getY() + i / 5D);
                particleLoc.setZ(loc.getZ() + cos(angle) * cylinderRadius);
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
                displayParticle(Particle.REDSTONE, particleLoc, 1, dustOptions);
            }
        }
    }

    /**
     * @param loc            what location should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect         which particle effect should be displayed.
     * @param particleCount  the amount of particles that should be displayed.
     */

    public static void playCylinderAnimation(Location loc, double cylinderRadius, Particle effect, int particleCount) {
        Location particleLoc = loc.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(loc.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(loc.getY() + i / 5D);
                particleLoc.setZ(loc.getZ() + cos(angle) * cylinderRadius);
                displayParticle(effect, loc, particleCount);
            }
        }
    }

    /**
     * Plays a circular effect around a location
     *
     * @param particle     particle effect
     * @param location     center of circle
     * @param circleRadius radius of the circle
     */
    public static void playCircularEffectAround(Particle particle, Location location, double circleRadius) {
        Location loc = location.clone();
        for (int i = 0; i < 10; i++) {
            double angle = i / 10D * Math.PI * 2;
            displayParticle(
                    particle,
                    loc.clone().add(Math.sin(angle) * circleRadius, 0, Math.cos(angle) * circleRadius),
                    1
            );
        }
    }

    /**
     * @param loc        what location should the star be around.
     * @param starRadius is how big the star should be.
     * @param effect     which particle effect should be displayed.
     */
    public static void playStarAnimation(Location loc, float starRadius, Particle effect) {
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
                loc.add(v);
                displayParticle(effect, loc, 1);
                loc.subtract(v);
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
        playParticleLinkAnimation(to, from, effect, 1, -1);
    }

    public static void playParticleLinkAnimation(Location to, Location from, Particle effect, double yOffset, int period) {
        playParticleLinkAnimation(to, from, effect, yOffset, .5, period);
    }

    public static void playParticleLinkAnimation(Location to, Location from, Particle effect, double yOffset, double forwardAmount, int period) {
        to = to.clone().add(0, yOffset, 0);
        from = from.clone().add(0, yOffset, 0);
        LocationBuilder lineLocation = new LocationBuilder(to).faceTowards(from);
        double maxI = Math.floor(to.distance(from));
        if (period == -1) {
            for (int i = 0; i < maxI / forwardAmount; i++) {
                displayParticle(effect, lineLocation, 1);
                lineLocation.forward(forwardAmount);
            }
        } else {
            new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (i >= maxI / forwardAmount) {
                        this.cancel();
                    }
                    displayParticle(effect, lineLocation, 1);
                    lineLocation.forward(forwardAmount);
                    i++;
                }
            }.runTaskTimer(Warlords.getInstance(), 0, period);
        }
    }

    public static void playParticleLinkAnimation(Location to, Location from, Particle effect, int period) {
        playParticleLinkAnimation(to, from, effect, 1, period);
    }

    public static void playParticleLinkAnimation(Location to, Location from, int red, int green, int blue, int amount) {
        playParticleLinkAnimation(to, from, red, green, blue, amount, 1);
    }

    public static void playParticleLinkAnimation(Location to, Location from, int red, int green, int blue, int amount, int size) {
        to = to.clone();
        from = from.clone();
        Location lineLocation = to.add(0, 1, 0).clone();
        lineLocation.setDirection(lineLocation.toVector().subtract(from.add(0, 1, 0).toVector()).multiply(-1));
        for (int i = 0; i < Math.floor(to.distance(from)) * 2; i++) {
            for (int i1 = 0; i1 < amount; i1++) {
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), size);
                displayParticle(Particle.REDSTONE, lineLocation, amount, dustOptions);
            }
            lineLocation.add(lineLocation.getDirection().multiply(.5));
        }
    }

    public static void playRandomHitEffect(Location loc, int red, int green, int blue, int amount) {
        for (int i = 0; i < amount; i++) {
            Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
            displayParticle(
                    Particle.REDSTONE,
                    loc.clone().add(
                            (Math.random() * 2) - 1,
                            1.2 + (Math.random() * 2) - 1,
                            (Math.random() * 2) - 1
                    ),
                    amount,
                    dustOptions
            );
        }
    }

    public static void strikeLightning(Location location, boolean isSilent, int amount) {
        for (int i = 0; i < amount; i++) {
            strikeLightning(location, isSilent);
        }
    }

    public static void strikeLightning(Location location, boolean isSilent) {
        location.getWorld().strikeLightningEffect(location);
        new BukkitRunnable() {

            @Override
            public void run() {
                location.getWorld().getNearbyPlayers(location, 50).forEach(player -> {
                    player.stopSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER);
                });
            }
        }.runTaskLater(Warlords.getInstance(), 1);

    }

    public static void strikeLightningTicks(Location location, boolean isSilent, int ticksLived) {
        LightningStrike lightningStrike = (LightningStrike) location.getWorld().spawnEntity(location, EntityType.LIGHTNING);
        lightningStrike.setSilent(isSilent);
        lightningStrike.setTicksLived(ticksLived);
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
                loc.add(x, y, z);
                loc.getWorld().spawnParticle(effect, loc, particleCount, 0, 0, 0, 0, null, true);
                loc.subtract(x, y, z);

                if (t > Math.PI * amountOfSwirls) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delayBetweenParticles);
    }

    public static void playCircularEffectAround(
            Game game,
            Location location,
            Particle effect,
            int particleCount,
            double radius,
            double yAxisElevation,
            double yLimit,
            int interval,
            int delayBetweenParticles,
            int amountOfSwirls,
            int counter
    ) {
        Location loc = location.clone();
        new GameRunnable(game) {
            double t = counter;

            @Override
            public void run() {
                t++;
                t = t + Math.PI / interval;
                double x = radius * cos(t);
                double y = yAxisElevation * t;
                double z = radius * sin(t);
                if (y > yLimit) {
                    y = yLimit;
                }
                loc.add(x, y, z);
                displayParticle(effect, loc, particleCount);
                loc.subtract(x, y, z);

                if (t > Math.PI * amountOfSwirls) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, delayBetweenParticles);
    }

    public static void playCircularShieldAnimation(
            Location location,
            Particle particle,
            int amountOfCircles,
            double circleRadius,
            double distance
    ) {
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
                        matrix.translateVector(
                                loc.getWorld(),
                                distance,
                                Math.sin(angle) * circleRadius,
                                Math.cos(angle) * circleRadius
                        ),
                        1
                );
            }
        }
    }

    /**
     * @param location
     * @param particle          particle effect of outer circle
     * @param innerParticle     particle effect of inner circle
     * @param amountOfCircles   amount of circles to spawn
     * @param circleRadius      how big the circle has to be
     * @param innerCricleRadius how big the inner circle has to be
     * @param distance          how far away from the location the circles have to be
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
                displayParticle(
                        particle,
                        matrix.translateVector(loc.getWorld(), distance, Math.sin(angle) * circleRadius, Math.cos(angle) * circleRadius),
                        1
                );
            }

            for (int c = 0; c < 10; c++) {
                double angle = c / 10D * Math.PI * 2;
                displayParticle(
                        innerParticle,
                        matrix.translateVector(loc.getWorld(), distance, Math.sin(angle) * innerCricleRadius, Math.cos(angle) * innerCricleRadius),
                        1
                );
            }
        }
    }

    public static void playCrownAnimation(Location loc, Particle particle) {
        double angle = 0;
        for (int i = 0; i < 9; i++) {
            double x = .4 * Math.cos(angle);
            double z = .4 * Math.sin(angle);
            angle += 40;
            Vector v = new Vector(x, 2, z);
            displayParticle(
                    particle,
                    loc.clone().add(v),
                    1
            );
        }
    }

    /**
     * @param particle which particle to display
     * @param loc      location of the particle
     * @param count    particle count
     * @param offsetX  particle X axis offset
     * @param offsetY  particle Y axis offset
     * @param offsetZ  particle Z axis offset
     * @param speed    speed of the particle animation
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

    /**
     * @param particle which particle to display
     * @param loc      location of the particle
     * @param count    particle count
     * @param offsetX  particle X axis offset
     * @param offsetY  particle Y axis offset
     * @param offsetZ  particle Z axis offset
     * @param speed    speed of the particle animation
     * @param data     optional extra data for the particle (e.g. DustOptions)
     */
    public static <T> void displayParticle(
            Particle particle,
            Location loc,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            T data
    ) {
        loc.getWorld().spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, data, true);
    }

    /**
     * @param loc at what location should the firework be played at,
     * @param fe  which effects should the firework have.
     */
    public static void playFirework(Location loc, FireworkEffect fe) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(fe);
        fireworkMeta.setPower(1);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    /**
     * @param loc        at what location should the firework be played at,
     * @param fe         which effects should the firework have.
     * @param flightTime 1 = 0.5 seconds of flight time.
     */
    public static void playFirework(Location loc, FireworkEffect fe, int flightTime) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(fe);
        fireworkMeta.setPower(flightTime);
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate();
    }

    public static void playSpiralAnimation(
            @Nonnull WarlordsEntity wp,
            Location playerLoc,
            final int maxAnimationEffects,
            final int maxAnimationTime,
            BiConsumer<Matrix4d, Integer> playAdditionalEffects,
            Particle... particles
    ) {
        playSpiralAnimation(false, wp, playerLoc, maxAnimationEffects, maxAnimationTime, playAdditionalEffects, new ArrayList<>(), particles);
    }

    public static void playSpiralAnimation(
            boolean vertical,
            @Nonnull WarlordsEntity wp,
            Location playerLoc,
            final int maxAnimationEffects,
            final int maxAnimationTime,
            BiConsumer<Matrix4d, Integer> playAdditionalEffects,
            List<Pair<Particle, Object>> customParticles,
            Particle... particles
    ) {
        List<Pair<Particle, Object>> particlesList = new ArrayList<>(customParticles);
        for (Particle particle : particles) {
            particlesList.add(new Pair<>(particle, null));
        }
        new GameRunnable(wp.getGame()) {

            final Matrix4d center = new Matrix4d(playerLoc);
            int animationTimer = 0;

            @Override
            public void run() {
                this.playEffect();
                this.playEffect();
            }

            public void playEffect() {

                if (animationTimer > maxAnimationTime) {
                    this.cancel();
                }

                playAdditionalEffects.accept(center, animationTimer);
                for (int i = 0; i < maxAnimationEffects; i++) {
                    double angle = Math.toRadians(i * 90) + animationTimer * 0.15;
                    double width = animationTimer * 0.3;
                    for (Pair<Particle, Object> customParticle : particlesList) {
                        double x = vertical ? sin(angle) * width : animationTimer / 2D;
                        double y = vertical ? animationTimer / 2D : sin(angle) * width;
                        double z = cos(angle) * width;
                        EffectUtils.displayParticle(
                                customParticle.getA(),
                                center.translateVector(wp.getWorld(), x, y, z),
                                1,
                                customParticle.getB()
                        );
                    }
                }

                animationTimer++;
            }
        }.runTaskTimer(0, 1);
    }

    public static void playSpiralAnimation(
            @Nonnull WarlordsEntity wp,
            Location playerLoc,
            final int maxAnimationEffects,
            final int maxAnimationTime,
            BiConsumer<Matrix4d, Integer> playAdditionalEffects,
            List<Pair<Particle, Object>> customParticles,
            Particle... particles
    ) {
        playSpiralAnimation(false, wp, playerLoc, maxAnimationEffects, maxAnimationTime, playAdditionalEffects, customParticles, particles);
    }

}
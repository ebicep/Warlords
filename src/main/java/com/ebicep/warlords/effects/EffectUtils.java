package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EffectUtils {

    /**
     * @param player       what player should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red          is the RGB assigned color for the particles.
     * @param green        is the RGB assigned color for the particles.
     * @param blue         is the RGB assigned color for the particles.
     */
    public static void playSphereAnimation(Player player, double sphereRadius, int red, int green, int blue) {
        playSphereAnimation(player.getLocation(), sphereRadius, red, green, blue);
    }

    public static void playSphereAnimation(Location particleLoc, double sphereRadius, int red, int green, int blue) {
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = Math.cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), particleLoc, 500);
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
    public static void playSphereAnimation(Player player, double sphereRadius, ParticleEffect effect, int particleCount) {
        playSphereAnimation(player.getLocation(), sphereRadius, effect, particleCount);
    }

    public static void playSphereAnimation(Location particleLoc, double sphereRadius, ParticleEffect effect, int particleCount) {
        particleLoc.add(0, 1, 0);
        for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
            double radius = Math.sin(i) * sphereRadius + 0.5;
            double y = Math.cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;

                particleLoc.add(x, y, z);
                effect.display(0, 0, 0, 0, particleCount, particleLoc, 500);
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
                double x = Math.cos(angle) * ratio * helixRadius;
                double z = Math.sin(angle) * ratio * helixRadius;
                location.add(x, 0, z);
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), location, 500);
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
    public static void playHelixAnimation(Player player, double helixRadius, ParticleEffect effect, int particleCount) {
        playHelixAnimation(player.getLocation(), helixRadius, effect, particleCount);
    }

    public static void playHelixAnimation(Location location, double helixRadius, ParticleEffect effect, int particleCount) {
        double rotation = Math.PI / 4;
        int particles = 20;
        int strands = 4;
        int curve = 10;
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
     * @param player         what player should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param red            which particle effect should be displayed.
     * @param green          the amount of particles that should be displayed.
     * @param blue           the amount of particles that should be displayed.
     */
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
                particleLoc.setZ(location.getZ() + Math.cos(angle) * cylinderRadius);

                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), particleLoc, 500);
            }
        }
    }

    /**
     * @param player         what player should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect         which particle effect should be displayed.
     * @param particleCount  the amount of particles that should be displayed.
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

    /**
     * @param location         what location should the cylinder be around.
     * @param cylinderRadius is how big the helix should be.
     * @param effect         which particle effect should be displayed.
     * @param particleCount  the amount of particles that should be displayed.
     */
    public static void playCylinderAnimation(Location location, double cylinderRadius, ParticleEffect effect, int particleCount) {
        Location particleLoc = location.clone();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                double angle = j / 10D * Math.PI * 2;
                particleLoc.setX(location.getX() + Math.sin(angle) * cylinderRadius);
                particleLoc.setY(location.getY() + i / 5D);
                particleLoc.setZ(location.getZ() + Math.cos(angle) * cylinderRadius);

                effect.display(0, 0, 0, 0, particleCount, particleLoc, 500);
            }
        }
    }

    /**
     * @param player     what player should the star be around.
     * @param starRadius is how big the star should be.
     * @param effect     which particle effect should be displayed.
     */
    public static void playStarAnimation(Player player, float starRadius, ParticleEffect effect) {
        Location location = player.getLocation();
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
                Vector v = new Vector(Math.cos(angle), 0, Math.sin(angle));
                v.multiply((spikeHeight - height) * radius / spikeHeight);
                v.setY(starRadius + height);
                EffectUtils.rotateAroundAxisX(v, xRotation);
                location.add(v);
                effect.display(0, 0, 0, 0, 1, location, 500);
                location.subtract(v);
            }
        }
    }

    /**
     * @param location   what location should the star be around.
     * @param starRadius is how big the star should be.
     * @param effect     which particle effect should be displayed.
     */
    public static void playStarAnimation(Location location, float starRadius, ParticleEffect effect) {
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
                Vector v = new Vector(Math.cos(angle), 0, Math.sin(angle));
                v.multiply((spikeHeight - height) * radius / spikeHeight);
                v.setY(starRadius + height);
                EffectUtils.rotateAroundAxisY(v, xRotation);
                location.add(v);
                effect.display(0, 0, 0, 0, 1, location, 500);
                location.subtract(v);
            }
        }
    }

    /**
     * @param location1    point A
     * @param location2    point B
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
            ArmorStand chain = from.getWorld().spawn(from, ArmorStand.class);
            chain.setHeadPose(new EulerAngle(from.getDirection().getY() * -1, 0, 0));
            chain.setGravity(false);
            chain.setVisible(false);
            chain.setBasePlate(false);
            chain.setMarker(true);
            chain.setHelmet(item);
            from.add(from.getDirection().multiply(1.1));
            chains.add(chain);
            if(to.distanceSquared(from) < .3) {
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

    public static void playChainAnimation(Player player1, Player player2, ItemStack item, int ticksLived) {
        playChainAnimation(player1.getLocation(), player2.getLocation(), item, ticksLived);
    }

    public static void playChainAnimation(WarlordsPlayer player1, WarlordsPlayer player2, ItemStack item, int ticksLived) {
        playChainAnimation(player1.getLocation(), player2.getLocation(), item, ticksLived);
    }

    public static void playParticleLinkAnimation(Location to, Location from, ParticleEffect effect) {
        to = to.clone();
        from = from.clone();
        Location lineLocation = to.add(0, 1, 0).clone();
        lineLocation.setDirection(lineLocation.toVector().subtract(from.add(0, 1, 0).toVector()).multiply(-1));
        for (int i = 0; i < Math.floor(to.distance(from)) * 2; i++) {
            effect.display(0, 0, 0, 0, 1, lineLocation, 500);
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
                ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(red, green, blue), lineLocation, 500);
            }
            lineLocation.add(lineLocation.getDirection().multiply(.5));
        }
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos + v.getZ() * sin;
        z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
        x = v.getX() * cos - v.getY() * sin;
        y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }
}
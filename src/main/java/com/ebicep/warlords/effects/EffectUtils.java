package com.ebicep.warlords.effects;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class EffectUtils {

    public static final double PARTICLE_RANGE = 100.0;
    public static final double PARTICLE_RANGE_SQ = PARTICLE_RANGE * PARTICLE_RANGE;
    private static final Color DEFAULT_COLOR = Color.fromRGB(255, 0, 0);
    private static final Particle.Spell DEFAULT_SPELL = new Particle.Spell(Color.fromRGB(140, 25, 240), 1);
    private static final Particle.DustOptions DEFAULT_DUST = new Particle.DustOptions(DEFAULT_COLOR, 1);
    private static final Particle.DustTransition DEFAULT_DUST_TRANSITION = new Particle.DustTransition(DEFAULT_COLOR, Color.WHITE, 1);
    private static final Float DEFAULT_FLOAT = 1f;
    private static final Integer DEFAULT_INTEGER = 0;
    private static final BlockData DEFAULT_BLOCK_DATA = Material.STONE.createBlockData();
    private static final ItemStack DEFAULT_ITEM = new ItemStack(Material.STONE);

    /**
     * Provides required particle data when callers omit it (Paper 1.21+).
     * Covers Spell, DustOptions, DustTransition, Color, Float, Integer, BlockData, and ItemStack.
     * Vibration and Trail require a Location and must be supplied by the caller.
     */
    @Nullable
    public static Object resolveParticleData(Particle particle, @Nullable Object data) {
        if (data != null) {
            return data;
        }
        Class<?> dataType = particle.getDataType();
        if (dataType == Void.class) {
            return null;
        }
        if (dataType == Particle.Spell.class) {
            return DEFAULT_SPELL;
        }
        if (dataType == Particle.DustOptions.class) {
            return DEFAULT_DUST;
        }
        if (dataType == Particle.DustTransition.class) {
            return DEFAULT_DUST_TRANSITION;
        }
        if (dataType == Color.class) {
            return DEFAULT_COLOR;
        }
        if (dataType == Float.class) {
            return DEFAULT_FLOAT;
        }
        if (dataType == Integer.class) {
            return DEFAULT_INTEGER;
        }
        if (dataType == BlockData.class) {
            return DEFAULT_BLOCK_DATA;
        }
        if (dataType == ItemStack.class) {
            return DEFAULT_ITEM;
        }
        // Vibration / Trail need a Location — caller must pass data
        return null;
    }

    /**
     * @param center       what location should the sphere be around.
     * @param sphereRadius is how big the sphere should be.
     * @param red          is the RGB assigned color for the particles.
     * @param green        is the RGB assigned color for the particles.
     * @param blue         is the RGB assigned color for the particles.
     */
    public static void playSphereAnimation(Location center, double sphereRadius, int red, int green, int blue) {
        playSphereAnimation(center, sphereRadius, red, green, blue, 1.5f, 4, 0.75f);
    }

    public static void playSphereAnimation(Location center, double sphereRadius, int red, int green, int blue, float size, int verticalQuality, float density) {
        Particle.DustOptions data = new Particle.DustOptions(Color.fromRGB(red, green, blue), size);
        Location particleLoc = new Location(center.getWorld(), 0, 0, 0);

        double centerX = center.getX();
        double centerY = center.getY() + 1; // one block above
        double centerZ = center.getZ();

        double verticalStep = Math.PI / verticalQuality; // latitudinal slices

        for (double i = 0; i <= Math.PI; i += verticalStep) {
            double ringRadius = sin(i) * sphereRadius;
            double y = cos(i) * sphereRadius;

            // fewer points near poles
            double circumference = 2 * Math.PI * ringRadius;
            double points = Math.max(1, circumference * density); // min 1 point to keep pole visible
            double horizontalStep = (2 * Math.PI) / points;

            for (double a = 0; a < Math.PI * 2; a += horizontalStep) {
                double x = cos(a) * ringRadius;
                double z = sin(a) * ringRadius;

                particleLoc.setX(centerX + x);
                particleLoc.setY(centerY + y);
                particleLoc.setZ(centerZ + z);

                displayParticle(Particle.DUST, particleLoc, 1, data);
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
        if (loc.getBlock().getType().isOccluding()) {
            return;
        }
        Object resolved = resolveParticleData(particle, data);
        for (Player player : loc.getWorld().getPlayers()) {
            if (!isWithinParticleRange(player, loc)) {
                continue;
            }
            player.spawnParticle(particle, loc, count, 0, 0, 0, 0, resolved, true);
        }
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
        double baseX = loc.getX();
        double baseY = loc.getY() + 1;
        double baseZ = loc.getZ();
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        double step = Math.PI / dens;
        for (double i = 0; i <= Math.PI; i += step) {
            double radius = sin(i) * sphereRadius + 0.5;
            double y = cos(i) * sphereRadius;
            for (double a = 0; a < Math.PI * 2; a += step) {
                particleLoc.setX(baseX + cos(a) * radius);
                particleLoc.setY(baseY + y);
                particleLoc.setZ(baseZ + sin(a) * radius);
                displayParticle(effect, particleLoc, particleCount);
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
        if (loc.getBlock().getType().isOccluding()) {
            return;
        }
        Object data = resolveParticleData(particle, null);
        for (Player player : loc.getWorld().getPlayers()) {
            if (!isWithinParticleRange(player, loc)) {
                continue;
            }
            player.spawnParticle(particle, loc, count, 0, 0, 0, 0, data, true);
        }
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
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        double baseX = loc.getX();
        double baseY = loc.getY();
        double baseZ = loc.getZ();
        for (int i = 1; i <= strands; i++) {
            double strandAngle = 2 * Math.PI * i / strands + rotation;
            for (int j = 1; j <= particles; j++) {
                float ratio = (float) j / particles;
                double angle = curve * ratio * 2 * Math.PI / strands + strandAngle;
                double x = cos(angle) * ratio * helixRadius;
                double z = sin(angle) * ratio * helixRadius;
                particleLoc.setX(baseX + x);
                particleLoc.setY(baseY);
                particleLoc.setZ(baseZ + z);
                displayParticle(Particle.DUST, particleLoc, 1, dustOptions);
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
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        double baseX = loc.getX();
        double baseY = loc.getY();
        double baseZ = loc.getZ();
        for (int i = 1; i <= strands; i++) {
            double strandAngle = 2 * Math.PI * i / strands + rotation;
            for (int j = 1; j <= helixDots; j++) {
                float ratio = (float) j / helixDots;
                double angle = curve * ratio * 2 * Math.PI / strands + strandAngle;
                double x = cos(angle) * ratio * helixRadius;
                double z = sin(angle) * ratio * helixRadius;
                particleLoc.setX(baseX + x);
                particleLoc.setY(baseY);
                particleLoc.setZ(baseZ + z);
                displayParticle(effect, particleLoc, particleCount);
            }
        }
    }

    public static void playCylinderAnimation(Location loc, double cylinderRadius, int red, int green, int blue) {
        playCylinderAnimation(
                loc,
                cylinderRadius,
                Particle.DUST,
                new Particle.DustOptions(Color.fromRGB(red, green, blue), 1),
                1,
                10,
                10,
                0.2
        );
    }

    public static void playCylinderAnimation(
            Location loc,
            double cylinderRadius,
            @Nullable Particle effect,
            @Nullable Object data,
            int particleCount,
            int cylinderDots,
            int cylinderHeight,
            double spaceBetweenParticles
    ) {
        World world = loc.getWorld();
        double baseX = loc.getX();
        double baseY = loc.getY();
        double baseZ = loc.getZ();
        // precompute sin/cos values for all cylinderDots
        double[] cosValues = new double[cylinderDots];
        double[] sinValues = new double[cylinderDots];
        for (int j = 0; j < cylinderDots; j++) {
            double angle = j * 2 * Math.PI / cylinderDots;
            cosValues[j] = cos(angle) * cylinderRadius;
            sinValues[j] = sin(angle) * cylinderRadius;
        }

        Location particleLoc = new Location(world, 0, 0, 0);
        boolean hasData = (data != null);
        boolean hasEffect = (effect != null);
        for (int i = 0; i < cylinderHeight; i++) {
            double y = baseY + i * spaceBetweenParticles;
            for (int j = 0; j < cylinderDots; j++) {
                particleLoc.setX(baseX + cosValues[j]);
                particleLoc.setY(y);
                particleLoc.setZ(baseZ + sinValues[j]);

                if (hasData) {
                    displayParticle(effect, particleLoc, particleCount, data);
                } else if (hasEffect) {
                    displayParticle(effect, particleLoc, particleCount);
                }
            }
        }
    }

    public static void playCylinderAnimation(Location loc, double cylinderRadius, int red, int green, int blue, int cylinderDots, int cylinderHeight) {
        playCylinderAnimation(
                loc,
                cylinderRadius,
                Particle.DUST,
                new Particle.DustOptions(Color.fromRGB(red, green, blue), 1),
                1,
                cylinderDots,
                cylinderHeight,
                0.2
        );
    }

    public static void playCylinderAnimation(
            Location loc,
            double cylinderRadius,
            int red,
            int green,
            int blue,
            int cylinderDots,
            int cylinderHeight,
            double spaceBetweenParticles
    ) {
        playCylinderAnimation(
                loc,
                cylinderRadius,
                Particle.DUST,
                new Particle.DustOptions(Color.fromRGB(red, green, blue), 1),
                1,
                cylinderDots,
                cylinderHeight,
                spaceBetweenParticles
        );
    }

    public static void playCylinderAnimation(Location loc, double cylinderRadius, Particle effect, int particleCount) {
        playCylinderAnimation(
                loc,
                cylinderRadius,
                effect,
                null,
                particleCount,
                10,
                10,
                0.2
        );
    }

    public static void playCylinderAnimation(Location loc, double cylinderRadius, Particle effect, int cylinderDots, int cylinderHeight, int particleCount) {
        playCylinderAnimation(
                loc,
                cylinderRadius,
                effect,
                null,
                particleCount,
                cylinderDots,
                cylinderHeight,
                0.2
        );
    }


    public static void playCircularEffectAround(
            Particle particle,
            Location location,
            double circleRadius,
            int amountOfParticles
    ) {
        playCircularEffectAround(null, particle, location, circleRadius, amountOfParticles);
    }

    public static void playCircularEffectAround(
            @Nullable Player player,
            Particle particle,
            Location location,
            double circleRadius,
            int amountOfParticles
    ) {
        playCircularEffectAround(player, particle, location, circleRadius, amountOfParticles, 0, 0, 0, 0);
    }

    /**
     * Plays a circular effect around a location
     *
     * @param particle          particle effect
     * @param location          center of circle
     * @param circleRadius      radius of the circle
     * @param amountOfParticles amount of particles
     * @param xOffset           x offset
     * @param yOffset           y offset
     * @param zOffset           z offset
     * @param speed             speed of the particles
     */
    public static void playCircularEffectAround(
            @Nullable Player player,
            Particle particle,
            Location location,
            double circleRadius,
            int amountOfParticles,
            double xOffset,
            double yOffset,
            double zOffset,
            double speed
    ) {
        double baseX = location.getX();
        double baseY = location.getY();
        double baseZ = location.getZ();
        Location particleLoc = new Location(location.getWorld(), 0, 0, 0);
        double angleStep = Math.PI * 2 / amountOfParticles;
        for (int i = 0; i < amountOfParticles; i++) {
            double angle = i * angleStep;
            particleLoc.setX(baseX + sin(angle) * circleRadius);
            particleLoc.setY(baseY);
            particleLoc.setZ(baseZ + cos(angle) * circleRadius);
            displayParticle(player, particle, particleLoc, 1, xOffset, yOffset, zOffset, speed);
        }
    }

    public static void displayParticle(
            @Nullable Player player,
            Particle particle,
            Location loc,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed
    ) {
        displayParticle(player, particle, loc, count, offsetX, offsetY, offsetZ, speed, resolveParticleData(particle, null));
    }

    public static void displayParticle(
            @Nullable Player player,
            Particle particle,
            Location loc,
            int count,
            double offsetX,
            double offsetY,
            double offsetZ,
            double speed,
            Object data
    ) {
        if (loc.getBlock().getType().isOccluding()) {
            return;
        }
        Object resolved = resolveParticleData(particle, data);
        if (player == null) {
            for (Player receiver : loc.getWorld().getPlayers()) {
                if (!isWithinParticleRange(receiver, loc)) {
                    continue;
                }
                receiver.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, resolved, true);
            }
        } else if (isWithinParticleRange(player, loc)) {
            player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, resolved, true);
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
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        for (int i = 0; i < spikesHalf * 2; i++) {
            double xRotation = i * Math.PI / spikesHalf;
            for (int x = 0; x < particles; x++) {
                double angle = 2 * Math.PI * x / particles;
                float height = random.nextFloat() * spikeHeight;
                Vector v = new Vector(cos(angle), 0, sin(angle));
                v.multiply((spikeHeight - height) * radius / spikeHeight);
                v.setY(starRadius + height);
                rotateAroundAxisY(v, xRotation);
                particleLoc.setX(loc.getX() + v.getX());
                particleLoc.setY(loc.getY() + v.getY());
                particleLoc.setZ(loc.getZ() + v.getZ());
                displayParticle(effect, particleLoc, 1);
            }
        }
    }

    public static Vector rotateAroundAxisY(Vector v, double angle) {
        double x, z, cos, sin;
        cos = cos(angle);
        sin = sin(angle);
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
                    }
            );
            from.add(from.getDirection().multiply(1.25));
            chains.add(chain);
            if (to.distanceSquared(from) < .3) {
                break;
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                if (chains.isEmpty()) {
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

    public static void playChainAnimation(Game game, Location location1, Location location2, ItemStack item, float initialDisplacement, float increment, int ticksLived) {
        Vector direction = location2.toVector().subtract(location1.toVector()).normalize().multiply(increment);
        double pitch = new LocationBuilder(location1).faceTowards(location2).getPitch();
        LocationBuilder start = new LocationBuilder(location1).faceTowards(location2).forward(initialDisplacement).lookRight().pitch(0);
        List<Entity> chains = new ArrayList<>();
        double maxDistance = Math.ceil(location1.distance(location2)) + 1;
        for (double dist = increment; dist < maxDistance; dist += increment) {
            chains.add(location1.getWorld().spawn(
                    start,
                    ItemDisplay.class,
                    false,
                    display -> {
                        display.setItemStack(item);
                        display.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
                        display.setTransformation(new Transformation(
                                new Vector3f(0f, 0f, 0f),
                                new AxisAngle4f((float) Math.toRadians(-pitch), 0, 0, 1),
                                new Vector3f(increment, 1f, 1f),
                                new AxisAngle4f()
                        ));
                    }
            ));
            start.add(direction);
        }

        new GameRunnable(game) {

            @Override
            public void run() {
                chains.forEach(Entity::remove);
            }

        }.runTaskLater(ticksLived);
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

    public static void playParticleLinkAnimation(Location to, Location from, int red, int green, int blue, int amount, float size) {
        final double stepSize = 1.25;

        Location start = from.clone().add(0, 1, 0);
        Location end = to.clone().add(0, 1, 0);
        Vector direction = end.toVector().subtract(start.toVector()).normalize().multiply(stepSize);
        double distance = start.distance(end);
        int steps = (int) Math.floor(distance / stepSize);

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), size);
        Location current = end.clone();
        for (int i = 0; i < steps; i++) {
            displayParticle(Particle.DUST, current, amount, dustOptions);
            current.subtract(direction);
        }
    }

    public static void playRandomHitEffect(Location loc, int red, int green, int blue, int amount) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(red, green, blue), 1);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        double baseX = loc.getX();
        double baseY = loc.getY();
        double baseZ = loc.getZ();
        for (int i = 0; i < amount; i++) {
            particleLoc.setX(baseX + random.nextDouble(-1, 1));
            particleLoc.setY(baseY + 1.2 + random.nextDouble(-1, 1));
            particleLoc.setZ(baseZ + random.nextDouble(-1, 1));
            displayParticle(Particle.DUST, particleLoc, amount, dustOptions);
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
                location.getWorld().getPlayers().forEach(player -> {
                    player.stopSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER);
                });
            }
        }.runTaskLater(Warlords.getInstance(), 1);

    }

    public static void strikeLightningTicks(Location location, boolean isSilent, int ticksLived) {
        LightningStrike lightningStrike = (LightningStrike) location.getWorld().spawnEntity(location, EntityType.LIGHTNING_BOLT);
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
            particleLoc.setX(location.getX() + sin(angle) * cylinderRadius);
            particleLoc.setZ(location.getZ() + cos(angle) * cylinderRadius);

            strikeLightning(particleLoc, isSilent);
        }
    }

    public static Vector rotateAroundAxisX(Vector v, double angle) {
        double y, z, cos, sin;
        cos = cos(angle);
        sin = sin(angle);
        y = v.getY() * cos - v.getZ() * sin;
        z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double angle) {
        double x, y, cos, sin;
        cos = cos(angle);
        sin = sin(angle);
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
        playCircularEffectAround(null, game, location, effect, particleCount, radius, yAxisElevation, interval, delayBetweenParticles, amountOfSwirls);
    }

    public static void playCircularEffectAround(
            @Nullable Player player,
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
                displayParticle(player, effect, loc, particleCount, 0, 0, 0, 0);
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
                                sin(angle) * circleRadius,
                                cos(angle) * circleRadius
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
                        matrix.translateVector(loc.getWorld(), distance, sin(angle) * circleRadius, cos(angle) * circleRadius),
                        1
                );
            }

            for (int c = 0; c < 10; c++) {
                double angle = c / 10D * Math.PI * 2;
                displayParticle(
                        innerParticle,
                        matrix.translateVector(loc.getWorld(), distance, sin(angle) * innerCricleRadius, cos(angle) * innerCricleRadius),
                        1
                );
            }
        }
    }

    public static void playBlossomAnimation(Location location, double radius, Particle particle, int ticksLived) {
        int petals = 7;
        double yOffset = 0.05;
        double rotation = ticksLived * 0.05;

        for (double theta = 0; theta <= Math.PI * 2; theta += 0.1) {
            double r = Math.sin(petals * theta) * radius;

            double angle = theta + rotation;

            double x = r * Math.cos(angle);
            double z = r * Math.sin(angle);

            Location point = location.clone().add(x, yOffset, z);

            displayParticle(particle, point, 0, 0, 0, 0, 1);
        }
    }

    public static void playCrownAnimation(Location loc, Particle particle) {
        Location particleLoc = new Location(loc.getWorld(), 0, 0, 0);
        double baseX = loc.getX();
        double baseY = loc.getY() + 2;
        double baseZ = loc.getZ();
        double angle = 0;
        for (int i = 0; i < 9; i++) {
            particleLoc.setX(baseX + .4 * cos(angle));
            particleLoc.setY(baseY);
            particleLoc.setZ(baseZ + .4 * sin(angle));
            angle += 40;
            displayParticle(particle, particleLoc, 1);
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
        displayParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, resolveParticleData(particle, null));
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
        if (loc.getBlock().getType().isOccluding()) {
            return;
        }
        Object resolved = resolveParticleData(particle, data);
        for (Player player : loc.getWorld().getPlayers()) {
            if (!isWithinParticleRange(player, loc)) {
                continue;
            }
            player.spawnParticle(particle, loc, count, offsetX, offsetY, offsetZ, speed, resolved, true);
        }
    }

    private static boolean isWithinParticleRange(Player player, Location loc) {
        return player.getWorld().equals(loc.getWorld()) &&
                player.getLocation().distanceSquared(loc) <= PARTICLE_RANGE_SQ;
    }

    /**
     * @param loc at what location should the firework be played at,
     * @param fe  which effects should the firework have.
     */
    public static void playFirework(Location loc, FireworkEffect fe) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
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
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
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

    public static void drawRing(Location center, double radius, double step, Particle particle) {
        if (radius <= 0) return;
        double twoPi = Math.PI * 2.0;
        double angStep = Math.max(0.02, step / Math.max(0.1, radius));
        double baseX = center.getX();
        double y = center.getY() + 1;
        double baseZ = center.getZ();
        Location particleLoc = new Location(center.getWorld(), 0, y, 0);
        for (double a = 0; a < twoPi; a += angStep) {
            particleLoc.setX(baseX + cos(a) * radius);
            particleLoc.setZ(baseZ + sin(a) * radius);
            displayParticle(particle, particleLoc, 1);
        }
    }
}

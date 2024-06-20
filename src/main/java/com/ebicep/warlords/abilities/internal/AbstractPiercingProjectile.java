package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;


public abstract class AbstractPiercingProjectile extends AbstractAbility implements ProjectileAbility, HitBox {

    public int playersHit = 0;
    public int playersHitBySplash = 0;
    public int directHits = 0;
    public int numberOfDismounts = 0;
    protected final boolean hitTeammates;
    protected FloatModifiable hitboxInflation = new FloatModifiable(0.75f);
    protected int maxTicks;
    protected double maxDistance;
    protected float forwardTeleportAmount = 0;
    protected int maxAngleOfShots = 45;
    protected int shotsFiredAtATime = 1;
    protected HashMap<InternalProjectile, List<InternalProjectile>> internalProjectileGroup = new HashMap<>();
    protected double projectileSpeed;
    private final List<PendingHit> PENDING_HITS = new ArrayList<>();

    public AbstractPiercingProjectile(
            String name,
            float cooldown,
            float energyCost,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates
    ) {
        super(name, cooldown, energyCost);
        this.projectileSpeed = projectileSpeed;
        this.maxDistance = maxDistance;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
        this.hitTeammates = hitTeammates;
    }

    public AbstractPiercingProjectile(
            String name,
            float cooldown,
            float energyCost,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates,
            float startCooldown
    ) {
        super(name, cooldown, energyCost, startCooldown);
        this.projectileSpeed = projectileSpeed;
        this.maxDistance = maxDistance;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
        this.hitTeammates = hitTeammates;
    }

    /**
     * Plays this projectile effect at a location
     *
     * @param projectile
     */
    protected void playEffect(@Nonnull InternalProjectile projectile) {
        for (InternalProjectileTask task : projectile.tasks) {
            task.run(projectile);
        }
        playEffect(projectile.currentLocation, projectile.ticksLived);
    }

    @Deprecated
    protected abstract void playEffect(@Nonnull Location currentLocation, int ticksLived);

    /**
     * Called when the projectile is destroyed by a collision
     *
     * @param projectile The projectile
     * @param hit        The player that this projectile impacted on, if any
     */
    protected abstract int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit);

    /**
     * Modifies the speed every tick, in case it is needed
     *
     * @param projectile
     */
    protected void updateSpeed(InternalProjectile projectile) {
    }

    /**
     * see {@link net.minecraft.world.entity.projectile.ProjectileUtil}
     * see {@link net.minecraft.world.phys.AABB}
     *
     * @param projectile      The projectile
     * @param currentLocation The current location of the projectile
     * @param speed           The speed of the projectile
     * @param shooter         The shooter of the projectile
     * @return The hit result = block or entity hit or null
     */
    @Nullable
    protected HitResult checkCollisionAndMove(InternalProjectile projectile, Location currentLocation, Vector speed, WarlordsEntity shooter) {
        Vec3 currentPosition;
        if (projectile.getTicksLived() == 0) {
            // for initially shooting entities directly in front of player
            Location behindCurrent = new LocationBuilder(currentLocation).backward(2);
            currentPosition = new Vec3(behindCurrent.getX(), behindCurrent.getY(), behindCurrent.getZ());
        } else {
            currentPosition = new Vec3(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        }
        currentLocation.add(speed);
        Vec3 nextPosition = new Vec3(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());

        @Nullable
        HitResult hit = null;
        double hitDistance = Double.MAX_VALUE;
        for (Entity entity : currentLocation.getWorld().getEntities()) {
            WarlordsEntity wp = getFromEntity(entity);
            if (wp == null || (!hitTeammates && !shooter.isEnemyAlive(wp)) || !wp.isAlive() || wp == shooter) {
                continue;
            }
            // This logic does not properly deal with an EnderDragon entity, as it has a complex hitbox
            assert entity instanceof CraftEntity;
            net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
            AABB aabb = nmsEntity.getBoundingBox().inflate(hitboxInflation.getCalculatedValue());
            Optional<Vec3> vec3 = aabb.clip(currentPosition, nextPosition);
            if (vec3.isEmpty()) {
                continue;
            }
            Vec3 vec = vec3.get();
            double distance = currentPosition.distanceToSqr(vec);
            if (shouldEndProjectileOnHit(projectile, wp)) {
                if (hit == null || distance < hitDistance) {
                    hitDistance = distance;
                    hit = new EntityHitResult(nmsEntity);
                    currentLocation.set(vec.x, vec.y, vec.z);
                }
            } else {
                PENDING_HITS.add(new PendingHit(
                        new Location(
                                currentLocation.getWorld(),
                                vec.x,
                                vec.y,
                                vec.z
                        ), distance, wp)
                );
            }
        }

        if (projectile.getTicksLived() == 0) {
            // for initially shooting entities directly in front of player - revert for blocks
            Location startingLocation = projectile.getStartingLocation();
            currentPosition = new Vec3(startingLocation.getX(), startingLocation.getY(), startingLocation.getZ());
        }
        try {
            BlockIterator itr = new BlockIterator(currentLocation.getWorld(),
                    new Vector(currentPosition.x, currentPosition.y, currentPosition.z),
                    speed,
                    0,
                    (int) (projectileSpeed + 1)
            );
            while (itr.hasNext()) {
                Block block = itr.next();
                if (!block.getType().isSolid() || block.getType() == Material.BARRIER || block.getBlockData() instanceof Banner) {
                    continue;
                }
                BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
                ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
                BlockState blockData = world.getBlockState(pos);
                VoxelShape collisionShape = blockData.getCollisionShape(world, pos);
                if (collisionShape.isEmpty()) {
                    continue;
                }
                BlockHitResult blockHitResult = collisionShape.clip(currentPosition, nextPosition, pos);
                // Flags have no hitbox while they are considered solid??
                if (blockHitResult == null) {
                    continue;
                }
                if (!shouldEndProjectileOnHit(projectile, block)) {
                    continue;
                }
                Vec3 location = blockHitResult.getLocation();
                double distance = currentPosition.distanceToSqr(location);
                if ((hit == null || distance < hitDistance)) {
                    hitDistance = distance;
                    hit = blockHitResult;
                    currentLocation.set(location.x, location.y, location.z);
                }
                // If we hit this point, we either have collided with a
                // player closer by, or we hit a block. Blocks are
                // checked in order, so we can bail out early
                break;
            }
        } catch (Exception e) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(e);
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(
                    getName() + " - " + shooter + " - " + shooter.getSpecClass() + " - " +
                            currentLocation + " - " + speed + " - " +
                            projectile.getStartingLocation()
            );
            projectile.cancel();
            return null;
        }

        if (!PENDING_HITS.isEmpty()) {
            Collections.sort(PENDING_HITS);
            for (PendingHit p : PENDING_HITS) {
                if (hit == null || p.distance < hitDistance) {
                    this.onNonCancellingHit(projectile, p.hit, p.loc);
                } else {
                    break;
                }
            }
            PENDING_HITS.clear();
        }
        return hit;
    }

    @Nullable
    protected WarlordsEntity getFromEntity(Entity e) {
        if (e instanceof Horse) {
            List<Entity> passengers = e.getPassengers();
            return Warlords.getPlayer(passengers.isEmpty() ? null : passengers.get(0));
        }
        return Warlords.getPlayer(e);
    }

    /**
     * Should the collision with this object cause the projectile to consider itself destroyed?
     *
     * @param projectile
     * @param wp
     * @return true if it should destroy itself
     */
    protected abstract boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp);

    /**
     * Should the collision with this object cause the projectile to consider itself destroyed?
     *
     * @param projectile
     * @param block
     * @return true if it should destroy itself
     */
    protected abstract boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block);

    /**
     * Called when the projectile hits a player, but the `shouldEndProjectileOnHit` says the projectile keeps flying
     *
     * @param projectile
     * @param hit
     * @param impactLocation
     */
    protected abstract void onNonCancellingHit(
            @Nonnull InternalProjectile projectile,
            @Nonnull WarlordsEntity hit,
            @Nonnull Location impactLocation
    );

    /**
     * Calculated the initial projectile location
     *
     * @param shooter
     * @param startingLocation
     * @return
     */
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        //return new LocationBuilder(startingLocation).backward(.5f);
        return startingLocation.clone().add(startingLocation.getDirection().multiply(0.2));
    }

    /**
     * Calculated the initial projectile speed
     *
     * @param shooter
     * @param startingLocation
     * @return
     */
    protected Vector getProjectileStartingSpeed(WarlordsEntity shooter, Location startingLocation) {
        return startingLocation.getDirection().multiply(projectileSpeed);
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity shooter) {
        fire(shooter, shooter.getEyeLocation());
        return true;
    }

    public void fire(@Nonnull WarlordsEntity shooter, Location startingLocation) {
        List<Location> projectileLocations = getLocationsToFireShots(startingLocation);
        List<InternalProjectile> internalProjectiles = new ArrayList<>();
        for (Location projectileLocation : projectileLocations) {
            InternalProjectile projectile = new InternalProjectile(shooter, projectileLocation);
            internalProjectiles.add(projectile);
        }

        for (InternalProjectile projectile : internalProjectiles) {
            internalProjectileGroup.put(projectile, internalProjectiles);
        }

        for (InternalProjectile projectile : internalProjectiles) {
            onSpawn(projectile);
            projectile.runTaskTimer(Warlords.getInstance(), 0, 1);
        }
    }

    /**
     * @return List of locations in a 2D cone to fire projectiles, number of projectiles depend on numberOfShotsAtATime
     */
    public List<Location> getLocationsToFireShots(Location startingLocation) {
        List<Location> locations = new ArrayList<>();

        Location playerLocation = new LocationBuilder(startingLocation.clone()).backward(forwardTeleportAmount);
        double beginningYaw = playerLocation.getYaw() - (maxAngleOfShots / 2d);
        double angleBetweenShots = (double) maxAngleOfShots / (shotsFiredAtATime + 1);
        for (int i = 1; i <= shotsFiredAtATime; i++) {
            Location locationToAdd = new LocationBuilder(playerLocation.clone())
                    .yaw((float) (beginningYaw + (angleBetweenShots * i)))
                    .forward(forwardTeleportAmount);
            locations.add(locationToAdd);
        }

        return locations;
    }

    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        playSound(projectile);
    }

    protected void playSound(@Nonnull InternalProjectile projectile) {
        final String activationSound = getActivationSound();
        if (activationSound != null) {
            Utils.playGlobalSound(projectile.getStartingLocation(), activationSound, getSoundVolume(), getSoundPitch());
        }
    }

    /**
     * Gets the sound used when this projectile is activated
     *
     * @return
     */
    @Nullable
    protected abstract String getActivationSound();

    protected abstract float getSoundVolume();

    protected abstract float getSoundPitch();

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        hitboxInflation.tick();
        super.runEveryTick(warlordsEntity);
    }

    public int getDirectHits() {
        return directHits;
    }

    public void setShotsFiredAtATime(int shotsFiredAtATime) {
        this.shotsFiredAtATime = shotsFiredAtATime;
    }

    public List<InternalProjectile> getOtherProjectiles(InternalProjectile internalProjectile) {
        List<InternalProjectile> otherProjectiles = new ArrayList<>();
        for (InternalProjectile projectile : internalProjectileGroup.get(internalProjectile)) {
            if (projectile != internalProjectile) {
                otherProjectiles.add(projectile);
            }
        }
        return otherProjectiles;
    }

    public List<InternalProjectile> getProjectiles(InternalProjectile internalProjectile) {
        return new ArrayList<>(internalProjectileGroup.get(internalProjectile));
    }

    public double getProjectileSpeed() {
        return projectileSpeed;
    }

    public void setProjectileSpeed(double projectileSpeed) {
        this.projectileSpeed = projectileSpeed;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitboxInflation;
    }

    public interface InternalProjectileTask {
        void run(InternalProjectile projectile);

        default void onDestroy(InternalProjectile projectile) {
        }
    }

    private record PendingHit(Location loc, double distance, WarlordsEntity hit) implements Comparable<PendingHit> {

        @Override
        public int compareTo(PendingHit o) {
            return Double.compare(distance, o.distance);
        }

        @Override
        public String toString() {
            return "PendingHit{" +
                    "loc=" + loc +
                    ", distance=" + distance +
                    ", hit=" + hit +
                    '}';
        }
    }

    public class InternalProjectile extends BukkitRunnable {
        private final List<WarlordsEntity> hit = new ArrayList<>();
        private final List<InternalProjectileTask> tasks = new ArrayList<>();
        private final Location startingLocation;
        private final Location currentLocation;
        private final Vector speed;
        private final WarlordsEntity shooter;
        private int ticksLived = 0;
        private double blocksTravelled = 0;

        private InternalProjectile(WarlordsEntity shooter, Location startingLocation) {
            this.currentLocation = modifyProjectileStartingLocation(shooter, startingLocation);
            boolean nanX = Double.isNaN(currentLocation.getX());
            boolean nanYaw = Double.isNaN(currentLocation.getYaw());
            boolean nanPitch = Double.isNaN(currentLocation.getPitch());
            boolean nanY = Double.isNaN(currentLocation.getY());
            boolean nanZ = Double.isNaN(currentLocation.getZ());
            if (nanX || nanYaw || nanPitch || nanY || nanZ) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("NaN: " + this);
            }
            if (nanX) {
                currentLocation.setX(startingLocation.getX());
            }
            if (nanY) {
                currentLocation.setY(startingLocation.getY());
            }
            if (nanZ) {
                currentLocation.setZ(startingLocation.getZ());
            }
            if (nanPitch) {
                currentLocation.setPitch(startingLocation.getPitch());
            }
            if (nanYaw) {
                currentLocation.setYaw(startingLocation.getYaw());
            }
            this.speed = getProjectileStartingSpeed(shooter, startingLocation);
            this.shooter = shooter;
            this.startingLocation = currentLocation.clone();
        }

        @Override
        public String toString() {
            return "InternalProjectile{" +
                    "hit=" + hit +
                    ", tasks=" + tasks +
                    ", startingLocation=" + startingLocation +
                    ", currentLocation=" + currentLocation +
                    ", speed=" + speed +
                    ", ticksLived=" + ticksLived +
                    ", shooter=" + shooter +
                    '}';
        }

        @Override
        public void run() {
            if (!shooter.getGame().isFrozen()) {
                updateSpeed(this);
                currentLocation.setDirection(speed);
                HitResult hitResult = checkCollisionAndMove(this, currentLocation, speed, shooter);
                if (hitResult != null) {
                    int hitBySplash = onHit(this, hitResult instanceof EntityHitResult entityHitResult ?
                                                  getFromEntity(entityHitResult.getEntity().getBukkitEntity()) :
                                                  null
                    );
                    if (hitResult.getType() == HitResult.Type.ENTITY) {
                        directHits++;
                    }
                    if (hitBySplash > 0) {
                        playersHit++;
                        playersHitBySplash += hitBySplash;
                    }
                    cancel();
                } else if (ticksLived >= maxTicks) {
                    cancel();
                } else {
                    playEffect(this);
                    ticksLived++;
                    blocksTravelled += speed.length();
                    //cancel after 15 seconds
                    if (ticksLived > 15 * 20) {
                        cancel();
                    }
                }
            }
        }

        @Override
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            for (InternalProjectileTask task : tasks) {
                task.onDestroy(this);
            }
        }

        public int getTicksLived() {
            return ticksLived;
        }

        public void setTicksLived(int ticksLived) {
            this.ticksLived = ticksLived;
        }

        public List<WarlordsEntity> getHit() {
            return hit;
        }

        public Location getStartingLocation() {
            return startingLocation;
        }

        public Location getCurrentLocation() {
            return currentLocation;
        }

        public Vector getSpeed() {
            return speed;
        }

        public WarlordsEntity getShooter() {
            return shooter;
        }

        public org.bukkit.World getWorld() {
            return currentLocation.getWorld();
        }

        public void addTask(InternalProjectileTask task) {
            this.tasks.add(task);
        }

        public List<InternalProjectileTask> getTasks() {
            return tasks;
        }

        public double getBlocksTravelled() {
            return blocksTravelled;
        }
    }
}
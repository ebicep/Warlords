package com.ebicep.warlords.abilties.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.Utils;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public abstract class AbstractPiercingProjectileBase extends AbstractAbility {

    public int playersHit = 0;
    public int playersHitBySplash = 0;
    public int directHits = 0;
    public int numberOfDismounts = 0;

    protected final int maxTicks;
    protected final boolean hitTeammates;
    //protected final boolean canBeReflected;
    protected final float playerHitbox = 0.75f;
    protected double maxDistance;
    protected float forwardTeleportAmount = 0;
    protected int maxAngleOfShots = 45;
    protected int shotsFiredAtATime = 1;
    protected HashMap<InternalProjectile, List<InternalProjectile>> internalProjectileGroup = new HashMap<>();
    protected double projectileSpeed;
    private final List<PendingHit> PENDING_HITS = new ArrayList<>();

    public AbstractPiercingProjectileBase(
            String name,
            float minDamageHeal,
            float maxDamageHeal,
            float cooldown,
            float energyCost,
            float critChance,
            float critMultiplier,
            double projectileSpeed,
            double maxDistance,
            boolean hitTeammates
    ) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.projectileSpeed = projectileSpeed;
        this.maxDistance = maxDistance;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
        this.hitTeammates = hitTeammates;
        //this.canBeReflected = canBeReflected;
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
     * Called when the projectile is destroyed by an collision
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
        updateSpeed(projectile.getSpeed(), projectile.getTicksLived());
    }

    @Deprecated
    protected void updateSpeed(Vector speedVector, int ticksLived) {
    }

    double lerp(double a, double b, double target) {
        return a + target * (b - a);
    }

    @Nullable
    protected HitResult checkCollisionAndMove(InternalProjectile projectile, Location currentLocation, Vector speed, WarlordsEntity shooter) {
        return null;
//        Vec3 before = new Vec3(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
//        currentLocation.add(speed);
//        Vec3 after = new Vec3(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
//        int radius = 3;/* TODO */
//        PlayerFilter.entitiesInRectangle(
//                currentLocation.getWorld(),
//                Math.min(before.x - radius, after.x - radius), Math.min(before.y - radius, after.y - radius), Math.min(before.z - radius, after.z - radius),
//                Math.max(before.x + radius, after.x + radius), Math.max(before.y + radius, after.y + radius), Math.max(before.z + radius, after.z + radius)
//        ).enemiesOf(shooter).filter(e -> true /* TODO */);
//
//        @Nullable
//        HitResult hit = null;
//        double hitDistance = 0;
//        for (Entity entity : currentLocation.getWorld().getEntities()) {
//            WarlordsEntity wp = getFromEntity(entity);
//            if (wp != null && (hitTeammates || shooter.isEnemyAlive(wp)) && wp.isAlive() && wp != shooter) {
//                // This logic does not properly deal with an EnderDragon entity, as it has a complex hitbox
//                assert entity instanceof CraftEntity;
//                net.minecraft.world.entity.Entity nmsEntity = ((CraftEntity) entity).getHandle();
//                AABB aabb = nmsEntity.getBoundingBox();
//                // Increase the size of the boundingbox so entities are easier
//                // to hit. This is needed because people see their projectiles
//                // as something big, instead of a tiny point
//                aabb = new AABB(
//                        aabb.minX - playerHitbox,
//                        aabb.minY - playerHitbox,
//                        aabb.minZ - playerHitbox,
//                        aabb.maxX + playerHitbox,
//                        aabb.maxY + playerHitbox,
//                        aabb.maxZ + playerHitbox
//                );
//                HitResult mop = aabb.clip(after, before);
//                if (mop != null) {
//                    mop.entity = nmsEntity;
//                    double distance = before.distanceSquared(mop.pos);
//                    if (shouldEndProjectileOnHit(projectile, wp)) {
//                        if (hit == null || distance < hitDistance) {
//                            hitDistance = distance;
//                            hit = mop;
//                        }
//                    } else {
//                        PENDING_HITS.add(new PendingHit(
//                                new Location(
//                                        currentLocation.getWorld(),
//                                        mop.pos.a,
//                                        mop.pos.b,
//                                        mop.pos.c
//                                ), distance, wp)
//                        );
//                    }
//                }
//            }
//        }
//        BlockIterator itr = new BlockIterator(currentLocation.getWorld(), new Vector(before.a, before.b, before.c), speed, 0, (int) (projectileSpeed + 1));
//        while (itr.hasNext()) {
//            Block block = itr.next();
//            if (block.getType().isSolid() && block.getType() != Material.BARRIER && block.getType() != Material.STANDING_BANNER) {
//                BlockPos pos = new BlockPos(block.getX(), block.getY(), block.getZ());
//                ServerLevel world = ((CraftWorld) block.getWorld()).getHandle();
//                BlockState type = world.getBlockState(pos);
//                AABB box = type.getBlock().a(world, pos, type);
//                HitResult mop = box.a(after, before);
//                // Flags have no hitbox while they are considered solid??
//                if (mop != null) {
//                    double distance = before.distanceSquared(mop.pos);
//                    if (shouldEndProjectileOnHit(projectile, block)) {
//                        if ((hit == null || distance < hitDistance)) {
//                            hitDistance = distance;
//                            hit = mop;
//                        }
//                        // If we hit this point, we either have collided with a
//                        // player closer by, or we hit a block. Blocks are
//                        // checked in order so we can bail out early
//                        break;
//                    }
//                }
//            }
//        }
//        if (hit != null) {
//            currentLocation.setX(hit.pos.a);
//            currentLocation.setY(hit.pos.b);
//            currentLocation.setZ(hit.pos.c);
//        }
//        if (!PENDING_HITS.isEmpty()) {
//            Collections.sort(PENDING_HITS);
//            for (PendingHit p : PENDING_HITS) {
//                if (hit == null || hitDistance < p.distance) {
//                    this.onNonCancellingHit(projectile, p.hit, p.loc);
//                } else {
//                    break;
//                }
//            }
//            PENDING_HITS.clear();
//        }
//        return hit;

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
    protected Location getProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
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
    public boolean onActivate(@Nonnull WarlordsEntity shooter, @Nonnull Player player) {
        shooter.subtractEnergy(energyCost, false);


        List<Location> projectileLocations = getLocationsToFireShots(shooter.getEntity());
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Shot projectile (" + projectileLocations.size() + ")" + shooter.getName() + " -
        // " + shooter.getSpecClass());
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

        return true;
    }

    /**
     * @param player Player that fires the shots
     * @return List of locations in a 2D cone to fire projectiles, number of projectiles depend on numberOfShotsAtATime
     */
    public List<Location> getLocationsToFireShots(LivingEntity player) {
        List<Location> locations = new ArrayList<>();

        Location playerLocation = new LocationBuilder(player.getEyeLocation().clone()).backward(forwardTeleportAmount);
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
        final String activationSound = getActivationSound();
        final float soundVolume = getSoundVolume();
        final float soundPitch = getSoundPitch();
        if (activationSound != null) {
            Utils.playGlobalSound(projectile.getStartingLocation(), activationSound, soundVolume, soundPitch);
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
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
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

    }

    public class InternalProjectile extends BukkitRunnable {
        private final List<WarlordsEntity> hit = new ArrayList<>();
        private final List<InternalProjectileTask> tasks = new ArrayList<>();
        private final Location startingLocation;
        private final Location currentLocation;
        private final Vector speed;
        private final WarlordsEntity shooter;
        private int ticksLived = 0;

        private InternalProjectile(WarlordsEntity shooter, Location startingLocation) {
            this.currentLocation = getProjectileStartingLocation(shooter, startingLocation);
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
                HitResult hasCollided = checkCollisionAndMove(this, currentLocation, speed, shooter);
                if (hasCollided != null) {
                    int hitBySplash = onHit(this, hasCollided instanceof EntityHitResult entityHitResult ?
                                                  getFromEntity(entityHitResult.getEntity().getBukkitEntity()) :
                                                  null
                    );
                    if (hasCollided instanceof EntityHitResult) {
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

    }
}
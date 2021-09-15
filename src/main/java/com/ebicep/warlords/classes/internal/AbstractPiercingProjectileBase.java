package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.ebicep.warlords.util.LocationBuilder;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public abstract class AbstractPiercingProjectileBase extends AbstractAbility {
    private final List<PendingHit> PENDING_HITS = new ArrayList<>();

    protected final double projectileSpeed;
    protected final int maxTicks;
    protected final double maxDistance;
    protected final boolean hitTeammates;
    protected final float playerHitbox = 0.7f;

    public AbstractPiercingProjectileBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, double projectileSpeed, double maxDistance, boolean hitTeammates) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.projectileSpeed = projectileSpeed;
        this.maxDistance = maxDistance;
        this.maxTicks = (int) (maxDistance / projectileSpeed) + 1;
        this.hitTeammates = hitTeammates;
    }

    /**
     * Gets the sound used when this projectile is activated
     * @return
     */
    @Nullable
    protected abstract String getActivationSound();

    @Deprecated
    protected abstract void playEffect(@Nonnull Location currentLocation, int ticksLived);

    /**
     * Plays this projectile effect at a location
     * @param projectile
     */
    @SuppressWarnings("deprecation")
    protected void playEffect(@Nonnull InternalProjectile projectile) {
        for (InternalProjectileTask task : projectile.tasks) {
            task.run(projectile);
        }
        playEffect(projectile.currentLocation, projectile.ticksLived);
    }

    /**
     * Should the collision with this object cause the projectile to consider itself destroyed?
     * @param projectile
     * @param block
     * @return true if it should destroy itself
     */
    protected abstract boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block);

    /**
     * Should the collision with this object cause the projectile to consider itself destroyed?
     * @param projectile
     * @param wp
     * @return true if it should destroy itself
     */
    protected abstract boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsPlayer wp);

    /**
     * Called when the projectile hits a player, but the `shouldEndProjectileOnHit` says the projectile keeps flying
     * @param projectile
     * @param hit
     * @param impactLocation
     */
    protected abstract void onNonCancellingHit(
            @Nonnull InternalProjectile projectile,
            @Nonnull WarlordsPlayer hit,
            @Nonnull Location impactLocation
    );

    @Deprecated
    protected abstract void onHit(
            @Nonnull WarlordsPlayer shooter,
            @Nonnull Location currentLocation,
            @Nonnull Location startingLocation,
            @Nullable WarlordsPlayer hit
    );

    /**
     * Called when the projectile is destroyed by an collision
     * @param projectile The projectile
     * @param hit The player that this projectile impacted on, if any
     */
    @SuppressWarnings("deprecation")
    protected void onHit(
            @Nonnull InternalProjectile projectile,
            @Nullable WarlordsPlayer hit
    ) {
        onHit(projectile.shooter, projectile.currentLocation, projectile.startingLocation, hit);
    }

    @Nullable
    protected WarlordsPlayer getFromEntity(Entity e) {
        if (e instanceof Horse) {
            return Warlords.getPlayer(e.getPassenger());
        }
        return Warlords.getPlayer(e);
    }

    /**
     * Modifies the speed every tick, in case it is needed
     * @param projectile
     */
    @SuppressWarnings("deprecation")
    protected void updateSpeed(InternalProjectile projectile) {
        updateSpeed(projectile.getSpeed(), projectile.getTicksLived());
    }

    @Deprecated
    protected void updateSpeed(Vector speedVector, int ticksLived) {
    }

    @Nullable
    protected MovingObjectPosition checkCollisionAndMove(InternalProjectile projectile, Location currentLocation, Vector speed, WarlordsPlayer shooter) {
        Vec3D before = new Vec3D(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        currentLocation.add(speed);
        Vec3D after = new Vec3D(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        @Nullable
        MovingObjectPosition hit = null;
        double hitDistance = 0;
        for (Entity entity : currentLocation.getWorld().getEntities()) {
            WarlordsPlayer wp = getFromEntity(entity);
            if (wp != null && (hitTeammates || shooter.isEnemyAlive(wp)) && wp.isAlive() && wp != shooter) {
                // This logic does not properly deal with an EnderDragon entity, as it has a complex hitbox
                assert entity instanceof CraftEntity;
                net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
                AxisAlignedBB aabb = nmsEntity.getBoundingBox();
                // Increase the size of the boundingbox so entities are easier
                // to hit. This is needed because people see their projectiles
                // as something big, instead of a tiny point
                aabb = new AxisAlignedBB(
                        aabb.a - playerHitbox,
                        aabb.b - playerHitbox,
                        aabb.c - playerHitbox,
                        aabb.d + playerHitbox,
                        aabb.e + playerHitbox,
                        aabb.f + playerHitbox
                );
                MovingObjectPosition mop = aabb.a(after, before);
                if (mop != null) {
                    mop.entity = nmsEntity;
                    double distance = before.distanceSquared(mop.pos);
                    if(shouldEndProjectileOnHit(projectile, wp)) {
                        if (hit == null || distance < hitDistance) {
                            hitDistance = distance;
                            hit = mop;
                        }
                    } else {
                        PENDING_HITS.add(new PendingHit(
                                new Location(
                                        currentLocation.getWorld(),
                                        mop.pos.a,
                                        mop.pos.b,
                                        mop.pos.c
                                ), distance, wp)
                        );
                    }
                }
            }
        }
        BlockIterator itr = new BlockIterator(currentLocation.getWorld(), new Vector(before.a, before.b, before.c), speed, 0, (int) (projectileSpeed + 1));
        while (itr.hasNext()) {
            Block block = itr.next();
            if (block.getType().isSolid() && block.getType() != Material.BARRIER && block.getType() != Material.STANDING_BANNER) {
                BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
                WorldServer world = ((CraftWorld) block.getWorld()).getHandle();
                IBlockData type = world.getType(pos);
                AxisAlignedBB box = type.getBlock().a(world, pos, type);
                MovingObjectPosition mop = box.a(after, before);
                // Flags have no hitbox while they are considered solid??
                if (mop != null) {
                    double distance = before.distanceSquared(mop.pos);
                    if (shouldEndProjectileOnHit(projectile, block)) {
                        if ((hit == null || distance < hitDistance)) {
                            hitDistance = distance;
                            hit = mop;
                        }
                        // If we hit this point, we either have collided with a
                        // player closer by, or we hit a block. Blocks are
                        // checked in order so we can bail out early
                        break;
                    }
                }
            }
        }
        if (hit != null) {
            currentLocation.setX(hit.pos.a);
            currentLocation.setY(hit.pos.b);
            currentLocation.setZ(hit.pos.c);
        }
        if (!PENDING_HITS.isEmpty()) {
            Collections.sort(PENDING_HITS);
            for (PendingHit p : PENDING_HITS) {
                if (hit == null || hitDistance < p.distance) {
                    this.onNonCancellingHit(projectile, p.hit, p.loc);
                } else {
                    break;
                }
            }
            PENDING_HITS.clear();
        }
        return hit;
    }

    /**
     * Calculated the initial projectile location
     * @param shooter
     * @param startingLocation
     * @return
     */
    protected Location getProjectileStartingLocation(WarlordsPlayer shooter, Location startingLocation) {
        return startingLocation.clone().add(startingLocation.getDirection().multiply(0.2));
    }
    /**
     * Calculated the initial projectile speed
     * @param shooter
     * @param startingLocation
     * @return
     */
    protected Vector getProjectileStartingSpeed(WarlordsPlayer shooter, Location startingLocation) {
        return startingLocation.getDirection().multiply(projectileSpeed);
    }

    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        final String activationSound = getActivationSound();
        if (activationSound != null) {
            for (Player player1 : projectile.getStartingLocation().getWorld().getPlayers()) {
                player1.playSound(projectile.getStartingLocation(), activationSound, 2.3f, 1);
            }
        }
    }

    @Override
    public void onActivate(WarlordsPlayer shooter, Player player) {
        shooter.subtractEnergy(energyCost);
        Location startingLocation = player.getEyeLocation();
        InternalProjectile projectile = new InternalProjectile(shooter, startingLocation);
        onSpawn(projectile);
        projectile.runTaskTimer(Warlords.getInstance(), 0, 1);
    }

    public class InternalProjectile extends BukkitRunnable {
        private final List<WarlordsPlayer> hit = new ArrayList<>();
        private final List<InternalProjectileTask> tasks = new ArrayList<>();
        private final Location startingLocation;
        private final Location currentLocation;
        private final Vector speed;
        private int ticksLived = 0;
        private final WarlordsPlayer shooter;

        private InternalProjectile(WarlordsPlayer shooter, Location startingLocation) {
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
        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            for (InternalProjectileTask task : tasks) {
                task.onDestroy(this);
            }
        }

        @Override
        public void run() {
            updateSpeed(this);
            MovingObjectPosition hasCollided = checkCollisionAndMove(this, currentLocation, speed, shooter);
            if (hasCollided != null) {
                onHit(this, hasCollided.entity == null ? null : getFromEntity(hasCollided.entity.getBukkitEntity()));
                cancel();
            } else if (ticksLived >= maxTicks) {
                cancel();
            } else {
                playEffect(this);
                ticksLived++;
            }
        }

        public int getTicksLived() {
            return ticksLived;
        }

        public void setTicksLived(int ticksLived) {
            this.ticksLived = ticksLived;
        }

        public List<WarlordsPlayer> getHit() {
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

        public WarlordsPlayer getShooter() {
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

    public interface InternalProjectileTask {
        public void run(InternalProjectile projectile);

        public default void onDestroy(InternalProjectile projectile) {
        }
    }

    private final class PendingHit implements Comparable<PendingHit> {
        final Location loc;
        final double distance;
        final WarlordsPlayer hit;

        public PendingHit(Location loc, double distance, WarlordsPlayer hit) {
            this.loc = loc;
            this.distance = distance;
            this.hit = hit;
        }
        @Override
        public int compareTo(PendingHit o) {
            return Double.compare(distance, o.distance);
        }

    }
}
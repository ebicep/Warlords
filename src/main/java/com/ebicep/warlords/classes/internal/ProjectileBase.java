package com.ebicep.warlords.classes.internal;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public abstract class ProjectileBase extends AbstractAbility {
    
    protected final double projectileSpeed;
    protected final int maxTicks;
    protected final double maxDistance;

    public ProjectileBase(String name, float minDamageHeal, float maxDamageHeal, float cooldown, int energyCost, int critChance, int critMultiplier, double projectileSpeed, double maxDistance) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier);
        this.projectileSpeed = projectileSpeed;
        this.maxDistance = maxDistance;
        this.maxTicks = (int)(maxDistance / projectileSpeed) + 1;
    }

    @Nonnull
    protected abstract String getActivationSound();
    
    protected abstract void playEffect(@Nonnull Location currentLocation, int ticksLived);
    
    protected abstract void onHit(
        @Nonnull WarlordsPlayer shooter,
        @Nonnull Location currentLocation,
        @Nonnull Location startingLocation,
        @Nullable Entity hit
    );
    
    @Override
    public void onActivate(WarlordsPlayer shooter, Player player) {
        shooter.subtractEnergy(energyCost);
        Location startingLocation = player.getEyeLocation();
        
        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(startingLocation, getActivationSound(), 2, 1);
        }
        new BukkitRunnable() {

            private final Location currentLocation = startingLocation.clone().add(startingLocation.getDirection().multiply(0.2));
            private final Vector speed = startingLocation.getDirection().multiply(projectileSpeed);
            private int ticksLived = 0;

            @Override
            public void run() {
                MovingObjectPosition hasCollided = checkCollisionAndMove(currentLocation, speed, shooter);
                if (hasCollided != null) {
                    onHit(shooter, currentLocation, startingLocation, hasCollided.entity == null ? null : hasCollided.entity.getBukkitEntity());
                    cancel();
                } else if (ticksLived >= maxTicks) {
                    cancel();
                } else {
                    playEffect(currentLocation, ticksLived++);
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 1);
    }
    
    @Nullable
    private MovingObjectPosition checkCollisionAndMove(Location currentLocation, Vector speed, WarlordsPlayer shooter) {
        Vec3D before = new Vec3D(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        currentLocation.add(speed);
        Vec3D after = new Vec3D(currentLocation.getX(), currentLocation.getY(), currentLocation.getZ());
        @Nullable
        MovingObjectPosition hit = null;
        double hitDistance = 0;
        for (Entity entity : currentLocation.getWorld().getEntities()) {
            if (
                (entity instanceof Zombie || entity instanceof Player || entity instanceof Horse) &&
                entity != shooter.getEntity() &&
                (!(entity instanceof Player) || ((Player)entity).getGameMode() != GameMode.SPECTATOR)
            ) {
                // This logic does not properly deal with an EnderDragon entity, as it has an complex hitbox
                assert entity instanceof CraftEntity;
                net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
                AxisAlignedBB aabb = nmsEntity.getBoundingBox();
                MovingObjectPosition mop = aabb.a(after, before);
                if (mop != null) {
                    mop.entity = nmsEntity;
                    double distance = before.distanceSquared(mop.pos);
                    if (hit == null || distance < hitDistance) {
                        hitDistance = distance;
                        hit = mop;
                    }
                }
            }
        }
        BlockIterator itr = new BlockIterator(
            currentLocation.getWorld(),
            new Vector(before.a, before.b, before.c),
            speed,
            0, (int) (projectileSpeed + 1)
        );
        while (itr.hasNext()) {
            Block block = itr.next();
            if (!block.isEmpty()) {
                BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
                WorldServer world = ((CraftWorld) block.getWorld()).getHandle();
                IBlockData type = world.getType(pos);
                AxisAlignedBB box = type.getBlock().a(world, pos, type);
                MovingObjectPosition mop = box.a(after, before);
                if (mop != null) {
                    double distance = before.distanceSquared(mop.pos);
                    if (hit == null || distance < hitDistance) {
                        hit = mop;
                    }
                    break;
                }
            }
        }
        if (hit != null) {
            currentLocation.setX(hit.pos.a);
            currentLocation.setY(hit.pos.b);
            currentLocation.setZ(hit.pos.c);
        }
        return hit;
    }
    // public MovingObjectPosition a(Vec3D vec3d, Vec3D vec3d1) 
   
}

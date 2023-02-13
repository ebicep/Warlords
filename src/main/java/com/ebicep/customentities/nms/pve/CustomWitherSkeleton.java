package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomWitherSkeleton extends WitherSkeleton implements CustomEntity<CustomWitherSkeleton> {

    private final PathfinderGoalFireAtPlayer pathfinderGoalFireAtPlayer = new PathfinderGoalFireAtPlayer(this, 30);
    private boolean stunned;

    public CustomWitherSkeleton(org.bukkit.World world) {
        this(((CraftWorld) world).getHandle());
    }

    public CustomWitherSkeleton(ServerLevel serverLevel) {
        super(EntityType.WITHER_SKELETON, serverLevel);
        resetAI();
        giveBaseAI(1.2, 1.0, 100);
        this.goalSelector.addGoal(2, pathfinderGoalFireAtPlayer);
    }

    @Override
    public CustomWitherSkeleton get() {
        return this;
    }

    @Override
    public void setStunned(boolean stunned) {
        this.stunned = stunned;
    }

    @Override
    public boolean canCollideWithBukkit(@Nonnull Entity entity) {
        return !stunned;
    }

    public PathfinderGoalFireAtPlayer getPathfinderGoalFireAtPlayer() {
        return pathfinderGoalFireAtPlayer;
    }

    static class PathfinderGoalFireAtPlayer extends Goal {

        private final Mob self;

        private final AtomicInteger fireTickDelay = new AtomicInteger(); //delay between shots

        private final AtomicInteger ticks = new AtomicInteger(); //counter for ticks
        private final AtomicInteger delay = new AtomicInteger((int) (Math.random() * 4)); //countdown for delay, starts at a random number between 0 and 4 so shots are not all fired at the same time

        public PathfinderGoalFireAtPlayer(Mob self, int fireTickDelay) {
            this.self = self;
            this.fireTickDelay.set(fireTickDelay);
        }

        @Override
        public boolean canUse() {
            if (delay.get() != 0) {
                delay.getAndDecrement();
                return false;
            }
            ticks.getAndIncrement();
            if (ticks.get() % fireTickDelay.get() == 0) {
                delay.set(fireTickDelay.get());
                return true;
            }

            return false;
        }

        @Override
        public void tick() {
            if (self.getTarget() == null) {
                return;
            }
            LivingEntity target = self.getTarget();

            //Location targetLocation = target.getBukkitEntity().getLocation();
            WarlordsEntity warlordsEntitySelf = Warlords.getPlayer(self.getBukkitEntity());
            WarlordsEntity warlordsEntityTarget = Warlords.getPlayer(target.getBukkitEntity());
            if (warlordsEntitySelf != null && warlordsEntityTarget != null) {
                Location lookAtLocation = lookAtLocation(warlordsEntitySelf.getLocation(), predictFutureLocation(warlordsEntitySelf, warlordsEntityTarget));
                self.getBukkitEntity().teleport(lookAtLocation);
                warlordsEntitySelf.getSpec().getWeapon().onActivate(warlordsEntitySelf, null);
            }
        }

        public static Location lookAtLocation(Location loc, Location toLookAt) {
            //Clone the loc to prevent applied changes to the input loc
            loc = loc.clone();

            // Values of change in distance (make it relative)
            double dx = toLookAt.getX() - loc.getX();
            double dy = toLookAt.getY() - loc.getY();
            double dz = toLookAt.getZ() - loc.getZ();

            // Set yaw
            if (dx != 0) {
                // Set yaw start value based on dx
                if (dx < 0) {
                    loc.setYaw((float) (1.5 * Math.PI));
                } else {
                    loc.setYaw((float) (0.5 * Math.PI));
                }
                loc.setYaw(loc.getYaw() - (float) Math.atan(dz / dx));
            } else if (dz < 0) {
                loc.setYaw((float) Math.PI);
            }

            // Get the distance from dx/dz
            double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

            // Set pitch
            loc.setPitch((float) -Math.atan(dy / dxz));

            // Set values, convert to degrees (invert the yaw since Bukkit uses a different yaw dimension format)
            loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
            loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);

            return loc;
        }

        //should just use arrow mechanic - https://gist.github.com/Minikloon/4f53ea780350c7b86761318ca313a9ed
        public static Location predictFutureLocation(WarlordsEntity self, WarlordsEntity target) {
            if (target == null) {
                return self.getLocation();
            }
            if (target.getCurrentVector() == null) {
                return target.getLocation();
            }
            Location location = target.getLocation().clone();
            Vector oldVectorToSubtract = target.getCurrentVector().clone();
            if (oldVectorToSubtract == null) {
                return location;
            }
            if (!self.getWorld().equals(target.getWorld())) {
                return location;
            }
            Vector vector = target.getCurrentVector().clone();

            List<Location> locations = target.getLocations();
            if (!locations.isEmpty()) {
                //check if player is standing still
                Location previousLocation = locations.get(locations.size() - 1);
                if (previousLocation.getX() == location.getX() && previousLocation.getY() == location.getY() && previousLocation.getZ() == location.getZ()) {
                    return location;
                } else {
                    double distance = self.getLocation().distanceSquared(target.getLocation());
                    if (distance >= 100) {
                        //idk
                        oldVectorToSubtract.setX(oldVectorToSubtract.getX() * .7);
                        oldVectorToSubtract.setZ(oldVectorToSubtract.getZ() * .7);
                        if (oldVectorToSubtract.getY() == 0 && !target.getEntity().isOnGround()) {
                            oldVectorToSubtract.setY(.35);
                        } else if (oldVectorToSubtract.getY() > .5) {
                            oldVectorToSubtract.setY(oldVectorToSubtract.getY() * 1.1);
                        } else {
                            oldVectorToSubtract.setY(oldVectorToSubtract.getY() * .75);
                        }
                        vector.subtract(oldVectorToSubtract);
                    } else {
                        vector.setY(vector.getY() / Math.pow(distance, 1.1));
                    }
                    //multiply more the farther away the player is
                    if (distance > 100) {
                        return location.add(vector.multiply(2 + Math.log(distance) / 2.5));
                    } else {
                        return location.add(vector.multiply(1));
                    }
                }
            }

            return location;
        }

        public int getFireTickDelay() {
            return fireTickDelay.get();
        }

        public void setFireTickDelay(int fireTickDelay) {
            this.fireTickDelay.set(fireTickDelay);
        }
    }

}


package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.util.Vector;

import java.util.List;

public class CustomSkeleton extends EntitySkeleton implements CustomEntity<CustomSkeleton> {

    private final PathfinderGoalFireAtPlayer pathfinderGoalFireAtPlayer = new PathfinderGoalFireAtPlayer(this, 20);

    public CustomSkeleton(World world) {
        super(world);
        this.goalSelector.a(7, pathfinderGoalFireAtPlayer);
    }


    @Override
    public void spawn(Location location) {
        setPosition(location.getX(), location.getY(), location.getZ());
        getBukkitEntity().setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(this);

    }

    @Override
    public CustomSkeleton get() {
        return this;
    }

    public PathfinderGoalFireAtPlayer getPathfinderGoalFireAtPlayer() {
        return pathfinderGoalFireAtPlayer;
    }

    static class PathfinderGoalFireAtPlayer extends PathfinderGoal {

        private final EntityInsentient self;

        private int fireTickDelay; //delay between shots

        private int ticks = 0; //counter for ticks
        private int delay = (int) (Math.random() * 4); //countdown for delay, starts at a random number between 0 and 4 so shots are not all fired at the same time

        public PathfinderGoalFireAtPlayer(EntityInsentient self, int fireTickDelay) {
            this.self = self;
            this.fireTickDelay = fireTickDelay;
        }

        @Override
        public boolean a() {
            if (delay != 0) {
                delay--;
                return false;
            }
            ticks++;
            if (ticks % fireTickDelay == 0) {
                delay = fireTickDelay;
                return true;
            }

            return false;
        }

        @Override
        public void c() {
            if (self.getGoalTarget() == null) {
                return;
            }
            EntityLiving target = self.getGoalTarget();

            //Location targetLocation = target.getBukkitEntity().getLocation();
            WarlordsEntity warlordsEntitySelf = Warlords.getPlayer(self.getBukkitEntity());
            WarlordsEntity warlordsEntityTarget = Warlords.getPlayer(target.getBukkitEntity());
            if (warlordsEntitySelf != null && warlordsEntityTarget != null) {
                Location lookAtLocation = lookAtLocation(warlordsEntitySelf.getLocation(), predictFutureLocation(warlordsEntitySelf, warlordsEntityTarget));
                self.getBukkitEntity().teleport(lookAtLocation);
                warlordsEntitySelf.getSpec().getWeapon().onActivate(warlordsEntitySelf, null);
            }
        }

        //should just use arrow mechanic - https://gist.github.com/Minikloon/4f53ea780350c7b86761318ca313a9ed
        public static Location predictFutureLocation(WarlordsEntity self, WarlordsEntity target) {
            Location location = target.getLocation().clone();
            Vector oldVectorToSubtract = target.getCurrentVector().clone();
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
                loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
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


        public int getFireTickDelay() {
            return fireTickDelay;
        }

        public void setFireTickDelay(int fireTickDelay) {
            this.fireTickDelay = fireTickDelay;
        }
    }

}


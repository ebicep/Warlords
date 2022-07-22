package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

//FOR TESTING
public class PathfinderGoalFireAtPlayer extends PathfinderGoal {

    private final EntityInsentient self;
    private EntityLiving target;

    private double entityMoveSpeed = 1.5;
    private float maxAttackDistance = 15 * 15;
    private int fireTickDelay; //delay between shots

    //f = seeTime
    //g = reloadtime
    private int delay = (int) (Math.random() * 4); //countdown for delay, starts at a random number between 0 and 4 so shots are not all fired at the same time

    public PathfinderGoalFireAtPlayer(EntityInsentient self, int fireTickDelay) {
        this.self = self;
        this.fireTickDelay = fireTickDelay;
    }

    /**
     * Returns whether the PathfinderGoal should begin execution.
     */
    @Override
    public boolean a() {
        EntityLiving entityliving = this.self.getGoalTarget();
        if (entityliving == null) {
            return false;
        } else {
            this.target = entityliving;
            return true;
        }
    }

    /**
     * Returns whether an in-progress PathfinderGoal should continue executing
     */
    @Override
    public boolean b() {
        return this.a() || !this.self.getNavigation().m();
    }

    /**
     * Resets the task
     */
    @Override
    public void d() {
        System.out.println("Reset");
        this.target = null;
        this.delay = fireTickDelay;

    }

    /**
     * Updates the task
     */
    @Override
    public void e() {
        double d0 = this.self.e(this.target.locX, this.target.getBoundingBox().b, this.target.locZ);
        if (d0 <= (double) this.maxAttackDistance) {
            this.self.getNavigation().n();
        } else {
            this.self.getNavigation().a(this.target, this.entityMoveSpeed);
        }

        this.self.getControllerLook().a(this.target, 30.0F, 30.0F);

        if (--this.delay == 0) {
            if (d0 > (double) this.maxAttackDistance) {
                return;
            }

            WarlordsEntity warlordsEntitySelf = Warlords.getPlayer(self.getBukkitEntity());
            WarlordsEntity warlordsEntityTarget = Warlords.getPlayer(target.getBukkitEntity());
            if (warlordsEntitySelf == null || warlordsEntityTarget == null) {
                return;
            }
            Location lookAtLocation = lookAtLocation(warlordsEntitySelf.getLocation(), predictFutureLocation(warlordsEntitySelf, warlordsEntityTarget));
            self.getBukkitEntity().teleport(lookAtLocation);
            warlordsEntitySelf.getSpec().getWeapon().onActivate(warlordsEntitySelf, null);
            this.delay = fireTickDelay;
        } else if (this.delay < 0) {
            this.delay = fireTickDelay;
        }
    }

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
}
package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PredictTargetFutureLocationGoal extends Goal {

    private final Mob self;
    private final AtomicInteger delay = new AtomicInteger((int) (Math.random() * 10)); //countdown for delay, starts at a random number so shots are not all fired at the same time

    private float yaw;
    private float pitch;

    public PredictTargetFutureLocationGoal(Mob self) {
        this.self = self;
    }

    @Override
    public boolean canUse() {
        if (delay.get() != 0) {
            delay.getAndDecrement();
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        if (self.getTarget() == null) {
            return;
        }
        LivingEntity target = self.getTarget();

        WarlordsEntity warlordsEntitySelf = Warlords.getPlayer(self.getBukkitEntity());
        WarlordsEntity warlordsEntityTarget = Warlords.getPlayer(target.getBukkitEntity());
        if (warlordsEntitySelf != null && warlordsEntityTarget != null) {
            Location lookAtLocation = lookAtLocation(warlordsEntitySelf.getLocation(), predictFutureLocation(warlordsEntitySelf, warlordsEntityTarget));
            float yaw = lookAtLocation.getYaw();
            float pitch = lookAtLocation.getPitch();
            if (!NumberConversions.isFinite(yaw) || !NumberConversions.isFinite(pitch)) {
                return;
            }
            this.yaw = yaw;
            this.pitch = pitch;
            //TODO maybe look every second so its not choppy
        }
    }

    public void lookAtPredicted() {
        self.getBukkitEntity().setRotation(yaw, pitch);
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public static Location lookAtLocation(Location loc, Location toLookAt) {
        Vector vector = toLookAt.toVector().subtract(loc.toVector());
        return loc.clone().setDirection(vector);
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
            }
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

        return location;
    }

}

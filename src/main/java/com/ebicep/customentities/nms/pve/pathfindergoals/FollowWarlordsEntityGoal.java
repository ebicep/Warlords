package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Location;

import java.util.EnumSet;

public class FollowWarlordsEntityGoal extends Goal {

    private final Mob mob;
    private final WarlordsEntity warlordsEntity;
    private final double speed;
    private final float maxDistanceSquared;

    private double targetX;
    private double targetY;
    private double targetZ;

    public FollowWarlordsEntityGoal(Mob mob, WarlordsEntity warlordsEntity, double speed, float maxDistance) {
        adjustedTickDelay(1);
        this.mob = mob;
        this.warlordsEntity = warlordsEntity;
        this.speed = speed;
        this.maxDistanceSquared = maxDistance * maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (warlordsEntity.isDead()) {
            return false;
        }
        Location followingLocation = warlordsEntity.getLocation();
        targetX = followingLocation.getX();
        targetY = followingLocation.getY();
        targetZ = followingLocation.getZ();
        double distanceSquared = followingLocation.distanceSquared(mob.getBukkitEntity().getLocation());
        return distanceSquared > maxDistanceSquared;
    }

    @Override
    public boolean canContinueToUse() {
        boolean inProgress = mob.getNavigation().isInProgress();
        boolean targetAlive = !warlordsEntity.isDead();
        double distance = mob.getBukkitEntity().getLocation().distanceSquared(warlordsEntity.getLocation());
        return inProgress && targetAlive && distance > maxDistanceSquared;
    }

    @Override
    public void start() {
        super.start();
        mob.getNavigation().moveTo(targetX, targetY, targetZ, speed);
    }

}

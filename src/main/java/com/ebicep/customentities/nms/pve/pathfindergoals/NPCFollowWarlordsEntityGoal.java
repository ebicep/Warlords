package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

public class NPCFollowWarlordsEntityGoal implements Goal {

    private final WarlordsEntity warlordsEntity;
    private final double speed;
    private final float maxDistanceSquared;
    private NPC npc;


    public NPCFollowWarlordsEntityGoal(NPC npc, WarlordsEntity warlordsEntity, double speed, float maxDistance) {
        this.npc = npc;
        this.warlordsEntity = warlordsEntity;
        this.speed = speed;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    public void reset() {

    }

    @Override
    public void run(GoalSelector goalSelector) {
        double distance = npc.getStoredLocation().distanceSquared(warlordsEntity.getLocation());
        if (distance < maxDistanceSquared) {
            goalSelector.finish();
        }
    }

    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        if (!npc.isSpawned()) {
            return false;
        }
        if (warlordsEntity.isDead()) {
            return false;
        }
        Location followingLocation = warlordsEntity.getLocation();
        double distanceSquared = followingLocation.distanceSquared(npc.getStoredLocation());
        boolean outsideRange = distanceSquared > maxDistanceSquared;
        if (outsideRange) {
            npc.getNavigator().setTarget(warlordsEntity.getEntity(), false);
        }
        return outsideRange;
    }
}

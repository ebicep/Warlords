package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.citizensnpcs.api.ai.Goal;
import net.citizensnpcs.api.ai.GoalSelector;
import net.citizensnpcs.api.npc.NPC;

public class NPCFollowWarlordsEntityGoal implements Goal {

    private final WarlordsEntity warlordsEntity;
    private final float maxDistanceSquared;
    private NPC npc;

    public NPCFollowWarlordsEntityGoal(NPC npc, WarlordsEntity warlordsEntity, float maxDistance) {
        this.npc = npc;
        this.warlordsEntity = warlordsEntity;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    public void reset() {

    }

    @Override
    public void run(GoalSelector goalSelector) {
//        goalSelector.
    }

    @Override
    public boolean shouldExecute(GoalSelector goalSelector) {
        return false;
    }

//    @Override
//    public BehaviorStatus run() {
//        double distance = npc.getStoredLocation().distanceSquared(warlordsEntity.getLocation());
//        if (distance < maxDistanceSquared) {
//            return BehaviorStatus.SUCCESS;
//        }
//        return BehaviorStatus.RUNNING;
//    }
//
//    @Override
//    public boolean shouldExecute() {
//        if (!npc.isSpawned()) {
//            return false;
//        }
//        if (warlordsEntity.isDead()) {
//            return false;
//        }
//        Location followingLocation = warlordsEntity.getLocation();
//        double distanceSquared = followingLocation.distanceSquared(npc.getStoredLocation());
//        boolean outsideRange = distanceSquared > maxDistanceSquared;
//        if (outsideRange) {
//            npc.getNavigator().setTarget(warlordsEntity.getEntity(), false);
//        }
//        return outsideRange;
//    }

}

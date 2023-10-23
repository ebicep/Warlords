package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;

public class NPCFollowWarlordsEntityGoal implements Behavior {

    private final NPC npc;
    private final WarlordsEntity warlordsEntity;
    private final float maxDistanceSquared;

    public NPCFollowWarlordsEntityGoal(NPC npc, WarlordsEntity warlordsEntity, float maxDistance) {
        this.npc = npc;
        this.warlordsEntity = warlordsEntity;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    public void reset() {
        npc.getNavigator().cancelNavigation();
    }

    @Override
    public BehaviorStatus run() {
        double distance = npc.getStoredLocation().distanceSquared(warlordsEntity.getLocation());
        if (distance < maxDistanceSquared) {
            return BehaviorStatus.SUCCESS;
        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        boolean shouldFollow = shouldFollow(npc, warlordsEntity, maxDistanceSquared);
        if (shouldFollow) {
            npc.getNavigator().setTarget(warlordsEntity.getEntity(), false);
        }
        return shouldFollow;
    }

    public static boolean shouldFollow(NPC npc, WarlordsEntity toFollow, double maxDistanceSquared) {
        if (!npc.isSpawned()) {
            return false;
        }
        if (toFollow.isDead()) {
            return false;
        }
        return toFollow.getLocation().distanceSquared(npc.getStoredLocation()) >= maxDistanceSquared;
    }

}

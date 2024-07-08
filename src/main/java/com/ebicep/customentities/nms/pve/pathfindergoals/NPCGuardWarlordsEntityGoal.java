package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;

import java.util.Collections;

public class NPCGuardWarlordsEntityGoal implements Behavior {

    private final NPC npc;
    private final WarlordsEntity warlordsEntity;
    private final float maxDistanceSquared;
    private WarlordsEntity warlordsEntityTarget;

    public NPCGuardWarlordsEntityGoal(NPC npc, WarlordsEntity warlordsEntity, float maxDistance) {
        this.npc = npc;
        this.warlordsEntity = warlordsEntity;
        this.maxDistanceSquared = maxDistance * maxDistance;
    }

    @Override
    public void reset() {
        npc.getNavigator().cancelNavigation();
        warlordsEntityTarget = null;
    }

    @Override
    public BehaviorStatus run() {
        // outside range = success = go back to following execution
        double distance = npc.getStoredLocation().distanceSquared(warlordsEntity.getLocation());
        EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
        boolean following = entityTarget != null && entityTarget.getTarget() == warlordsEntity.getEntity();
        if (distance >= maxDistanceSquared && !following) {
            return BehaviorStatus.FAILURE;
        }
        if (following) {
            // stop following when inside range, *9 so no jitter back and forth
            if (distance < maxDistanceSquared * .9) {
                return BehaviorStatus.SUCCESS;
            }
            return BehaviorStatus.RUNNING;
        }

        // inside range = target enemy
        if (warlordsEntityTarget == null || entityTarget == null) {
            return BehaviorStatus.FAILURE;
        }
        if (warlordsEntityTarget.isDead() || !warlordsEntityTarget.getEntity().isValid()) {
            return BehaviorStatus.SUCCESS;
        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        if (NPCFollowWarlordsEntityGoal.shouldFollow(npc, warlordsEntity, maxDistanceSquared)) {
            npc.getNavigator().setTarget(warlordsEntity.getEntity(), false);
            return true;
        }
        warlordsEntityTarget = NPCTargetAggroWarlordsEntityGoal.getTarget(npc, maxDistanceSquared, Collections.emptyList());
        if (warlordsEntityTarget == null) {
            return false;
        }
        Entity target = warlordsEntityTarget.getEntity();
        npc.getNavigator().setTarget(target, true);
        return true;
    }

}

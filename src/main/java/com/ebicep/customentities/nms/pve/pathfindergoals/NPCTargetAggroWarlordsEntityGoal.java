package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.RandomCollection;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Comparator;
import java.util.List;

/**
 * Aggro system
 * <p>
 * All warlords players have an extra aggro weight
 * <p>
 * Closest player weight = 100 + aggro bonus
 * <p>
 * Block distance to the closest player is x
 * <p>
 * All other players have 100 + aggro bonus - 10(distance - x). So every block away from distance to closest player is 10 aggro less
 * <p>
 * ex. (w=100,d=20), (w=50,d=25), (w=0,d>=30)
 * Random target is chosen from weighted list
 */
public class NPCTargetAggroWarlordsEntityGoal extends BehaviorGoalAdapter {

    private final double range;
    private NPC npc;
    private Entity target;
    private WarlordsEntity warlordsEntityTarget;

    public NPCTargetAggroWarlordsEntityGoal(NPC npc, double range) {
        this.npc = npc;
        this.range = range;
    }

    @Override
    public void reset() {
        this.npc.getNavigator().cancelNavigation();
        this.target = null;
    }

    @Override
    public BehaviorStatus run() {
        if (warlordsEntityTarget.isDead()) {
            return BehaviorStatus.SUCCESS;
        }
        if (npc.getNavigator().getEntityTarget() == null) {
            return BehaviorStatus.FAILURE;
        }
        if (!target.isValid()) {
            return BehaviorStatus.SUCCESS;
        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        if (!npc.isSpawned()) {
            return false;
        }
        Entity npcEntity = npc.getEntity();
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(npcEntity);
        if (thisWarlordsEntity == null) {
            return false;
        }
        Location location = npcEntity.getLocation();
        List<Entity> list = GoalUtils.getNearbyWarlordEntities(npcEntity, thisWarlordsEntity, range);
        list.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
        if (list.isEmpty()) {
            return false;
        }
        Entity closestEntity = list.get(0);
        double distanceToClosest = location.distanceSquared(closestEntity.getLocation());
        RandomCollection<Entity> randomCollection = new RandomCollection<>();
        for (Entity entity : list) {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity);
            if (warlordsEntity != null) {
                randomCollection.add(entity == closestEntity ?
                                     1000 + warlordsEntity.getBonusAggroWeight() :
                                     1000 + warlordsEntity.getBonusAggroWeight() - 10 * (location.distanceSquared(entity.getLocation()) - distanceToClosest),
                        entity
                );
            }
        }
        if (randomCollection.getSize() == 0) {
            return false;
        }
        this.target = randomCollection.next();
        this.warlordsEntityTarget = Warlords.getPlayer(this.target);
        this.npc.getNavigator().setTarget(this.target, true);
        return true;
    }
}
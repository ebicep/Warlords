package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.RandomCollection;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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
    private final NPC npc;
    private WarlordsEntity warlordsEntityTarget;
    private final List<Predicate<WarlordsEntity>> extraFilters = new ArrayList<>();

    public NPCTargetAggroWarlordsEntityGoal(NPC npc, double range) {
        this.npc = npc;
        this.range = range;
    }

    @SafeVarargs
    public NPCTargetAggroWarlordsEntityGoal(NPC npc, double range, Predicate<WarlordsEntity>... extraFilters) {
        this.npc = npc;
        this.range = range;
        this.extraFilters.addAll(Arrays.asList(extraFilters));
    }

    @Override
    public void reset() {
        this.npc.getNavigator().cancelNavigation();
        this.warlordsEntityTarget = null;
    }

    @Override
    public BehaviorStatus run() {
        if (warlordsEntityTarget.isDead()) {
            return BehaviorStatus.SUCCESS;
        }
        if (!warlordsEntityTarget.getEntity().isValid()) {
            return BehaviorStatus.SUCCESS;
        }
        EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
        if (entityTarget == null) {
            return BehaviorStatus.FAILURE;
        }
        // safe guard
        if (entityTarget.getTarget() instanceof Player player && player.getGameMode() == GameMode.SPECTATOR) {
            return BehaviorStatus.FAILURE;
        }
//        WarlordsEntity currentTarget = Warlords.getPlayer(entityTarget.getTarget());
//        if (!Objects.equals(currentTarget, warlordsEntityTarget)) {
//            return BehaviorStatus.FAILURE;
//        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        warlordsEntityTarget = getTarget(npc, range, extraFilters);
        if (warlordsEntityTarget == null) {
            return false;
        }
        Entity target = warlordsEntityTarget.getEntity();
        npc.getNavigator().setTarget(target, true);
        return true;
    }

    @Nullable
    public static WarlordsEntity getTarget(NPC npc, double range, List<Predicate<WarlordsEntity>> extraFilters) {
        if (!npc.isSpawned()) {
            return null;
        }
        Entity npcEntity = npc.getEntity();
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(npcEntity);
        if (thisWarlordsEntity == null) {
            return null;
        }
        Location location = npcEntity.getLocation();
        List<WarlordsEntity> list = GoalUtils.getNearbyEnemies(npcEntity, thisWarlordsEntity, range, extraFilters);
        list.sort(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)));
        if (list.isEmpty()) {
            return null;
        }
        WarlordsEntity closestEntity = list.get(0);
        double distanceToClosest = location.distanceSquared(closestEntity.getLocation());
        RandomCollection<WarlordsEntity> randomCollection = new RandomCollection<>();
        for (WarlordsEntity warlordsEntity : list) {
            randomCollection.add(warlordsEntity == closestEntity ?
                                 1000 + warlordsEntity.getBonusAggroWeight() :
                                 1000 + warlordsEntity.getBonusAggroWeight() - 10 * (location.distanceSquared(warlordsEntity.getLocation()) - distanceToClosest),
                    warlordsEntity
            );
        }
        if (randomCollection.getSize() == 0) {
            return null;
        }
        return randomCollection.next();
    }
}
package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.customentities.nms.pve.pathfindergoals.NPCTargetAggroWarlordsEntityGoal;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseSpawner;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class NPCTowerDefensePathfindGoal extends BehaviorGoalAdapter {

    private final NPC npc;
    private final TowerDefenseSpawner towerDefenseSpawner;
    private final AbstractMob mob;
    private final TowerDefenseOption.TowerDefenseAttackingMobData mobData;

    private WarlordsEntity warlordsEntityTarget;
    private List<Predicate<WarlordsEntity>> extraFilters = new ArrayList<>();


    public NPCTowerDefensePathfindGoal(NPC npc, TowerDefenseSpawner towerDefenseSpawner, AbstractMob mob, TowerDefenseOption.TowerDefenseAttackingMobData mobData) {
        this.npc = npc;
        this.towerDefenseSpawner = towerDefenseSpawner;
        this.mob = mob;
        this.mobData = mobData;
        this.extraFilters.add(warlordsEntity -> {
            if (warlordsEntity instanceof WarlordsNPC wNPC) {
                return mobData.getAttackingTeam() == wNPC.getTeam();
            }
            return false;
        });
        // prevent mob from going backwards to attack defending mob TODO test
        this.extraFilters.add(warlordsEntity -> npc.isSpawned() &&
                npc.getEntity() instanceof LivingEntity livingEntity &&
                livingEntity.hasLineOfSight(warlordsEntity.getEntity())
        );
    }


    @Override
    public void reset() {
        this.npc.getNavigator().cancelNavigation();
    }

    @Override
    public BehaviorStatus run() {
        if (!npc.getNavigator().isNavigating()) {
            return BehaviorStatus.FAILURE;
        }
        if (warlordsEntityTarget.isDead()) {
            return BehaviorStatus.SUCCESS;
        }
        if (!warlordsEntityTarget.getEntity().isValid()) {
            return BehaviorStatus.SUCCESS;
        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        warlordsEntityTarget = NPCTargetAggroWarlordsEntityGoal.getTarget(npc, 5, extraFilters);
        if (warlordsEntityTarget == null) {
            if (!npc.getNavigator().isNavigating()) {
                towerDefenseSpawner.pathFindToNextWaypoint(mob, mobData);
            }
            return false;
        }
        Entity target = warlordsEntityTarget.getEntity();
        npc.getNavigator().setTarget(target, true);
        return true;
    }
}

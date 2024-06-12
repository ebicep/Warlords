package com.ebicep.warlords.game.option.towerdefense.mobs;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseSpawner;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class NPCTowerDefensePathfindGoal extends BehaviorGoalAdapter {

    private final NPC npc;
    private final TowerDefenseSpawner towerDefenseSpawner;
    private final AbstractMob mob;
    private final TowerDefenseOption.TowerDefenseAttackingMobData mobData;
    private WarlordsEntity warlordsEntityTarget;


    public NPCTowerDefensePathfindGoal(NPC npc, TowerDefenseSpawner towerDefenseSpawner, AbstractMob mob, TowerDefenseOption.TowerDefenseAttackingMobData mobData) {
        this.npc = npc;
        this.towerDefenseSpawner = towerDefenseSpawner;
        this.mob = mob;
        this.mobData = mobData;
    }

    @Override
    public void reset() {
        this.npc.getNavigator().cancelNavigation();
    }

    @Override
    public BehaviorStatus run() {
        if (!towerDefenseSpawner.getTowerDefenseOption().isMovement()) {
            return BehaviorStatus.FAILURE;
        }
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
        if (!towerDefenseSpawner.getTowerDefenseOption().isMovement()) {
            npc.getNavigator().setPaused(true);
            return false;
        }
        EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
        if (entityTarget != null) {
            warlordsEntityTarget = Warlords.getPlayer(entityTarget.getTarget());
            if (warlordsEntityTarget != null) {
                return true;
            }
        }
        Entity npcEntity = npc.getEntity();
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(npcEntity);
        if (thisWarlordsEntity == null) {
            return false;
        }
        warlordsEntityTarget = getTarget(thisWarlordsEntity);
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

    @Nullable
    public WarlordsEntity getTarget(WarlordsEntity thisWarlordsEntity) {
        double range = npc.getNavigator().getDefaultParameters().attackRange() + 2;
        List<WarlordsEntity> targets = PlayerFilterGeneric
                .entitiesAround(thisWarlordsEntity, range, range, range)
                .warlordsNPCs()
                .aliveEnemiesOf(thisWarlordsEntity)
                .filter(warlordsNPC -> mobData.getAttackingTeam() == warlordsNPC.getTeam())
                .filter(warlordsNPC -> {
                    TowerDefenseOption.TowerDefenseMobData mobData = towerDefenseSpawner.getMobs().get(warlordsNPC.getMob());
                    if (!(mobData instanceof TowerDefenseOption.TowerDefenseDefendingMobData defendingMobData)) {
                        return false;
                    }
                    return defendingMobData.getTargetedBy().isEmpty();
                })
                .filter(warlordsNPC -> {
                    if (npc.getEntity() instanceof LivingEntity livingEntity) {
                        Location eyeLocation = livingEntity.getEyeLocation();
                        if (warlordsNPC.getEntity() instanceof LivingEntity targetEntity) {
                            return LocationUtils.getDotToLocation(eyeLocation, targetEntity.getEyeLocation()) > .5;
                        }
                        return LocationUtils.getDotToLocation(eyeLocation, warlordsNPC.getLocation()) > 0.5;
                    }
                    return false;
                })
                .stream()
                .collect(Collectors.toList());
        return targets.isEmpty() ? null : targets.get(0);
    }

}

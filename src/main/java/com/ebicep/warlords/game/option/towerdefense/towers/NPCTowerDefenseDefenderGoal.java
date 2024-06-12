package com.ebicep.warlords.game.option.towerdefense.towers;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NPCTowerDefenseDefenderGoal extends BehaviorGoalAdapter {

    private final TowerDefenseTowerMob towerMob;
    private final NPC npc;
    private final double range;
    private WarlordsEntity warlordsEntityTarget;

    public NPCTowerDefenseDefenderGoal(TowerDefenseTowerMob towerMob, double range) {
        this.towerMob = towerMob;
        this.range = range;
        this.npc = towerMob.getNpc();
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
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        warlordsEntityTarget = getTarget();
        if (warlordsEntityTarget == null) {
            return false;
        }
        Entity target = warlordsEntityTarget.getEntity();
        npc.getNavigator().setTarget(target, true);
        return true;
    }

    private WarlordsEntity getTarget() {
        if (!npc.isSpawned()) {
            return null;
        }
        Entity npcEntity = npc.getEntity();
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(npcEntity);
        if (thisWarlordsEntity == null) {
            return null;
        }
        List<WarlordsNPC> list = getNearbyEnemies(thisWarlordsEntity);
        if (list.isEmpty()) {
            return null;
        }
        WarlordsNPC target = list.get(0);
        NPC targetNpc = target.getNpc();
        EntityTarget targetsTarget = targetNpc.getNavigator().getEntityTarget();
        if (targetsTarget == null) {
            targetNpc.getNavigator().setTarget(npcEntity, true);
        }
        return target;
    }

    @Nonnull
    public List<WarlordsNPC> getNearbyEnemies(WarlordsEntity thisWarlordsEntity) {
        if (!(towerMob.getPveOption() instanceof TowerDefenseOption towerDefenseOption)) {
            return Collections.emptyList();
        }
        Location eye;
        if (npc.getEntity() instanceof LivingEntity livingEntity) {
            eye = livingEntity.getEyeLocation();
        } else {
            eye = npc.getStoredLocation();
        }
        return PlayerFilterGeneric.entitiesAround(thisWarlordsEntity, range, range, range)
                                  .warlordsNPCs()
                                  .aliveEnemiesOf(thisWarlordsEntity)
                                  .filter(warlordsNPC -> {
                                      TowerDefenseOption.TowerDefenseMobData mobData = towerDefenseOption.getMobsMap().get(warlordsNPC.getMob());
                                      if (!(mobData instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData)) {
                                          return false;
                                      }
                                      return attackingMobData.getAttackingTeam() == towerMob.getSpawner().getTeam();
                                  })
                                  .filter(warlordsNPC ->
                                          warlordsNPC.getEntity() instanceof LivingEntity livingEntity &&
                                                  LocationUtils.getDotToLocation(livingEntity.getEyeLocation(), eye) > 0.5
                                  )
                                  .sorted((o1, o2) -> {
                                      // whoever has least targetedBy
                                      TowerDefenseOption.TowerDefenseMobData mobData1 = towerDefenseOption.getMobsMap().get(o1.getMob());
                                      TowerDefenseOption.TowerDefenseMobData mobData2 = towerDefenseOption.getMobsMap().get(o2.getMob());
                                      if (!(mobData1 instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData1) ||
                                              !(mobData2 instanceof TowerDefenseOption.TowerDefenseAttackingMobData attackingMobData2)) {
                                          return 0;
                                      }
                                      return Integer.compare(attackingMobData1.getTargetedBy().size(), attackingMobData2.getTargetedBy().size());
                                  })
                                  .stream()
                                  .collect(Collectors.toList());
    }


}
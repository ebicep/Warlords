package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.bosses.bossminions.NarmerAcolyte;
import net.citizensnpcs.api.ai.tree.BehaviorGoalAdapter;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;

import java.util.List;

public class NPCTargetNarmerAcolyteGoal extends BehaviorGoalAdapter {

    private final double range;
    private NPC npc;
    private WarlordsEntity warlordsEntityTarget;

    public NPCTargetNarmerAcolyteGoal(NPC npc, double range) {
        this.npc = npc;
        this.range = range;
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
        } else {
            return BehaviorStatus.RUNNING;
        }
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
        List<Entity> list = GoalUtils.getNearbyMatchingTeam(npcEntity, thisWarlordsEntity.getTeam(), range);
        list.removeIf(entity -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity);
            return warlordsEntity == null || !(warlordsEntity instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof NarmerAcolyte);
        });
        if (list.isEmpty()) {
            return false;
        }
        //sort lowest health first
        list.sort((o1, o2) -> {
            WarlordsEntity warlordsEntity1 = Warlords.getPlayer(o1);
            WarlordsEntity warlordsEntity2 = Warlords.getPlayer(o2);
            return Double.compare(warlordsEntity1 != null ? warlordsEntity1.getCurrentHealth() : Double.MAX_VALUE,
                    warlordsEntity2 != null ? warlordsEntity2.getCurrentHealth() : Double.MAX_VALUE
            );
        });
        this.warlordsEntityTarget = Warlords.getPlayer(list.get(0));
        npc.getNavigator().setTarget(warlordsEntityTarget.getEntity(), true);
        return true;
    }
}
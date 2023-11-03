package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.game.Team;
import net.citizensnpcs.api.ai.tree.Behavior;
import net.citizensnpcs.api.ai.tree.BehaviorStatus;
import net.citizensnpcs.api.npc.NPC;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

import java.util.List;

public class NPCFleeWarlordsEntityGoal implements Behavior {

    private final NPC npc;
    private final Team selfTeam;

    public NPCFleeWarlordsEntityGoal(NPC npc, Team selfTeam) {
        this.npc = npc;
        this.selfTeam = selfTeam;
    }

    @Override
    public void reset() {
        npc.getNavigator().cancelNavigation();
    }

    @Override
    public BehaviorStatus run() {
        if (!npc.getNavigator().isNavigating()) {
            return BehaviorStatus.SUCCESS;
        }
        return BehaviorStatus.RUNNING;
    }

    @Override
    public boolean shouldExecute() {
        Entity selfEntity = npc.getEntity();
        List<Entity> nearbyEnemies = GoalUtils.getNearbyWarlordEntities(selfEntity, selfTeam, 10);
        if (nearbyEnemies.isEmpty()) {
            return false;
        }
        Entity closestEnemy = nearbyEnemies.get(0);
        if (!(((CraftEntity) selfEntity).getHandle() instanceof PathfinderMob pathfinderMob)) {
            return true;
        }
        Vec3 vec3 = DefaultRandomPos.getPosAway(pathfinderMob, 16, 7, ((CraftEntity) closestEnemy).getHandle().position());
        if (vec3 == null) {
            return false;
        }
        npc.getNavigator().setTarget(new Location(selfEntity.getWorld(), vec3.x, vec3.y, vec3.z));
        return true;
    }

}

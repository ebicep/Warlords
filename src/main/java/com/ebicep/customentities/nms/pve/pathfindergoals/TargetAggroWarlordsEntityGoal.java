package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.RandomCollection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.EnumSet;
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
public class TargetAggroWarlordsEntityGoal extends TargetGoal {
    protected LivingEntity targetEntity;

    public TargetAggroWarlordsEntityGoal(Mob entitycreature) {
        this(entitycreature, false, true);
    }

    public TargetAggroWarlordsEntityGoal(Mob entitycreature, boolean checkSight, boolean onlyNearby) {
        super(entitycreature, checkSight, onlyNearby);
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(mob.getBukkitEntity());
        if (thisWarlordsEntity == null) {
            return false;
        }
        double followRange = this.getFollowDistance();
        List<LivingEntity> list = GoalUtils.getNearbyMatchingTeam(this.mob, thisWarlordsEntity, followRange); // getEntitiesWithinAABB
        list.sort((o1, o2) -> Double.compare(o1.distanceToSqr(this.mob), o2.distanceToSqr(this.mob)));
        if (list.isEmpty()) {
            return false;
        }
        LivingEntity closestEntity = list.get(0);
        double distanceToClosest = this.mob.distanceToSqr(closestEntity); // getDistanceSqToEntity
        RandomCollection<LivingEntity> randomCollection = new RandomCollection<>();
        for (LivingEntity entity : list) {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity.getBukkitEntity());
            if (warlordsEntity != null) {
                randomCollection.add(entity == closestEntity ?
                                     1000 + warlordsEntity.getBonusAggroWeight() :
                                     1000 + warlordsEntity.getBonusAggroWeight() - 10 * (this.mob.distanceToSqr(entity) - distanceToClosest),
                        entity
                );
            }
        }
        if (randomCollection.getSize() == 0) {
            return false;
        }
        this.targetEntity = randomCollection.next();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getTarget() != null && mob.getTarget().valid;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.targetEntity, EntityTargetEvent.TargetReason.CUSTOM, true);
        super.start();
    }


}
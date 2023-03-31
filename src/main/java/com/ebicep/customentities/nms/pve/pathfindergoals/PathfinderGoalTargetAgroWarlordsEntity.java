package com.ebicep.customentities.nms.pve.pathfindergoals;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.java.RandomCollection;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PathfinderGoalTarget;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Comparator;
import java.util.List;

/**
 * Agro system
 * <p>
 * All warlords players have an extra agro weight
 * <p>
 * Closest player weight = 100 + agro bonus
 * <p>
 * Block distance to the closest player is x
 * <p>
 * All other players have 100 + agro bonus - 10(distance - x). So every block away from distance to closest player is 10 agro less
 * <p>
 * ex. (w=100,d=20), (w=50,d=25), (w=0,d>=30)
 * Random target is chosen from weighted list
 */
public class PathfinderGoalTargetAgroWarlordsEntity extends PathfinderGoalTarget {
    public static final int EXTRA_RANGE_CHECK = 5;
    protected final DistanceComparator nearestTargetSelector;
    protected EntityLiving targetEntity;

    public PathfinderGoalTargetAgroWarlordsEntity(EntityCreature entitycreature) {
        this(entitycreature, false, true);
    }

    public PathfinderGoalTargetAgroWarlordsEntity(EntityCreature entitycreature, boolean checkSight, boolean onlyNearby) {
        super(entitycreature, checkSight, onlyNearby);
        this.nearestTargetSelector = new DistanceComparator(entitycreature);
        this.a(1);
    }

    public boolean a() {
        WarlordsEntity thisWarlordsEntity = Warlords.getPlayer(e.getBukkitEntity());
        if (thisWarlordsEntity == null) {
            return false;
        }
        double followRange = this.f(); // GenericAttributes.FOLLOW_RANGE - default = 16
        List<EntityLiving> list = this.e.world.a(EntityLiving.class, this.e.getBoundingBox().grow(followRange, 4.0, followRange)); // getEntitiesWithinAABB
        list.removeIf(entity -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity.getBukkitEntity());
            return warlordsEntity == null || warlordsEntity.isTeammate(thisWarlordsEntity);
        });
        list.sort(this.nearestTargetSelector);
        if (list.isEmpty()) {
            return false;
        }
        EntityLiving closestEntity = list.get(0);
        double distanceToClosest = this.e.h(closestEntity); // getDistanceSqToEntity
        RandomCollection<EntityLiving> randomCollection = new RandomCollection<>();
        for (EntityLiving entity : list) {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(entity.getBukkitEntity());
            if (warlordsEntity != null) {
                randomCollection.add(entity == closestEntity ?
                                     1000 + warlordsEntity.getBonusAgroWeight() :
                                     1000 + warlordsEntity.getBonusAgroWeight() - 10 * (e.h(entity) - distanceToClosest),
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

    public void c() {
        this.e.setGoalTarget(this.targetEntity, EntityTargetEvent.TargetReason.CUSTOM, true);
        super.c();
    }

    public static class DistanceComparator implements Comparator<Entity> {
        private final Entity a;

        public DistanceComparator(Entity entity) {
            this.a = entity;
        }

        public int compare(Entity object, Entity object1) {
            return this.a(object, object1);
        }

        public int a(Entity entity, Entity entity1) {
            double d0 = this.a.h(entity);
            double d1 = this.a.h(entity1);
            return Double.compare(d0, d1);
        }
    }
}
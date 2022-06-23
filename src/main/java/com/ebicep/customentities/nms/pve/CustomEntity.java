package com.ebicep.customentities.nms.pve;

import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.Location;

import java.util.List;

import static com.ebicep.warlords.util.java.ReflectionUtils.getPrivateField;

public interface CustomEntity {

    static void clearPathfinderGoals(EntityInsentient entity) {
        //this.goalSelector = new PathfinderGoalSelector(world.methodProfiler);
        List<?> goalB = (List<?>) getPrivateField("b", PathfinderGoalSelector.class, entity.goalSelector);
        goalB.clear();
        List<?> goalC = (List<?>) getPrivateField("c", PathfinderGoalSelector.class, entity.goalSelector);
        goalC.clear();
        List<?> targetB = (List<?>) getPrivateField("b", PathfinderGoalSelector.class, entity.targetSelector);
        targetB.clear();
        List<?> targetC = (List<?>) getPrivateField("c", PathfinderGoalSelector.class, entity.targetSelector);
        targetC.clear();
    }

    void spawn(Location location);

    EntityInsentient get();


}

package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;

public class CustomZombie extends EntityZombie implements CustomEntity<CustomZombie> {

    public CustomZombie(World world) {
        super(world);
        setBaby(false);

        //https://github.com/ZeroedInOnTech/1.8.8/blob/master/1.8.8/Build%20918/src/minecraft/net/minecraft/entity/monster/EntityZombie.java
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, EntityHuman.class, 1.0, true));
        //this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0)); ??
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 80.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public void onDeath(CustomZombie entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {
        //test
        /*WarlordsEntity we = Warlords.getPlayer(this.getBukkitEntity());
        if (we != null) {
            for (WarlordsEntity enemy : PlayerFilter
                    .entitiesAround(we, 20, 20, 20)
                    .aliveEnemiesOf(we)
            ) {
                enemy.addDamageInstance(we, "Test", 500, 500, -1, 100, false);
            }
        }*/
    }

    @Override
    public CustomZombie get() {
        return this;
    }

}

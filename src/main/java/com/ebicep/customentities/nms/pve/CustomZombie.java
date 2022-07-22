package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;

public class CustomZombie extends EntityZombie implements CustomEntity<CustomZombie> {

    //https://github.com/ZeroedInOnTech/1.8.8/blob/master/1.8.8/Build%20918/src/minecraft/net/minecraft/entity/monster/EntityZombie.java
    public CustomZombie(World world) {
        super(world);
        setBaby(false);

        resetAI(world);
        giveBaseAI();
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

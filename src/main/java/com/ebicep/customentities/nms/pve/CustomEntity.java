package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;

public interface CustomEntity<T extends EntityInsentient> {

    void spawn(Location location);

    default void onDeath(T entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {

    }

    T get();


}

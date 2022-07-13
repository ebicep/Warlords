package com.ebicep.customentities.nms.pve;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

public interface CustomEntity<T extends EntityInsentient> {

    default void spawn(Location location) {
        T customEntity = get();
        customEntity.setPosition(location.getX(), location.getY(), location.getZ());
        customEntity.setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(customEntity);
    }

    default void onDeath(T entity, Location deathLocation, WaveDefenseOption waveDefenseOption) {

    }

    T get();


}

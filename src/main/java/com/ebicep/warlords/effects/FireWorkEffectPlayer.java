package com.ebicep.warlords.effects;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

@Deprecated
public class FireWorkEffectPlayer {

    @Deprecated
    public static void playFirework(Location loc, FireworkEffect fe) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(fe); // add the effect to the firework
        fireworkMeta.setPower(1); // set the power of the firework
        firework.setFireworkMeta(fireworkMeta); // set the firework meta data
        firework.detonate();
    }
}

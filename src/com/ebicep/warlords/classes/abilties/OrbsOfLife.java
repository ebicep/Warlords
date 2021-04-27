package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class OrbsOfLife extends AbstractAbility {

    public OrbsOfLife() {
        super("Orbs of Life", 302, 504, 20, 20, 0, 0, "orbs of life description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation();
        Orb orb = new Orb(((CraftWorld) player.getWorld()).getHandle(), location);
        ArmorStand test = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        test.setVisible(false);
        //WOW need to set passenger to orb or else the orb will move   like ???
        test.setPassenger(orb.spawn(location).getBukkitEntity());
    }
}

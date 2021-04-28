package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
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
        Warlords.getPlayer(player).setOrbOfLife(13);
    }
}

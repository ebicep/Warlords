package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TotemThunderlord extends AbstractAbility {

    public TotemThunderlord() {
        super("Capacitor Totem", -404, -503, 60 + 2, 20, 20, 200, "capacitor totem description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);

        Location standLocation = player.getLocation();
        standLocation.setYaw(0);
        standLocation.add(0, -1, 0);
        ArmorStand totemStand = e.getPlayer().getWorld().spawn(standLocation, ArmorStand.class);
        totemStand.setVisible(false);
        totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 4));

        Totem capacitorTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 8);
        Warlords.totems.add(capacitorTotem);
    }
}

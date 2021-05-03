package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TotemEarthwarden extends AbstractAbility {

    public TotemEarthwarden() {
        super("Healing Totem", 168, 841, 60 + 12, 60, 15, 200, "healing totem description");
        //168 - 227
        //841 - 1138
        //1.35x
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
        totemStand.setHelmet(new ItemStack(Material.RED_ROSE, 1, (short) 7));

        Totem healingTotem = new Totem(((CraftWorld) player.getWorld()).getHandle(), warlordsPlayer, totemStand, 5);
        Warlords.totems.add(healingTotem);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
        }
    }

}

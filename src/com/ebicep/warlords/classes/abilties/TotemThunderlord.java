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

public class TotemThunderlord extends AbstractAbility {

    public TotemThunderlord() {
        super("Capacitor Totem", -404, -523, 60 + 2, 20, 20, 200,
                "§7Place a highly conductive totem\n" +
                        "§7on the ground. Casting Chain Lightning\n" +
                        "§7or Lightning Rod on the totem will cause\n" +
                        "§7it to pulse, dealing §c404 §7- §c523 §7damage\n" +
                        "§7to all enemies nearby. Lasts §68 §7seconds.");
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

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "shaman.totem.activation", 1, 1);
        }
    }
}

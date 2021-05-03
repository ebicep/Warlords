package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.AbstractAbility;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class FallenSouls extends AbstractAbility {

    public FallenSouls() {
        super("Fallen Souls", -197, -254, 0, 55, 20, 180, "fallen souls description");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Location location = player.getLocation().add(player.getLocation().getDirection().multiply(-1));
        float yaw = -15;
        float locationYaw = location.getYaw();

        for (int i = 0; i < 3; i++) {
            location.setYaw(locationYaw + yaw);
            ArmorStand fallenSoul = player.getWorld().spawn(location.subtract(0, .5, 0), ArmorStand.class);
            location.add(0, .5, 0);
            Warlords.getFallenSouls().add(new FallenSoul(Warlords.getPlayer(player), fallenSoul, player.getLocation(), location.getDirection(), this));
            yaw += 15;
        }

        for(Player player1: Bukkit.getOnlinePlayers()){
            player1.playSound(player.getLocation(), "shaman.lightningbolt.impact", 1, 1.5f);
        }
    }
}

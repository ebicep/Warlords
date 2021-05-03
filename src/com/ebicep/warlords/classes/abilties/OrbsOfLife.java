package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class OrbsOfLife extends AbstractAbility {

    public OrbsOfLife() {
        super("Orbs of Life", 302, 504, 20, 20, 0, 0,
                "§7Striking and hitting enemies with\n" +
                "§7abilities causes them to drop an orb of\n" +
                "§7life that lasts §68 §7seconds, restoring\n" +
                "§a%dynamic.value% §7health to the ally that pick it up.\n" +
                "§7Other nearby allies recover §a%dynamic.value% §7health.\n" +
                "§7Lasts §613.2 §7seconds.");
    }

    @Override
    public void onActivate(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Warlords.getPlayer(player).setOrbOfLife(13);

        for (Player player1 : Bukkit.getOnlinePlayers()) {
            player1.playSound(player.getLocation(), "warrior.revenant.orbsoflife", 1, 1);
        }
    }
}

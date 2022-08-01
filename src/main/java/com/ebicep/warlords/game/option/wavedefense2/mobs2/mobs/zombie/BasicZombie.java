package com.ebicep.warlords.game.option.wavedefense2.mobs2.mobs.zombie;

import com.ebicep.warlords.game.option.wavedefense2.WaveDefenseOption2;
import com.ebicep.warlords.game.option.wavedefense2.mobs2.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicZombie extends AbstractZombie implements BasicMob {

    public BasicZombie(Location spawnLocation) {
        super(spawnLocation,
                "Lunar Lancer",
                new Utils.SimpleEntityEquipment(
                        new ItemStack(Material.CARPET, 1, (short) 14),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOOD_AXE)
                ),
                2800,
                0.38f,
                0,
                200,
                300);
    }


    @Override
    public void onSpawn() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "BasicZombie onSpawn");
    }

    @Override
    public void whileAlive() {
        Bukkit.broadcastMessage(ChatColor.GRAY + "BasicZombie whileAlive");
    }

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption2 waveDefenseOption) {
        Bukkit.broadcastMessage(ChatColor.RED + "BasicZombie onDeath");
        dropItem();
    }
}

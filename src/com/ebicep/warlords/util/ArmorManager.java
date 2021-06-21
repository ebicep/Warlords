package com.ebicep.warlords.util;

import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.maps.Team;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorManager {

    public static void resetArmor(WarlordsPlayer wp, Player p) {
        Team team = wp.getTeam();
        PlayerClass playerClass = wp.getSpec();
        ItemStack[] armor = new ItemStack[4];

        if (team == Team.BLUE) {
            armor[0] = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
            meta0.setColor(Color.fromRGB(51, 76, 178));
            armor[0].setItemMeta(meta0);
            armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
            meta1.setColor(Color.fromRGB(51, 76, 178));
            armor[1].setItemMeta(meta1);
            armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
            meta2.setColor(Color.fromRGB(51, 76, 178));
            armor[2].setItemMeta(meta2);

            if (playerClass instanceof AbstractPaladin) {
                armor[3] = new ItemStack(Material.RED_ROSE, 1, (short) 6);
            } else if (playerClass instanceof AbstractWarrior) {
                armor[3] = new ItemStack(Material.WOOD_PLATE);
            } else if (playerClass instanceof AbstractMage) {
                armor[3] = new ItemStack(Material.SAPLING, 1, (short) 5);
            } else {
                armor[3] = new ItemStack(Material.SAPLING, 1, (short) 1);
            }

        } else {
            armor[0] = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
            meta0.setColor(Color.fromRGB(153, 51, 51));
            armor[0].setItemMeta(meta0);
            armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
            meta1.setColor(Color.fromRGB(153, 51, 51));
            armor[1].setItemMeta(meta1);
            armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
            meta2.setColor(Color.fromRGB(153, 51, 51));
            armor[2].setItemMeta(meta2);

            if (playerClass instanceof AbstractPaladin) {
                armor[3] = new ItemStack(Material.DEAD_BUSH);
            } else if (playerClass instanceof AbstractWarrior) {
                armor[3] = new ItemStack(Material.STONE_PLATE);
            } else if (playerClass instanceof AbstractMage) {
                armor[3] = new ItemStack(Material.RED_ROSE, 1, (short) 5);
            } else {
                armor[3] = new ItemStack(Material.SAPLING, 1, (short) 0);
            }
        }
        p.getInventory().setArmorContents(armor);
    }
}

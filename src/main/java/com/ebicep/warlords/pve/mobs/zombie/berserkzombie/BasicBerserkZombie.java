package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicBerserkZombie extends AbstractBerserkZombie {

    public BasicBerserkZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Berserker",
                MobTier.BASE,
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOOD_SWORD)
                ),
                2800,
                0.38f,
                0,
                200,
                300
        );
    }

}

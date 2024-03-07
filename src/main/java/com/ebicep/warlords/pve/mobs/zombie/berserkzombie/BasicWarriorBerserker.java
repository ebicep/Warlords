package com.ebicep.warlords.pve.mobs.zombie.berserkzombie;

import com.ebicep.warlords.player.general.ArmorManager;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.BasicMob;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BasicWarriorBerserker extends AbstractBerserkZombie implements BasicMob {

    public BasicWarriorBerserker(Location spawnLocation) {
        super(
                spawnLocation,
                "Warrior Berserker",
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOODEN_SWORD)
                ),
                2800,
                0.38f,
                0,
                200,
                300,
                new BerserkerZombieWoundingStrike(497, 632)
        );
    }

    public BasicWarriorBerserker(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                new Utils.SimpleEntityEquipment(
                        ArmorManager.Helmets.SIMPLE_WARRIOR_HELMET.itemRed,
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                        new ItemStack(Material.WOODEN_SWORD)
                ),
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new BerserkerZombieWoundingStrike(497, 632)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.BASIC_WARRIOR_BERSERKER;
    }
}

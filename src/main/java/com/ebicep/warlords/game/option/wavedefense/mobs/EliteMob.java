package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class EliteMob {

    public static final Function<Location, PartialMonster> ELITE_ZOMBIE = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            "Elite Zombie",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.DEAD_BUSH),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_CRYSTALS)
            ),
            4000,
            0.2f
    );
}

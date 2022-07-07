package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomEnderman;
import com.ebicep.customentities.nms.pve.CustomMagmaCube;
import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BossMob {

    public static final Function<Location, PartialMonster> ZOMBOID = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()), (mob) -> {
            }, "Zomboid",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET, 1, (short) 7),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.STICK)
            ),
            10000,
            0.45f
    );

    public static final Function<Location, PartialMonster> SIN = loc -> PartialMonster.fromCustomEntity(
            CustomMagmaCube.class,
            () -> new CustomMagmaCube(((CraftWorld) loc.getWorld()).getHandle()), (mob) -> {
            },
            "Sin",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET, 1, (short) 7),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.STICK)
            ),
            25000,
            0.6f
    );

    public static final Function<Location, PartialMonster> XATAH = loc -> PartialMonster.fromCustomEntity(
            CustomEnderman.class,
            () -> new CustomEnderman(((CraftWorld) loc.getWorld()).getHandle()), (mob) -> {
            },
            "Xatah",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET, 1, (short) 7),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.STICK)
            ),
            100000,
            0.6f
    );
}

package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomMagmaCube;
import com.ebicep.customentities.nms.pve.CustomSkeleton;
import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class EliteMob {

    public static final Function<Location, PartialMonster> ELITE_ZOMBIE = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()), (mob) -> {
            }, "Illusion Swordsman",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            4000,
            0.35f,
            0,
            300,
            600
    );
    public static final Function<Location, PartialMonster> ELITE_SKELETON = loc -> PartialMonster.fromCustomEntity(
            CustomSkeleton.class,
            () -> new CustomSkeleton(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Illusion Warlock",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET, 1, (short) 1),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.BOW)
            ),
            1200,
            0.3f,
            0,
            0,
            0
    );
    public static final Function<Location, PartialMonster> MAGMA_CUBE = loc -> PartialMonster.fromCustomEntity(
            CustomMagmaCube.class,
            () -> new CustomMagmaCube(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Illusion Illumination",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            3000,
            0.35f,
            20,
            100,
            200
    );
    public static final Function<Location, PartialMonster> ILLUSION_APPRENTICE = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Illusion Apprentice",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.DEAD_BUSH),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.SPIDER_EYE)
            ),
            7000,
            0.36f,
            0,
            400,
            800
    );
}

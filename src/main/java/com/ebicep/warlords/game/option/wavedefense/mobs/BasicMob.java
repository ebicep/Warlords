package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.*;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BasicMob {

    public static final Function<Location, PartialMonster> ZOMBIE = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Lancer",
            loc,
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
            300
    );
    public static final Function<Location, PartialMonster> SKELETON = loc -> PartialMonster.fromCustomEntity(
            CustomSkeleton.class,
            () -> new CustomSkeleton(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Mage",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("4oot"),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                    Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                    new ItemStack(Material.BOW)
            ),
            1600,
            0.25f,
            0,
            0,
            0
    );
    public static final Function<Location, PartialMonster> PIGZOMBIE = loc -> PartialMonster.fromCustomEntity(
            CustomPigZombie.class,
            () -> new CustomPigZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Disciple",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.WOOD, 1, (short) 3),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                    Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 190),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 190),
                    new ItemStack(Material.COOKIE)
            ),
            2800,
            0.42f,
            0,
            250,
            350
    );
    public static final Function<Location, PartialMonster> WOLF = loc -> PartialMonster.fromCustomEntity(
            CustomWolf.class,
            () -> new CustomWolf(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Hound",
            loc,
            null,
            900,
            0.5f,
            0,
            600,
            800
    );
    public static final Function<Location, PartialMonster> SPIDER = loc -> PartialMonster.fromCustomEntity(
            CustomSpider.class,
            () -> new CustomSpider(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Venari",
            loc,
            null,
            2200,
            0.45f,
            0,
            300,
            450
    );
    public static final Function<Location, PartialMonster> SLIME = loc -> PartialMonster.fromCustomEntity(
            CustomSlime.class,
            () -> new CustomSlime(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar Anomaly",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            3000,
            0.5f,
            20,
            50,
            100
    );
}

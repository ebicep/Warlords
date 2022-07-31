package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.CustomZombie;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BossMob {

    public static final Function<Location, PartialMonster> NARMER = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Narmer",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getMobSkull(SkullType.WITHER),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.STICK)
            ),
            10000,
            0.5f,
            0,
            400,
            600
    );
    public static final Function<Location, PartialMonster> BOLTARO = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Boltaro",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("ChybaWonsz"),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_LEGGINGS),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.COOKED_FISH)
            ),
            15000,
            0.6f,
            5,
            200,
            300
    );
    public static final Function<Location, PartialMonster> MITHRA = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Mithra",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("ChybaWonsz"),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.IRON_AXE)
            ),
            17000,
            0.45f,
            25,
            400,
            600
    );
    public static final Function<Location, PartialMonster> PHYSIRA = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Physira",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("ChybaWonsz"),
                    new ItemStack(Material.CHAINMAIL_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.CHAINMAIL_BOOTS),
                    new ItemStack(Material.BLAZE_POWDER)
            ),
            17000,
            0.45f,
            0,
            600,
            800
    );
    public static final Function<Location, PartialMonster> ZENITH = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Zenith",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("4oot"),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                    Utils.applyColorTo(Material.LEATHER_LEGGINGS, 250, 104, 255),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
                    new ItemStack(Material.DIAMOND_SPADE)
            ),
            20000,
            0.5f,
            10,
            800,
            1000
    );
    public static final Function<Location, PartialMonster> JUDAS = loc -> PartialMonster.fromCustomEntity(
            CustomZombie.class,
            () -> new CustomZombie(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Judas",
            loc,
            new Utils.SimpleEntityEquipment(
                    Utils.getPlayerSkull("4oot"),
                    Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 255, 104, 255),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    Utils.applyColorTo(Material.LEATHER_BOOTS, 250, 104, 255),
                    new ItemStack(Material.DIAMOND_SPADE)
            ),
            50000,
            0.5f,
            20,
            1500,
            2000
    );
}

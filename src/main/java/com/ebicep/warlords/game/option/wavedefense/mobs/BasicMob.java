package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.*;
import com.ebicep.warlords.effects.ParticleEffect;
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
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS),
                    new ItemStack(Material.LEATHER_BOOTS),
                    new ItemStack(Material.WOOD_AXE)
            ),
            2000,
            0.35f,
            0,
            180,
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
                    new ItemStack(Material.CARPET, 1, (short) 3),
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS),
                    new ItemStack(Material.LEATHER_BOOTS),
                    new ItemStack(Material.BOW)
            ),
            1000,
            0.2f,
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
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.LEATHER_CHESTPLATE),
                    new ItemStack(Material.LEATHER_LEGGINGS),
                    new ItemStack(Material.LEATHER_BOOTS),
                    new ItemStack(Material.COOKIE)
            ),
            2500,
            0.4f,
            0,
            200,
            300
    );
    public static final Function<Location, PartialMonster> SPIDER = loc -> PartialMonster.fromCustomEntity(
            CustomSpider.class,
            () -> new CustomSpider(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
                ParticleEffect.CLOUD.display(0, 0, 0, 0.1f, 30, mob.getBukkitEntity().getLocation(), 500);
            },
            "Lunar Venari",
            loc,
            null,
            1600,
            0.4f,
            0,
            250,
            400
    );
    public static final Function<Location, PartialMonster> CREEPER = loc -> PartialMonster.fromCustomEntity(
            CustomCreeper.class,
            () -> new CustomCreeper(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Creeper",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> SLIME = loc -> PartialMonster.fromCustomEntity(
            CustomSlime.class,
            () -> new CustomSlime(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Lunar ",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            2500,
            0.5f,
            20,
            50,
            100
    );
    public static final Function<Location, PartialMonster> GUARDIAN = loc -> PartialMonster.fromCustomEntity(
            CustomGuardian.class,
            () -> new CustomGuardian(((CraftWorld) loc.getWorld()).getHandle()),
            (mob) -> {
            },
            "Guardian",
            loc,
            null,
            1500,
            0.3f,
            0,
            200,
            300
    );
}

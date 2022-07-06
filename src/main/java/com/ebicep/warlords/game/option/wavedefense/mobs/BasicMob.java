package com.ebicep.warlords.game.option.wavedefense.mobs;

import com.ebicep.customentities.nms.pve.*;
import com.ebicep.warlords.game.option.wavedefense.PartialMonster;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Guardian;
import org.bukkit.inventory.ItemStack;

import java.util.function.Function;

public class BasicMob {

    public static final Function<Location, PartialMonster> ZOMBIE = loc -> PartialMonster.fromCustomEntity(
    CustomZombie.class,
            "Zombie",
            loc,
            new Utils.SimpleEntityEquipment(
            new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            2000,
            0.3f
    );
    public static final Function<Location, PartialMonster> SKELETON = loc -> PartialMonster.fromCustomEntity(
            CustomSkeleton.class,
            "Skeleton",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET, 1, (short) 3),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.BOW)
            ),
            1000,
            0.2f
    );
    public static final Function<Location, PartialMonster> PIGZOMBIE = loc -> PartialMonster.fromCustomEntity(
            CustomPigZombie.class,
            "Pig Zombie",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.GOLD_CHESTPLATE),
                    new ItemStack(Material.GOLD_CHESTPLATE),
                    new ItemStack(Material.GOLD_CHESTPLATE),
                    new ItemStack(Material.COOKIE)
            ),
            2500,
            0.4f
    );
    public static final Function<Location, PartialMonster> SPIDER = loc -> PartialMonster.fromCustomEntity(
            CustomSpider.class,
            "Spider",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            1500,
            0.2f
    );
    public static final Function<Location, PartialMonster> BLAZE = loc -> PartialMonster.fromEntity(
            Blaze.class,
            "BLAZE",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> CREEPER = loc -> PartialMonster.fromCustomEntity(
            CustomCreeper.class,
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
            "Chessking",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            3000,
            0.5f
    );
    public static final Function<Location, PartialMonster> MAGMA_CUBE = loc -> PartialMonster.fromCustomEntity(
            CustomMagmaCube.class,
            "Magma Cube",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            ),
            3000,
            0.1f
    );
    public static final Function<Location, PartialMonster> GHAST = loc -> PartialMonster.fromEntity(
            Ghast.class,
            "Ghast",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> ENDERMAN = loc -> PartialMonster.fromEntity(
            Enderman.class,
            "Enderman",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> GUARDIAN = loc -> PartialMonster.fromEntity(
            Guardian.class,
            "Guardian",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
}

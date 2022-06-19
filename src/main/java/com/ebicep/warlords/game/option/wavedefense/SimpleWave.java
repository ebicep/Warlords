package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.player.WarlordsNPC;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import static org.bukkit.entity.EntityType.BLAZE;
import org.bukkit.inventory.ItemStack;

public class SimpleWave implements Wave {

    double totalWeight;
    List<Pair<Double, Function<Location, PartialMonster>>> entries;
    final int count;
    final String message;
    public static final Function<Location, PartialMonster> ZOMBIE = loc -> PartialMonster.fromEntity(
            Zombie.class,
            "Zombie",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> SKELETON = loc -> PartialMonster.fromEntity(
            Skeleton.class,
            "Skeleton",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> PIGZOMBIE = loc -> PartialMonster.fromEntity(
            PigZombie.class,
            "PigZombie",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
    );
    public static final Function<Location, PartialMonster> SPIDER = loc -> PartialMonster.fromEntity(
            Spider.class,
            "Spider",
            loc,
            new Utils.SimpleEntityEquipment(
                    new ItemStack(Material.CARPET),
                    new ItemStack(Material.DIAMOND_CHESTPLATE),
                    new ItemStack(Material.DIAMOND_LEGGINGS),
                    new ItemStack(Material.DIAMOND_BOOTS),
                    new ItemStack(Material.PRISMARINE_SHARD)
            )
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

    public SimpleWave(int count, @Nullable String message) {
        this.count = count;
        this.message = message;
    }

    public SimpleWave add(Function<Location, PartialMonster> factory) {
        return add(entries.isEmpty() ? 0 : totalWeight / entries.size(), factory);
    }

    public SimpleWave add(double baseWeight, Function<Location, PartialMonster> factory) {
        totalWeight += baseWeight;
        entries.add(new Pair<>(baseWeight, factory));
        return this;
    }

    @Override
    public PartialMonster spawnRandomMonster(Location loc, Random random) {
        double index = random.nextDouble() * totalWeight;
        for (Pair<Double, Function<Location, PartialMonster>> entry : entries) {
            if (index < entry.getA()) {
                return entry.getB().apply(loc);
            }
            index -= entry.getA();
        }
        return PartialMonster.fromEntity("No monsters in wave!", WarlordsNPC.spawnZombieNoAI(loc, null));
    }

    @Override
    public int getMonsterCount() {
        return count;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

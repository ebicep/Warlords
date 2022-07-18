package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public enum RewardTypes {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            new ItemStack(Material.BLAZE_POWDER),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addSyntheticShards(amount.intValue())
    ),
    FAIRY_ESSENCE(
            "Fairy Essence",
            new ItemStack(Material.INK_SACK, 1, (short) 13),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addFairyEssence(amount.intValue())
    ),
    COMMON_STAR_PIECE(
            "Common Star Piece",
            new ItemStack(Material.NETHER_STAR),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addCommonStarPiece()
    ),
    RARE_STAR_PIECE(
            "Rare Star Piece",
            new ItemStack(Material.NETHER_STAR),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addRareStarPiece()
    ),
    EPIC_STAR_PIECE(
            "Epic Star Piece",
            new ItemStack(Material.NETHER_STAR),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addEpicStarPiece()
    ),
    LEGENDARY_STAR_PIECE(
            "Legendary Star Piece",
            new ItemStack(Material.NETHER_STAR),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addLegendaryStarPiece()
    ),
    SUPPLY_DROP_TOKEN(
            "Supply Drop Token",
            new ItemStack(Material.FIREWORK_CHARGE),
            (databasePlayer, amount) -> databasePlayer.getPveStats().addSupplyDropToken(amount.intValue())
    ),


    ;

    public final String name;
    public final ItemStack item;
    public final BiConsumer<DatabasePlayer, Float> biConsumer;


    RewardTypes(String name, ItemStack item, BiConsumer<DatabasePlayer, Float> biConsumer) {
        this.name = name;
        this.item = item;
        this.biConsumer = biConsumer;
    }
}

package com.ebicep.warlords.pve.events.supplydrop;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.RandomCollection;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public enum SupplyDropRewards {

    SYNTHETIC_SHARDS_3("3 Synthetic Shards", databasePlayerPvE -> databasePlayerPvE.addSyntheticShards(3), 150),
    SYNTHETIC_SHARDS_5("5 Synthetic Shards", databasePlayerPvE -> databasePlayerPvE.addSyntheticShards(5), 200),
    SYNTHETIC_SHARDS_10("10 Synthetic Shards", databasePlayerPvE -> databasePlayerPvE.addSyntheticShards(10), 100),
    SYNTHETIC_SHARDS_20("20 Synthetic Shards", databasePlayerPvE -> databasePlayerPvE.addSyntheticShards(20), 50),
    SYNTHETIC_SHARDS_50("50 Synthetic Shards", databasePlayerPvE -> databasePlayerPvE.addSyntheticShards(50), 20),
    COMMON_STAR_PIECE("Common Star Piece", DatabasePlayerPvE::addCommonStarPiece, 10),
    RARE_STAR_PIECE("Rare Star Piece", DatabasePlayerPvE::addRareStarPiece, 3),
    EPIC_STAR_PIECE("Epic Star Piece", DatabasePlayerPvE::addEpicStarPiece, 1),
    SKILL_BOOST_MODIFIER("Skill Boost Modifier", DatabasePlayerPvE::addSkillBoostModifier, 1),
    COINS_1000("1,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(1000), 100),
    COINS_2000("2,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(2000), 150),
    COINS_5000("5,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(5000), 100),
    COINS_10000("10,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(10000), 50),
    COINS_50000("50,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(50000), 20),
    COINS_100000("100,000 Coins", databasePlayerPvE -> databasePlayerPvE.addCoins(100000), 10),
    FAIRY_ESSENCE_20("20 Fairy Essence", databasePlayerPvE -> databasePlayerPvE.addFairyEssence(20), 50),
    FAIRY_ESSENCE_40("40 Fairy Essence", databasePlayerPvE -> databasePlayerPvE.addFairyEssence(40), 20),

    ;

    public static final RandomCollection<SupplyDropRewards> RANDOM_COLLECTION = new RandomCollection<>();

    static {
        for (SupplyDropRewards supplyDropRewards : values()) {
            RANDOM_COLLECTION.add(supplyDropRewards.dropChance, supplyDropRewards);
        }
    }

    public final String name;
    public final Consumer<DatabasePlayerPvE> giveReward;
    public final int dropChance;

    SupplyDropRewards(String name, Consumer<DatabasePlayerPvE> giveReward, int dropChance) {
        this.name = name;
        this.giveReward = giveReward;
        this.dropChance = dropChance;
    }

    public static SupplyDropRewards getRandomReward() {
        return RANDOM_COLLECTION.next();
    }

    public String getType() {
        return dropChance <= 10 ? "RARE" : "COMMON";
    }

    public ChatColor getChatColor() {
        return dropChance <= 10 ? ChatColor.BLUE : ChatColor.GREEN;
    }

    public void givePlayerRewardTitle(Player player) {
        PacketUtils.sendTitle(
                player.getUniqueId(),
                getChatColor() + getType() + "!",
                ChatColor.GOLD + name,
                0, 40, 0
        );
    }
}

package com.ebicep.warlords.pve.events.supplydrop;

import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.java.RandomCollection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum SupplyDropRewards {

    SYNTHETIC_SHARDS_3("3 Synthetic Shards", 1500, WeaponsPvE.COMMON, Currencies.SYNTHETIC_SHARD, 3),
    SYNTHETIC_SHARDS_5("5 Synthetic Shards", 2000, WeaponsPvE.COMMON, Currencies.SYNTHETIC_SHARD, 5),
    SYNTHETIC_SHARDS_10("10 Synthetic Shards", 1000, WeaponsPvE.COMMON, Currencies.SYNTHETIC_SHARD, 10),
    SYNTHETIC_SHARDS_20("20 Synthetic Shards", 500, WeaponsPvE.RARE, Currencies.SYNTHETIC_SHARD, 20),
    SYNTHETIC_SHARDS_50("50 Synthetic Shards", 200, WeaponsPvE.EPIC, Currencies.SYNTHETIC_SHARD, 50),
    COMMON_STAR_PIECE("Common Star Piece", 100, WeaponsPvE.COMMON, Currencies.COMMON_STAR_PIECE, 1) {
        @Override
        public Component getDropMessage() {
            return getStarPieceDropMessage();
        }
    },
    RARE_STAR_PIECE("Rare Star Piece", 10, WeaponsPvE.RARE, Currencies.RARE_STAR_PIECE, 1) {
        @Override
        public Component getDropMessage() {
            return getStarPieceDropMessage();
        }
    },
    EPIC_STAR_PIECE("Epic Star Piece", 1, WeaponsPvE.EPIC, Currencies.EPIC_STAR_PIECE, 1) {
        @Override
        public Component getDropMessage() {
            return getStarPieceDropMessage();
        }
    },
    SKILL_BOOST_MODIFIER("Skill Boost Modifier", 10, WeaponsPvE.EPIC, Currencies.SKILL_BOOST_MODIFIER, 1),
    COINS_1000("1,000 Coins", 1000, WeaponsPvE.COMMON, Currencies.COIN, 1000),
    COINS_2000("2,000 Coins", 1500, WeaponsPvE.COMMON, Currencies.COIN, 2000),
    COINS_5000("5,000 Coins", 1000, WeaponsPvE.COMMON, Currencies.COIN, 5000),
    COINS_10000("10,000 Coins", 500, WeaponsPvE.COMMON, Currencies.COIN, 10000),
    COINS_50000("50,000 Coins", 200, WeaponsPvE.RARE, Currencies.COIN, 50000),
    COINS_100000("100,000 Coins", 100, WeaponsPvE.EPIC, Currencies.COIN, 100000),
    FAIRY_ESSENCE_20("20 Fairy Essence", 500, WeaponsPvE.RARE, Currencies.FAIRY_ESSENCE, 20),
    FAIRY_ESSENCE_40("40 Fairy Essence", 200, WeaponsPvE.RARE, Currencies.FAIRY_ESSENCE, 40),

    ;

    public static final RandomCollection<SupplyDropRewards> RANDOM_COLLECTION = new RandomCollection<>();

    static {
        for (SupplyDropRewards supplyDropRewards : values()) {
            RANDOM_COLLECTION.add(supplyDropRewards.dropChance, supplyDropRewards);
        }
    }

    public final String name;
    public final int dropChance;
    public final WeaponsPvE rarity; //using for convenience
    public final Currencies currency;
    public final long currencyAmount;

    SupplyDropRewards(String name, int dropChance, WeaponsPvE rarity, Currencies currency, long currencyAmount) {
        this.name = name;
        this.dropChance = dropChance;
        this.rarity = rarity;
        this.currency = currency;
        this.currencyAmount = currencyAmount;
    }

    public static SupplyDropRewards getRandomReward() {
        return RANDOM_COLLECTION.next();
    }

    public void giveReward(DatabasePlayerPvE databasePlayerPvE) {
        databasePlayerPvE.addCurrency(currency, currencyAmount);
    }

    public Component getDropMessage() {
        return Component.text("You received ", NamedTextColor.GRAY)
                        .append(Component.text(name, getTextColor()))
                        .append(Component.text(" from the supply drop."));
    }

    public NamedTextColor getTextColor() {
        return rarity.textColor;
    }

    protected Component getStarPieceDropMessage() {
        return Component.text("A ", NamedTextColor.GRAY)
                        .append(Component.text(getType() + " Star Piece ", getTextColor()))
                        .append(Component.text("has been bestowed upon you."));
    }

    public String getType() {
        return rarity.name;
    }

    public void givePlayerRewardTitle(Player player) {
        player.showTitle(Title.title(
                Component.text(getType().toUpperCase() + "!", getTextColor()),
                Component.text(name, NamedTextColor.GOLD),
                Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
        ));
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.2f);
    }
}

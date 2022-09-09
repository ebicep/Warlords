package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public enum Currencies {

    SYNTHETIC_SHARD(
            "Synthetic Shard",
            ChatColor.WHITE,
            new ItemStack(Material.BLAZE_POWDER)
    ),
    LEGEND_FRAGMENTS(
            "Legend Fragments",
            ChatColor.GOLD,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    FAIRY_ESSENCE(
            "Fairy Essence",
            ChatColor.LIGHT_PURPLE,
            new ItemStack(Material.INK_SACK, 1, (short) 13)
    ),
    COMMON_STAR_PIECE(
            "Common Star Piece",
            ChatColor.GREEN,
            new ItemStack(Material.NETHER_STAR)
    ),
    RARE_STAR_PIECE(
            "Rare Star Piece",
            ChatColor.BLUE,
            new ItemStack(Material.NETHER_STAR)
    ),
    EPIC_STAR_PIECE(
            "Epic Star Piece",
            ChatColor.DARK_PURPLE,
            new ItemStack(Material.NETHER_STAR)
    ),
    LEGENDARY_STAR_PIECE(
            "Legendary Star Piece",
            ChatColor.GOLD,
            new ItemStack(Material.NETHER_STAR)
    ),
    SUPPLY_DROP_TOKEN(
            "Supply Drop Token",
            ChatColor.YELLOW,
            new ItemStack(Material.FIREWORK_CHARGE)
    ),
    COIN(
            "Coin",
            ChatColor.YELLOW,
            new ItemStack(Material.GOLD_NUGGET)
    ),
    SKILL_BOOST_MODIFIER(
            "Skill Boost Modifier",
            ChatColor.BLACK,
            new ItemStack(Material.GOLD_NUGGET)
    ),


    ;

    public static final Currencies[] VALUES = values();
    public static final HashMap<UUID, PvECoinSummary> CACHED_PLAYER_COIN_STATS = new HashMap<>();
    public final String name;
    public final ChatColor chatColor;
    public final ItemStack item;

    Currencies(String name, ChatColor chatColor, ItemStack item) {
        this.name = name;
        this.chatColor = chatColor;
        this.item = item;
    }

    public static PvECoinSummary getCoinGainFromGameStats(
            WarlordsEntity warlordsPlayer,
            WaveDefenseOption waveDefenseOption,
            boolean recalculate
    ) {
        if (!recalculate &&
                CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid()) &&
                CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid()) != null
        ) {
            return CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid());
        }

        LinkedHashMap<String, Long> coinSummary = new LinkedHashMap<>(waveDefenseOption.getCachedBaseCoinSummary());
        //TODO event for upgrade
        long totalCoinsEarned = 0;
        for (Long value : coinSummary.values()) {
            totalCoinsEarned += value;
        }

        long guildCoinsEarned = Math.min(300, Math.round(totalCoinsEarned * .02));

        if (CACHED_PLAYER_COIN_STATS.containsKey(warlordsPlayer.getUuid())) {
            return CACHED_PLAYER_COIN_STATS.get(warlordsPlayer.getUuid())
                    .setCoinSummary(coinSummary)
                    .setTotalCoinsGained(totalCoinsEarned)
                    .setTotalGuildCoinsGained(guildCoinsEarned);
        } else {
            PvECoinSummary pvECoinSummary = new PvECoinSummary(coinSummary, totalCoinsEarned, guildCoinsEarned);
            CACHED_PLAYER_COIN_STATS.put(warlordsPlayer.getUuid(), pvECoinSummary);
            return pvECoinSummary;
        }
    }

    public String getColoredName() {
        return chatColor + name;
    }

    public static class PvECoinSummary {
        private LinkedHashMap<String, Long> coinSummary;
        private long totalCoinsGained;
        private long totalGuildCoinsGained;

        public PvECoinSummary(LinkedHashMap<String, Long> coinSummary, long totalCoinsGained, long totalGuildCoinsGained) {
            this.coinSummary = coinSummary;
            this.totalCoinsGained = totalCoinsGained;
            this.totalGuildCoinsGained = totalGuildCoinsGained;
        }

        public LinkedHashMap<String, Long> getCoinSummary() {
            return coinSummary;
        }

        public PvECoinSummary setCoinSummary(LinkedHashMap<String, Long> coinSummary) {
            this.coinSummary = coinSummary;
            return this;
        }

        public long getTotalCoinsGained() {
            return totalCoinsGained;
        }

        public PvECoinSummary setTotalCoinsGained(long totalCoinsGained) {
            this.totalCoinsGained = totalCoinsGained;
            return this;
        }

        public long getTotalGuildCoinsGained() {
            return totalGuildCoinsGained;
        }

        public PvECoinSummary setTotalGuildCoinsGained(long totalGuildCoinsGained) {
            this.totalGuildCoinsGained = totalGuildCoinsGained;
            return this;
        }
    }
}

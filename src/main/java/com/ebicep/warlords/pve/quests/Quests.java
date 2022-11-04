package com.ebicep.warlords.pve.quests;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.*;

public enum Quests {

    DAILY_300_KA("Tribute",
            "Get 300 Kills/Assists in 1 game",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
            return total.getKills() + total.getAssists() >= 300;
        }
    },
    DAILY_2_PLAYS("Motivate",
            "Play 2 games",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 30L);
                put(Currencies.COIN, 7500L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() + 1 >= 2;
        }
    },
    DAILY_WIN("Triumphant",
            "Win a game",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 50L);
                put(Currencies.COIN, 15000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getWins() + 1 >= 1;
        }
    },

    WEEKLY_20_PLAYS("Devotion",
            "Play 20 games",
            PlayersCollections.WEEKLY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 300L);
                put(Currencies.COIN, 50000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() + 1 >= 20;
        }
    },
    WEEKLY_30_ENDLESS("Conquest",
            "Reach Wave 30 in a game of Endless",
            PlayersCollections.WEEKLY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.SYNTHETIC_SHARD, 150L);
                put(Currencies.COIN, 25000L);
            }}
    ) {
        @Override
        public boolean checkReward(WaveDefenseOption waveDefenseOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS && waveDefenseOption.getWavesCleared() >= 30;
        }
    },

    ;

    public static final Quests[] VALUES = values();
    public static final HashMap<UUID, List<Quests>> CACHED_PLAYER_QUESTS = new HashMap<>();

    public static List<Quests> getQuestsFromGameStats(WarlordsPlayer warlordsPlayer, WaveDefenseOption waveDefenseOption, boolean recalculate) {
        if (!QuestCommand.isQuestsEnabled) {
            return new ArrayList<>();
        }
        if (!recalculate && CACHED_PLAYER_QUESTS.containsKey(warlordsPlayer.getUuid()) && CACHED_PLAYER_QUESTS.get(
                warlordsPlayer.getUuid()) != null) {
            return CACHED_PLAYER_QUESTS.get(warlordsPlayer.getUuid());
        }
        List<Quests> questsCompleted = new ArrayList<>();

        PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
        if (total.getDamage() + total.getHealing() + total.getAbsorbed() < 100_000) {
            return questsCompleted;
        }

        for (Quests quest : VALUES) {
            if (quest.expireOn != null && quest.expireOn.isBefore(Instant.now())) {
                continue;
            }
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), quest.time, databasePlayer -> {
                if (databasePlayer.getPveStats().getQuestsCompleted().containsKey(quest)) {
                    return;
                }
                if (quest.checkReward(waveDefenseOption, warlordsPlayer, databasePlayer)) {
                    questsCompleted.add(quest);
                }
            });
        }

        CACHED_PLAYER_QUESTS.put(warlordsPlayer.getUuid(), questsCompleted);
        return questsCompleted;
    }

    public abstract boolean checkReward(
            WaveDefenseOption waveDefenseOption,
            WarlordsPlayer warlordsPlayer,
            DatabasePlayer databasePlayer
    );

    public final String name;
    public final String description;
    public final PlayersCollections time;
    public final Instant expireOn;
    public final LinkedHashMap<Currencies, Long> rewards;

    Quests(String name, String description, PlayersCollections time, Instant expireOn, LinkedHashMap<Currencies, Long> rewards) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.expireOn = expireOn;
        this.rewards = rewards;
    }

    public ItemStack getItemStack(boolean completed) {
        ItemBuilder itemBuilder = new ItemBuilder(completed ? Material.EMPTY_MAP : Material.PAPER)
                //.name(ChatColor.GREEN + time.name + ": " + name)
                .name(ChatColor.GREEN + name)
                .lore(
                        ChatColor.GRAY + description,
                        "",
                        ChatColor.GRAY + "Rewards:"
                );
        rewards.forEach((currencies, aLong) -> itemBuilder.addLore(ChatColor.DARK_GRAY + " +" + currencies.getCostColoredName(aLong)));
        if (completed) {
            itemBuilder.addLore("", ChatColor.GREEN + "Completed!");
        } else {
            itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            itemBuilder.enchant(Enchantment.OXYGEN, 1);
        }
        return itemBuilder.get();

    }

    public String getHoverText() {
        StringBuilder hoverText = new StringBuilder(ChatColor.GREEN + description + "\n");
        rewards.forEach((currencies, aLong) -> hoverText.append(ChatColor.GRAY).append(" - ").append(currencies.getCostColoredName(aLong)).append("\n"));
        return hoverText.toString();
    }

}

package com.ebicep.warlords.pve.quests;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.onslaught.DatabasePlayerOnslaughtStats;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.util.*;

public enum Quests {
    DAILY_300_KA("Tribute [LEGACY]",
            "Get 150 Kills/Assists in 1 game",
            null,
            null,
            null
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return false;
        }
    },
    DAILY_150_KA("Tribute",
            "Get 150 Kills/Assists in 1 game",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 7500L);
                put(Currencies.SYNTHETIC_SHARD, 30L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            if (pveOption instanceof WaveDefenseOption) {
                PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
                return total.getKills() + total.getAssists() >= 150;
            }
            return false;
        }
    },
    DAILY_2_PLAYS("Motivate",
            "Play 2 games",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 7500L);
                put(Currencies.SYNTHETIC_SHARD, 30L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() + 1 >= 2;
        }

        @Override
        public String getProgress(DatabasePlayer databasePlayer) {
            return ChatColor.GOLD.toString() + databasePlayer.getPveStats().getPlays() + ChatColor.AQUA + "/" + ChatColor.GOLD + "2";
        }

        @Override
        public String getNoProgress() {
            return ChatColor.GOLD + "0" + ChatColor.AQUA + "/" + ChatColor.GOLD + "2";
        }
    },
    DAILY_WIN("Triumphant",
            "Win a game",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 15000L);
                put(Currencies.SYNTHETIC_SHARD, 50L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                return databasePlayer.getPveStats().getWins() >= 1 || waveDefenseOption.getWavesCleared() >= waveDefenseOption.getMaxWave();
            }
            return false;
        }
    },
    DAILY_20_WAVE_CLEAR("Wave Clearer",
            "Clear a total of 20 waves",
            PlayersCollections.DAILY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 7500L);
                put(Currencies.SYNTHETIC_SHARD, 30L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                return databasePlayer.getPveStats().getWaveDefenseStats().getTotalWavesCleared() + waveDefenseOption.getWavesCleared() >= 20;
            }
            return false;
        }

        @Override
        public String getProgress(DatabasePlayer databasePlayer) {
            return ChatColor.GOLD.toString() + databasePlayer.getPveStats().getWaveDefenseStats().getTotalWavesCleared() + ChatColor.AQUA + "/" + ChatColor.GOLD + "20";
        }

        @Override
        public String getNoProgress() {
            return ChatColor.GOLD + "0" + ChatColor.AQUA + "/" + ChatColor.GOLD + "20";
        }
    },

    WEEKLY_20_PLAYS("Devotion",
            "Play 20 games",
            PlayersCollections.WEEKLY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 50000L);
                put(Currencies.SYNTHETIC_SHARD, 300L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            return databasePlayer.getPveStats().getPlays() + 1 >= 20;
        }

        @Override
        public String getProgress(DatabasePlayer databasePlayer) {
            return ChatColor.GOLD.toString() + databasePlayer.getPveStats().getPlays() + ChatColor.AQUA + "/" + ChatColor.GOLD + "20";
        }

        @Override
        public String getNoProgress() {
            return ChatColor.GOLD + "0" + ChatColor.AQUA + "/" + ChatColor.GOLD + "20";
        }
    },
    WEEKLY_30_ENDLESS("Conquest",
            "Clear wave 30 in a game of Endless",
            PlayersCollections.WEEKLY,
            null,
            new LinkedHashMap<>() {{
                put(Currencies.COIN, 25000L);
                put(Currencies.SYNTHETIC_SHARD, 150L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                return waveDefenseOption.getDifficulty() == DifficultyIndex.ENDLESS && waveDefenseOption.getWavesCleared() >= 30;
            }
            return false;
        }
    },
    WEEKLY_5000_ONSLAUGHT("Slayer",
            "Get 5,000 Kills/Assists in Onslaught",
            PlayersCollections.WEEKLY,
            null,
            new LinkedHashMap<>() {{
                put(MobDrops.ZENITH_STAR, 1L);
            }}
    ) {
        @Override
        public boolean checkReward(PveOption pveOption, WarlordsPlayer warlordsPlayer, DatabasePlayer databasePlayer) {
            if (pveOption instanceof OnslaughtOption) {
                DatabasePlayerOnslaughtStats onslaughtStats = databasePlayer.getPveStats().getOnslaughtStats();
                PlayerStatisticsMinute.Entry total = warlordsPlayer.getMinuteStats().total();
                return onslaughtStats.getKills() + onslaughtStats.getAssists() + total.getKills() + total.getAssists() >= 5000;
            }
            return false;
        }

        @Override
        public String getProgress(DatabasePlayer databasePlayer) {
            DatabasePlayerOnslaughtStats onslaughtStats = databasePlayer.getPveStats().getOnslaughtStats();
            return ChatColor.GOLD.toString() + (onslaughtStats.getKills() + onslaughtStats.getAssists()) + ChatColor.AQUA + "/" + ChatColor.GOLD + "5,000";
        }

        @Override
        public String getNoProgress() {
            return ChatColor.GOLD + "0" + ChatColor.AQUA + "/" + ChatColor.GOLD + "5,000";
        }
    },

    ;

    public static final Quests[] VALUES = values();
    public static final HashMap<UUID, List<Quests>> CACHED_PLAYER_QUESTS = new HashMap<>();

    public static List<Quests> getQuestsFromGameStats(WarlordsPlayer warlordsPlayer, PveOption pveOption, boolean recalculate) {
        if (!QuestCommand.isQuestsEnabled) {
            CACHED_PLAYER_QUESTS.put(warlordsPlayer.getUuid(), new ArrayList<>());
            return new ArrayList<>();
        }
        if (!recalculate && CACHED_PLAYER_QUESTS.containsKey(warlordsPlayer.getUuid()) && CACHED_PLAYER_QUESTS.get(
                warlordsPlayer.getUuid()) != null) {
            return CACHED_PLAYER_QUESTS.get(warlordsPlayer.getUuid());
        }
        List<Quests> questsCompleted = new ArrayList<>();

        for (Quests quest : VALUES) {
            if (quest.time == null) {
                continue;
            }
            if (quest.expireOn != null && quest.expireOn.isBefore(Instant.now())) {
                continue;
            }
            DatabaseManager.getPlayer(warlordsPlayer.getUuid(), quest.time, databasePlayer -> {
                if (databasePlayer.getPveStats().getQuestsCompleted().containsKey(quest)) {
                    return;
                }
                if (quest.checkReward(pveOption, warlordsPlayer, databasePlayer)) {
                    questsCompleted.add(quest);
                }
            });
        }

        CACHED_PLAYER_QUESTS.put(warlordsPlayer.getUuid(), questsCompleted);
        return questsCompleted;
    }

    public abstract boolean checkReward(
            PveOption pveOption,
            WarlordsPlayer warlordsPlayer,
            DatabasePlayer databasePlayer
    );

    public final String name;
    public final String description;
    public final PlayersCollections time;
    public final Instant expireOn;
    public final LinkedHashMap<Spendable, Long> rewards;

    Quests(String name, String description, PlayersCollections time, Instant expireOn, LinkedHashMap<Spendable, Long> rewards) {
        this.name = name;
        this.description = description;
        this.time = time;
        this.expireOn = expireOn;
        this.rewards = rewards;
    }

    public ItemStack getItemStack(DatabasePlayer databasePlayer, boolean completed) {
        ItemBuilder itemBuilder = new ItemBuilder(completed ? Material.MAP : Material.PAPER)
                .name(Component.text(time.name + ": " + name, NamedTextColor.GREEN))
                //.name(Component.text(name)
                .loreLEGACY(
                        ChatColor.GRAY + description,
                        "",
                        ChatColor.GRAY + "Progress: " + (completed ? ChatColor.GREEN + "Completed" :
                                                         databasePlayer == null ? getNoProgress() : getProgress(databasePlayer)),
                        "",
                        ChatColor.GRAY + "Rewards:"
                );
        rewards.forEach((currencies, aLong) -> itemBuilder.addLore(ChatColor.DARK_GRAY + " +" + currencies.getCostColoredName(aLong)));
        if (!completed) {
            itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
            itemBuilder.enchant(Enchantment.OXYGEN, 1);
        }
        return itemBuilder.get();

    }

    public String getNoProgress() {
        return ChatColor.GREEN + "Started";
    }

    public String getProgress(DatabasePlayer databasePlayer) {
        return ChatColor.GREEN + "Started";
    }

    public String getHoverText() {
        StringBuilder hoverText = new StringBuilder(ChatColor.GREEN + description + "\n");
        rewards.forEach((currencies, aLong) -> hoverText.append(ChatColor.GRAY).append(" - ").append(currencies.getCostColoredName(aLong)).append("\n"));
        return hoverText.toString();
    }

}

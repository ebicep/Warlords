package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable1;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable2;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable3;
import com.ebicep.warlords.pve.bountysystem.rewards.WeeklyRewardSpendable4;
import org.bukkit.Material;

import java.util.LinkedHashMap;

public enum GuildBounty {

    KILL_MOBS("Extermination", "Kill 200,000 mobs. Every guild member contributes to this counter.", 200_000, Material.IRON_SWORD, WeeklyRewardSpendable1.REWARD, 15_000, 30_000),
    COMPLETE_EXTREME("United Front", "Complete Extreme Mode with 4 guild members.", 1, Material.NETHER_STAR, WeeklyRewardSpendable4.REWARD, 10_000, 30_000),
    PLAY_GAMES("War Games", "Play 200 games. Every guild member contributes to this counter.", 200, Material.DIAMOND_SWORD, WeeklyRewardSpendable2.REWARD, 10_000, 30_000),
    COMPLETE_ANOMALIES("Investigative Unit", "Successfully complete 30 Anomaly investigations with at least 1 other guild member in the party.", 30, Material.SPYGLASS, WeeklyRewardSpendable3.REWARD, 10_000, 30_000),
    //COMPLETE_REGNUM("Two Crowns, One Guild", "Complete the Regnum of Two Crowns raid with at least 4 guild members in the party.", 1, Material.GOLDEN_HELMET, WeeklyRewardSpendable4.REWARD, 25_000, 100_000),
    KILL_SKELETONS("Bone Collector", "Kill 50,000 Skeletons. Every guild member contributes to this counter.", 50_000, Material.SKELETON_SKULL, WeeklyRewardSpendable2.REWARD, 10_000, 30_000),
    REACH_ENDLESS_WAVE_100("Endless Resolve", "Reach Wave 100 in Endless with at least 4 guild members in the party.", 100, Material.BEACON, WeeklyRewardSpendable4.REWARD, 10_000, 50_000),
    KILL_ZOMBIES("Graveyard Shift", "Kill 50,000 Zombies. Every guild member contributes to this counter.", 50_000, Material.ZOMBIE_HEAD, WeeklyRewardSpendable2.REWARD, 10_000, 30_000),
    REACH_ONSLAUGHT_60_MINUTES("Hold the Line", "Reach 60 minutes in Onslaught with at least 3 guild members in the party.", 60 * 60, Material.CLOCK, WeeklyRewardSpendable4.REWARD, 10_000, 30_000),

    ;

    public static final GuildBounty[] VALUES = values();

    private final String name;
    private final String description;
    private final long target;
    private final Material material;
    private final LinkedHashMap<Spendable, Long> playerRewards;
    private final long guildCoins;
    private final long guildExperience;

    GuildBounty(String name, String description, long target, Material material, LinkedHashMap<Spendable, Long> playerRewards, long guildCoins, long guildExperience) {
        this.name = name;
        this.description = description;
        this.target = target;
        this.material = material;
        this.playerRewards = new LinkedHashMap<>(playerRewards);
        this.guildCoins = guildCoins;
        this.guildExperience = guildExperience;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public long getTarget() {
        return target;
    }

    public Material getMaterial() {
        return material;
    }

    public LinkedHashMap<Spendable, Long> getPlayerRewards() {
        return new LinkedHashMap<>(playerRewards);
    }

    public long getGuildCoins() {
        return guildCoins;
    }

    public long getGuildExperience() {
        return guildExperience;
    }
}

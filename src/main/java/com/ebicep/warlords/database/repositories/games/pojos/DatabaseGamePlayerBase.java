package com.ebicep.warlords.database.repositories.games.pojos;

import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DatabaseGamePlayerBase {

    protected UUID uuid;
    protected String name;
    protected Specializations spec;
    @Field("skill_boost")
    protected SkillBoosts skillBoost;
    @Field("blocks_travelled")
    protected int blocksTravelled;
    @Field("x_locations")
    protected String xLocations;
    @Field("z_locations")
    protected String zLocations;
    @Field("total_kills")
    protected int totalKills;
    @Field("total_assists")
    protected int totalAssists;
    @Field("total_deaths")
    protected int totalDeaths;
    @Field("total_damage")
    protected long totalDamage;
    @Field("total_healing")
    protected long totalHealing;
    @Field("total_absorbed")
    protected long totalAbsorbed;
    protected List<Integer> kills;
    protected List<Integer> assists;
    protected List<Integer> deaths;
    protected List<Long> damage;
    protected List<Long> healing;
    protected List<Long> absorbed;
    @Field("experience_earned_spec")
    protected long experienceEarnedSpec;
    @Field("experience_earned_universal")
    protected long experienceEarnedUniversal;
    @Id
    private String id;

    public DatabaseGamePlayerBase() {
    }

    public DatabaseGamePlayerBase(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(warlordsPlayer, true);
        long experienceEarnedUniversal = expSummary.values().stream().mapToLong(Long::longValue).sum();
        long experienceEarnedSpec = ExperienceManager.getSpecExpFromSummary(expSummary);
        this.uuid = warlordsPlayer.getUuid();
        this.name = warlordsPlayer.getName();
        this.spec = warlordsPlayer.getSpecClass();
        this.skillBoost = PlayerSettings.getPlayerSettings(warlordsPlayer.getUuid()).getSkillBoostForClass();
        this.blocksTravelled = warlordsPlayer.getBlocksTravelled();
        this.xLocations = warlordsPlayer.getLocations()
                                        .stream()
                                        .map(Location::getX)
                                        .map(String::valueOf)
                                        .map(s -> s.substring(0, s.indexOf(".") + 2))
                                        .collect(Collectors.joining(",", "", ","));
        this.zLocations = warlordsPlayer.getLocations()
                                        .stream()
                                        .map(Location::getZ)
                                        .map(String::valueOf)
                                        .map(s -> s.substring(0, s.indexOf(".") + 2))
                                        .collect(Collectors.joining(",", "", ","));
        this.totalKills = warlordsPlayer.getMinuteStats().total().getKills();
        this.totalAssists = warlordsPlayer.getMinuteStats().total().getAssists();
        this.totalDeaths = warlordsPlayer.getMinuteStats().total().getDeaths();
        this.totalDamage = warlordsPlayer.getMinuteStats().total().getDamage();
        this.totalHealing = warlordsPlayer.getMinuteStats().total().getHealing();
        this.totalAbsorbed = warlordsPlayer.getMinuteStats().total().getAbsorbed();
        if (!(this instanceof DatabaseGamePlayerPvEWaveDefense)) {
            this.kills = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getKills).toList();
            this.assists = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getAssists).toList();
            this.deaths = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getDeaths).toList();
            this.damage = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getDamage).toList();
            this.healing = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getHealing).toList();
            this.absorbed = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getAbsorbed).toList();
        }
        this.experienceEarnedSpec = experienceEarnedSpec;
        this.experienceEarnedUniversal = experienceEarnedUniversal;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Specializations getSpec() {
        return spec;
    }

    public SkillBoosts getSkillBoost() {
        return skillBoost;
    }

    public int getBlocksTravelled() {
        return blocksTravelled;
    }

    public String getxLocations() {
        return xLocations;
    }

    public String getzLocations() {
        return zLocations;
    }

    public String getKDAString() {
        return ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + getTotalKills() + ChatColor.GRAY + ":" + ChatColor.GOLD + getTotalAssists() + ChatColor.GRAY + ":" + ChatColor.RED + getTotalDeaths() + ChatColor.DARK_GRAY + "]";
    }

    public int getTotalKills() {
        return totalKills;
    }

    public int getTotalAssists() {
        return totalAssists;
    }

    public int getTotalDeaths() {
        return totalDeaths;
    }

    public long getTotalDHP() {
        return totalDamage + totalHealing + totalAbsorbed;
    }

    public long getTotalDamage() {
        return totalDamage;
    }

    public long getTotalHealing() {
        return totalHealing;
    }

    public long getTotalAbsorbed() {
        return totalAbsorbed;
    }

    public List<Integer> getKills() {
        return kills;
    }

    public List<Integer> getAssists() {
        return assists;
    }

    public List<Integer> getDeaths() {
        return deaths;
    }

    public List<Long> getDamage() {
        return damage;
    }

    public List<Long> getHealing() {
        return healing;
    }

    public List<Long> getAbsorbed() {
        return absorbed;
    }

    public long getExperienceEarnedSpec() {
        return experienceEarnedSpec;
    }

    public long getExperienceEarnedUniversal() {
        return experienceEarnedUniversal;
    }
}

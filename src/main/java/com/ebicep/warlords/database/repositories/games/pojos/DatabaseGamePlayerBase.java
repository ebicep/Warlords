package com.ebicep.warlords.database.repositories.games.pojos;

import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.player.ExperienceManager;
import com.ebicep.warlords.player.PlayerStatistics;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseGamePlayerBase {

    protected String uuid;
    protected String name;
    protected Classes spec;
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

    public DatabaseGamePlayerBase() {
    }

    public DatabaseGamePlayerBase(WarlordsPlayer warlordsPlayer) {
        LinkedHashMap<String, Long> expSummary = ExperienceManager.getExpFromGameStats(warlordsPlayer, true);
        long experienceEarnedUniversal = expSummary.values().stream().mapToLong(Long::longValue).sum();
        long experienceEarnedSpec = ExperienceManager.getSpecExpFromSummary(expSummary);
        this.uuid = warlordsPlayer.getUuid().toString();
        this.name = warlordsPlayer.getName();
        this.spec = warlordsPlayer.getSpecClass();
        this.blocksTravelled = warlordsPlayer.getBlocksTravelledCM() / 100;
        this.xLocations = warlordsPlayer.getLocations().stream().map(Location::getX).map(String::valueOf).map(s -> s.substring(0, s.indexOf(".") + 2)).collect(Collectors.joining(",", "", ","));
        this.zLocations = warlordsPlayer.getLocations().stream().map(Location::getZ).map(String::valueOf).map(s -> s.substring(0, s.indexOf(".") + 2)).collect(Collectors.joining(",", "", ","));
        this.totalKills = warlordsPlayer.getStats().total().getKills();
        this.totalAssists = warlordsPlayer.getStats().total().getAssists();
        this.totalDeaths = warlordsPlayer.getStats().total().getDeaths();
        this.totalDamage = warlordsPlayer.getStats().total().getDamage();
        this.totalHealing = warlordsPlayer.getStats().total().getHealing();
        this.totalAbsorbed = warlordsPlayer.getStats().total().getAbsorbed();
        this.kills = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getKills).collect(Collectors.toList());
        this.assists = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getAssists).collect(Collectors.toList());
        this.deaths = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getDeaths).collect(Collectors.toList());
        this.damage = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getDamage).collect(Collectors.toList());
        this.healing = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getHealing).collect(Collectors.toList());
        this.absorbed = warlordsPlayer.getStats().stream().map(PlayerStatistics.Entry::getAbsorbed).collect(Collectors.toList());
        this.experienceEarnedSpec = experienceEarnedSpec;
        this.experienceEarnedUniversal = experienceEarnedUniversal;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Classes getSpec() {
        return spec;
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

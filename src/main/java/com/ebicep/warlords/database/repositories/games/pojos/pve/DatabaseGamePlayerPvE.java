package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatabaseGamePlayerPvE extends DatabaseGamePlayerBase {

    @Field("longest_time_in_combat")
    private int longestTimeInCombat;
    @Field("most_damage_in_round")
    private long mostDamageInRound;
    @Field("most_damage_in_wave")
    private long mostDamageInWave;
    private int prestige;
    private int level;
    private AbstractWeapon weapon;
    @Field("upgrade_log")
    private List<AbilityTree.UpgradeLog> upgradeLog;
    @Field("mob_kills")
    private Map<String, Long> mobKills = new LinkedHashMap<>();
    @Field("mob_assists")
    private Map<String, Long> mobAssists = new LinkedHashMap<>();
    @Field("mob_deaths")
    private Map<String, Long> mobDeaths = new LinkedHashMap<>();
    @Field("coins_gained")
    private long coinsGained;
    @Field("guild_coins_gained")
    private long guildCoinsGained;
    @Field("guild_exp_gained")
    private long guildExpGained;
    @Field("weapons_found")
    private List<AbstractWeapon> weaponsFound = new ArrayList<>();
    @Field("legend_fragments_gain")
    private long legendFragmentsGained;

    public DatabaseGamePlayerPvE() {
    }

    public DatabaseGamePlayerPvE(WarlordsPlayer warlordsPlayer, WaveDefenseOption waveDefenseOption) {
        super(warlordsPlayer);
        DatabaseManager.getPlayer(warlordsPlayer.getUuid(), databasePlayer -> {
            this.prestige = databasePlayer.getSpec(warlordsPlayer.getSpecClass()).getPrestige();
        });
        this.level = ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass());
        this.weapon = warlordsPlayer.getAbstractWeapon();
        this.upgradeLog = warlordsPlayer.getAbilityTree().getUpgradeLog();
        this.mobKills = warlordsPlayer.getMinuteStats().total().getMobKills();
        this.mobAssists = warlordsPlayer.getMinuteStats().total().getMobAssists();
        this.mobDeaths = warlordsPlayer.getMinuteStats().total().getMobDeaths();
        Currencies.PvECoinSummary coinGainFromGameStats = Currencies.getCoinGainFromGameStats(warlordsPlayer, waveDefenseOption, true);
        this.coinsGained = coinGainFromGameStats.getTotalCoinsGained();
        this.guildCoinsGained = coinGainFromGameStats.getTotalGuildCoinsGained();
        this.guildExpGained = GuildExperienceUtils.getExpFromWaveDefense(warlordsPlayer, true).values().stream().mapToLong(aLong -> aLong).sum();
        this.weaponsFound.addAll(waveDefenseOption.getWaveDefenseStats()
                .getPlayerWeaponsFound()
                .getOrDefault(warlordsPlayer.getUuid(), new ArrayList<>()));
        this.legendFragmentsGained = waveDefenseOption.getWaveDefenseStats()
                .getPlayerLegendFragmentGain()
                .getOrDefault(warlordsPlayer.getUuid(), 0L);
    }

    public int getLongestTimeInCombat() {
        return longestTimeInCombat;
    }

    public long getMostDamageInRound() {
        return mostDamageInRound;
    }

    public long getMostDamageInWave() {
        return mostDamageInWave;
    }

    public int getPrestige() {
        return prestige;
    }

    public int getLevel() {
        return level;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public List<AbilityTree.UpgradeLog> getUpgradeLog() {
        return upgradeLog;
    }

    public Map<String, Long> getMobKills() {
        return mobKills;
    }

    public Map<String, Long> getMobAssists() {
        return mobAssists;
    }

    public Map<String, Long> getMobDeaths() {
        return mobDeaths;
    }

    public long getCoinsGained() {
        return coinsGained;
    }

    public long getGuildCoinsGained() {
        return guildCoinsGained;
    }

    public long getGuildExpGained() {
        return guildExpGained;
    }

    public List<AbstractWeapon> getWeaponsFound() {
        return weaponsFound;
    }

    public long getLegendFragmentsGained() {
        return legendFragmentsGained;
    }
}

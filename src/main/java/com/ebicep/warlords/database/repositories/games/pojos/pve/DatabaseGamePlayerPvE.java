package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseStats;
import com.ebicep.warlords.guilds.GuildExperienceUtils;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.quests.Quests;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabaseGamePlayerPvE extends DatabaseGamePlayerBase {

    @Field("longest_time_in_combat")
    private int longestTimeInCombat;
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
    @Field("quests_completed")
    private List<Quests> questsCompleted = new ArrayList<>();

    public DatabaseGamePlayerPvE() {
    }

    public DatabaseGamePlayerPvE(WarlordsPlayer warlordsPlayer, WaveDefenseOption waveDefenseOption) {
        super(warlordsPlayer);
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName());
        UUID uuid = warlordsPlayer.getUuid();
        WaveDefenseStats.PlayerWaveDefenseStats playerWaveDefenseStats = waveDefenseOption.getWaveDefenseStats()
                .getPlayerWaveDefenseStats(uuid);
        Collection<Long> values = playerWaveDefenseStats.getWaveDamage().values();
        if (!values.isEmpty()) {
            this.mostDamageInWave = Collections.max(values);
        }
        DatabaseManager.getPlayer(uuid, databasePlayer -> {
            this.prestige = databasePlayer.getSpec(warlordsPlayer.getSpecClass()).getPrestige();
        });
        this.level = ExperienceManager.getLevelForSpec(uuid, warlordsPlayer.getSpecClass());
        this.weapon = warlordsPlayer.getWeapon();
        this.upgradeLog = warlordsPlayer.getAbilityTree().getUpgradeLog();
        this.mobKills = warlordsPlayer.getMinuteStats().total().getMobKills();
        this.mobAssists = warlordsPlayer.getMinuteStats().total().getMobAssists();
        this.mobDeaths = warlordsPlayer.getMinuteStats().total().getMobDeaths();
        Currencies.PvECoinSummary coinGainFromGameStats = Currencies.getCoinGainFromGameStats(warlordsPlayer, waveDefenseOption, true);
        this.coinsGained = coinGainFromGameStats.getTotalCoinsGained();
        this.guildCoinsGained = coinGainFromGameStats.getTotalGuildCoinsGained();
        this.guildExpGained = GuildExperienceUtils.getExpFromWaveDefense(warlordsPlayer, true).values().stream().mapToLong(aLong -> aLong).sum();
        this.weaponsFound.addAll(playerWaveDefenseStats.getWeaponsFound());
        this.legendFragmentsGained = playerWaveDefenseStats.getLegendFragmentGain();
        List<Quests> questsFromGameStats = Quests.getQuestsFromGameStats(warlordsPlayer, waveDefenseOption, true);
        this.questsCompleted.addAll(questsFromGameStats);
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName() + " DONE");
    }

    public int getLongestTimeInCombat() {
        return longestTimeInCombat;
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

    public List<Quests> getQuestsCompleted() {
        return questsCompleted;
    }
}

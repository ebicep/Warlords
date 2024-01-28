package com.ebicep.warlords.database.repositories.games.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.game.option.pvp.siege.SiegeStats;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class DatabaseGamePlayerSiege extends DatabaseGamePlayerBase {

    @Field("seconds_in_combat")
    private int secondsInCombat;
    @Field("seconds_in_respawn")
    private int secondsInRespawn;

    @Field("points_captured")
    private int pointsCaptured;
    @Field("points_captured_fail")
    private int pointsCapturedFail;
    @Field("time_on_point")
    private long timeOnPoint; // seconds
    @Field("payloads_escorted")
    private int payloadsEscorted;
    @Field("payloads_escorted_fail")
    private int payloadsEscortedFail;
    @Field("points_defended")
    private int payloadsDefended;
    @Field("points_defended_fail")
    private int payloadsDefendedFail;
    @Field("time_on_payload_escorting")
    private long timeOnPayloadEscorting; // seconds
    @Field("time_on_payload_defending")
    private long timeOnPayloadDefending; // seconds

    @Field("spec_stats")
    private Map<Specializations, DatabaseGamePlayerSiege> specStats = null; // spec stats, must be wary of recursion so default to null

    public DatabaseGamePlayerSiege() {
    }

    public DatabaseGamePlayerSiege(WarlordsPlayer warlordsPlayer, Specializations spec) {
        ExperienceManager.ExperienceSummary expGain = ExperienceManager.getExpFromGameStats(warlordsPlayer, false);
        long experienceEarnedSpec = expGain.getSpecExpGain(spec);
        this.skillBoost = PlayerSettings.getPlayerSettings(warlordsPlayer.getUuid()).getSkillBoostForSpec(spec);
        this.blocksTravelled = warlordsPlayer.getBlocksTravelled();
        PlayerStatisticsMinute minuteStats = warlordsPlayer.getSpecMinuteStats().getOrDefault(spec, new PlayerStatisticsMinute());
        PlayerStatisticsMinute.Entry total = minuteStats.total();
        this.totalKills = total.getKills();
        this.totalAssists = total.getAssists();
        this.totalDeaths = total.getDeaths();
        this.totalDamage = total.getDamage();
        this.totalHealing = total.getHealing();
        this.totalAbsorbed = total.getAbsorbed();
        this.kills = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getKills).toList();
        this.assists = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getAssists).toList();
        this.deaths = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getDeaths).toList();
        this.damage = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getDamage).toList();
        this.healing = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getHealing).toList();
        this.absorbed = minuteStats.stream().map(PlayerStatisticsMinute.Entry::getAbsorbed).toList();
        this.experienceEarnedSpec = experienceEarnedSpec;
    }

    public DatabaseGamePlayerSiege(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(warlordsPlayer, gameWinEvent, counted);
        this.specStats = new HashMap<>();
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
        this.secondsInRespawn = warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent() / 20;
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (!(option instanceof SiegeOption siegeOption)) {
                continue;
            }
            Map<Specializations, SiegeStats> siegeStats = siegeOption.getPlayerSiegeStats().get(warlordsPlayer.getUuid());
            if (siegeStats == null) {
                return;
            }
            siegeStats.forEach((specializations, stats) -> {
                DatabaseGamePlayerSiege specStat = specStats.computeIfAbsent(specializations, k -> new DatabaseGamePlayerSiege(warlordsPlayer, specializations));
                specStat.secondsInCombat += warlordsPlayer.getSpecMinuteStats().getOrDefault(specializations, new PlayerStatisticsMinute()).total().getTimeInCombat();
                specStat.secondsInRespawn += warlordsPlayer.getSpecMinuteStats().getOrDefault(specializations, new PlayerStatisticsMinute()).total().getRespawnTimeSpent() / 20;
                specStat.pointsCaptured += stats.getPointsCaptured();
                specStat.pointsCapturedFail += stats.getPointsCapturedFail();
                specStat.timeOnPoint += stats.getTimeOnPointTicks() / 20;
                specStat.payloadsEscorted += stats.getPayloadsEscorted();
                specStat.payloadsEscortedFail += stats.getPayloadsEscortedFail();
                specStat.payloadsDefended += stats.getPayloadsDefended();
                specStat.payloadsDefendedFail += stats.getPayloadsDefendedFail();
                specStat.timeOnPayloadEscorting += stats.getTimeOnPayloadEscortingTicks() / 20;
                specStat.timeOnPayloadDefending += stats.getTimeOnPayloadDefendingTicks() / 20;

                this.pointsCaptured += stats.getPointsCaptured();
                this.pointsCapturedFail += stats.getPointsCapturedFail();
                this.timeOnPoint += stats.getTimeOnPointTicks() / 20;
                this.payloadsEscorted += stats.getPayloadsEscorted();
                this.payloadsEscortedFail += stats.getPayloadsEscortedFail();
                this.payloadsDefended += stats.getPayloadsDefended();
                this.payloadsDefendedFail += stats.getPayloadsDefendedFail();
                this.timeOnPayloadEscorting += stats.getTimeOnPayloadEscortingTicks() / 20;
                this.timeOnPayloadDefending += stats.getTimeOnPayloadDefendingTicks() / 20;
            });
            return;
        }
    }

    public Map<Specializations, DatabaseGamePlayerSiege> getSpecStats() {
        return specStats;
    }

    public int getSecondsInCombat() {
        return secondsInCombat;
    }

    public int getSecondsInRespawn() {
        return secondsInRespawn;
    }

    public int getPointsCaptured() {
        return pointsCaptured;
    }

    public int getPointsCapturedFail() {
        return pointsCapturedFail;
    }

    public long getTimeOnPoint() {
        return timeOnPoint;
    }

    public int getPayloadsEscorted() {
        return payloadsEscorted;
    }

    public int getPayloadsEscortedFail() {
        return payloadsEscortedFail;
    }

    public int getPayloadsDefended() {
        return payloadsDefended;
    }

    public int getPayloadsDefendedFail() {
        return payloadsDefendedFail;
    }

    public long getTimeOnPayloadEscorting() {
        return timeOnPayloadEscorting;
    }

    public long getTimeOnPayloadDefending() {
        return timeOnPayloadDefending;
    }
}

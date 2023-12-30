package com.ebicep.warlords.database.repositories.games.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

public class DatabaseGamePlayerCTF extends DatabaseGamePlayerBase {

    @Field("seconds_in_combat")
    private int secondsInCombat;
    @Field("seconds_in_respawn")
    private int secondsInRespawn;

    @Field("flag_captures")
    private int flagCaptures;
    @Field("flag_returns")
    private int flagReturns;
    @Field("total_damage_on_carrier")
    private long totalDamageOnCarrier;
    @Field("total_healing_on_carrier")
    private long totalHealingOnCarrier;
    @Field("damage_on_carrier")
    private List<Long> damageOnCarrier;
    @Field("healing_on_carrier")
    private List<Long> healingOnCarrier;


    public DatabaseGamePlayerCTF() {
    }

    public DatabaseGamePlayerCTF(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(warlordsPlayer, gameWinEvent, counted);
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
        this.secondsInRespawn = warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent() / 20;
        this.flagCaptures = warlordsPlayer.getFlagsCaptured();
        this.flagReturns = warlordsPlayer.getFlagsReturned();
        this.totalDamageOnCarrier = warlordsPlayer.getMinuteStats().total().getDamageOnCarrier();
        this.totalHealingOnCarrier = warlordsPlayer.getMinuteStats().total().getHealingOnCarrier();
        this.damageOnCarrier = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getDamageOnCarrier).toList();
        this.healingOnCarrier = warlordsPlayer.getMinuteStats().stream().map(PlayerStatisticsMinute.Entry::getHealingOnCarrier).toList();
    }

    public int getSecondsInCombat() {
        return secondsInCombat;
    }

    public void setSecondsInCombat(int secondsInCombat) {
        this.secondsInCombat = secondsInCombat;
    }

    public int getSecondsInRespawn() {
        return secondsInRespawn;
    }

    public void setSecondsInRespawn(int secondsInRespawn) {
        this.secondsInRespawn = secondsInRespawn;
    }

    public int getFlagCaptures() {
        return flagCaptures;
    }

    public void setFlagCaptures(int flagCaptures) {
        this.flagCaptures = flagCaptures;
    }

    public int getFlagReturns() {
        return flagReturns;
    }

    public void setFlagReturns(int flagReturns) {
        this.flagReturns = flagReturns;
    }

    public long getTotalDamageOnCarrier() {
        return totalDamageOnCarrier;
    }

    public void setTotalDamageOnCarrier(long totalDamageOnCarrier) {
        this.totalDamageOnCarrier = totalDamageOnCarrier;
    }

    public long getTotalHealingOnCarrier() {
        return totalHealingOnCarrier;
    }

    public void setTotalHealingOnCarrier(long totalHealingOnCarrier) {
        this.totalHealingOnCarrier = totalHealingOnCarrier;
    }

    public List<Long> getDamageOnCarrier() {
        return damageOnCarrier;
    }

    public void setDamageOnCarrier(List<Long> damageOnCarrier) {
        this.damageOnCarrier = damageOnCarrier;
    }

    public List<Long> getHealingOnCarrier() {
        return healingOnCarrier;
    }

    public void setHealingOnCarrier(List<Long> healingOnCarrier) {
        this.healingOnCarrier = healingOnCarrier;
    }
}

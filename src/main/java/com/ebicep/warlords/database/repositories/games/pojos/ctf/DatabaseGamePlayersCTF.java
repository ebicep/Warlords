package com.ebicep.warlords.database.repositories.games.pojos.ctf;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.PlayerStatisticsMinute.Entry;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseGamePlayersCTF {

    protected List<DatabaseGamePlayerCTF> blue = new ArrayList<>();
    protected List<DatabaseGamePlayerCTF> red = new ArrayList<>();

    public DatabaseGamePlayersCTF() {
    }

    public DatabaseGamePlayersCTF(@Nonnull Game game) {
        game.warlordsPlayers().forEach(warlordsPlayer -> {
            if (warlordsPlayer.getTeam() == Team.BLUE) {
                blue.add(new DatabaseGamePlayerCTF(warlordsPlayer));
            } else if (warlordsPlayer.getTeam() == Team.RED) {
                red.add(new DatabaseGamePlayerCTF(warlordsPlayer));
            }
        });
    }

    public DatabaseGamePlayersCTF(List<DatabaseGamePlayerCTF> blue, List<DatabaseGamePlayerCTF> red) {
        this.blue = blue;
        this.red = red;
    }

    public List<DatabaseGamePlayerCTF> getBlue() {
        return blue;
    }

    public List<DatabaseGamePlayerCTF> getRed() {
        return red;
    }

    public static class DatabaseGamePlayerCTF extends DatabaseGamePlayerBase {

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

        public DatabaseGamePlayerCTF(WarlordsPlayer warlordsPlayer) {
            super(warlordsPlayer);
            this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
            this.secondsInRespawn = Math.round(warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent());
            this.flagCaptures = warlordsPlayer.getFlagsCaptured();
            this.flagReturns = warlordsPlayer.getFlagsReturned();
            this.totalDamageOnCarrier = warlordsPlayer.getMinuteStats().total().getDamageOnCarrier();
            this.totalHealingOnCarrier = warlordsPlayer.getMinuteStats().total().getHealingOnCarrier();
            this.damageOnCarrier = warlordsPlayer.getMinuteStats().stream().map(Entry::getDamageOnCarrier).collect(Collectors.toList());
            this.healingOnCarrier = warlordsPlayer.getMinuteStats().stream().map(Entry::getHealingOnCarrier).collect(Collectors.toList());
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
}

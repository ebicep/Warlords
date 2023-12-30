package com.ebicep.warlords.database.repositories.games.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pvp.siege.SiegeOption;
import com.ebicep.warlords.game.option.pvp.siege.SiegeStats;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

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

    public DatabaseGamePlayerSiege() {
    }

    public DatabaseGamePlayerSiege(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(warlordsPlayer, gameWinEvent, counted);
        this.secondsInCombat = warlordsPlayer.getMinuteStats().total().getTimeInCombat();
        this.secondsInRespawn = warlordsPlayer.getMinuteStats().total().getRespawnTimeSpent() / 20;
        for (Option option : warlordsPlayer.getGame().getOptions()) {
            if (option instanceof SiegeOption siegeOption) {
                SiegeStats siegeStats = siegeOption.getPlayerSiegeStats().get(warlordsPlayer.getUuid());
                if (siegeStats == null) {
                    return;
                }
                this.pointsCaptured = siegeStats.getPointsCaptured();
                this.pointsCapturedFail = siegeStats.getPointsCapturedFail();
                this.timeOnPoint = siegeStats.getTimeOnPointTicks() / 20;
                this.payloadsEscorted = siegeStats.getPayloadsEscorted();
                this.payloadsEscortedFail = siegeStats.getPayloadsEscortedFail();
                this.payloadsDefended = siegeStats.getPayloadsDefended();
                this.payloadsDefendedFail = siegeStats.getPayloadsDefendedFail();
                this.timeOnPayloadEscorting = siegeStats.getTimeOnPayloadEscortingTicks() / 20;
                this.timeOnPayloadDefending = siegeStats.getTimeOnPayloadDefendingTicks() / 20;
                return;
            }
        }
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

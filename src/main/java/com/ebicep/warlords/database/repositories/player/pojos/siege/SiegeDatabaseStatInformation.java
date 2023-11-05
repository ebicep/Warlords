package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class SiegeDatabaseStatInformation extends AbstractDatabaseStatInformation {

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

    @Field("total_time_played")
    private long totalTimePlayed = 0;

    public SiegeDatabaseStatInformation() {
    }

    @Override
    public void updateCustomStats(
            DatabasePlayer databasePlayer,
            DatabaseGameBase databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerBase gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        assert databaseGame instanceof DatabaseGameSiege;
        assert gamePlayer instanceof DatabaseGamePlayerSiege;

        DatabaseGamePlayerSiege gamePlayerSiege = (DatabaseGamePlayerSiege) gamePlayer;
        this.pointsCaptured += gamePlayerSiege.getPointsCaptured() * multiplier;
        this.pointsCapturedFail += gamePlayerSiege.getPointsCapturedFail() * multiplier;
        this.timeOnPoint += gamePlayerSiege.getTimeOnPoint() * multiplier;
        this.payloadsEscorted += gamePlayerSiege.getPayloadsEscorted() * multiplier;
        this.payloadsEscortedFail += gamePlayerSiege.getPayloadsEscortedFail() * multiplier;
        this.payloadsDefended += gamePlayerSiege.getPayloadsDefended() * multiplier;
        this.payloadsDefendedFail += gamePlayerSiege.getPayloadsDefendedFail() * multiplier;
        this.timeOnPayloadEscorting += gamePlayerSiege.getTimeOnPayloadEscorting() * multiplier;
        this.timeOnPayloadDefending += gamePlayerSiege.getTimeOnPayloadDefending() * multiplier;
        this.totalTimePlayed += (long) ((DatabaseGameSiege) databaseGame).getTimeElapsed() * multiplier;
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

    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}

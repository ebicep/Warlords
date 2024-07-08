package com.ebicep.warlords.database.repositories.player.pojos.siege;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGamePlayerSiege;
import com.ebicep.warlords.database.repositories.games.pojos.siege.DatabaseGameSiege;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import org.springframework.data.mongodb.core.mapping.Field;

public class SiegeDatabaseStatInformation extends AbstractDatabaseStatInformation<DatabaseGameSiege, DatabaseGamePlayerSiege> implements SiegeStats {

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
    public void updateStats(
            DatabasePlayer databasePlayer,
            DatabaseGameSiege databaseGame,
            GameMode gameMode,
            DatabaseGamePlayerSiege gamePlayer,
            DatabaseGamePlayerResult result,
            int multiplier,
            PlayersCollections playersCollection
    ) {
        super.updateStats(databasePlayer, databaseGame, gameMode, gamePlayer, result, multiplier, playersCollection);
        this.pointsCaptured += gamePlayer.getPointsCaptured() * multiplier;
        this.pointsCapturedFail += gamePlayer.getPointsCapturedFail() * multiplier;
        this.timeOnPoint += gamePlayer.getTimeOnPoint() * multiplier;
        this.payloadsEscorted += gamePlayer.getPayloadsEscorted() * multiplier;
        this.payloadsEscortedFail += gamePlayer.getPayloadsEscortedFail() * multiplier;
        this.payloadsDefended += gamePlayer.getPayloadsDefended() * multiplier;
        this.payloadsDefendedFail += gamePlayer.getPayloadsDefendedFail() * multiplier;
        this.timeOnPayloadEscorting += gamePlayer.getTimeOnPayloadEscorting() * multiplier;
        this.timeOnPayloadDefending += gamePlayer.getTimeOnPayloadDefending() * multiplier;
        this.totalTimePlayed += (long) databaseGame.getTimeElapsed() * multiplier;
    }

    @Override
    public int getPointsCaptured() {
        return pointsCaptured;
    }

    @Override
    public int getPointsCapturedFail() {
        return pointsCapturedFail;
    }

    @Override
    public long getTimeOnPoint() {
        return timeOnPoint;
    }

    @Override
    public int getPayloadsEscorted() {
        return payloadsEscorted;
    }

    @Override
    public int getPayloadsEscortedFail() {
        return payloadsEscortedFail;
    }

    @Override
    public int getPayloadsDefended() {
        return payloadsDefended;
    }

    @Override
    public int getPayloadsDefendedFail() {
        return payloadsDefendedFail;
    }

    @Override
    public long getTimeOnPayloadEscorting() {
        return timeOnPayloadEscorting;
    }

    @Override
    public long getTimeOnPayloadDefending() {
        return timeOnPayloadDefending;
    }

    @Override
    public long getTotalTimePlayed() {
        return totalTimePlayed;
    }
}

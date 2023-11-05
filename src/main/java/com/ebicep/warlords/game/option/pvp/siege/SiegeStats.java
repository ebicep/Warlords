package com.ebicep.warlords.game.option.pvp.siege;

public final class SiegeStats {
    private int pointsCaptured = 0; //
    private int pointsCapturedFail = 0;
    private long timeOnPointTicks = 0; //
    private int payloadsEscorted = 0; //
    private int payloadsEscortedFail = 0;//
    private int payloadsDefended = 0;//
    private int payloadsDefendedFail = 0;//
    private long timeOnPayloadEscortingTicks = 0;//
    private long timeOnPayloadDefendingTicks = 0;//

    public int getPointsCaptured() {
        return pointsCaptured;
    }

    public void addPointsCaptured() {
        this.pointsCaptured++;
    }

    public int getPointsCapturedFail() {
        return pointsCapturedFail;
    }

    public void addPointsCapturedFail() {
        this.pointsCapturedFail++;
    }

    public long getTimeOnPointTicks() {
        return timeOnPointTicks;
    }

    public void addTimeOnPointTicks() {
        this.timeOnPointTicks++;
    }

    public int getPayloadsEscorted() {
        return payloadsEscorted;
    }

    public void addPayloadsEscorted() {
        this.payloadsEscorted++;
    }

    public int getPayloadsEscortedFail() {
        return payloadsEscortedFail;
    }

    public void addPayloadsEscortedFail() {
        this.payloadsEscortedFail++;
    }

    public int getPayloadsDefended() {
        return payloadsDefended;
    }

    public void addPayloadsDefended() {
        this.payloadsDefended++;
    }

    public int getPayloadsDefendedFail() {
        return payloadsDefendedFail;
    }

    public void addPayloadsDefendedFail() {
        this.payloadsDefendedFail++;
    }

    public long getTimeOnPayloadEscortingTicks() {
        return timeOnPayloadEscortingTicks;
    }

    public void addTimeOnPayloadEscorting() {
        this.timeOnPayloadEscortingTicks++;
    }

    public long getTimeOnPayloadDefendingTicks() {
        return timeOnPayloadDefendingTicks;
    }

    public void addTimeOnPayloadDefending() {
        this.timeOnPayloadDefendingTicks++;
    }
}

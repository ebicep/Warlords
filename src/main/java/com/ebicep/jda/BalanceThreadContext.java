package com.ebicep.jda;

public final class BalanceThreadContext {

    private static volatile long activeBalanceThreadId;
    private static volatile long latestBalanceThreadId;

    private BalanceThreadContext() {
    }

    public static long getActiveBalanceThreadId() {
        return activeBalanceThreadId;
    }

    public static long getLatestBalanceThreadId() {
        return latestBalanceThreadId;
    }

    public static void setActiveBalanceThreadId(long threadId) {
        activeBalanceThreadId = threadId;
        latestBalanceThreadId = threadId;
    }

    public static void clearActiveBalanceThreadId() {
        activeBalanceThreadId = 0;
    }

    public static void clearLatestBalanceThreadId() {
        latestBalanceThreadId = 0;
    }

}

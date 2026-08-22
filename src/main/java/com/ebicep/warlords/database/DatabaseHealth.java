package com.ebicep.warlords.database;

import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.MongoException;
import org.bson.Document;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public final class DatabaseHealth {

    private static volatile boolean dbHealthy = false;
    private static final AtomicBoolean OUTAGE_LOGGED = new AtomicBoolean(false);

    private DatabaseHealth() {
    }

    public static boolean isOperational() {
        return DatabaseManager.enabled && dbHealthy;
    }

    public static boolean isDbHealthy() {
        return dbHealthy;
    }

    public static void markHealthy() {
        boolean wasUnhealthy = !dbHealthy;
        dbHealthy = true;
        OUTAGE_LOGGED.set(false);
        GameManager.gameStartingDisabled = false;
        if (wasUnhealthy && DatabaseManager.enabled) {
            ChatUtils.MessageType.WARLORDS.sendMessage("Database connection restored.");
            flushPendingWrites();
        }
    }

    public static void markUnhealthy(Throwable cause) {
        dbHealthy = false;
        GameManager.gameStartingDisabled = true;
        if (OUTAGE_LOGGED.compareAndSet(false, true)) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Database unavailable: " + cause.getMessage());
            ChatUtils.MessageType.WARLORDS.sendErrorMessage(cause);
        }
    }

    public static void probe() {
        if (!DatabaseManager.enabled || DatabaseManager.warlordsDatabase == null) {
            return;
        }
        try {
            DatabaseManager.warlordsDatabase.runCommand(new Document("ping", 1));
            markHealthy();
        } catch (Exception e) {
            markUnhealthy(e);
        }
    }

    public static <T> Optional<T> tryDb(Supplier<T> action) {
        if (!isOperational()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(action.get());
        } catch (RuntimeException e) {
            if (isMongoFailure(e)) {
                markUnhealthy(e);
                return Optional.empty();
            }
            throw e;
        }
    }

    public static void runDb(Runnable action) {
        if (!isOperational()) {
            return;
        }
        try {
            action.run();
        } catch (RuntimeException e) {
            if (isMongoFailure(e)) {
                markUnhealthy(e);
                return;
            }
            throw e;
        }
    }

    public static boolean isMongoFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof MongoException || current instanceof DataAccessException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private static void flushPendingWrites() {
        if (DatabaseManager.playerService != null) {
            DatabaseUpdater.updatePlayers(DatabaseManager.playerService);
        }
        DatabaseUpdater.flushPendingGameSaves();
    }
}

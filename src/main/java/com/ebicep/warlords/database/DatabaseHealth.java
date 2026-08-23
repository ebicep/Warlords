package com.ebicep.warlords.database;

import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.mongodb.DuplicateKeyException;
import com.mongodb.ErrorCategory;
import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
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
        if (isDuplicateKeyFailure(cause)) {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Duplicate key during database write (ignored for health): " + cause.getMessage());
            return;
        }
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

    public static boolean isMongoFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (isDuplicateKeyFailure(current)) {
                return false;
            }
            if (current instanceof MongoException || current instanceof DataAccessException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    public static boolean isDuplicateKeyFailure(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            switch (current) {
                case MongoWriteException mongoWriteException when mongoWriteException.getError().getCategory() == ErrorCategory.DUPLICATE_KEY -> {
                    return true;
                }
                case org.springframework.dao.DuplicateKeyException ignored -> {
                    return true;
                }
                case DuplicateKeyException ignored -> {
                    return true;
                }
                default -> {
                }
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

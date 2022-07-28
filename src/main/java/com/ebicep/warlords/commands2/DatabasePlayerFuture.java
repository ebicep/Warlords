package com.ebicep.warlords.commands2;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.concurrent.CompletableFuture;

public class DatabasePlayerFuture {

    private final CompletableFuture<DatabasePlayer> future;

    public DatabasePlayerFuture(CompletableFuture<DatabasePlayer> future) {
        this.future = future;
    }

    public CompletableFuture<DatabasePlayer> getFuture() {
        return future;
    }

}

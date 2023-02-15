package com.ebicep.warlords.commands;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

import java.util.concurrent.CompletableFuture;

public record DatabasePlayerFuture(CompletableFuture<DatabasePlayer> future) {

}

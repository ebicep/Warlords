package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.chat.ChatChannels;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CommandAlias("database")
@CommandPermission("group.adminisrator")
public class DatabaseCommand extends BaseCommand {

    @Subcommand("removefromcache")
    public void removeFromCache(CommandIssuer issuer, String uuid) {
        for (PlayersCollections value : PlayersCollections.ACTIVE_COLLECTIONS) {
            ConcurrentHashMap<UUID, DatabasePlayer> cache = DatabaseManager.CACHED_PLAYERS.get(value);
            if (cache != null) {
                cache.remove(UUID.fromString(uuid));
                ChatChannels.sendDebugMessage(issuer, "Removed " + uuid + " from " + value.name() + " cache");
            }
        }
    }

    @Subcommand("printcache")
    public void printCache(CommandIssuer issuer) {
        for (PlayersCollections value : PlayersCollections.ACTIVE_COLLECTIONS) {
            ConcurrentHashMap<UUID, DatabasePlayer> cache = DatabaseManager.CACHED_PLAYERS.get(value);
            if (cache != null) {
                ChatChannels.sendDebugMessage(issuer, "Printing " + value.name() + " cache");
                for (UUID uuid : cache.keySet()) {
                    ChatChannels.sendDebugMessage(issuer, uuid.toString() + " - " + cache.get(uuid));
                }
            }
        }
    }

}

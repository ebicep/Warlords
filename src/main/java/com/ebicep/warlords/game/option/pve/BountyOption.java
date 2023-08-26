package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;

import javax.annotation.Nonnull;
import java.util.*;

public class BountyOption implements Option {

    @Override
    public void start(@Nonnull Game game) {
        Map<UUID, List<TracksDuringGame>> tracksDuringGames = new HashMap<>();
        game.forEachOfflinePlayer((offlinePlayer, team) -> {
            if (team == null) {
                return;
            }
            UUID uniqueId = offlinePlayer.getUniqueId();
            for (PlayersCollections collection : AbstractBounty.MAX_BOUNTIES.keySet()) {
                DatabaseManager.getPlayer(uniqueId, collection, databasePlayer -> {
                    List<AbstractBounty> trackableBounties = databasePlayer.getPveStats().getTrackableBounties();
                    for (AbstractBounty bounty : trackableBounties) {
                        if (bounty instanceof TracksDuringGame tracksDuringGame && tracksDuringGame.trackGame(game)) {
                            tracksDuringGames.computeIfAbsent(uniqueId, k -> new ArrayList<>()).add(tracksDuringGame);
                        }
                    }
                });
            }
        });
        game.registerEvents(TracksDuringGame.getListener(tracksDuringGames));
    }

}

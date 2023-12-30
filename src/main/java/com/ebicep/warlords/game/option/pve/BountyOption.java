package com.ebicep.warlords.game.option.pve;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.pve.events.EventMode;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.bountysystem.trackers.TracksDuringGame;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BountyOption implements Option {

    private final List<AbstractBounty> trackedBounties = new ArrayList<>();

    @Override
    public void start(@Nonnull Game game) {
        game.forEachOfflinePlayer((offlinePlayer, team) -> {
            if (team == null) {
                return;
            }
            UUID uniqueId = offlinePlayer.getUniqueId();
            for (PlayersCollections activeCollection : PlayersCollections.ACTIVE_COLLECTIONS) {
                BountyUtils.BountyInfo bountyInfo = BountyUtils.BOUNTY_COLLECTION_INFO.get(activeCollection.name);
                if (bountyInfo == null) {
                    continue;
                }
                DatabaseManager.getPlayer(uniqueId, activeCollection, databasePlayer -> {
                    List<AbstractBounty> trackableBounties = databasePlayer.getPveStats().getTrackableBounties();
                    addTracksDuringGameBounties(game, trackableBounties);
                    if (activeCollection == PlayersCollections.LIFETIME && DatabaseGameEvent.eventIsActive()) {
                        DatabaseGameEvent currentGameEvent = DatabaseGameEvent.currentGameEvent;
                        EventMode eventMode = currentGameEvent.getEvent().eventsStatsFunction
                                .apply(databasePlayer.getPveStats().getEventStats())
                                .get(currentGameEvent.getStartDateSecond());
                        if (eventMode == null) {
                            return;
                        }
                        addTracksDuringGameBounties(game, eventMode.getTrackableBounties());
                    }
                    trackedBounties.forEach(bounty -> bounty.init(databasePlayer));
                });
            }
        });
    }

    private void addTracksDuringGameBounties(@Nonnull Game game, List<AbstractBounty> trackableBounties) {
        for (AbstractBounty bounty : trackableBounties) {
            if (bounty instanceof TracksDuringGame tracksDuringGame && tracksDuringGame.trackGame(game)) {
                trackedBounties.add(bounty);
            }
        }
    }

    @Override
    public void onGameCleanup(@Nonnull Game game) {
        trackedBounties.forEach(AbstractBounty::unregister);
    }
}

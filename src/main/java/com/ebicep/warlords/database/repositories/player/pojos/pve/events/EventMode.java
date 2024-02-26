package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;

import java.util.List;
import java.util.Map;

public interface EventMode {

    long getEventPointsSpent();

    void addEventPointsSpent(long eventPointsSpent);

    Map<String, Long> getRewardsPurchased();

    int getEventPlays();

    long getEventPointsCumulative();

    Map<Bounty, Long> getCompletedBounties();

    int getBountiesCompleted();

    void addBountiesCompleted();

    List<AbstractBounty> getActiveEventBounties();

    default List<AbstractBounty> getTrackableBounties() {
        return getActiveEventBounties().stream()
                                       .filter(abstractBounty -> abstractBounty != null && abstractBounty.isStarted() && abstractBounty.getProgress() != null)
                                       .toList();
    }


}

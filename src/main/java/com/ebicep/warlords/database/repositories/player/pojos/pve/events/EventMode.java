package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import com.ebicep.warlords.pve.bountysystem.AbstractBounty;
import com.ebicep.warlords.pve.bountysystem.Bounty;

import java.util.List;
import java.util.Map;

public interface EventMode {

    long getEventPointsSpent();

    void addEventPointsSpent(long eventPointsSpent);

    Map<String, Long> getRewardsPurchased();

    int getPlays();

    long getEventPointsCumulative();

    Map<Bounty, Long> getCompletedBounties();

    int getBountiesCompleted();

    void addBountiesCompleted();

    List<AbstractBounty> getActiveBounties();

    default List<AbstractBounty> getTrackableBounties() {
        return getActiveBounties().stream()
                                  .filter(abstractBounty -> abstractBounty != null && abstractBounty.isStarted() && abstractBounty.getProgress() != null)
                                  .toList();
    }


}

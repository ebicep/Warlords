package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

import java.util.Map;

public interface EventMode {

    long getEventPointsSpent();

    void addEventPointsSpent(long eventPointsSpent);

    Map<String, Long> getRewardsPurchased();

}

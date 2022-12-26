package com.ebicep.warlords.database.repositories.player.pojos.pve.events;

public interface EventMode {

    long getEventPointsSpent();

    void addEventPointsSpent(long eventPointsSpent);

}

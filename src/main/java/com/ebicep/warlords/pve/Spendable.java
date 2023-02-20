package com.ebicep.warlords.pve;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;

public interface Spendable {

    void addToPlayer(DatabasePlayer databasePlayer, long amount);

    Long getFromPlayer(DatabasePlayer databasePlayer);

    default void subtractFromPlayer(DatabasePlayer databasePlayer, long amount) {
        addToPlayer(databasePlayer, -amount);
    }

    String getCostColoredName(long cost);


}

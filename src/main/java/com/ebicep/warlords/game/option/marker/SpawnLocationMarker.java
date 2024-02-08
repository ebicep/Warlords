package com.ebicep.warlords.game.option.marker;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.WarlordsEntity;

public interface SpawnLocationMarker extends LocationMarker {

    /**
     * Get the priority of this spawnpoint
     *
     * @param player the player to check
     * @return the priority, higher priorities should be preferred above lower
     * ones
     */
    double getPriority(WarlordsEntity player);

    /**
     * Get the priority of this spawnpoint for a specific team - for alternative use cases
     *
     * @param team the team to check
     * @return the priority, higher priorities should be preferred above lower
     * ones
     */
    double getPriorityTeam(Team team);
}

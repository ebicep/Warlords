package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.game.option.pve.raid.rooms.RaidRoom;

import java.util.List;

public interface RaidDefinition {

    String getName();

    /**
     * @return the catalog entry this raid pays out from on completion
     */
    Raid getRaid();

    List<RaidRoom> createRooms(RaidOption raidOption);

}

package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.game.option.pve.raid.rooms.RaidRoom;

import java.util.List;

public interface RaidDefinition {

    String getName();

    List<RaidRoom> createRooms(RaidOption raidOption);

}

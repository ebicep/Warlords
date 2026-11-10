package com.ebicep.warlords.game.option.pve.raid.raids;

import com.ebicep.warlords.game.option.pve.raid.Raid;
import com.ebicep.warlords.game.option.pve.raid.RaidDefinition;
import com.ebicep.warlords.game.option.pve.raid.RaidOption;
import com.ebicep.warlords.game.option.pve.raid.rooms.CombatRaidRoom;
import com.ebicep.warlords.game.option.pve.raid.rooms.ObjectiveRaidRoom;
import com.ebicep.warlords.game.option.pve.raid.rooms.RaidRoom;
import com.ebicep.warlords.game.option.pve.raid.rooms.SurvivalRaidRoom;

import java.util.List;

public class RegnumOfTwoCrownsRaid implements RaidDefinition {

    @Override
    public String getName() {
        return "Regnum of Two Crowns";
    }

    @Override
    public Raid getRaid() {
        return Raid.REGNUM_OF_TWO_CROWNS;
    }

    @Override
    public List<RaidRoom> createRooms(RaidOption raidOption) {
        return List.of(
                new CombatRaidRoom(),
        new SurvivalRaidRoom(),
        new ObjectiveRaidRoom() //new PhysiraMithraBossRoom()
        );
    }

}
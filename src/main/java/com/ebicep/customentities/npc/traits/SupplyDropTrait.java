package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import net.citizensnpcs.api.event.NPCRightClickEvent;

import static com.ebicep.warlords.pve.events.supplydrop.SupplyDropManager.openSupplyDropMenu;

public class SupplyDropTrait extends WarlordsTrait {

    public SupplyDropTrait() {
        super("SupplyDropTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        openSupplyDropMenu(event.getClicker());
    }

}

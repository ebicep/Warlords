package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.pve.bountysystem.BountyMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class BountyMenuTrait extends WarlordsTrait {

    public BountyMenuTrait() {
        super("BountyMenuTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        BountyMenu.openBountyMenu(event.getClicker());
    }

}

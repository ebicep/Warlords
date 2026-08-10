package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.supporter.SupporterShopMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class SupporterShopTrait extends WarlordsTrait {

    public SupporterShopTrait() {
        super("SupporterShopTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        SupporterShopMenu.open(event.getClicker());
    }
}

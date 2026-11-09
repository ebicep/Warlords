package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.supporter.SupporterShopMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SupporterShopTrait extends WarlordsTrait {

    public SupporterShopTrait() {
        super("SupporterShopTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage(Component.text("Supporter shop coming soon!", NamedTextColor.RED));
        //SupporterShopMenu.open(event.getClicker());
    }
}

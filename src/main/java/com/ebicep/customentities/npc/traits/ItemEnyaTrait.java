package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.pve.items.menu.ItemCraftingMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ItemEnyaTrait extends WarlordsTrait {

    public ItemEnyaTrait() {
        super("Ethical Enya");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        ItemCraftingMenu.openItemCraftingMenu(player, new HashMap<>());
    }


}

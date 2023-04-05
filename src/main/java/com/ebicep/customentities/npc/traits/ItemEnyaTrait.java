package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.items.menu.ItemCraftingMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;

public class ItemEnyaTrait extends WarlordsTrait {

    public ItemEnyaTrait() {
        super("Ethical Enya");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            ItemCraftingMenu.openItemCraftingMenu(player, databasePlayer);
        });
    }


}

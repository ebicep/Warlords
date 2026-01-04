package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.items.menu.ItemCraftingMenu;
import com.ebicep.warlords.pve.newitems.menu.NewItemRerollMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ItemEnyaTrait extends WarlordsTrait {

    public ItemEnyaTrait() {
        super("Ethical Enya");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        NewItemRerollMenu.open(player);
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GOLD + "Echelon Trader");
    }
}

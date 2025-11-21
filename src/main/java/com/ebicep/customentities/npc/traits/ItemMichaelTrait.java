package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.items.menu.ItemMichaelMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;

public class ItemMichaelTrait extends WarlordsTrait {

    public ItemMichaelTrait() {
        super("Mysterious Michael");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        ItemMichaelMenu.openMichaelItemMenu(player, databasePlayer);
    }

}

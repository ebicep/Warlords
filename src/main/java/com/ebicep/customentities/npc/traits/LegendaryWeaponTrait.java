package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.weapons.menu.WeaponLegendaryCraftMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;

public class LegendaryWeaponTrait extends WarlordsTrait {

    public LegendaryWeaponTrait() {
        super("LegendaryWeaponTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabasePlayer databasePlayer = DatabaseManager.getPlayer(player);
        WeaponLegendaryCraftMenu.openWeaponLegendaryCraftMenu(player, databasePlayer);
    }

}

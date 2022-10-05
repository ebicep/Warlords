package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.weapons.menu.WeaponLegendaryCraftMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class LegendaryWeaponTrait extends Trait {

    public LegendaryWeaponTrait() {
        super("LegendaryWeaponTrait");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            Player player = event.getClicker();
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                WeaponLegendaryCraftMenu.openWeaponLegendaryCraftMenu(player, databasePlayer);
            });
        }
    }

}

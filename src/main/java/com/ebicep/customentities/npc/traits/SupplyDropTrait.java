package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;

import static com.ebicep.warlords.pve.events.supplydrop.SupplyDropManager.openSupplyDropMenu;

public class SupplyDropTrait extends Trait {

    public SupplyDropTrait() {
        super("SupplyDropTrait");
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            openSupplyDropMenu(event.getClicker());
        }
    }

}

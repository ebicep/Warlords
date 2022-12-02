package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;

public class WeaponMangerTrait extends WarlordsTrait {

    public WeaponMangerTrait() {
        super("WeaponMangerTrait");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        WeaponManagerMenu.openWeaponInventoryFromExternal(player, true);
    }

}

package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.weapons.menu.TutorialGuideMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TutorialGuideTrait extends WarlordsTrait {

    public TutorialGuideTrait() {
        super("TutorialGuideTrait");
    }

    @Override
    public void onAttach() {
        updateHologram();
    }

    private void updateHologram() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "NEW PLAYER? CLICK ME!");
        hologramTrait.setLine(1, ChatColor.AQUA + "Tutorial Guide");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        TutorialGuideMenu.openMainMenu(player);
    }

}
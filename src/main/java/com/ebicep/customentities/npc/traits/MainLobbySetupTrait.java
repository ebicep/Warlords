package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.menu.generalmenu.MainLobbySetupMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MainLobbySetupTrait extends WarlordsTrait {

    public MainLobbySetupTrait() {
        super("MainLobbySetupTrait");
    }

    @Override
    public void onAttach() {
        updateHologram();
    }

    private void updateHologram() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "TEAM & CLASS SETUP");
        hologramTrait.setLine(1, ChatColor.AQUA + "Click Me!");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        if (!MainLobbySetupMenu.isInMainLobbyGame(player)) {
            player.sendMessage(Component.text("Enter the playing area first!", NamedTextColor.RED));
            return;
        }
        MainLobbySetupMenu.openSetupMenu(player);
    }

}

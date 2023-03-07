package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.OnslaughtMenu;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class OnslaughtStartTrait extends WarlordsTrait {

    public OnslaughtStartTrait() {
        super("OnslaughtStartTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.ONSLAUGHT) + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.ONSLAUGHT) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.RED + "Onslaught");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        OnslaughtMenu.openMenu(event.getClicker().getPlayer());
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        OnslaughtMenu.openMenu(event.getClicker().getPlayer());
    }
}

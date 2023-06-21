package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.pve.TreasureHuntMenu;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class TreasureHuntStartTrait extends WarlordsTrait {

    public TreasureHuntStartTrait() {
        super("TreasureHuntStartTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.TREASURE_HUNT) + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.TREASURE_HUNT) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cryptic Conquest");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        TreasureHuntMenu.openDifficultyMenu(event.getClicker());
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        TreasureHuntMenu.openDifficultyMenu(event.getClicker());
    }
}

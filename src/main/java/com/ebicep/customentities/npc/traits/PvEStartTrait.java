package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

import static com.ebicep.warlords.pve.DifficultyMenu.openDifficultyMenu;

public class PvEStartTrait extends WarlordsTrait {

    public PvEStartTrait() {
        super("PveStartTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.WAVE_DEFENSE) + " Players");
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.WAVE_DEFENSE) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.GOLD + ChatColor.BOLD.toString() + "Wave Defense");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
        hologramTrait.setLine(4,
                ChatColor.RED + ChatColor.MAGIC.toString() + "  " + ChatColor.RED + ChatColor.BOLD + " NEW EXTREME MODE! " + ChatColor.RED + ChatColor.MAGIC + "  "
        );
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        openDifficultyMenu(event.getClicker().getPlayer());
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        openDifficultyMenu(event.getClicker().getPlayer());
    }
}

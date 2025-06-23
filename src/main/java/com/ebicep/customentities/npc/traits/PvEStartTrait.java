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

    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public PvEStartTrait() {
        super("PveStartTrait");
    }

    @Override
    public void onAttach() {
        updateHologram(true);
    }

    @Override
    public void run() {
        if (ticks++ % 20 != 0) {
            return;
        }
        updateHologram(false);
    }

    private void updateHologram(boolean init) {
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.WAVE_DEFENSE);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.WAVE_DEFENSE);
        if (init || playerCount != lastPlayerCount || playerCountInLobby != lastPlayerCountInLobby) {
            lastPlayerCount = playerCount;
            lastPlayerCountInLobby = playerCountInLobby;
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerCount + " Players");
            hologramTrait.setLine(1, ChatColor.GRAY.toString() + playerCountInLobby + " in Lobby");
            if (init) {
                hologramTrait.setLine(2, ChatColor.GOLD + ChatColor.BOLD.toString() + "Wave Defense");
//                hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
            }
        }
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

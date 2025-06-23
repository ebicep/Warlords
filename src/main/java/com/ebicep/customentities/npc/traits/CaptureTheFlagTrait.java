package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameMode;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class CaptureTheFlagTrait extends WarlordsTrait {

    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public CaptureTheFlagTrait() {
        super("GameStartTrait");
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
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.CAPTURE_THE_FLAG);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.CAPTURE_THE_FLAG);
        if (init || playerCount != lastPlayerCount || playerCountInLobby != lastPlayerCountInLobby) {
            lastPlayerCount = playerCount;
            lastPlayerCountInLobby = playerCountInLobby;
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerCount + " Players");
            hologramTrait.setLine(1, ChatColor.GRAY.toString() + playerCountInLobby + " in Lobby");
            if (init) {
                hologramTrait.setLine(2, ChatColor.AQUA + ChatColor.BOLD.toString() + "Capture The Flag");
//                hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
            }
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        GameStartCommand.startGamePublic(event.getClicker(), GameMode.CAPTURE_THE_FLAG);
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        GameStartCommand.startGamePublic(event.getClicker(), GameMode.CAPTURE_THE_FLAG);
    }
}

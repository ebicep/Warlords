package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameMode;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class SiegeTrait extends WarlordsTrait {

    public SiegeTrait() {
        super("SiegeTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0,
                ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.SIEGE) + " Players"
        );
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.SIEGE) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.AQUA + ChatColor.BOLD.toString() + "Siege");
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        GameStartCommand.startGamePublic(event.getClicker(), GameMode.SIEGE);
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        GameStartCommand.startGamePublic(event.getClicker(), GameMode.SIEGE);
    }
}

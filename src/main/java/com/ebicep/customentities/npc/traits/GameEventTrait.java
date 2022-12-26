package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.events.pojos.DatabaseGameEvent;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameEventTrait extends WarlordsTrait {

    public GameEventTrait() {
        super("GameEventTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0,
                ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.EVENT_WAVE_DEFENSE) + " Players"
        );
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.EVENT_WAVE_DEFENSE) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.RED + DatabaseGameEvent.currentGameEvent.getEvent().name);
        hologramTrait.setLine(3, ChatColor.YELLOW + ChatColor.BOLD.toString() + "CLICK TO PLAY");
        String timeTill = DateUtil.getTimeTill(DatabaseGameEvent.currentGameEvent.getEndDate(),
                true,
                true,
                true,
                true
        );
        if (timeTill.equals("0 seconds")) {
            hologramTrait.setLine(4, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ended!");
        } else {
            hologramTrait.setLine(4, ChatColor.GOLD.toString() + ChatColor.BOLD + "Ends in " + timeTill);
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        DatabaseGameEvent.currentGameEvent.getEvent().openMenu(player);
    }
}

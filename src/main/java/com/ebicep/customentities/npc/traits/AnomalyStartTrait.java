package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyMenu;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyOption;
import com.ebicep.warlords.pve.OnslaughtMenu;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class AnomalyStartTrait extends WarlordsTrait {

    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public AnomalyStartTrait() {
        super("AnomalyStartTrait");
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
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.ONSLAUGHT);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.ONSLAUGHT);
        if (init || playerCount != lastPlayerCount || playerCountInLobby != lastPlayerCountInLobby) {
            lastPlayerCount = playerCount;
            lastPlayerCountInLobby = playerCountInLobby;
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerCount + " Players");
            hologramTrait.setLine(1, ChatColor.GRAY.toString() + playerCountInLobby + " in Lobby");
            if (init) {
                hologramTrait.setLine(2, ChatColor.GREEN + ChatColor.BOLD.toString() + "Anomaly");
                hologramTrait.setLine(3, ChatColor.GRAY + "CURRENT ANOMALY: " + ChatColor.GOLD + AnomalyOption.getDailyAnomaly().getName());
            }
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        AnomalyMenu.openAnomalyMenu(event.getClicker());
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        AnomalyMenu.openAnomalyMenu(event.getClicker());
    }
}

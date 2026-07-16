package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyMenu;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyRotation;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

public class AnomalyStartTrait extends WarlordsTrait {

    private int ticks;
    private long lastPlayerCount;
    private long lastPlayerCountInLobby;
    private long lastRotationHour = Long.MIN_VALUE;

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
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.ANOMALY);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.ANOMALY);
        long rotationHour = AnomalyRotation.getRotationStart().getEpochSecond();
        if (init || playerCount != lastPlayerCount || playerCountInLobby != lastPlayerCountInLobby || rotationHour != lastRotationHour) {
            lastPlayerCount = playerCount;
            lastPlayerCountInLobby = playerCountInLobby;
            lastRotationHour = rotationHour;
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + playerCount + " Players");
            hologramTrait.setLine(1, ChatColor.GRAY + String.valueOf(playerCountInLobby) + " in Lobby");
            hologramTrait.setLine(2, ChatColor.GREEN + ChatColor.BOLD.toString() + "Anomaly");
            hologramTrait.setLine(3, ChatColor.GRAY + "CURRENT ANOMALY: " + ChatColor.GOLD + AnomalyRotation.getCurrentAnomaly().getName());
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

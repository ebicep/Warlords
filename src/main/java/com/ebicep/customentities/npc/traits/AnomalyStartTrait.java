package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyMenu;
import com.ebicep.warlords.game.option.pve.anomaly.AnomalyRotation;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class AnomalyStartTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-anomaly");
    private int ticks;
    private long lastPlayerCount;
    private long lastPlayerCountInLobby;
    private long lastRotationHour = Long.MIN_VALUE;

    public AnomalyStartTrait() {
        super("AnomalyStartTrait");
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
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
        if (!init && playerCount == lastPlayerCount && playerCountInLobby == lastPlayerCountInLobby || rotationHour != lastRotationHour) {
            return;
        }
        lastPlayerCount = playerCount;
        lastPlayerCountInLobby = playerCountInLobby;
        lastRotationHour = rotationHour;
        labelHologram.update(
                npc,
                ComponentBuilder.create("CURRENT ANOMALY: ", NamedTextColor.RED).append(Component.text(AnomalyRotation.getCurrentAnomaly().getName(), NamedTextColor.GOLD))
                        .newLine("Anomaly", NamedTextColor.GREEN, TextDecoration.BOLD)
                        .newLine(playerCountInLobby + " in Lobby", NamedTextColor.GRAY)
                        .newLine(playerCount + " Players", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .build()
        );
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

package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class DarkDescentStartTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-dark-descent");
    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public DarkDescentStartTrait() {
        super("DarkDescentStartTrait");
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
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.DARK_DESCENT);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.DARK_DESCENT);
        if (!init && playerCount == lastPlayerCount && playerCountInLobby == lastPlayerCountInLobby) {
            return;
        }
        lastPlayerCount = playerCount;
        lastPlayerCountInLobby = playerCountInLobby;
        labelHologram.update(
                npc,
                ComponentBuilder.create("IN DEVELOPMENT", NamedTextColor.RED, TextDecoration.BOLD)
                        .newLine("Dark Descent", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                        .newLine(playerCountInLobby + " in Lobby", NamedTextColor.GRAY)
                        .newLine(playerCount + " Players", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .build()
        );
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage(Component.text("Dark Descent is currently in development, check back later!", NamedTextColor.RED));
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        event.getClicker().sendMessage(Component.text("Dark Descent is currently in development, check back later!", NamedTextColor.RED));
    }
}

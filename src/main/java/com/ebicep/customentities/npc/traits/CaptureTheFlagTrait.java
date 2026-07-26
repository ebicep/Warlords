package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.GameStartCommand;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class CaptureTheFlagTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-ctf");
    private int ticks = 0;
    private long lastPlayerCount = 0;
    private long lastPlayerCountInLobby = 0;

    public CaptureTheFlagTrait() {
        super("GameStartTrait");
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
        long playerCount = Warlords.getGameManager().getPlayerCount(GameMode.CAPTURE_THE_FLAG);
        long playerCountInLobby = Warlords.getGameManager().getPlayerCountInLobby(GameMode.CAPTURE_THE_FLAG);
        if (!init && playerCount == lastPlayerCount && playerCountInLobby == lastPlayerCountInLobby) {
            return;
        }
        lastPlayerCount = playerCount;
        lastPlayerCountInLobby = playerCountInLobby;
        labelHologram.update(
                npc,
                ComponentBuilder.create("Capture The Flag", NamedTextColor.AQUA, TextDecoration.BOLD)
                        .newLine(playerCountInLobby + " in Lobby", NamedTextColor.GRAY)
                        .newLine(playerCount + " Players", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .build()
        );
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

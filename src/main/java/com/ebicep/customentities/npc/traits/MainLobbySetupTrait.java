package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.menu.generalmenu.MainLobbySetupMenu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

public class MainLobbySetupTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-setup");

    public MainLobbySetupTrait() {
        super("MainLobbySetupTrait");
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
        labelHologram.update(
                npc,
                ComponentBuilder.create("Click Me!", NamedTextColor.AQUA)
                        .newLine("TEAM & CLASS SETUP", NamedTextColor.YELLOW, TextDecoration.BOLD)
                        .build()
        );
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        Player player = event.getClicker();
        if (!MainLobbySetupMenu.isInMainLobbyGame(player)) {
            player.sendMessage(Component.text("Enter the playing area first!", NamedTextColor.RED));
            return;
        }
        MainLobbySetupMenu.openSetupMenu(player);
    }

}

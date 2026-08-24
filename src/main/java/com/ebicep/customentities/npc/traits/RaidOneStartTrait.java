package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.game.option.pve.raid.Raid;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class RaidOneStartTrait extends WarlordsTrait implements HasNPCLabelHologram {

    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-raid-one-start");
    private final Raid raid;

    public RaidOneStartTrait() {
        super("RaidOneStartTrait");
        raid = Raid.REGNUM_OF_TWO_CROWNS;
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
        labelHologram.update(
                npc,
                ComponentBuilder.create(
                                "Two crowns claim the same throne. Every step is a lie, and every mistake is final.",
                                NamedTextColor.GRAY,
                                TextDecoration.ITALIC
                        )
                        .newLine("-ˋˏ ༻❁༺ ˎˊ-", NamedTextColor.DARK_GRAY, TextDecoration.BOLD)
                        .newLine("REGNUM OF TWO CROWNS", NamedTextColor.GOLD, TextDecoration.BOLD)
                        .build()
        );
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage(Component.text("This raid is currently in development, check back later!", NamedTextColor.RED));
        //RaidMenu.openRaidMenu(event.getClicker(), raid);
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        event.getClicker().sendMessage(Component.text("This raid is currently in development, check back later!", NamedTextColor.RED));
        //RaidMenu.openRaidMenu(event.getClicker(), raid);
    }
}

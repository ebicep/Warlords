package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.game.option.pve.raid.Raid;
import com.ebicep.warlords.game.option.pve.raid.RaidMenu;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

public class RaidOneStartTrait extends WarlordsTrait {

    private final Raid raid;

    public RaidOneStartTrait() {
        super("RaidOneStartTrait");
        raid = Raid.REGNUM_OF_TWO_CROWNS;
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GRAY + ChatColor.ITALIC.toString() + "Two crowns claim the same throne. Every step is a lie, and every mistake is final.");
        hologramTrait.setLine(1, ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "-ˋˏ ༻❁༺ ˎˊ-");
        hologramTrait.setLine(2, ChatColor.GOLD + ChatColor.BOLD.toString() + "REGNUM OF TWO CROWNS");
        hologramTrait.setMargin(1, "bottom", 0.6);
        hologramTrait.setMargin(2, "bottom", 0.2);
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

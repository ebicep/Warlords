package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.effects.EffectUtils;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

public class RaidStartTrait extends WarlordsTrait {

    public RaidStartTrait() {
        super("RaidStartTrait");
    }

    @Override
    public void onAttach() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.GRAY + ChatColor.ITALIC.toString() + "Two crowns claim the same throne. Every step is a lie, and every mistake is final.");
        hologramTrait.setLine(1, ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "-ˋˏ ༻❁༺ ˎˊ-");
        hologramTrait.setLine(2, ChatColor.GOLD + ChatColor.BOLD.toString() + "REGNUM OF TWO CROWNS");
        hologramTrait.setMargin(1, "bottom", 0.5);
        hologramTrait.setMargin(2, "bottom", 0.2);
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        event.getClicker().sendMessage(Component.text("Nine crowns lost to time's decline, Nine kings bound in dust and spine. When the stars in crimson line, You'll face your end before The Nine.", NamedTextColor.RED));
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        event.getClicker().sendMessage(Component.text("Nine crowns lost to time's decline, Nine kings bound in dust and spine. When the stars in crimson line, You'll face your end before The Nine.", NamedTextColor.RED));
    }
}

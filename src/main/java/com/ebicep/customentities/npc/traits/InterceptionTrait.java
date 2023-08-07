package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMode;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;

public class InterceptionTrait extends WarlordsTrait {

    public InterceptionTrait() {
        super("InterceptionTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0,
                ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.INTERCEPTION) + " Players"
        );
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.INTERCEPTION) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.BLUE + ChatColor.BOLD.toString() + "Domination");
        hologramTrait.setLine(3, ChatColor.RED + ChatColor.BOLD.toString() + "IN DEVELOPMENT");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        //GameStartCommand.startGamePublic(event.getClicker(), GameMode.INTERCEPTION);
        event.getClicker().getPlayer().sendMessage(Component.text("Domination is currently in development, check back later!", NamedTextColor.RED));
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        //GameStartCommand.startGamePublic(event.getClicker(), GameMode.INTERCEPTION);
        event.getClicker().getPlayer().sendMessage(Component.text("Domination is currently in development, check back later!", NamedTextColor.RED));
    }
}

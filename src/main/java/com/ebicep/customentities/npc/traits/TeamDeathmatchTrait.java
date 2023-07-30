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

public class TeamDeathmatchTrait extends WarlordsTrait {

    public TeamDeathmatchTrait() {
        super("TeamDeathmatchTrait");
    }

    @Override
    public void run() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0,
                ChatColor.YELLOW.toString() + ChatColor.BOLD + Warlords.getGameManager().getPlayerCount(GameMode.TEAM_DEATHMATCH) + " Players"
        );
        hologramTrait.setLine(1, ChatColor.GRAY.toString() + Warlords.getGameManager().getPlayerCountInLobby(GameMode.TEAM_DEATHMATCH) + " in Lobby");
        hologramTrait.setLine(2, ChatColor.GRAY + ChatColor.BOLD.toString() + "Team Deathmatch");
        hologramTrait.setLine(3, ChatColor.RED + ChatColor.BOLD.toString() + "IN DEVELOPMENT");
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        //GameStartCommand.startGamePublic(event.getClicker(), GameMode.TEAM_DEATHMATCH);
        event.getClicker().getPlayer().sendMessage(Component.text("Team Deathmatch is currently in development, check back later!", NamedTextColor.RED));
    }

    @Override
    public void leftClick(NPCLeftClickEvent event) {
        //GameStartCommand.startGamePublic(event.getClicker(), GameMode.TEAM_DEATHMATCH);
        event.getClicker().getPlayer().sendMessage(Component.text("Team Deathmatch is currently in development, check back later!", NamedTextColor.RED));
    }
}

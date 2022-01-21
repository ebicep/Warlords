package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.customentities.npc.NPCManager;
import com.ebicep.customentities.npc.traits.GameStartTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.maps.state.PreLobbyState;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        if (Warlords.game.getPlayers().containsKey(player.getUniqueId()) && Warlords.game.getState() instanceof PreLobbyState) {
            if (Warlords.game.isPrivate()) {
                player.sendMessage(ChatColor.RED + "You cannot leave private games!");
            } else {
                if (NPCManager.gameStartNPC.hasTrait(GameStartTrait.class)) {
                    NPCManager.gameStartNPC.getTraitNullable(GameStartTrait.class).removePlayer(player);
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "This command can only be used in a game lobby!");
        }

        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("lobby").setExecutor(this);
    }

}

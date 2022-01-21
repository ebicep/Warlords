package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.party.Party;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class BotCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.bot")) {
            sender.sendMessage(ChatColor.RED + "Insufficient Permissions!");
            return true;
        }

        Optional<TextChannel> botTeams = BotManager.getTextChannelCompsByName("bot-teams");
        Optional<TextChannel> gsTeams = BotManager.getTextChannelCompsByName("gs-teams");
        if (!botTeams.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Could not find bot-teams!");
            return true;
        }
        if (args.length <= 0) {
            sender.sendMessage(ChatColor.RED + "Invalid Arguments!");
            return true;
        }
        String input = args[0];
        switch (input.toLowerCase()) {
            case "balance":
            case "balance2":
            case "experimental":
            case "experimental2":
            case "experimental3":
                Player player = (Player) sender;
                Optional<Party> currentParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
                if (!currentParty.isPresent()) {
                    sender.sendMessage(ChatColor.RED + "You are not in a party!");
                    return true;
                }
                StringBuilder players = new StringBuilder();
                currentParty.get().getPartyPlayers().forEach(partyPlayer -> {
                    players.append(Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName()).append(",");
                });
                players.setLength(players.length() - 1);
                botTeams.get().sendMessage("/" + input + " " + players).queue();
                sender.sendMessage(ChatColor.GREEN + "Balanced party in bot-teams!");
                return true;
            case "inputgame":
            case "inputexperimental":
                if (!sender.hasPermission("warlords.game.bot.inputgames")) {
                    sender.sendMessage(ChatColor.RED + "Insufficient Permissions!");
                    return true;
                }
                if(args.length == 1) {
                    BotManager.getTextChannelCompsByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage("/" + input + " " + DatabaseManager.lastWarlordsPlusString).queue());
                    sender.sendMessage(ChatColor.GREEN + "Inputted game!");
                    return true;
                }
                if(!args[1].contains("png")) {
                    sender.sendMessage(ChatColor.RED + "Invalid Image!");
                    return true;
                }
                BotManager.getTextChannelCompsByName("games-backlog").ifPresent(textChannel -> textChannel.sendMessage("/" + input + " " + DatabaseManager.lastWarlordsPlusString + " " + args[1]).queue());
                return true;
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("bot").setExecutor(this);
    }
}

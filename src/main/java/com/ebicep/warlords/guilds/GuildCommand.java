package com.ebicep.warlords.guilds;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuildCommand implements TabExecutor {

    private static final String[] guildOptions = {
            "create"
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        Player player = BaseCommand.requirePlayer(sender);

        if (player == null) {
            return true;
        }

        if (args.length == 0) {
            ChatUtils.sendMessageToPlayer(player, ChatColor.GOLD + "Guild Commands: \n" +
                            ChatColor.YELLOW + "/p create" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Creates a guild" + "\n" +
                            ChatColor.YELLOW + "/p invite <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Invites another player to your guild" + "\n" +
                            ChatColor.YELLOW + "/p list" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Lists the players in your current guild" + "\n" +
                            ChatColor.YELLOW + "/p leave" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Leaves your current guild" + "\n" +
                            ChatColor.YELLOW + "/p disband" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Disbands the guild" + "\n" +
                            ChatColor.YELLOW + "/p kick <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Removes a player from your guild" + "\n" +
                            ChatColor.YELLOW + "/p transfer <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Transfers ownership of the guild to a player" + "\n" +
                            ChatColor.YELLOW + "/p promote <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Promotes a player in the guild" + "\n" +
                            ChatColor.YELLOW + "/p mute " + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Mutes the guild" + "\n" +
                            ChatColor.YELLOW + "/p unmute " + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Unmutes the guild" + "\n"
                    ,
                    ChatColor.GREEN, false);
            return true;
        }

        String option = args[0];

        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);

        //Commands that require a guild
        switch (option) {
            case "list":
            case "invite":
            case "mute":
            case "unmute":
            case "disband":
            case "leave":
            case "transfer":
            case "kick":
            case "promote":
            case "demote": {
                if (guildPlayerPair == null) {
                    ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You are not in a guild.", ChatColor.GREEN, true);
                    return true;
                }
                GuildPlayer guildPlayer = guildPlayerPair.getB();
                Guild guild = guildPlayerPair.getA();
                switch (option) {
                    case "list": {
                        ChatUtils.sendCenteredMessage(player, guild.getList());
                        break;
                    }
                    case "invite": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.INVITE)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to invite players to your guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild invite <player>", ChatColor.GREEN, true);
                            return true;
                        }
                        String playerNameToInvite = args[1];
                        Player playerToInvite = Bukkit.getPlayer(playerNameToInvite);
                        if (playerToInvite == null) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Player " + playerNameToInvite + " is not online.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (playerToInvite.getUniqueId().equals(player.getUniqueId())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You cannot invite yourself to your own guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        GuildManager.addInvite(player, playerToInvite.getPlayer(), guild);

                        break;
                    }
                    case "mute": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to mute your guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (guild.isMuted()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "The guild is already muted.", ChatColor.GREEN, true);
                            return true;
                        }
                        guild.setMuted(true);
                        break;
                    }
                    case "unmute": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to unmute your guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (!guild.isMuted()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "The guild is already unmuted.", ChatColor.GREEN, true);
                            return true;
                        }
                        guild.setMuted(false);
                        break;
                    }
                    case "disband": {
                        if (!guild.getCurrentMaster().equals(player.getUniqueId())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You must be the Guild Master to disband the guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        String guildName = guild.getName();
                        SignGUI.open(player, new String[]{"", guildName, "Type your guild", "name to confirm"}, (p, lines) -> {
                            String confirmation = lines[0];
                            if (confirmation.equals(guildName)) {
                                guild.disband();
                            } else {
                                ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild was not disbanded because your input did not match your guild name.", ChatColor.GREEN, true);
                            }
                        });
                        break;
                    }
                    case "leave": {
                        if (guild.getCurrentMaster().equals(player.getUniqueId())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You can only leave through disbanding or transferring the guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        guild.leave(player);
                        break;
                    }
                    case "transfer": {
                        if (!guild.getCurrentMaster().equals(player.getUniqueId())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You must be the Guild Master to transfer the guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild transfer <player>", ChatColor.GREEN, true);
                            return true;
                        }
                        String playerNameToTransfer = args[1];
                        Optional<GuildPlayer> playerToTransfer = guild.getPlayerMatchingName(playerNameToTransfer);
                        if (!playerToTransfer.isPresent()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Could not find " + playerNameToTransfer + " in your guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        if (playerToTransfer.get().equals(guildPlayer)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You are already the guild master.", ChatColor.GREEN, true);
                            return true;
                        }
                        SignGUI.open(player, new String[]{"", "Type CONFIRM", "Exiting will read", "current text!"}, (p, lines) -> {
                            String confirmation = lines[0];
                            if (confirmation.equals("CONFIRM")) {
                                guild.transfer(playerToTransfer.get());
                            } else {
                                ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild was not transferred because you did not input CONFIRM", ChatColor.GREEN, true);
                            }
                        });
                        break;
                    }
                    case "kick": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.KICK)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to kick players from the guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild kick <player>", ChatColor.GREEN, true);
                            return true;
                        }
                        String playerNameToKick = args[1];
                        Optional<GuildPlayer> playerToKick = guild.getPlayerMatchingName(playerNameToKick);
                        if (!playerToKick.isPresent()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Could not find " + playerToKick + " in your guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        if (playerToKick.get().equals(guildPlayer)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You cannot kick yourself from your own guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (guild.getRoleLevel(guildPlayer) >= guild.getRoleLevel(playerToKick.get())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You can only kick players with a lower rank than you!", ChatColor.GREEN, true);
                            return true;
                        }
                        guild.kick(playerToKick.get());
                        Player kickedPlayer = Bukkit.getPlayer(playerToKick.get().getUUID());
                        if (kickedPlayer != null) {
                            ChatUtils.sendMessageToPlayer(kickedPlayer, ChatColor.RED + "You were kicked from the guild!", ChatColor.GREEN, true);
                        }

                        break;
                    }
                    case "promote":
                    case "demote": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.CHANGE_ROLE)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You do not have permission to " + option + " players in the guild.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild " + option + " <player>", ChatColor.GREEN, true);
                            return true;
                        }
                        String playerNameToChange = args[1];
                        Optional<GuildPlayer> playerToChange = guild.getPlayerMatchingName(playerNameToChange);
                        if (!playerToChange.isPresent()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Could not find " + playerToChange + " in your guild!", ChatColor.GREEN, true);
                            return true;
                        }
                        if (playerToChange.get().equals(guildPlayer)) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You cannot " + playerToChange + " yourself.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (guild.getRoleLevel(guildPlayer) >= guild.getRoleLevel(playerToChange.get())) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You can only " + option + "  players with a lower rank than you!", ChatColor.GREEN, true);
                            return true;
                        }
                        if (option.equalsIgnoreCase("promote")) {
                            if (guild.getRoleLevel(guildPlayer) + 1 == guild.getRoleLevel(playerToChange.get())) {
                                ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You cannot promote " + playerToChange.get().getName() + " any higher!", ChatColor.GREEN, true);
                                return true;
                            }
                            guild.promote(playerToChange.get());
                        } else {
                            if (guild.getRoles().get(guild.getRoles().size() - 1).getPlayers().contains(playerToChange.get().getUUID())) {
                                ChatUtils.sendMessageToPlayer(player, ChatColor.RED + playerToChange.get().getName() + " already has the lower role!", ChatColor.GREEN, true);
                                return true;
                            }
                            guild.demote(playerToChange.get());
                        }
                        break;
                    }
                }
            }
        }

        //Commands that does not need a guild
        switch (option) {
            case "create":
            case "join": {
                if (guildPlayerPair != null) {
                    ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "You are already in a guild.", ChatColor.GREEN, true);
                    return true;
                }
                switch (option) {
                    case "create": {
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild create <guild name>", ChatColor.GREEN, true);
                            return true;
                        }

                        StringBuilder guildName = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            guildName.append(args[i]).append(" ");
                        }
                        guildName.setLength(guildName.length() - 1);
                        if (guildName.length() > 15) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild name cannot be longer than 15 characters.", ChatColor.GREEN, true);
                            return true;
                        }
                        //check if name has special characters
                        if (!guildName.toString().matches("[a-zA-Z0-9 ]+")) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild name cannot contain special characters.", ChatColor.GREEN, true);
                            return true;
                        }

                        Guild newGuild = new Guild(player, guildName.toString());
                        boolean addedSuccessfully = GuildManager.addGuild(newGuild);
                        if (addedSuccessfully) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.GREEN + "You created guild " + ChatColor.GOLD + guildName, ChatColor.GREEN, true);
                        } else {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "There is already a guild with that name.", ChatColor.GREEN, true);
                        }
                        break;
                    }

                    case "join": {
                        if (args.length < 2) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Usage: /guild join <guild name>", ChatColor.GREEN, true);
                            return true;
                        }
                        StringBuilder guildName = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            guildName.append(args[i]).append(" ");
                        }
                        guildName.setLength(guildName.length() - 1);
                        Optional<Guild> optionalGuild = GuildManager.getGuildFromName(guildName.toString());
                        if (!optionalGuild.isPresent()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild " + guildName + " does not exist.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (!optionalGuild.get().isOpen() && !GuildManager.getGuildFromInvite(player, guildName.toString()).isPresent()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild " + guildName + " is not open, and you are not invited to it.", ChatColor.GREEN, true);
                            return true;
                        }
                        if (optionalGuild.get().getPlayers().size() >= optionalGuild.get().getPlayerLimit()) {
                            ChatUtils.sendMessageToPlayer(player, ChatColor.RED + "Guild " + guildName + " is full.", ChatColor.GREEN, true);
                            return true;
                        }
                        optionalGuild.get().join(player);
                        break;
                    }
                }
            }
        }


        switch (option) {


        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }

    public void register(Warlords instance) {
        instance.getCommand("guild").setExecutor(this);
        instance.getCommand("guild").setTabCompleter(this);
    }
}

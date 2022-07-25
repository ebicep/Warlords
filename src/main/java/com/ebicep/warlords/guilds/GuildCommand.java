package com.ebicep.warlords.guilds;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.Pair;
import org.apache.commons.lang.StringUtils;
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
            Guild.sendGuildMessage(player, ChatColor.GOLD + "Guild Commands: \n" +
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
            );
            return true;
        }

        String option = args[0];

        Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);

        //Commands that require a guild
        switch (option) {
            case "menu":
            case "list":
            case "invite":
            case "mute":
            case "unmute":
            case "disband":
            case "leave":
            case "transfer":
            case "kick":
            case "promote":
            case "demote":
            case "rename": {
                if (guildPlayerPair == null) {
                    Guild.sendGuildMessage(player, ChatColor.RED + "You are not in a guild.");
                    return true;
                }
                GuildPlayer guildPlayer = guildPlayerPair.getB();
                Guild guild = guildPlayerPair.getA();
                switch (option) {
                    case "menu": {
                        GuildMenu.openGuildMenu(guild, player);
                        break;
                    }
                    case "list": {
                        ChatUtils.sendCenteredMessage(player, guild.getList());
                        break;
                    }
                    case "invite": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.INVITE)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to invite players to your guild.");
                            return true;
                        }
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild invite <player>");
                            return true;
                        }
                        String playerNameToInvite = args[1];
                        Player playerToInvite = Bukkit.getPlayer(playerNameToInvite);
                        if (playerToInvite == null) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Player " + playerNameToInvite + " is not online.");
                            return true;
                        }
                        if (playerToInvite.getUniqueId().equals(player.getUniqueId())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot invite yourself to your own guild.");
                            return true;
                        }
                        GuildManager.addInvite(player, playerToInvite.getPlayer(), guild);

                        break;
                    }
                    case "mute": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to mute your guild.");
                            return true;
                        }
                        if (guild.isMuted()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "The guild is already muted.");
                            return true;
                        }
                        guild.setMuted(true);
                        break;
                    }
                    case "unmute": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to unmute your guild.");
                            return true;
                        }
                        if (!guild.isMuted()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "The guild is already unmuted.");
                            return true;
                        }
                        guild.setMuted(false);
                        break;
                    }
                    case "disband": {
                        if (!guild.getCurrentMaster().equals(player.getUniqueId())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You must be the Guild Master to disband the guild!");
                            return true;
                        }
                        String guildName = guild.getName();
                        SignGUI.open(player, new String[]{"", guildName, "Type your guild", "name to confirm"}, (p, lines) -> {
                            String confirmation = lines[0];
                            if (confirmation.equals(guildName)) {
                                guild.disband();
                            } else {
                                Guild.sendGuildMessage(player, ChatColor.RED + "Guild was not disbanded because your input did not match your guild name.");
                            }
                        });
                        break;
                    }
                    case "leave": {
                        if (guild.getCurrentMaster().equals(player.getUniqueId())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You can only leave through disbanding or transferring the guild!");
                            return true;
                        }
                        guild.leave(player);
                        break;
                    }
                    case "transfer": {
                        if (!guild.getCurrentMaster().equals(player.getUniqueId())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You must be the Guild Master to transfer the guild!");
                            return true;
                        }
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild transfer <player>");
                            return true;
                        }
                        String playerNameToTransfer = args[1];
                        Optional<GuildPlayer> playerToTransfer = guild.getPlayerMatchingName(playerNameToTransfer);
                        if (!playerToTransfer.isPresent()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Could not find " + playerNameToTransfer + " in your guild!");
                            return true;
                        }
                        if (playerToTransfer.get().equals(guildPlayer)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You are already the guild master.");
                            return true;
                        }
                        SignGUI.open(player, new String[]{"", "Type CONFIRM", "Exiting will read", "current text!"}, (p, lines) -> {
                            String confirmation = lines[0];
                            if (confirmation.equals("CONFIRM")) {
                                guild.transfer(playerToTransfer.get());
                            } else {
                                Guild.sendGuildMessage(player, ChatColor.RED + "Guild was not transferred because you did not input CONFIRM");
                            }
                        });
                        break;
                    }
                    case "kick": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.KICK)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to kick players from the guild.");
                            return true;
                        }
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild kick <player>");
                            return true;
                        }
                        String playerNameToKick = args[1];
                        Optional<GuildPlayer> playerToKick = guild.getPlayerMatchingName(playerNameToKick);
                        if (!playerToKick.isPresent()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Could not find " + playerToKick + " in your guild!");
                            return true;
                        }
                        if (playerToKick.get().equals(guildPlayer)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot kick yourself from your own guild.");
                            return true;
                        }
                        if (guild.getRoleLevel(guildPlayer) >= guild.getRoleLevel(playerToKick.get())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You can only kick players with a lower rank than you!");
                            return true;
                        }
                        guild.kick(playerToKick.get());
                        Player kickedPlayer = Bukkit.getPlayer(playerToKick.get().getUUID());
                        if (kickedPlayer != null) {
                            Guild.sendGuildMessage(kickedPlayer, ChatColor.RED + "You were kicked from the guild!");
                        }

                        break;
                    }
                    case "promote":
                    case "demote": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.CHANGE_ROLE)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to " + option + " players in the guild.");
                            return true;
                        }
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild " + option + " <player>");
                            return true;
                        }
                        String playerNameToChange = args[1];
                        Optional<GuildPlayer> playerToChange = guild.getPlayerMatchingName(playerNameToChange);
                        if (!playerToChange.isPresent()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Could not find " + playerToChange + " in your guild!");
                            return true;
                        }
                        if (playerToChange.get().equals(guildPlayer)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot " + playerToChange + " yourself.");
                            return true;
                        }
                        if (guild.getRoleLevel(guildPlayer) >= guild.getRoleLevel(playerToChange.get())) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You can only " + option + "  players with a lower rank than you!");
                            return true;
                        }
                        if (option.equalsIgnoreCase("promote")) {
                            if (guild.getRoleLevel(guildPlayer) + 1 == guild.getRoleLevel(playerToChange.get())) {
                                Guild.sendGuildMessage(player, ChatColor.RED + "You cannot promote " + playerToChange.get().getName() + " any higher!");
                                return true;
                            }
                            guild.promote(playerToChange.get());
                        } else {
                            if (guild.getRoles().get(guild.getRoles().size() - 1).getPlayers().contains(playerToChange.get().getUUID())) {
                                Guild.sendGuildMessage(player, ChatColor.RED + playerToChange.get().getName() + " already has the lower role!");
                                return true;
                            }
                            guild.demote(playerToChange.get());
                        }
                        break;
                    }
                    case "rename": {
                        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.CHANGE_NAME)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to rename the guild.");
                            return true;
                        }
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild rename <name>");
                            return true;
                        }
                        String newName = StringUtils.join(args, " ", 1, args.length);
                        if (newName.length() > 15) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot be longer than 15 characters.");
                            return true;
                        }
                        //check if name has special characters
                        if (!newName.matches("[a-zA-Z0-9 ]+")) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot contain special characters.");
                            return true;
                        }
                        if (GuildManager.existingGuildWithName(newName)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "A guild with that name already exists.");
                            return true;
                        }
                        guild.setName(newName);
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
                    Guild.sendGuildMessage(player, ChatColor.RED + "You are already in a guild.");
                    return true;
                }
                switch (option) {
                    case "create": {
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild create <guild name>");
                            return true;
                        }

                        String guildName = StringUtils.join(args, " ", 1, args.length);
                        if (guildName.length() > 15) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot be longer than 15 characters.");
                            return true;
                        }
                        //check if name has special characters
                        if (!guildName.matches("[a-zA-Z0-9 ]+")) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot contain special characters.");
                            return true;
                        }
                        if (GuildManager.existingGuildWithName(guildName)) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "A guild with that name already exists.");
                            return true;
                        }
                        GuildManager.addGuild(new Guild(player, guildName));
                        Guild.sendGuildMessage(player, ChatColor.GREEN + "You created guild " + ChatColor.GOLD + guildName);
                        break;
                    }
                    case "join": {
                        if (args.length < 2) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Usage: /guild join <guild name>");
                            return true;
                        }
                        String guildName = StringUtils.join(args, " ", 1, args.length);
                        Optional<Guild> optionalGuild = GuildManager.getGuildFromName(guildName);
                        if (!optionalGuild.isPresent()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " does not exist.");
                            return true;
                        }
                        if (!optionalGuild.get().isOpen() && !GuildManager.getGuildFromInvite(player, guildName).isPresent()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " is not open, and you are not invited to it.");
                            return true;
                        }
                        if (optionalGuild.get().getPlayers().size() >= optionalGuild.get().getPlayerLimit()) {
                            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " is full.");
                            return true;
                        }
                        optionalGuild.get().join(player);
                        break;
                    }
                }
            }
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

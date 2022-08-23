package com.ebicep.warlords.guilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.logs.AbstractGuildLog;
import com.ebicep.warlords.guilds.menu.GuildMenu;
import com.ebicep.warlords.util.bukkit.signgui.SignGUI;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;


@CommandAlias("guild|g")
@Conditions("database:guild|database:player")
public class GuildCommand extends BaseCommand {

    @Subcommand("leaderboard")
    public class GuildLeaderboardCommand extends BaseCommand {

        @Subcommand("experience|EXP|exp")
        public void experience(CommandIssuer issuer) {
            issuer.sendMessage(GuildLeaderboardManager.getLeaderboardList(GuildLeaderboardManager.DAILY_EXP, "Experience"));
        }

        @Subcommand("coins")
        public void coins(CommandIssuer issuer) {
            issuer.sendMessage(GuildLeaderboardManager.getLeaderboardList(GuildLeaderboardManager.DAILY_COINS, "Coins"));
        }

        @Subcommand("refresh")
        @CommandPermission("warlords.leaderboard.interaction")
        public void refresh(CommandIssuer issuer) {
            GuildLeaderboardManager.recalculateLeaderboards();
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Recalculated Guild Leaderboards", true);
        }

    }

    @Subcommand("create")
    @Description("Creates a guild")
    public void create(@Conditions("guild:false") Player player, String guildName) {
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        if (!Guild.CAN_CREATE.test(databasePlayer)) {
            player.sendMessage(ChatColor.RED + "You do not meet the requirements to create a guild.");
            return;
        }
        if (guildName.length() > 15) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot be longer than 15 characters.");
            return;
        }
        //check if name has special characters
        if (!guildName.matches("[a-zA-Z0-9 ]+")) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot contain special characters.");
            return;
        }
        if (GuildManager.existingGuildWithName(guildName)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "A guild with that name already exists.");
            return;
        }
        GuildManager.addGuild(new Guild(player, guildName));
        Guild.sendGuildMessage(player, ChatColor.GREEN + "You created guild " + ChatColor.GOLD + guildName);
    }

    @Subcommand("join")
    @CommandCompletion("@guildnames")
    @Description("Joins a guild")
    public void join(@Conditions("guild:false") Player player, String guildName) {
        Optional<Guild> optionalGuild = GuildManager.getGuildFromName(guildName);
        if (!optionalGuild.isPresent()) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " does not exist.");
            return;
        }
        if (!optionalGuild.get().isOpen() && !GuildManager.getGuildFromInvite(player, guildName).isPresent()) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " is not open or you are not invited to it.");
            return;
        }
        if (optionalGuild.get().getPlayers().size() >= optionalGuild.get().getPlayerLimit()) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild " + guildName + " is full.");
            return;
        }
        optionalGuild.get().join(player);
    }

    @Subcommand("menu")
    @Description("Opens the guild menu")
    public void menu(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper) {
        GuildMenu.openGuildMenu(guildPlayerWrapper.getGuild(), player, 1);
    }

    @CommandAlias("gl")
    @Subcommand("list")
    @Description("Prints your guild list")
    public void list(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper) {
        ChatUtils.sendCenteredMessage(player, guildPlayerWrapper.getGuild().getList());
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    @Description("Invites a player to your guild")
    public void invite(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=INVITE") GuildPlayerWrapper guildPlayerWrapper, @Flags("other") Player target) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.getUniqueId().equals(player.getUniqueId())) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot invite yourself to your own guild.");
            return;
        }
        if (guild.getPlayerMatchingUUID(target.getUniqueId()).isPresent()) {
            Guild.sendGuildMessage(player, ChatColor.RED + "That player is already in your guild.");
            return;
        }
        if (GuildManager.hasInviteFromGuild(target, guild)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "That player has already been invited to your guild.");
            return;
        }
        GuildManager.addInvite(player, target, guild);
        Guild.sendGuildMessage(player,
                ChatColor.YELLOW + "You invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to the guild!\n" +
                        ChatColor.YELLOW + "They have" + ChatColor.RED + " 5 " + ChatColor.YELLOW + "minutes to accept!"
        );
    }

    @Subcommand("mute")
    @Description("Mutes the guild")
    public void mute(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=MUTE") GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You do not have permission to mute your guild.");
            return;
        }
        if (guild.isMuted()) {
            Guild.sendGuildMessage(player, ChatColor.RED + "The guild is already muted.");
            return;
        }
        guild.setMuted(true);
    }

    @Subcommand("disband")
    @Description("Disbands your guild")
    public void disband(@Conditions("guild:true") Player player, @Flags("master") GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        String guildName = guild.getName();
        SignGUI.open(player, new String[]{"", guildName, "Type your guild", "name to confirm"}, (p, lines) -> {
            String confirmation = lines[0];
            if (confirmation.equals(guildName)) {
                guild.disband();
            } else {
                Guild.sendGuildMessage(player, ChatColor.RED + "Guild was not disbanded because your input did not match your guild name.");
            }
        });
    }

    @Subcommand("leave")
    @Description("Leaves your guild")
    public void leave(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getCurrentMaster().equals(player.getUniqueId())) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You can only leave through disbanding or transferring the guild!");
            return;
        }
        guild.leave(player);
    }

    @Subcommand("transfer")
    @CommandCompletion("@guildmembers")
    @Description("Transfers ownership of your guild")
    public void transfer(@Conditions("guild:true") Player player, @Flags("master") GuildPlayerWrapper guildPlayerWrapper, GuildPlayer target) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.equals(guildPlayer)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You are already the guild master.");
            return;
        }
        SignGUI.open(player, new String[]{"", "Type CONFIRM", "Exiting will read", "current text!"}, (p, lines) -> {
            String confirmation = lines[0];
            if (confirmation.equals("CONFIRM")) {
                guild.transfer(target);
            } else {
                Guild.sendGuildMessage(player, ChatColor.RED + "Guild was not transferred because you did not input CONFIRM");
            }
        });
    }

    @Subcommand("kick|remove")
    @CommandCompletion("@guildmembers")
    @Description("Kicks a player from your guild")
    public void kick(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=KICK") GuildPlayerWrapper guildPlayerWrapper, @Conditions("lowerRank") GuildPlayer target) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.equals(guildPlayer)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot kick yourself from your own guild.");
            return;
        }

        guild.kick(guildPlayer, target);
        Player kickedPlayer = Bukkit.getPlayer(target.getUUID());
        if (kickedPlayer != null) {
            Guild.sendGuildMessage(kickedPlayer, ChatColor.RED + "You were kicked from the guild!");
        }
    }

    @Subcommand("promote")
    @CommandCompletion("@guildmembers")
    @Description("Promotes a player in your guild")
    public void promote(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=CHANGE_ROLE") GuildPlayerWrapper guildPlayerWrapper, @Conditions("lowerRank") GuildPlayer target) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getRoleLevel(guildPlayer) + 1 == guild.getRoleLevel(target)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "You cannot promote " + ChatColor.AQUA + target.getName() + ChatColor.RED + " any higher!");
            return;
        }
        guild.promote(guildPlayer, target);
    }

    @Subcommand("demote")
    @CommandCompletion("@guildmembers")
    @Description("Demotes a player in your guild")
    public void demote(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=CHANGE_ROLE") GuildPlayerWrapper guildPlayerWrapper, @Conditions("lowerRank") GuildPlayer target) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getRoles().get(guild.getRoles().size() - 1).getPlayers().contains(target.getUUID())) {
            Guild.sendGuildMessage(player, ChatColor.AQUA + target.getName() + ChatColor.RED + " already has the lowest role!");
            return;
        }
        guild.demote(guildPlayer, target);
    }

    @Subcommand("rename")
    @Description("Renames your guild")
    public void rename(@Conditions("guild:true") Player player, @Conditions("requirePerm:perm=CHANGE_NAME") GuildPlayerWrapper guildPlayerWrapper, String newName) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (newName.length() > 15) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot be longer than 15 characters.");
            return;
        }
        //check if name has special characters
        if (!newName.matches("[a-zA-Z0-9 ]+")) {
            Guild.sendGuildMessage(player, ChatColor.RED + "Guild name cannot contain special characters.");
            return;
        }
        if (GuildManager.existingGuildWithName(newName)) {
            Guild.sendGuildMessage(player, ChatColor.RED + "A guild with that name already exists.");
            return;
        }
        guild.setName(newName);
    }

    @Subcommand("log")
    @Description("View the audit log of your guild")
    public void log(@Conditions("guild:true") Player player, @Flags("master") GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        ChatUtils.sendMessageToPlayer(
                player,
                guild.getAuditLog().stream()
                        .map(AbstractGuildLog::getFormattedLog)
                        .collect(Collectors.joining("\n")),
                ChatColor.GREEN,
                false
        );
    }

    @Subcommand("open")
    @Description("Opens your guild so not invites are required to join")
    public void open(@Conditions("guild:true") Player player, @Flags("master") GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        guild.setOpen(true);
    }

    @Subcommand("close")
    @Description("Closes your guild so an invite is required to join")
    public void close(@Conditions("guild:true") Player player, @Flags("master") GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        guild.setOpen(false);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

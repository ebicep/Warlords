package com.ebicep.warlords.guilds.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.guilds.GuildLeaderboardManager;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.guilds.*;
import com.ebicep.warlords.guilds.menu.GuildMenu;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import de.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@CommandAlias("guild|g")
@Conditions("database:guild|database:player")
public class GuildCommand extends BaseCommand {

    @Subcommand("create")
    @Description("Creates a guild")
    public void create(@Conditions("guild:false") Player player, String guildName) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            if (!Guild.CAN_CREATE.test(databasePlayer)) {
                player.sendMessage(Component.text("You need at least 500,000 coins and 10 Normal/Hard PvE wins to create a guild.", NamedTextColor.RED));
                return;
            }
            if (guildName.length() > 15) {
                Guild.sendGuildMessage(player, Component.text("Guild name cannot be longer than 15 characters.", NamedTextColor.RED));
                return;
            }
            //check if name has special characters
            if (!guildName.matches("[a-zA-Z0-9 ]+")) {
                Guild.sendGuildMessage(player, Component.text("Guild name cannot contain special characters.", NamedTextColor.RED));
                return;
            }
            if (GuildManager.existingGuildWithName(guildName)) {
                Guild.sendGuildMessage(player, Component.text("A guild with that name already exists.", NamedTextColor.RED));
                return;
            }
            databasePlayer.getPveStats().subtractCurrency(Currencies.COIN, Guild.CREATE_COIN_COST);
            GuildManager.addGuild(new Guild(player, guildName));
            Guild.sendGuildMessage(player, Component.textOfChildren(
                    Component.text("You created guild ", NamedTextColor.RED),
                    Component.text(guildName, NamedTextColor.GOLD)
            ));
        });
    }

    @Subcommand("join")
    @CommandCompletion("@guildnames")
    @Description("Joins a guild")
    public void join(@Conditions("guild:false") Player player, String guildName) {
        Optional<Guild> optionalGuild = GuildManager.getGuildFromName(guildName);
        if (optionalGuild.isEmpty()) {
            Guild.sendGuildMessage(player, Component.text("Guild " + guildName + " does not exist.", NamedTextColor.RED));
            return;
        }
        Guild guild = optionalGuild.get();
        if (!guild.isOpen() && !GuildManager.hasInviteFromGuild(player, guild)) {
            Guild.sendGuildMessage(player,
                    Component.text("Guild " + guildName + " is not open or you are not invited to it.", NamedTextColor.RED)
            );
            return;
        }
        if (guild.getPlayers().size() >= guild.getPlayerLimit()) {
            Guild.sendGuildMessage(player, Component.text("Guild " + guildName + " is full.", NamedTextColor.RED));
            return;
        }
        GuildManager.removeGuildInvite(player, guild);
        guild.join(player);
        CustomScoreboard.updateLobbyPlayerNames();
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
        ChatUtils.sendMessage(player, false, guildPlayerWrapper.getGuild().getList());
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    @Description("Invites a player to your guild")
    public void invite(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=INVITE") GuildPlayerWrapper guildPlayerWrapper,
            @Flags("other") Player target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.getUniqueId().equals(player.getUniqueId())) {
            Guild.sendGuildMessage(player, Component.text("You cannot invite yourself to your own guild.", NamedTextColor.RED));
            return;
        }
        if (guild.getPlayerMatchingUUID(target.getUniqueId()).isPresent()) {
            Guild.sendGuildMessage(player, Component.text("That player is already in your guild.", NamedTextColor.RED));
            return;
        }
        if (GuildManager.hasInviteFromGuild(target, guild)) {
            Guild.sendGuildMessage(player, Component.text("That player has already been invited to your guild.", NamedTextColor.RED));
            return;
        }
        if (guild.getPlayers().size() >= guild.getPlayerLimit()) {
            Guild.sendGuildMessage(player, Component.text("Your guild is full.", NamedTextColor.RED));
            return;
        }
        GuildManager.addInvite(player, target, guild);
        Guild.sendGuildMessage(player,
                ChatColor.YELLOW + "You invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to the guild!\n" +
                        ChatColor.YELLOW + "They have" + Component.text(" 5 " + ChatColor.YELLOW + "minutes to accept!", NamedTextColor.RED)
        );
    }

    @Subcommand("mute")
    @Description("Mutes the guild")
    public void mute(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=MUTE") GuildPlayerWrapper guildPlayerWrapper
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
            Guild.sendGuildMessage(player, Component.text("You do not have permission to mute your guild.", NamedTextColor.RED));
            return;
        }
        if (guild.isMuted()) {
            Guild.sendGuildMessage(player, Component.text("The guild is already muted.", NamedTextColor.RED));
            return;
        }
        guild.setMuted(guildPlayer, true);
    }

    @Subcommand("unmute")
    @Description("Unmutes the guild")
    public void unmute(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=MUTE") GuildPlayerWrapper guildPlayerWrapper
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE)) {
            Guild.sendGuildMessage(player, Component.text("You do not have permission to unmute your guild.", NamedTextColor.RED));
            return;
        }
        if (!guild.isMuted()) {
            Guild.sendGuildMessage(player, Component.text("The guild is already unmuted.", NamedTextColor.RED));
            return;
        }
        guild.setMuted(guildPlayer, false);
    }

    @Subcommand("muteplayer")
    @CommandCompletion("@guildmembers")
    @Description("Mutes a player in the guild")
    public void mutePlayer(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=MUTE") GuildPlayerWrapper guildPlayerWrapper,
            @Conditions("lowerRank") GuildPlayer target,
            GuildPlayerMuteEntry.TimeUnit timeUnit,
            @co.aikar.commands.annotation.Optional Integer duration
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE_PLAYERS)) {
            Guild.sendGuildMessage(player, Component.text("You do not have permission to mute players in the guild.", NamedTextColor.RED));
            return;
        }
        if (target.isMuted()) {
            Guild.sendGuildMessage(player, Component.text("That player is already muted.", NamedTextColor.RED));
            return;
        }
        if (duration == null || timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT) {
            if (timeUnit == GuildPlayerMuteEntry.TimeUnit.PERMANENT) {
                guild.mutePlayer(guildPlayer, target);
                Guild.sendGuildMessage(player, Component.text("Muted ", NamedTextColor.RED)
                                                        .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                                        .append(Component.text(" permanently.", NamedTextColor.DARK_RED))
                );
            } else {
                Guild.sendGuildMessage(player, Component.text("You must specify a duration for temporary mutes.", NamedTextColor.RED));
            }
        } else if (duration <= 0) {
            Guild.sendGuildMessage(player, Component.text("Duration must be greater than 0.", NamedTextColor.RED));
        } else if (duration > timeUnit.maxAmount) {
            Guild.sendGuildMessage(player,
                    Component.text("Duration must be less than " + timeUnit.maxAmount + "for " + timeUnit.lyName + " mutes.", NamedTextColor.RED)
            );
        } else {
            guild.mutePlayer(guildPlayer, target, timeUnit, duration);
            Guild.sendGuildMessage(player, Component.text("Muted ", NamedTextColor.RED)
                                                    .append(Component.text(target.getName(), NamedTextColor.AQUA))
                                                    .append(Component.text(" for " + duration + " "))
                                                    .append(Component.text(timeUnit.name + (duration > 1 ? "s" : ""), NamedTextColor.DARK_RED))
            );
        }
    }

    @Subcommand("unmuteplayer")
    @CommandCompletion("@guildmembers")
    @Description("Unmutes a player in the guild")
    public void unmutePlayer(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=MUTE") GuildPlayerWrapper guildPlayerWrapper,
            @Conditions("lowerRank") GuildPlayer target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (!guild.playerHasPermission(guildPlayer, GuildPermissions.MUTE_PLAYERS)) {
            Guild.sendGuildMessage(player, Component.text("You do not have permission to unmute players in the guild.", NamedTextColor.RED));
            return;
        }
        if (!target.isMuted()) {
            Guild.sendGuildMessage(player, Component.text("That player is not muted.", NamedTextColor.RED));
            return;
        }
        guild.unmutePlayer(guildPlayer, target);
        Guild.sendGuildMessage(player, Component.textOfChildren(
                Component.text("Unmuted ", NamedTextColor.RED),
                Component.text(target.getName(), NamedTextColor.AQUA)
        ));
    }

    @Subcommand("disband")
    @Description("Disbands your guild")
    public void disband(
            @Conditions("guild:true") Player player,
            @Flags("master") GuildPlayerWrapper guildPlayerWrapper
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        String guildName = guild.getName();
        new SignGUI()
                .lines("", guildName, "Type your guild", "name to confirm")
                .onFinish((p, lines) -> {
                    String confirmation = lines[0];
                    if (confirmation.equals(guildName)) {
                        guild.disband();
                    } else {
                        Guild.sendGuildMessage(player,
                                Component.text("Guild was not disbanded because your input did not match your guild name.", NamedTextColor.RED)
                        );
                    }
                    return null;
                }).open(player);
    }

    @Subcommand("leave")
    @Description("Leaves your guild")
    public void leave(@Conditions("guild:true") Player player, GuildPlayerWrapper guildPlayerWrapper) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getCurrentMaster().equals(player.getUniqueId())) {
            Guild.sendGuildMessage(player, Component.text("Guild Masters can only leave through disbanding or transferring the guild!", NamedTextColor.RED));
            return;
        }
        guild.leave(player);
    }

    @Subcommand("transfer")
    @CommandCompletion("@guildmembers")
    @Description("Transfers ownership of your guild")
    public void transfer(
            @Conditions("guild:true") Player player,
            @Flags("master") GuildPlayerWrapper guildPlayerWrapper,
            GuildPlayer target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.equals(guildPlayer)) {
            Guild.sendGuildMessage(player, Component.text("You are already the guild master.", NamedTextColor.RED));
            return;
        }
        new SignGUI()
                .lines("", "Type CONFIRM", "Exiting will read", "current text!")
                .onFinish((p, lines) -> {
                    String confirmation = lines[0];
                    if (confirmation.equals("CONFIRM")) {
                        guild.transfer(target);
                    } else {
                        Guild.sendGuildMessage(player,
                                Component.text("Guild was not transferred because you did not input CONFIRM", NamedTextColor.RED)
                        );
                    }
                    return null;
                }).open(player);
    }

    @Subcommand("kick|remove")
    @CommandCompletion("@guildmembers")
    @Description("Kicks a player from your guild")
    public void kick(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=KICK") GuildPlayerWrapper guildPlayerWrapper,
            @Conditions("lowerRank") GuildPlayer target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (target.equals(guildPlayer)) {
            Guild.sendGuildMessage(player, Component.text("You cannot kick yourself from your own guild.", NamedTextColor.RED));
            return;
        }

        guild.kick(guildPlayer, target);
        Player kickedPlayer = Bukkit.getPlayer(target.getUUID());
        if (kickedPlayer != null) {
            Guild.sendGuildMessage(kickedPlayer, Component.text("You were kicked from the guild!", NamedTextColor.RED));
        }
    }

    @Subcommand("promote")
    @CommandCompletion("@guildmembers")
    @Description("Promotes a player in your guild")
    public void promote(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=CHANGE_ROLE") GuildPlayerWrapper guildPlayerWrapper,
            @Conditions("lowerRank") GuildPlayer target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getRoleLevel(guildPlayer) + 1 == guild.getRoleLevel(target)) {
            Guild.sendGuildMessage(player,
                    Component.text("You cannot promote ", NamedTextColor.RED)
                             .append(Component.text(target.getName(), NamedTextColor.AQUA))
                             .append(Component.text(" any higher!"))
            );
            return;
        }
        guild.promote(guildPlayer, target);
    }

    @Subcommand("demote")
    @CommandCompletion("@guildmembers")
    @Description("Demotes a player in your guild")
    public void demote(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=CHANGE_ROLE") GuildPlayerWrapper guildPlayerWrapper,
            @Conditions("lowerRank") GuildPlayer target
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (guild.getRoles().get(guild.getRoles().size() - 1).getPlayers().contains(target.getUUID())) {
            Guild.sendGuildMessage(player,
                    ChatColor.AQUA + target.getName() + Component.text(" already has the lowest role!", NamedTextColor.RED)
            );
            return;
        }
        guild.demote(guildPlayer, target);
    }

    @Subcommand("rename")
    @Description("Renames your guild")
    public void rename(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=CHANGE_NAME") GuildPlayerWrapper guildPlayerWrapper,
            String newName
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (newName.length() > 15 && !player.isOp()) {
            Guild.sendGuildMessage(player, Component.text("Guild name cannot be longer than 15 characters.", NamedTextColor.RED));
            return;
        }
        //check if name has special characters
        if (!newName.matches("[a-zA-Z\\d ]+")) {
            Guild.sendGuildMessage(player, Component.text("Guild name cannot contain special characters.", NamedTextColor.RED));
            return;
        }
        if (GuildManager.existingGuildWithName(newName)) {
            Guild.sendGuildMessage(player, Component.text("A guild with that name already exists.", NamedTextColor.RED));
            return;
        }
        guild.setName(newName);
    }

    @Subcommand("log")
    @Description("View the audit log of your guild")
    public void log(
            @Conditions("guild:true") Player player,
            @Flags("master") GuildPlayerWrapper guildPlayerWrapper,
            @co.aikar.commands.annotation.Optional Integer page
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        guild.printAuditLog(player, page == null ? Integer.MAX_VALUE : page);
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

    @Subcommand("tag")
    @Description("Sets the tag of your guild (Max 6 characters)")
    public void tag(
            @Conditions("guild:true") Player player,
            @Conditions("requirePerm:perm=MODIFY_TAG") GuildPlayerWrapper guildPlayerWrapper,
            String tag
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        if (tag.isEmpty()) {
            Guild.sendGuildMessage(player, Component.text("Tag cannot be empty.", NamedTextColor.RED));
            return;
        }
        if (tag.length() > 6) {
            Guild.sendGuildMessage(player, Component.text("Guild tag cannot be longer than 6 characters.", NamedTextColor.RED));
            return;
        }
        //check if name has special characters
        if (!tag.matches("[a-zA-Z\\d ]+")) {
            Guild.sendGuildMessage(player, Component.text("Guild tag cannot contain special characters.", NamedTextColor.RED));
            return;
        }
//        if (GuildManager.existingGuildWithTag(tag)) {
//            Guild.sendGuildMessage(player, Component.text("A guild with that tag already exists.");
//            return;
//        }
        guild.setTag(guildPlayer, tag);
    }

    @Subcommand("party")
    @Description("Creates a party in your guild")
    public void party(
            @Conditions("guild:true|party:false") Player player,
            @Conditions("requirePerm:perm=MODIFY_TAG") GuildPlayerWrapper guildPlayerWrapper
    ) {
        Guild guild = guildPlayerWrapper.getGuild();
        GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
        Party party = new Party(player.getUniqueId());
        PartyManager.PARTIES.add(party);
        for (Player onlinePlayer : guild.getOnlinePlayers()) {
            if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                party.invite(onlinePlayer.getUniqueId());
            }
            ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.GREEN.toString() + ChatColor.BOLD + "------------------------------------------");
            ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " created a guild party!");
            ChatUtils.sendCenteredMessage(onlinePlayer, Component.text(ChatColor.GOLD.toString() + ChatColor.BOLD + "Click here to join!")
                                                                 .clickEvent(ClickEvent.runCommand("/party join " + player.getName())));
            ChatUtils.sendCenteredMessage(onlinePlayer, ChatColor.GREEN.toString() + ChatColor.BOLD + "------------------------------------------");
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

    @Subcommand("motd")
    public class GuildMOTDCommand extends BaseCommand {

        @Subcommand("add")
        @Description("Adds a line in the MOTD of your guild")
        public void add(
                @Conditions("guild:true") Player player,
                @Conditions("requirePerm:perm=MODIFY_MOTD") GuildPlayerWrapper guildPlayerWrapper,
                String message
        ) {
            Guild guild = guildPlayerWrapper.getGuild();
            GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
            List<String> motd = guild.getMotd();
            if (motd.size() >= 10) {
                Guild.sendGuildMessage(player, Component.text("You can only have up to 10 lines in your MOTD.", NamedTextColor.RED));
                return;
            }
            message = message.replaceAll("&", "§");
            motd.add(message);
            guild.queueUpdate();
            Guild.sendGuildMessage(player, ChatColor.GRAY + "Appended " + ChatColor.RESET + message + ChatColor.GRAY + " to the MOTD.");
        }


        @Subcommand("set")
        @Description("Sets a line in the MOTD of your guild")
        public void set(
                @Conditions("guild:true") Player player,
                @Conditions("requirePerm:perm=MODIFY_MOTD") GuildPlayerWrapper guildPlayerWrapper,
                Integer line,
                String message
        ) {
            Guild guild = guildPlayerWrapper.getGuild();
            GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
            List<String> motd = guild.getMotd();
            if (line > motd.size() + 1) {
                Guild.sendGuildMessage(player, Component.text("You can only edit lines that already exist.", NamedTextColor.RED));
                return;
            }
            if (line < 1) {
                Guild.sendGuildMessage(player, Component.text("Line number must be greater than 0.", NamedTextColor.RED));
                return;
            }
            if (line > 10) {
                Guild.sendGuildMessage(player, Component.text("You can only have up to 10 lines in your MOTD.", NamedTextColor.RED));
                return;
            }
            message = message.replaceAll("&", "§");
            if (line == motd.size() + 1) {
                motd.add(message);
            } else {
                motd.set(line - 1, message);
            }
            guild.queueUpdate();
            Guild.sendGuildMessage(player,
                    Component.text("Set line ", NamedTextColor.GRAY)
                             .append(Component.text(line, NamedTextColor.RED))
                             .append(Component.text(" to "))
                             .append(PlainTextComponentSerializer.plainText().deserialize(message))
                             .append(Component.text("."))
            );
        }

        @Subcommand("preview")
        @Description("Shows the MOTD of your guild")
        public void preview(
                @Conditions("guild:true") Player player,
                @Conditions("requirePerm:perm=MODIFY_MOTD") GuildPlayerWrapper guildPlayerWrapper
        ) {
            Guild guild = guildPlayerWrapper.getGuild();
            GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
            List<String> motd = guild.getMotd();
            if (motd == null || motd.isEmpty()) {
                Guild.sendGuildMessage(player, Component.text("Your guild does not have a MOTD.", NamedTextColor.RED));
                return;
            }
            guild.sendMOTD(player);
        }

        @Subcommand("clear")
        @Description("Clears the MOTD of your guild")
        public void clear(
                @Conditions("guild:true") Player player,
                @Conditions("requirePerm:perm=MODIFY_MOTD") GuildPlayerWrapper guildPlayerWrapper
        ) {
            Guild guild = guildPlayerWrapper.getGuild();
            GuildPlayer guildPlayer = guildPlayerWrapper.getGuildPlayer();
            List<String> motd = guild.getMotd();
            if (motd == null || motd.isEmpty()) {
                Guild.sendGuildMessage(player, Component.text("Your guild does not have a MOTD.", NamedTextColor.RED));
                return;
            }
            guild.getMotd().clear();
            guild.queueUpdate();
            Guild.sendGuildMessage(player, Component.text("Cleared the MOTD.", NamedTextColor.GREEN));
        }

    }

    @Subcommand("leaderboard")
    public class GuildLeaderboardCommand extends BaseCommand {

        @Subcommand("experience|EXP|exp")
        public void experience(CommandIssuer issuer, @Default("DAILY") Timing timing) {
            Guild.sendGuildMessage(issuer.getIssuer(), GuildLeaderboardManager.getLeaderboardList(
                            GuildLeaderboardManager.EXPERIENCE_LEADERBOARD.get(timing),
                            timing.name + " Experience",
                            guild -> guild.getExperience(timing)
                    )
            );
        }

        @Subcommand("coins")
        public void coins(CommandIssuer issuer, @Default("DAILY") Timing timing) {
            Guild.sendGuildMessage(issuer.getIssuer(), GuildLeaderboardManager.getLeaderboardList(
                            GuildLeaderboardManager.COINS_LEADERBOARD.get(timing),
                            timing.name + " Coins",
                            guild -> guild.getCoins(timing)
                    )
            );
        }

        @Subcommand("refresh")
        @CommandPermission("warlords.leaderboard.interaction")
        public void refresh(CommandIssuer issuer) {
            GuildLeaderboardManager.recalculateAllLeaderboards();
            ChatChannels.sendDebugMessage(issuer, Component.text("Recalculated Guild Leaderboards", NamedTextColor.RED));
        }

    }

}

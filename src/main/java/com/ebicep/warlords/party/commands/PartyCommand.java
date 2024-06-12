package com.ebicep.warlords.party.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.queuesystem.QueueManager;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.PartyPlayerType;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.poll.polls.PartyPoll;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.*;


@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {

    @Subcommand("listall")
    @CommandPermission("group.administrator")
    @Description("Lists the all party lists")
    public void listAll(CommandIssuer issuer) {
        List<Party> parties = PartyManager.PARTIES;
        if (parties.isEmpty()) {
            ChatChannels.sendDebugMessage(issuer, Component.text("There are no parties!", NamedTextColor.GOLD));
        } else {
            if (issuer.getIssuer() instanceof Player player) {
                parties.forEach(party -> player.sendMessage(party.getPartyList()));
            } else {
                parties.forEach(party -> ChatChannels.sendDebugMessage(issuer, party.getPartyList()));
            }
        }
    }

    @Subcommand("debugcreate")
    @CommandPermission("group.administrator")
    @Description("Creates a party with all players on server")
    public void debugCreate(@Conditions("party:false") Player player) {
        Party party = new Party(player.getUniqueId(), false);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                continue;
            }
            party.join(onlinePlayer.getUniqueId());
        }
        PartyManager.PARTIES.add(party);
    }

    @CommandAlias("pl")
    @Subcommand("list")
    @Description("Lists the players in your party")
    public void list(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        player.sendMessage(partyPlayerWrapper.getParty().getPartyList());
    }

    @Subcommand("create")
    @Description("Creates a party")
    public void create(@Conditions("party:false") Player player) {
        Party party = new Party(player.getUniqueId(), false);
        PartyManager.PARTIES.add(party);
        player.performCommand("p list");
    }

    @Default
    @Subcommand("invite")
    @Description("Invites a player to your party")
    public void invite(Player player, @Flags("other") Player target) {
        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();
        if (playerUUID.equals(targetUUID)) {
            Party.sendPartyMessage(player, Component.text("You can't invite yourself to a party!", NamedTextColor.RED));
            return;
        }

        Party party;
        if (PartyManager.getPartyAndPartyPlayerFromAny(playerUUID) == null) {
            party = new Party((player).getUniqueId(), false);
            PartyManager.PARTIES.add(party);
        } else {
            party = Objects.requireNonNull(PartyManager.getPartyAndPartyPlayerFromAny(playerUUID)).getA();
        }
        if (!party.isAllInvite() &&
                !party.getPartyLeader().getUUID().equals(playerUUID) &&
                party.getPartyModerators().stream()
                     .noneMatch(partyPlayer -> partyPlayer.getUUID().equals(playerUUID))
        ) {
            Party.sendPartyMessage(player, Component.text("All invite is disabled!", NamedTextColor.RED));
            return;
        }
        if (PartyManager.inSameParty(playerUUID, targetUUID)) {
            Party.sendPartyMessage(player, Component.text("That player is already in the party!", NamedTextColor.RED));
            return;
        }
        if (party.getInvites().containsKey(targetUUID)) {
            Party.sendPartyMessage(player,
                    Component.text("That player has already been invited! (" + party.getInvites().get(targetUUID) + ")", NamedTextColor.RED)
            );
            return;
        }
        party.invite(targetUUID);
        party.sendMessageToAllPartyPlayers(
                Component.text().color(NamedTextColor.YELLOW)
                         .append(Permissions.getPrefixWithColor(player, true))
                         .append(Component.text(" invited "))
                         .append(Permissions.getPrefixWithColor(target, true))
                         .append(Component.text(" to the party!"))
                         .append(Component.newline())
                         .append(Component.text("They have"))
                         .append(Component.text(" 60 ", NamedTextColor.RED))
                         .append(Component.text("seconds to accept!"))
                         .build()
        );
        ChatUtils.sendCenteredMessage(target, Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
        ChatUtils.sendCenteredMessage(target,
                Component.text().color(NamedTextColor.YELLOW)
                         .append(Permissions.getPrefixWithColor(player, true))
                         .append(Component.text(" has invited you to join "))
                         .append(party.getPartyLeader()
                                      .getUUID()
                                      .equals(playerUUID) ?
                                 Component.text("their party!") :
                                 Component.textOfChildren(
                                         Permissions.getPrefixWithColor(player, true),
                                         Component.text("'s party!")
                                 ))
                         .build()
        );
        ChatUtils.sendCenteredMessage(target,
                Component.text().color(NamedTextColor.YELLOW)
                         .append(Component.text("You have"))
                         .append(Component.text(" 60 ", NamedTextColor.RED))
                         .append(Component.text("seconds to accept. "))
                         .append(Component.text("Click here to join!", NamedTextColor.GOLD)
                                          .hoverEvent(HoverEvent.showText(Component.text("Click to join the party!", NamedTextColor.GREEN)))
                                          .clickEvent(ClickEvent.runCommand("/party join " + party.getLeaderName())))
                         .build()
        );
        ChatUtils.sendCenteredMessage(target, Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
    }

    @Subcommand("join")
    @Description("Joins a party")
    public void join(@Conditions("party:false") Player player, @Flags("other") Player partyLeader) {
        Optional<Party> optionalParty = PartyManager.getPartyFromLeaderName(partyLeader.getName());
        if (optionalParty.isEmpty()) {
            Party.sendPartyMessage(player, Component.text("That player does not have a party!", NamedTextColor.RED));
            return;
        }
        Party party = optionalParty.get();
        if (!party.isOpen() && !party.getInvites().containsKey(player.getUniqueId())) {
            Party.sendPartyMessage(player, Component.text("Invite expired or party is closed!", NamedTextColor.RED));
            return;
        }
        party.join(player.getUniqueId());
        if (QueueManager.QUEUE.contains(player.getUniqueId())) {
            QueueManager.QUEUE.remove(player.getUniqueId());
            QueueManager.sendQueue();
        }
    }

    @Subcommand("leave")
    @Description("Leaves your party")
    public void leave(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        Party party = partyPlayerWrapper.getParty();
        party.leave(player.getUniqueId());
        Party.sendPartyMessage(player, Component.text("You left the party", NamedTextColor.GREEN));
    }

    @Subcommand("disband")
    @Description("Disbands your party")
    public void disband(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        Party party = partyPlayerWrapper.getParty();
        if (party.getPartyLeader().getUUID().equals(player.getUniqueId())) {
            party.disband();
        } else {
            Party.sendPartyMessage(player, Component.text("You are not the party leader!", NamedTextColor.RED));
        }
    }

    @Subcommand("promote")
    @CommandCompletion("@partymembers")
    @Description("Promotes a player in your party")
    public void promote(
            @Conditions("party:true") Player player,
            PartyPlayerWrapper partyPlayerWrapper,
            @Conditions("lowerRank") PartyPlayer targetPartyPlayer
    ) {
        partyPlayerWrapper.getParty().promote(targetPartyPlayer.getUUID());
    }

    @Subcommand("demote")
    @CommandCompletion("@partymembers")
    @Description("Demotes a player in your party")
    public void demote(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, @Conditions("lowerRank") PartyPlayer targetPartyPlayer) {
        partyPlayerWrapper.getParty().demote(targetPartyPlayer.getUUID());
    }

    @Subcommand("remove|kick")
    @CommandCompletion("@partymembers")
    @Description("Removes a player from your party")
    public void remove(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, @Conditions("lowerRank") PartyPlayer targetPartyPlayer) {
        partyPlayerWrapper.getParty().remove(targetPartyPlayer.getUUID());
    }

    @Subcommand("poll")
    @Description("Creates a poll in your party")
    public void poll(
            @Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper,
            @Split("/") @Syntax("question/answer/answer ...") String[] pollInfo
    ) {
        Party party = partyPlayerWrapper.getParty();
        if (partyPlayerWrapper.getPartyPlayer().getPartyPlayerType() == PartyPlayerType.MEMBER) {
            Party.sendPartyMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
            return;
        }
        if (!party.getPolls().isEmpty()) {
            Party.sendPartyMessage(player, Component.text("There is already an ongoing poll!", NamedTextColor.RED));
            return;
        }

        if (pollInfo.length <= 2) {
            Party.sendPartyMessage(player, Component.text("You must have a question and more than 1 answer!", NamedTextColor.RED));
            return;
        }
        List<String> pollOptions = new ArrayList<>(Arrays.asList(pollInfo));
        String question = pollOptions.get(0);
        pollOptions.remove(question);
        party.addPoll(new PartyPoll.Builder(party)
                .setQuestion(question)
                .setOptions(pollOptions)
                .get()
        );
    }

    @Subcommand("afk")
    @Description("Toggles your AFK status")
    public void afk(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        partyPlayerWrapper.getParty().afk(player.getUniqueId());
    }

    @Subcommand("open")
    @Description("Opens your party")
    public void open(@Conditions("party:true") Player player, @Flags("leader") PartyPlayerWrapper partyPlayerWrapper) {
        partyPlayerWrapper.getParty().setOpen(true);
    }

    @Subcommand("close")
    @Description("Closes your party")
    public void close(@Conditions("party:true") Player player, @Flags("leader") PartyPlayerWrapper partyPlayerWrapper) {
        partyPlayerWrapper.getParty().setOpen(false);
    }

    @Subcommand("outside")
    @Description("Prints the players outside your party")
    public void outside(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        List<PartyPlayer> partyPlayers = partyPlayerWrapper.getParty().getPartyPlayers();
        TextComponent.Builder outside = Component.text("Players Outside Party: ", NamedTextColor.YELLOW).toBuilder();
        outside.append(Bukkit.getOnlinePlayers()
                             .stream()
                             .filter(p -> partyPlayers.stream().noneMatch(partyPlayer -> partyPlayer.getUUID().equals(p.getUniqueId())))
                             .map(p -> Component.text(p.getName(), NamedTextColor.GREEN))
                             .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))));
        player.sendMessage(outside.build());
    }

    @Subcommand("leader")
    @CommandPermission("warlords.party.forceleader")
    @Description("Forces you to be the party leader")
    public void leader(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        partyPlayerWrapper.getParty().transfer(player.getUniqueId());
    }

    @Subcommand("transfer")
    @Description("Transfers party to another party")
    public void transfer(
            @Conditions("party:true") Player player,
            @Flags("leader") PartyPlayerWrapper partyPlayerWrapper,
            @Conditions("lowerRank") PartyPlayer targetPartyPlayer
    ) {
        partyPlayerWrapper.getParty().transfer(targetPartyPlayer.getUUID());
    }

    @Subcommand("forcejoin")
    @CommandPermission("warlords.party.forcejoin")
    @Description("Forces a player to join the party")
    public void forceJoin(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, String target) {
        Party party = partyPlayerWrapper.getParty();
        if (target.equalsIgnoreCase("@a")) {
            Bukkit.getOnlinePlayers().stream()
                  .filter(p -> !party.hasUUID(p.getUniqueId()))
                  .forEach(p -> party.join(p.getUniqueId()));
            return;
        }
        Player playerToForceInvite = Bukkit.getPlayer(target);
        if (playerToForceInvite == null) {
            Party.sendPartyMessage(player, Component.text("Cannot find a player with that name!", NamedTextColor.RED));
            return;
        }
        if (PartyManager.inSameParty(player.getUniqueId(), playerToForceInvite.getUniqueId())) {
            Party.sendPartyMessage(player, Component.text("That player is already in the party!", NamedTextColor.RED));
            return;
        }
        party.join(playerToForceInvite.getUniqueId());
    }

    @Subcommand("allinvite")
    @Description("Toggles all invite")
    public void allInvite(@Conditions("party:true") Player player, @Flags("leader") PartyPlayerWrapper partyPlayerWrapper) {
        Party party = partyPlayerWrapper.getParty();
        party.setAllInvite(!party.isAllInvite());
        if (party.isAllInvite()) {
            party.sendMessageToAllPartyPlayers(Component.text("All invite is now ON!", NamedTextColor.GREEN));
        } else {
            party.sendMessageToAllPartyPlayers(Component.text("All invite is now OFF!", NamedTextColor.RED));
        }

    }

    @Subcommand("invitequeue")
    @Description("Invites the players in the queue")
    public void inviteQueue(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        int partySize = partyPlayerWrapper.getParty().getPartyPlayers().size();
        if (partySize != 24) {
            int availableSpots = 24 - partySize;
            int onlineQueueSize = (int) QueueManager.QUEUE.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).count();
            List<UUID> toInvite = new ArrayList<>();
            int inviteNumber;
            if (availableSpots % 2 == 0) { //even spots
                inviteNumber = Math.min(availableSpots, onlineQueueSize % 2 == 0 ? onlineQueueSize : onlineQueueSize - 1);
            } else { //odd spots
                inviteNumber = Math.min(availableSpots, onlineQueueSize % 2 != 0 ? onlineQueueSize : onlineQueueSize - 1);
            }
            int counter = 0;
            if (inviteNumber != 0) {
                for (UUID uuid : QueueManager.QUEUE) {
                    Player invitePlayer = Bukkit.getPlayer(uuid);
                    if (invitePlayer != null) {
                        toInvite.add(uuid);
                        counter++;
                        if (counter >= inviteNumber) {
                            break;
                        }
                    }
                }
                toInvite.forEach(uuid -> player.performCommand("p invite " + Bukkit.getPlayer(uuid).getName()));
                QueueManager.QUEUE.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
                QueueManager.sendQueue();
            }
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

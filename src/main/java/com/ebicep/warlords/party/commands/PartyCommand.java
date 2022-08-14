package com.ebicep.warlords.party.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.queuesystem.QueueManager;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.PartyPlayerType;
import com.ebicep.warlords.poll.polls.PartyPoll;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.*;


@CommandAlias("party|p")
public class PartyCommand extends BaseCommand {

    @Subcommand("create")
    @Description("Creates a party")
    public void create(@Conditions("party:false") Player player) {
        Party party = new Party(player.getUniqueId(), false);
        PartyManager.PARTIES.add(party);
        Bukkit.dispatchCommand(player, "p list");
    }

    @Default
    @Subcommand("invite")
    @Description("Invites a player to your party")
    public void invite(Player player, @Flags("other") Player target) {
        UUID playerUUID = player.getUniqueId();
        UUID targetUUID = target.getUniqueId();
        if (playerUUID.equals(targetUUID)) {
            Party.sendPartyMessage(player, ChatColor.RED + "You can't invite yourself to a party!");
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
            Party.sendPartyMessage(player, ChatColor.RED + "All invite is disabled!");
            return;
        }
        if (PartyManager.inSameParty(playerUUID, targetUUID)) {
            Party.sendPartyMessage(player, ChatColor.RED + "That player is already in the party!");
            return;
        }
        if (party.getInvites().containsKey(targetUUID)) {
            Party.sendPartyMessage(player, ChatColor.RED + "That player has already been invited! (" + party.getInvites().get(targetUUID) + ")");
            return;
        }
        party.invite(targetUUID);
        party.sendMessageToAllPartyPlayers(
                ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " invited " + ChatColor.AQUA + target.getName() + ChatColor.YELLOW + " to the party!\n" +
                        ChatColor.YELLOW + "They have" + ChatColor.RED + " 60 " + ChatColor.YELLOW + "seconds to accept!",
                ChatColor.BLUE,
                true
        );
        ChatUtils.sendCenteredMessage(target, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
        ChatUtils.sendCenteredMessage(target, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has invited you to join " + (party.getPartyLeader().getUUID().equals(playerUUID) ? "their party!" : ChatColor.AQUA + party.getLeaderName() + ChatColor.YELLOW + "'s party!"));
        TextComponent message = new TextComponent(ChatColor.YELLOW + "You have" + ChatColor.RED + " 60 " + ChatColor.YELLOW + "seconds to accept. " + ChatColor.GOLD + "Click here to join!");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to join the party!").create()));
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + party.getLeaderName()));
        ChatUtils.sendCenteredMessageWithEvents(target, Collections.singletonList(message));
        ChatUtils.sendCenteredMessage(target, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
    }

    @Subcommand("join")
    @CommandCompletion("@partyleaders")
    @Description("Joins a party")
    public void join(@Conditions("party:false") Player player, @Values("@partyleaders") String partyLeaderName) {
        Optional<Party> optionalParty = PartyManager.getPartyFromLeaderName(partyLeaderName);
        if (!optionalParty.isPresent()) {
            Party.sendPartyMessage(player, ChatColor.RED + "That player does not have a party!");
            return;
        }
        Party party = optionalParty.get();
        if (!party.isOpen() && !party.getInvites().containsKey(player.getUniqueId())) {
            Party.sendPartyMessage(player, ChatColor.RED + "Invite expired or party is closed!");
            return;
        }
        party.join(player.getUniqueId());
        if (QueueManager.queue.contains(player.getUniqueId())) {
            QueueManager.queue.remove(player.getUniqueId());
            QueueManager.sendQueue();
        }
    }

    @Subcommand("leave")
    @Description("Leaves your party")
    public void leave(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        Party party = partyPlayerWrapper.getParty();
        party.leave(player.getUniqueId());
        Party.sendPartyMessage(player, ChatColor.GREEN + "You left the party");
    }

    @Subcommand("disband")
    @Description("Disbands your party")
    public void disband(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        Party party = partyPlayerWrapper.getParty();
        if (party.getPartyLeader().getUUID().equals(player.getUniqueId())) {
            party.disband();
        } else {
            Party.sendPartyMessage(player, ChatColor.RED + "You are not the party leader!");
        }
    }

    @CommandAlias("pl")
    @Subcommand("list")
    @Description("Lists the players in your party")
    public void list(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        player.sendMessage(partyPlayerWrapper.getParty().getPartyList());
    }

    @Subcommand("promote")
    @CommandCompletion("@partymembers")
    @Description("Promotes a player in your party")
    public void promote(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, @Conditions("lowerRank") PartyPlayer targetPartyPlayer) {
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
    public void poll(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, @Syntax("question answer answer ...") String[] pollInfo) {
        Party party = partyPlayerWrapper.getParty();
        if (partyPlayerWrapper.getPartyPlayer().getPartyPlayerType() == PartyPlayerType.MEMBER) {
            Party.sendPartyMessage(player, ChatColor.RED + "Insufficient Permissions!");
            return;
        }
        if (!party.getPolls().isEmpty()) {
            Party.sendPartyMessage(player, ChatColor.RED + "There is already an ongoing poll!");
            return;
        }

        if (pollInfo.length <= 2) {
            Party.sendPartyMessage(player, ChatColor.RED + "You must have a question and more than 1 answer!");
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
        StringBuilder outside = new StringBuilder(ChatColor.YELLOW + "Players Outside Party: ");
        int numberOfPlayersOutside = 0;
        List<PartyPlayer> partyPlayers = partyPlayerWrapper.getParty().getPartyPlayers();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (partyPlayers.stream().noneMatch(partyPlayer -> partyPlayer.getUUID().equals(p.getUniqueId()))) {
                numberOfPlayersOutside++;
                outside.append(ChatColor.GREEN).append(p.getName()).append(ChatColor.GRAY).append(", ");
            }
        }
        outside.setLength(outside.length() - 2);
        if (numberOfPlayersOutside == 0) {
            player.sendMessage(ChatColor.YELLOW + "There are no players outside of the party");
        } else {
            player.sendMessage(outside.toString());
        }
    }

    @Subcommand("leader")
    @CommandPermission("warlords.party.forceleader")
    @Description("Forces you to be the party leader")
    public void leader(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        partyPlayerWrapper.getParty().transfer(player.getUniqueId());
    }

    @Subcommand("transfer")
    @Description("Transfers party to another party")
    public void transfer(@Conditions("party:true") Player player, @Flags("leader") PartyPlayerWrapper partyPlayerWrapper, @Conditions("lowerRank") PartyPlayer targetPartyPlayer) {
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
            Party.sendPartyMessage(player, ChatColor.RED + "Cannot find a player with that name!");
            return;
        }
        if (PartyManager.inSameParty(player.getUniqueId(), playerToForceInvite.getUniqueId())) {
            Party.sendPartyMessage(player, ChatColor.RED + "That player is already in the party!");
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
            party.sendMessageToAllPartyPlayers(ChatColor.GREEN + "All invite is now ON!", ChatColor.BLUE, true);
        } else {
            party.sendMessageToAllPartyPlayers(ChatColor.RED + "All invite is now OFF!", ChatColor.BLUE, true);
        }

    }

    @Subcommand("invitequeue")
    @Description("Invites the players in the queue")
    public void inviteQueue(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper) {
        int partySize = partyPlayerWrapper.getParty().getPartyPlayers().size();
        if (partySize != 24) {
            int availableSpots = 24 - partySize;
            int onlineQueueSize = (int) QueueManager.queue.stream().filter(uuid -> Bukkit.getPlayer(uuid) != null).count();
            List<UUID> toInvite = new ArrayList<>();
            int inviteNumber;
            if (availableSpots % 2 == 0) { //even spots
                inviteNumber = Math.min(availableSpots, onlineQueueSize % 2 == 0 ? onlineQueueSize : onlineQueueSize - 1);
            } else { //odd spots
                inviteNumber = Math.min(availableSpots, onlineQueueSize % 2 != 0 ? onlineQueueSize : onlineQueueSize - 1);
            }
            int counter = 0;
            if (inviteNumber != 0) {
                for (UUID uuid : QueueManager.queue) {
                    Player invitePlayer = Bukkit.getPlayer(uuid);
                    if (invitePlayer != null) {
                        toInvite.add(uuid);
                        counter++;
                        if (counter >= inviteNumber) {
                            break;
                        }
                    }
                }
                toInvite.forEach(uuid -> Bukkit.dispatchCommand(player, "p invite " + Bukkit.getPlayer(uuid).getName()));
                QueueManager.queue.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
                QueueManager.sendQueue();
            }
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}

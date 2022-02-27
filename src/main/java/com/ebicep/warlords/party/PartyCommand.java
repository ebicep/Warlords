package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.option.ImposterModeOption;
import com.ebicep.warlords.queuesystem.QueueManager;
import com.ebicep.warlords.util.ChatUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PartyCommand implements TabExecutor {

    private static final String[] partyOptions = {
            "invite", "join", "leave", "disband", "list", "promote", "demote",
            "kick", "remove", "transfer", "poll", "afk", "close", "open",
            "outside", "leader", "forcejoin", "allinvite", "invitequeue",
    };

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        switch (s) {
            case "party":
            case "p":
                Player player = (Player) sender;
                if (args.length <= 0) {
                    Party.sendMessageToPlayer(player, ChatColor.GOLD + "Party Commands: \n" +
                                    ChatColor.YELLOW + "/p create" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Creates a party" + "\n" +
                                    ChatColor.YELLOW + "/p invite <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Invites another player to your party" + "\n" +
                                    ChatColor.YELLOW + "/p list" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Lists the players in your current party" + "\n" +
                                    ChatColor.YELLOW + "/p leave" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Leaves your current party" + "\n" +
                                    ChatColor.YELLOW + "/p disband" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Disbands the party" + "\n" +
                                    ChatColor.YELLOW + "/p (kick/remove) <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Removes a player from your party" + "\n" +
                                    ChatColor.YELLOW + "/p transfer <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Transfers ownership of the party to a player" + "\n" +
                                    ChatColor.YELLOW + "/p afk" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Toggles if you are AFK" + "\n" +
                                    ChatColor.YELLOW + "/p promote <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Promotes a player in the party" + "\n" +
                                    ChatColor.YELLOW + "/p demote <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Demotes a player in the party" + "\n" +
                                    ChatColor.YELLOW + "/p poll <question/answer/answer...>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Creates a poll to vote on" + "\n" +
                                    ChatColor.YELLOW + "/p (open/close)" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Opens/Closes the party" + "\n" +
                                    ChatColor.YELLOW + "/p outside" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Shows the players outside of the party" + "\n" +
                                    ChatColor.YELLOW + "/p leader" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Takes leader from current leader" + "\n" +
                                    ChatColor.YELLOW + "/p forcejoin <player>" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Forces a player to join your party" + "\n" +
                                    ChatColor.YELLOW + "/p allinvite" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Toggles All Invite" + "\n" +
                                    ChatColor.YELLOW + "/p invitequeue" + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + ChatColor.ITALIC + "Invites the players in queue" + "\n"
                            ,
                            true,
                            false);
                    return true;
                }
                Optional<Party> currentParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
                String input = args[0];
                if (input.equalsIgnoreCase("create")) {
                    if (currentParty.isPresent()) {
                        Party.sendMessageToPlayer(player, ChatColor.RED + "You are already in a party!", true, true);
                        return true;
                    }
                    Party party = new Party((player).getUniqueId(), false);
                    Warlords.partyManager.getParties().add(party);
                    Bukkit.dispatchCommand(sender, "p list");
                    return true;
                }
                if (!input.equalsIgnoreCase("join") && !input.equalsIgnoreCase("invite") && Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(input))) {
                    if (!currentParty.isPresent()) {
                        Party.sendMessageToPlayer(player, ChatColor.RED + "You are currently not in a party!", true, true);
                        return true;
                    }
                }
                switch (input.toLowerCase()) {
                    case "invite": {
                        if (args.length <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            return true;
                        }
                        String playerToInvite = args[1];
                        Player invitedPlayer = Bukkit.getPlayer(playerToInvite);
                        if (!currentParty.isPresent()) {
                            if (invitedPlayer == sender) {
                                Party.sendMessageToPlayer(player, ChatColor.RED + "You can't invite yourself to a party!", true, true);
                                return true;
                            }
                            Party party = new Party((player).getUniqueId(), false);
                            Warlords.partyManager.getParties().add(party);
                            currentParty = Optional.of(party);
                        }
                        if (!currentParty.get().isAllInvite() && !currentParty.get().getPartyLeader().getUuid().equals(player.getUniqueId()) && currentParty.get().getPartyModerators().stream().noneMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()))) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "All invite is Disabled!", true, true);
                            return true;
                        }
                        Player partyLeader = Bukkit.getPlayer(currentParty.get().getPartyLeader().getUuid());
                        if (invitedPlayer == null) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Unable to invite that player!", true, true);
                            return true;
                        }
                        if (Warlords.partyManager.inSameParty(player.getUniqueId(), invitedPlayer.getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "That player is already in the party!", true, true);
                            return true;
                        }
                        if (currentParty.get().getInvites().containsKey(invitedPlayer.getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "That player has already been invited! (" + currentParty.get().getInvites().get(invitedPlayer.getUniqueId()) + ")", true, true);
                        }
                        currentParty.get().invite(playerToInvite);
                        currentParty.get().sendMessageToAllPartyPlayers(
                                ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " invited " + ChatColor.AQUA + invitedPlayer.getName() + ChatColor.YELLOW + " to the party!\n" +
                                        ChatColor.YELLOW + "They have" + ChatColor.RED + " 60 " + ChatColor.YELLOW + "seconds to accept!",
                                true,
                                true
                        );
                        ChatUtils.sendCenteredMessage(invitedPlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
                        ChatUtils.sendCenteredMessage(invitedPlayer, ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has invited you to join " + (partyLeader.equals(player) ? "their party!" : ChatColor.AQUA + partyLeader.getName() + ChatColor.YELLOW + "'s party!"));
                        TextComponent message = new TextComponent(ChatColor.YELLOW + "You have" + ChatColor.RED + " 60 " + ChatColor.YELLOW + "seconds to accept. " + ChatColor.GOLD + "Click here to join!");
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to join the party!").create()));
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party join " + partyLeader.getName()));
                        ChatUtils.sendCenteredMessageWithEvents(invitedPlayer, Collections.singletonList(message));
                        ChatUtils.sendCenteredMessage(invitedPlayer, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
                        return true;
                    }
                    case "join": {
                        if (args.length <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            return true;
                        }
                        String playerToJoin = args[1];
                        if (Warlords.partyManager.inAParty(player.getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "You are already in a party!", true, true);
                            return true;
                        }
                        if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(playerToJoin))) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Cannot find a player with that name!", true, true);
                            return true;
                        }
                        if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equalsIgnoreCase(playerToJoin))) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "That player does not have a party!", true, true);
                            return true;
                        }
                        Player partyLeader = Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(playerToJoin)).findAny().get();
                        Optional<Party> party = Warlords.partyManager.getPartyFromLeader(partyLeader.getUniqueId());
                        if (!party.isPresent()) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "That player does not have a party!", true, true);
                            return true;
                        }
                        if (!party.get().isOpen() && !party.get().getInvites().containsKey(player.getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invite expired or party is closed!", true, true);
                            return true;
                        }
                        party.get().join(player.getUniqueId());
                        QueueManager.queue.remove(player.getUniqueId());
                        return true;
                    }
                    case "leave":
                        currentParty.get().leave(player.getUniqueId());
                        Party.sendMessageToPlayer(player, ChatColor.GREEN + "You left the party", true, true);
                        return true;
                    case "disband":
                        if (currentParty.get().getPartyLeader().getUuid().equals(player.getUniqueId())) {
                            currentParty.get().disband();
                        } else {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "You are not the party leader!", true, true);
                        }
                        return true;
                    case "list":
                        sender.sendMessage(currentParty.get().getPartyList());
                        return true;
                    case "promote":
                    case "demote":
                    case "kick":
                    case "remove":
                    case "transfer": {
                        if (args.length <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            return true;
                        }
                        String targetPlayer = args[1];
                        Party party = currentParty.get();
                        //player to act on cannot be leader
                        //player is moderator and commands are not promote, demote, or transfer
                        //player cannot be a member
                        boolean isModerator = party.getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()));
                        boolean isMember = party.getPartyMembers().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()));
                        if (
                                (party.getLeaderName().equalsIgnoreCase(targetPlayer)) ||
                                        (isModerator && (input.equalsIgnoreCase("promote") || input.equalsIgnoreCase("demote") || input.equalsIgnoreCase("transfer"))) ||
                                        (isMember)
                        ) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Insufficient Permissions!", true, true);
                            return true;
                        }
                        if (player.getName().equalsIgnoreCase(targetPlayer)) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "You cannot do this on yourself!", true, true);
                            return true;
                        }
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetPlayer);
                        if (offlinePlayer == null || !party.hasUUID(offlinePlayer.getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Cannot find player!", true, true);
                            return true;
                        }
                        if (input.equalsIgnoreCase("promote")) {
                            party.promote(targetPlayer);
                        } else if (input.equalsIgnoreCase("demote")) {
                            party.demote(targetPlayer);
                        } else if (input.equalsIgnoreCase("remove") || input.equalsIgnoreCase("kick")) {
                            party.remove(targetPlayer);
                        } else {
                            party.transfer(targetPlayer);
                        }
                        return true;
                    }
                    case "poll": {
                        if (args.length <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            return true;
                        }
                        Party party = currentParty.get();
                        if (party.getPartyMembers().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()))) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Insufficient Permissions!", true, true);
                            return true;
                        }
                        if (!currentParty.get().getPolls().isEmpty()) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "There is already an ongoing poll!", true, true);
                            return true;
                        }
                        String pollInfo = args[1];
                        int numberOfSlashes = (int) pollInfo.chars().filter(ch -> ch == '/').count();
                        if (numberOfSlashes <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "You must have a question and more than 1 answer!", true, true);
                        } else {
                            List<String> pollOptions = new ArrayList<>(Arrays.asList(pollInfo.split("/")));
                            String question = pollOptions.get(0);
                            pollOptions.remove(question);
                            currentParty.get().addPoll(
                                    new PollBuilder()
                                            .setQuestion(question)
                                            .setOptions(pollOptions)
                            );
                        }
                        return true;
                    }
                    case "pollanswer": {
                        if (args.length <= 1) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            return true;
                        }
                        Party party = currentParty.get();
                        if (party.getPolls().isEmpty() || party.getPolls().stream().allMatch(poll -> poll.getExcludedPlayers().contains(player.getUniqueId()))) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "There is no ongoing poll!", true, true);
                            return true;
                        }
                        try {
                            int answer = Integer.parseInt(args[1]);
                            Poll poll = currentParty.get().getPolls().stream().filter(p -> !p.getExcludedPlayers().contains(player.getUniqueId())).findFirst().get();
                            //Poll poll = ((ImposterModeOption) Warlords.getPlayer(player).getGame().getOptions().stream().filter(option -> option instanceof ImposterModeOption).findFirst().get()).getPoll();
                            HashMap<UUID, Integer> playerAnsweredWithOption = poll.getPlayerAnsweredWithOption();
                            if (playerAnsweredWithOption.containsKey(player.getUniqueId())) {
                                if (playerAnsweredWithOption.get(player.getUniqueId()) == answer) {
                                    Party.sendMessageToPlayer(player, ChatColor.RED + "You already voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.RED + "!", true, true);
                                } else {
                                    playerAnsweredWithOption.put(player.getUniqueId(), answer);
                                    Party.sendMessageToPlayer(player, ChatColor.GREEN + "You changed your vote to " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", true, true);
                                }
                            } else if (answer > 0 && answer <= poll.getOptions().size()) {
                                playerAnsweredWithOption.put(player.getUniqueId(), answer);
                                Party.sendMessageToPlayer(player, ChatColor.GREEN + "You voted for " + ChatColor.GOLD + poll.getOptions().get(answer - 1) + ChatColor.GREEN + "!", true, true);
                            } else {
                                Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                            }
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Invalid Arguments!", true, true);
                        }
                        return true;
                    }
                    case "pollend": {
                        Party party = currentParty.get();
                        if (party.getPolls().isEmpty()) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "There is no ongoing poll!", true, true);
                            return true;
                        }
                        Poll poll = currentParty.get().getPolls().get(0);
                        poll.setTimeLeft(0);
                        return true;
                    }
                    case "afk":
                        currentParty.get().afk(player.getUniqueId());
                        return true;
                    case "close":
                    case "open": {
                        if (!currentParty.get().getPartyLeader().getUuid().equals((player).getUniqueId())) {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Insufficient Permissions!", true, true);
                            return true;
                        }
                        currentParty.get().setOpen(!input.equalsIgnoreCase("close"));
                        return true;
                    }
                    case "outside": {
                        if (!currentParty.isPresent()) {
                            sender.sendMessage(ChatColor.RED + "You are currently not in a party!");
                            return true;
                        }
                        StringBuilder outside = new StringBuilder(ChatColor.YELLOW + "Players Outside Party: ");
                        int numberOfPlayersOutside = 0;
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (currentParty.get().getPartyPlayers().stream().noneMatch(partyPlayer -> partyPlayer.getUuid().equals(p.getUniqueId()))) {
                                numberOfPlayersOutside++;
                                outside.append(ChatColor.GREEN).append(p.getName()).append(ChatColor.GRAY).append(", ");
                            }
                        }
                        outside.setLength(outside.length() - 2);
                        if (numberOfPlayersOutside == 0) {
                            sender.sendMessage(ChatColor.YELLOW + "There are no players outside of the party");
                        } else {
                            sender.sendMessage(outside.toString());
                        }
                        return true;
                    }
                    case "leader": {
                        currentParty.ifPresent(party -> {
                            if (sender.hasPermission("warlords.party.forceleader")) {
                                party.transfer(sender.getName());
                            } else {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                            }
                        });
                        return true;
                    }
                    case "forcejoin": {
                        currentParty.ifPresent(party -> {
                            if (sender.hasPermission("warlords.party.forcejoin")) {
                                if (args.length < 2) {
                                    sender.sendMessage(ChatColor.RED + "Invalid Arguments! /p forcejoin [NAME or @a]");
                                    return;
                                }
                                String name = args[1];
                                if (name.equalsIgnoreCase("@a")) {
                                    Bukkit.getOnlinePlayers().stream()
                                            .filter(p -> !p.getName().equalsIgnoreCase(party.getLeaderName()) && !party.hasUUID(p.getUniqueId()))
                                            .forEach(p -> Bukkit.getServer().dispatchCommand(p, "p join " + party.getLeaderName()));
                                    return;
                                }
                                Player playerToForceInvite = Bukkit.getPlayer(name);
                                if (playerToForceInvite == null) {
                                    Party.sendMessageToPlayer(player, ChatColor.RED + "Cannot find a player with that name!", true, true);
                                    return;
                                }
                                if (Warlords.partyManager.inSameParty(player.getUniqueId(), playerToForceInvite.getUniqueId())) {
                                    Party.sendMessageToPlayer(player, ChatColor.RED + "That player is already in the party!", true, true);
                                    return;
                                }
                                Bukkit.getServer().dispatchCommand(playerToForceInvite, "p join " + party.getLeaderName());

                            } else {
                                sender.sendMessage(ChatColor.RED + "You do not have permission to do that.");
                            }
                        });
                        return true;
                    }
                    case "allinvite": {
                        Party party = currentParty.get();
                        if (party.getPartyLeader().getUuid().equals((player).getUniqueId())) {
                            party.setAllInvite(!party.isAllInvite());
                            if (party.isAllInvite()) {
                                party.sendMessageToAllPartyPlayers(ChatColor.GREEN + "All invite is now on", true, true);
                            } else {
                                party.sendMessageToAllPartyPlayers(ChatColor.RED + "All invite is now off", true, true);
                            }
                        } else {
                            Party.sendMessageToPlayer(player, ChatColor.RED + "Insufficient Permissions!", true, true);
                        }
                        return true;
                    }
                    case "invitequeue":
                        int partySize = currentParty.get().getPartyPlayers().size();
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
                                toInvite.forEach(uuid -> {
                                    Bukkit.dispatchCommand(sender, "p invite " + Bukkit.getPlayer(uuid).getName());
                                });
                                QueueManager.queue.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
                                QueueManager.sendNewQueue();
                            }
                        }
                        return true;
                    default:
                        Bukkit.getServer().dispatchCommand(sender, "p invite " + input);
                        return true;
                }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        String lastArg = args[args.length - 1];
        List<String> output;
        if (args.length > 1) {
            output = Bukkit.getOnlinePlayers().stream()
                    .filter(e -> e.getName().toLowerCase().startsWith(lastArg.toLowerCase()))
                    .map(e -> e.getName().charAt(0) + e.getName().substring(1))
                    .collect(Collectors.toList());
            Warlords.partyManager.getPartyFromAny(((Player) commandSender).getUniqueId()).ifPresent(party -> party.getPartyPlayers().stream()
                    .filter(PartyPlayer::isOffline)
                    .map(partyPlayer -> Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName())
                    .forEach(output::add));
            return output;
        }
        output = Bukkit.getOnlinePlayers().stream()
                .map(HumanEntity::getName)
                .filter(e -> e.toLowerCase().startsWith(lastArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
        Arrays.stream(partyOptions)
                .filter(e -> e.startsWith(lastArg.toLowerCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .forEach(output::add);
        Warlords.partyManager.getPartyFromAny(((Player) commandSender).getUniqueId()).ifPresent(party -> party.getPartyPlayers().stream()
                .filter(PartyPlayer::isOffline)
                .map(partyPlayer -> Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName())
                .forEach(output::add));
        return output;

    }

    public void register(Warlords instance) {
        instance.getCommand("party").setExecutor(this);
        instance.getCommand("party").setTabCompleter(this);
    }

}
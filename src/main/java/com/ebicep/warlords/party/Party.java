package com.ebicep.warlords.party;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class Party {

    private final List<PartyPlayer> partyPlayers = new ArrayList<>();
    private final List<Poll> polls = new ArrayList<>();
    private final HashMap<UUID, Integer> invites = new HashMap<>();
    private final BukkitTask partyTask;
    private final RegularGamesMenu regularGamesMenu = new RegularGamesMenu(this);
    private boolean open = false;
    private boolean allInvite = false;

    public Party(UUID leader, boolean open) {
        partyPlayers.add(new PartyPlayer(leader, PartyPlayerType.LEADER));
        this.open = open;
        partyTask = new BukkitRunnable() {

            @Override
            public void run() {
                invites.forEach((uuid, integer) -> invites.put(uuid, integer - 1));
                invites.entrySet().removeIf(invite -> {
                    if (invite.getValue() <= 0) {
                        sendMessageToAllPartyPlayers(
                                ChatColor.RED + "The party invite to " + ChatColor.AQUA + Bukkit.getOfflinePlayer(invite.getKey()).getName() + ChatColor.RED + " has expired!",
                                true,
                                true);
                    }
                    return invite.getValue() <= 0;
                });
                for (int i = 0; i < partyPlayers.size(); i++) {
                    PartyPlayer partyPlayer = partyPlayers.get(i);
                    if (partyPlayer != null && partyPlayer.getOfflineTimeLeft() != -1) {
                        int offlineTimeLeft = partyPlayer.getOfflineTimeLeft();
                        partyPlayer.setOfflineTimeLeft(offlineTimeLeft - 1);
                        if (offlineTimeLeft == 0) {
                            leave(partyPlayer.getUuid());
                            i--;
                        } else {
                            if (offlineTimeLeft % 60 == 0) {
                                sendMessageToAllPartyPlayers(
                                        ChatColor.AQUA + Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName() + ChatColor.YELLOW + " has " + ChatColor.RED + (offlineTimeLeft / 60) + ChatColor.YELLOW + " minutes to rejoin before getting kicked!",
                                        true,
                                        true);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public static void sendMessageToPlayer(Player player, String message, boolean withBorder, boolean centered) {
        if (centered) {
            if (withBorder) {
                ChatUtils.sendCenteredMessage(player, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            String[] messages = message.split("\n");
            for (String s : messages) {
                ChatUtils.sendCenteredMessage(player, s);
            }
            if (withBorder) {
                ChatUtils.sendCenteredMessage(player, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if (withBorder) {
                player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            player.sendMessage(message);
            if (withBorder) {
                player.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        }
    }

    public void invite(String name) {
        Player player = Bukkit.getPlayer(name);
        invites.put(player.getUniqueId(), 60);
    }

    public void join(UUID uuid) {
        invites.remove(uuid);
        partyPlayers.add(new PartyPlayer(uuid, PartyPlayerType.MEMBER));
        Player player = Bukkit.getPlayer(uuid);
        sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " joined the party", true, true);
        if (player.hasPermission("warlords.party.automoderator")) {
            promote(Bukkit.getOfflinePlayer(uuid).getName());
        }
        Bukkit.getPlayer(uuid).sendMessage(getPartyList());
        BotManager.sendStatusMessage(false);
    }

    public void leave(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PartyPlayer partyPlayer = getPartyPlayerByUUID(uuid);
        if (partyPlayer == null) return;

        partyPlayers.remove(partyPlayer);
        //if leader leaves
        if (partyPlayer.getPartyPlayerType() == PartyPlayerType.LEADER) {
            //disband party if no other members
            if (partyPlayers.isEmpty()) {
                if (partyPlayer.isOnline()) {
                    sendMessageToPlayer(player.getPlayer(), ChatColor.RED + "The party was disbanded", true, true);
                }
                disband();
            } else {
                //promote if moderators or else promote first person that joined
                PartyPlayer playerToPromote = partyPlayers.stream()
                        .filter(p -> p.getPartyPlayerType() == PartyPlayerType.MODERATOR)
                        .findFirst()
                        .orElse(partyPlayers.get(0));
                playerToPromote.setPartyPlayerType(PartyPlayerType.LEADER);

                sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", true, true);
                sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(playerToPromote.getUuid()).getName() + ChatColor.GREEN + " is now the new party leader", true, true);
            }
        } else {
            sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", true, true);
        }
    }

    public void transfer(String name) {
        partyPlayers.stream()
                .filter(partyPlayer -> Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    getPartyLeader().setPartyPlayerType(PartyPlayerType.MODERATOR);
                    partyPlayer.setPartyPlayerType(PartyPlayerType.LEADER);
                    String newLeaderName = Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName();
                    if (newLeaderName.equalsIgnoreCase("Plikie") || newLeaderName.equalsIgnoreCase("sumSmash")) {
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + newLeaderName + ChatColor.GREEN + " has hijacked the party!", true, true);
                    } else {
                        sendMessageToAllPartyPlayers(ChatColor.GREEN + "The party was transferred to " + ChatColor.AQUA + newLeaderName, true, true);
                    }
                });
    }

    public void remove(String name) {
        partyPlayers.stream()
                .filter(partyPlayer -> Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName().equalsIgnoreCase(name))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    partyPlayers.remove(partyPlayer);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(partyPlayer.getUuid());
                    sendMessageToAllPartyPlayers(ChatColor.AQUA + offlinePlayer.getName() + ChatColor.RED + " was removed from the party", true, true);
                    if (offlinePlayer.isOnline()) {
                        sendMessageToPlayer(offlinePlayer.getPlayer(), ChatColor.RED + "You were removed from the party", true, true);
                    }
                });
    }

    public void disband() {
        Warlords.partyManager.disbandParty(this);
        sendMessageToAllPartyPlayers(ChatColor.DARK_RED + "The party was disbanded", true, true);
        partyTask.cancel();
    }

    public String getPartyList() {
        PartyPlayer leader = getPartyLeader();
        StringBuilder stringBuilder = new StringBuilder(ChatColor.BLUE + "-----------------------------\n")
                .append(ChatColor.GOLD + "Party Members (").append(partyPlayers.size()).append(")\n \n")
                .append(ChatColor.YELLOW + "Party Leader: " + ChatColor.AQUA).append(Bukkit.getOfflinePlayer(leader.getUuid()).getName()).append(leader.getPartyListDot()).append("\n");

        List<PartyPlayer> moderators = getPartyModerators();
        if (!moderators.isEmpty()) {
            stringBuilder.append(ChatColor.YELLOW + "Party Moderators: " + ChatColor.AQUA);
            moderators.forEach(partyPlayer -> stringBuilder
                    .append(ChatColor.AQUA)
                    .append(Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName())
                    .append(partyPlayer.getPartyListDot())
            );
            stringBuilder.append("\n");
        }

        List<PartyPlayer> members = getPartyMembers();
        if (!members.isEmpty()) {
            stringBuilder.append(ChatColor.YELLOW + "Party Members: " + ChatColor.AQUA);
            members.forEach(partyPlayer -> stringBuilder
                    .append(ChatColor.AQUA)
                    .append(Bukkit.getOfflinePlayer(partyPlayer.getUuid()).getName())
                    .append(partyPlayer.getPartyListDot())
            );
        }
        stringBuilder.append(ChatColor.BLUE + "\n-----------------------------");
        return stringBuilder.toString();
    }

    public void afk(UUID uuid) {
        partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getUuid().equals(uuid))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    partyPlayer.setAFK(!partyPlayer.isAFK());
                    if (partyPlayer.isAFK()) {
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.RED + " is now AFK", true, true);
                    } else {
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.GREEN + " is no longer AFK", true, true);
                    }
                });
    }

    public void promote(String name) {
        UUID uuid = Bukkit.getOfflinePlayer(name).getUniqueId();
        if (getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(uuid))) {
            transfer(name);
        } else {
            partyPlayers.stream()
                    .filter(partyPlayer -> partyPlayer.getUuid().equals(uuid))
                    .findFirst()
                    .ifPresent(partyPlayer -> partyPlayer.setPartyPlayerType(PartyPlayerType.MODERATOR));
            sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.YELLOW + " was promoted to Party Moderator", true, true);
        }
    }

    public void demote(String name) {
        partyPlayers.stream()
                .filter(partyPlayer -> Bukkit.getOfflinePlayer(name).getUniqueId().equals(partyPlayer.getUuid()))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    if (partyPlayer.getPartyPlayerType() == PartyPlayerType.MODERATOR) {
                        partyPlayer.setPartyPlayerType(PartyPlayerType.MEMBER);
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(name).getName() + ChatColor.YELLOW + " was demoted to Party Member", true, true);
                    }
                });
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (open) {
            sendMessageToAllPartyPlayers(ChatColor.GREEN + "The party is now open", true, true);
        } else {
            sendMessageToAllPartyPlayers(ChatColor.RED + "The party is now closed", true, true);
        }
    }

    public boolean isAllInvite() {
        return allInvite;
    }

    public void setAllInvite(boolean allInvite) {
        this.allInvite = allInvite;
    }

    public PartyPlayer getPartyLeader() {
        return partyPlayers.stream().filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.LEADER).findFirst().get();
    }

    public String getLeaderName() {
        return Bukkit.getOfflinePlayer(getPartyLeader().getUuid()).getName();
    }

    public List<PartyPlayer> getPartyModerators() {
        return partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.MODERATOR)
                .sorted(Comparator.comparing(PartyPlayer::isOffline)
                        .thenComparing(PartyPlayer::isAFK))
                .collect(Collectors.toList());
    }

    public List<PartyPlayer> getPartyMembers() {
        return partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.MEMBER)
                .sorted(Comparator.comparing(PartyPlayer::isOffline)
                        .thenComparing(PartyPlayer::isAFK))
                .collect(Collectors.toList());
    }

    public PartyPlayer getPartyPlayerByUUID(UUID uuid) {
        return partyPlayers.stream().filter(partyPlayer -> partyPlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public void sendMessageToAllPartyPlayers(String message, boolean withBorder, boolean centered) {
        getAllPartyPeoplePlayerOnline().forEach(partyMember -> {
            sendMessageToPlayer(partyMember, message, withBorder, centered);
        });
    }

    public List<Player> getAllPartyPeoplePlayerOnline() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getPartyPlayers().stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId())))
                .collect(Collectors.toList());
    }

    public List<PartyPlayer> getPartyPlayers() {
        return partyPlayers;
    }

    public boolean allOnlineAndNoAFKs() {
        return partyPlayers.stream().noneMatch(partyPlayer -> !partyPlayer.isOnline() || partyPlayer.isAFK());
    }

    public boolean hasUUID(UUID uuid) {
        return partyPlayers.stream().anyMatch(partyPlayer -> partyPlayer.getUuid().equals(uuid));
    }

    public void addPoll(PollBuilder pollBuilder) {
        pollBuilder.setParty(this);
        polls.add(pollBuilder.get());
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public HashMap<UUID, Integer> getInvites() {
        return invites;
    }

    public RegularGamesMenu getRegularGamesMenu() {
        return regularGamesMenu;
    }
}

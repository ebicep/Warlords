package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.poll.polls.PartyPoll;
import com.ebicep.warlords.util.chat.ChatUtils;
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
    private final List<PartyPoll> polls = new ArrayList<>();
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
                                ChatColor.BLUE, true);
                    }
                    return invite.getValue() <= 0;
                });
                for (int i = 0; i < partyPlayers.size(); i++) {
                    PartyPlayer partyPlayer = partyPlayers.get(i);
                    if (partyPlayer != null && partyPlayer.getOfflineTimeLeft() != -1) {
                        int offlineTimeLeft = partyPlayer.getOfflineTimeLeft();
                        partyPlayer.setOfflineTimeLeft(offlineTimeLeft - 1);
                        if (offlineTimeLeft == 0) {
                            leave(partyPlayer.getUUID());
                            i--;
                        } else {
                            if (offlineTimeLeft % 60 == 0) {
                                sendMessageToAllPartyPlayers(
                                        ChatColor.AQUA + Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName() + ChatColor.YELLOW + " has " + ChatColor.RED + (offlineTimeLeft / 60) + ChatColor.YELLOW + " minutes to rejoin before getting kicked!",
                                        ChatColor.BLUE, true);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public static void sendPartyMessage(Player player, String message) {
        ChatUtils.sendMessageToPlayer(player, message, ChatColor.BLUE, true);
    }

    public void invite(String name) {
        Player player = Bukkit.getPlayer(name);
        invites.put(player.getUniqueId(), 60);
    }

    public void invite(UUID uuid) {
        invites.put(uuid, 60);
    }

    public void join(UUID uuid) {
        invites.remove(uuid);
        partyPlayers.add(new PartyPlayer(uuid, PartyPlayerType.MEMBER));
        Player player = Bukkit.getPlayer(uuid);
        sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.GREEN + " joined the party", ChatColor.BLUE, true);
        if (player.hasPermission("warlords.party.automoderator")) {
            promote(uuid);
        }
        Bukkit.getPlayer(uuid).sendMessage(getPartyList());
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
                    sendPartyMessage(player.getPlayer(), ChatColor.RED + "The party was disbanded");
                }
                disband();
            } else {
                //promote if moderators or else promote first person that joined
                PartyPlayer playerToPromote = partyPlayers.stream()
                        .filter(p -> p.getPartyPlayerType() == PartyPlayerType.MODERATOR)
                        .findFirst()
                        .orElse(partyPlayers.get(0));
                playerToPromote.setPartyPlayerType(PartyPlayerType.LEADER);

                sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", ChatColor.BLUE, true);
                sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(playerToPromote.getUUID()).getName() + ChatColor.GREEN + " is now the new party leader", ChatColor.BLUE, true);
            }
        } else {
            sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", ChatColor.BLUE, true);
        }
    }

    public void transfer(UUID uuid) {
        partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    getPartyLeader().setPartyPlayerType(PartyPlayerType.MODERATOR);
                    partyPlayer.setPartyPlayerType(PartyPlayerType.LEADER);
                    String newLeaderName = Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName();
                    sendMessageToAllPartyPlayers(ChatColor.GREEN + "The party was transferred to " + ChatColor.AQUA + newLeaderName, ChatColor.BLUE, true);
                });
    }

    public void remove(UUID uuid) {
        partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    partyPlayers.remove(partyPlayer);
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(partyPlayer.getUUID());
                    sendMessageToAllPartyPlayers(ChatColor.AQUA + offlinePlayer.getName() + ChatColor.RED + " was removed from the party", ChatColor.BLUE, true);
                    if (offlinePlayer.isOnline()) {
                        sendPartyMessage(offlinePlayer.getPlayer(), ChatColor.RED + "You were removed from the party");
                    }
                });
    }

    public void disband() {
        PartyManager.disbandParty(this);
        sendMessageToAllPartyPlayers(ChatColor.DARK_RED + "The party was disbanded", ChatColor.BLUE, true);
        partyTask.cancel();
    }

    public String getPartyList() {
        PartyPlayer leader = getPartyLeader();
        StringBuilder stringBuilder = new StringBuilder(ChatColor.BLUE + "-----------------------------\n").append(ChatColor.GOLD).append("Party Members (").append(partyPlayers.size())
                .append(")\n \n").append(ChatColor.YELLOW).append("Party Leader: ")
                .append(ChatColor.AQUA).append(Bukkit.getOfflinePlayer(leader.getUUID()).getName()).append(leader.getPartyListDot()).append("\n");

        List<PartyPlayer> moderators = getPartyModerators();
        if (!moderators.isEmpty()) {
            stringBuilder.append(ChatColor.YELLOW).append("Party Moderators: ").append(ChatColor.AQUA);
            moderators.forEach(partyPlayer -> stringBuilder
                    .append(ChatColor.AQUA)
                    .append(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName())
                    .append(partyPlayer.getPartyListDot())
            );
            stringBuilder.append("\n");
        }

        List<PartyPlayer> members = getPartyMembers();
        if (!members.isEmpty()) {
            stringBuilder.append(ChatColor.YELLOW).append("Party Members: ").append(ChatColor.AQUA);
            members.forEach(partyPlayer -> stringBuilder
                    .append(ChatColor.AQUA)
                    .append(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName())
                    .append(partyPlayer.getPartyListDot())
            );
        }
        stringBuilder.append(ChatColor.BLUE).append("\n-----------------------------");
        return stringBuilder.toString();
    }

    public void afk(UUID uuid) {
        partyPlayers.stream()
                .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    partyPlayer.setAFK(!partyPlayer.isAFK());
                    if (partyPlayer.isAFK()) {
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.RED + " is now AFK", ChatColor.BLUE, true);
                    } else {
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.GREEN + " is no longer AFK", ChatColor.BLUE, true);
                    }
                });
    }

    public void promote(UUID uuid) {
        if (getPartyModerators().stream().anyMatch(partyPlayer -> partyPlayer.getUUID().equals(uuid))) {
            transfer(uuid);
        } else {
            partyPlayers.stream()
                    .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                    .findFirst()
                    .ifPresent(partyPlayer -> partyPlayer.setPartyPlayerType(PartyPlayerType.MODERATOR));
            sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.YELLOW + " was promoted to Party Moderator", ChatColor.BLUE, true);
        }
    }

    public void demote(UUID uuid) {
        partyPlayers.stream()
                .filter(partyPlayer -> uuid.equals(partyPlayer.getUUID()))
                .findFirst()
                .ifPresent(partyPlayer -> {
                    if (partyPlayer.getPartyPlayerType() == PartyPlayerType.MODERATOR) {
                        partyPlayer.setPartyPlayerType(PartyPlayerType.MEMBER);
                        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(uuid).getName() + ChatColor.YELLOW + " was demoted to Party Member", ChatColor.BLUE, true);
                    }
                });
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (open) {
            sendMessageToAllPartyPlayers(ChatColor.GREEN + "The party is now open", ChatColor.BLUE, true);
        } else {
            sendMessageToAllPartyPlayers(ChatColor.RED + "The party is now closed", ChatColor.BLUE, true);
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
        return Bukkit.getOfflinePlayer(getPartyLeader().getUUID()).getName();
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
        return partyPlayers.stream().filter(partyPlayer -> partyPlayer.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public void sendMessageToAllPartyPlayers(String message, ChatColor borderColor, boolean centered) {
        getAllPartyPeoplePlayerOnline().forEach(partyMember -> {
            ChatUtils.sendMessageToPlayer(partyMember, message, borderColor, centered);
        });
    }

    public List<Player> getAllPartyPeoplePlayerOnline() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getPartyPlayers().stream().anyMatch(partyPlayer -> partyPlayer.getUUID().equals(player.getUniqueId())))
                .collect(Collectors.toList());
    }

    public List<PartyPlayer> getPartyPlayers() {
        return partyPlayers;
    }

    public boolean allOnlineAndNoAFKs() {
        return partyPlayers.stream().noneMatch(partyPlayer -> !partyPlayer.isOnline() || partyPlayer.isAFK());
    }

    public boolean hasUUID(UUID uuid) {
        return partyPlayers.stream().anyMatch(partyPlayer -> partyPlayer.getUUID().equals(uuid));
    }

    public void addPoll(PartyPoll poll) {
        polls.add(poll);
    }

    public List<PartyPoll> getPolls() {
        return polls;
    }

    public HashMap<UUID, Integer> getInvites() {
        return invites;
    }

    public RegularGamesMenu getRegularGamesMenu() {
        return regularGamesMenu;
    }
}

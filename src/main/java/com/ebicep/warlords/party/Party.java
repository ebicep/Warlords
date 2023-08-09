package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.poll.polls.PartyPoll;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
    private boolean open;
    private boolean allInvite = false;

    public Party(UUID leader) {
        this(leader, false);
    }

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
                                Component.text("The party invite to ", NamedTextColor.RED)
                                         .append(Component.text(Bukkit.getOfflinePlayer(invite.getKey()).getName(), NamedTextColor.AQUA))
                                         .append(Component.text(" has expired!"))
                        );
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
                                        Component.empty().color(NamedTextColor.YELLOW)
                                                 .append(Component.text(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName(), NamedTextColor.AQUA))
                                                 .append(Component.text(" has "))
                                                 .append(Component.text((offlineTimeLeft / 60), NamedTextColor.RED))
                                                 .append(Component.text(" minutes to rejoin before getting kicked!"))
                                );
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public void invite(UUID uuid) {
        invites.put(uuid, 60);
    }

    public void join(UUID uuid) {
        invites.remove(uuid);
        partyPlayers.add(new PartyPlayer(uuid, PartyPlayerType.MEMBER));
        Player player = Bukkit.getPlayer(uuid);
        sendMessageToAllPartyPlayers(Component.text(player.getName(), NamedTextColor.AQUA).append(Component.text(" joined the party", NamedTextColor.GREEN)));
        if (player.hasPermission("warlords.party.automoderator")) {
            promote(uuid);
        }
        Bukkit.getPlayer(uuid).sendMessage(getPartyList());
    }

    public void leave(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        PartyPlayer partyPlayer = getPartyPlayerByUUID(uuid);
        if (partyPlayer == null) {
            return;
        }

        partyPlayers.remove(partyPlayer);
        //if leader leaves
        if (partyPlayer.getPartyPlayerType() == PartyPlayerType.LEADER) {
            //disband party if no other members
            if (partyPlayers.isEmpty()) {
                if (partyPlayer.isOnline()) {
                    sendPartyMessage(player.getPlayer(), Component.text("The party was disbanded", NamedTextColor.RED));
                }
                disband();
            } else {
                //promote if moderators or else promote first person that joined
                PartyPlayer playerToPromote = partyPlayers.stream()
                                                          .filter(p -> p.getPartyPlayerType() == PartyPlayerType.MODERATOR)
                                                          .findFirst()
                                                          .orElse(partyPlayers.get(0));
                playerToPromote.setPartyPlayerType(PartyPlayerType.LEADER);

                sendMessageToAllPartyPlayers(Component.text(player.getName(), NamedTextColor.AQUA).append(Component.text(" left the party", NamedTextColor.RED)));
                sendMessageToAllPartyPlayers(Component.text(Bukkit.getOfflinePlayer(playerToPromote.getUUID()).getName(), NamedTextColor.AQUA)
                                                      .append(Component.text(" is now the new party leader", NamedTextColor.GREEN))
                );
            }
        } else {
            sendMessageToAllPartyPlayers(Component.text(player.getName(), NamedTextColor.AQUA).append(Component.text(" left the party",
                    NamedTextColor.RED
            )));
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
                        sendMessageToAllPartyPlayers(Component.text("The party was transferred to ", NamedTextColor.GREEN)
                                                              .append(Component.text(newLeaderName, NamedTextColor.AQUA)));
                    });
    }

    public void remove(UUID uuid) {
        partyPlayers.stream()
                    .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                    .findFirst()
                    .ifPresent(partyPlayer -> {
                        partyPlayers.remove(partyPlayer);
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(partyPlayer.getUUID());
                        sendMessageToAllPartyPlayers(Component.text(offlinePlayer.getName(), NamedTextColor.AQUA)
                                                              .append(Component.text(" was removed from the party", NamedTextColor.RED)));
                        if (offlinePlayer.isOnline()) {
                            sendPartyMessage(offlinePlayer.getPlayer(), Component.text("You were removed from the party", NamedTextColor.RED));
                        }
                    });
    }

    public void sendMessageToAllPartyPlayers(Component message) {
        getAllPartyPeoplePlayerOnline().forEach(partyMember -> ChatUtils.sendMessageToPlayer(partyMember, message, NamedTextColor.BLUE, true));
    }

    public static void sendPartyMessage(Player player, Component message) {
        ChatUtils.sendMessageToPlayer(player, message, NamedTextColor.BLUE, true);
    }

    public List<Player> getAllPartyPeoplePlayerOnline() {
        return Bukkit.getOnlinePlayers().stream()
                     .filter(player -> getPartyPlayers().stream().anyMatch(partyPlayer -> partyPlayer.getUUID().equals(player.getUniqueId())))
                     .collect(Collectors.toList());
    }

    public List<PartyPlayer> getPartyPlayers() {
        return partyPlayers;
    }

    public void disband() {
        PartyManager.disbandParty(this);
        sendMessageToAllPartyPlayers(Component.text("The party was disbanded", NamedTextColor.DARK_RED));
        partyTask.cancel();
    }

    public Component getPartyList() {
        PartyPlayer leader = getPartyLeader();
        TextComponent.Builder list = Component.text()
                                              .append(Component.text("-----------------------------", NamedTextColor.BLUE))
                                              .append(Component.newline())
                                              .append(Component.text("Party Members (" + partyPlayers.size() + ")\n \n", NamedTextColor.GOLD))
                                              .append(Component.newline())
                                              .append(Component.text("Party Leader: ", NamedTextColor.YELLOW))
                                              .append(Component.text(Bukkit.getOfflinePlayer(leader.getUUID()).getName(), NamedTextColor.AQUA))
                                              .append(leader.getPartyListDot())
                                              .append(Component.newline());

        List<PartyPlayer> moderators = getPartyModerators();
        if (!moderators.isEmpty()) {
            list.append(Component.text("Party Moderators: ", NamedTextColor.YELLOW));
            moderators.forEach(partyPlayer -> list
                    .append(Component.text(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName(), NamedTextColor.AQUA))
                    .append(partyPlayer.getPartyListDot())
            );
            list.append(Component.newline());
        }

        List<PartyPlayer> members = getPartyMembers();
        if (!members.isEmpty()) {
            list.append(Component.text("Party Members: ", NamedTextColor.YELLOW));
            members.forEach(partyPlayer -> list
                    .append(Component.text(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName(), NamedTextColor.AQUA))
                    .append(partyPlayer.getPartyListDot())
            );
        }

        list.append(Component.newline());
        list.append(Component.text("-----------------------------", NamedTextColor.BLUE));
        return list.build();
    }

    public void afk(UUID uuid) {
        partyPlayers.stream()
                    .filter(partyPlayer -> partyPlayer.getUUID().equals(uuid))
                    .findFirst()
                    .ifPresent(partyPlayer -> {
                        partyPlayer.setAFK(!partyPlayer.isAFK());
                        if (partyPlayer.isAFK()) {
                            sendMessageToAllPartyPlayers(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA)
                                                                  .append(Component.text(" is now AFK", NamedTextColor.RED)));
                        } else {
                            sendMessageToAllPartyPlayers(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA)
                                                                  .append(Component.text(" is no longer AFK", NamedTextColor.GREEN)));
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
            sendMessageToAllPartyPlayers(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA)
                                                  .append(Component.text(" was promoted to Party Moderator", NamedTextColor.YELLOW)));
        }
    }

    public void demote(UUID uuid) {
        partyPlayers.stream()
                    .filter(partyPlayer -> uuid.equals(partyPlayer.getUUID()))
                    .findFirst()
                    .ifPresent(partyPlayer -> {
                        if (partyPlayer.getPartyPlayerType() == PartyPlayerType.MODERATOR) {
                            partyPlayer.setPartyPlayerType(PartyPlayerType.MEMBER);
                            sendMessageToAllPartyPlayers(Component.text(Bukkit.getOfflinePlayer(uuid).getName(), NamedTextColor.AQUA)
                                                                  .append(Component.text(" was demoted to Party Member", NamedTextColor.YELLOW)));
                        }
                    });
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
        if (open) {
            sendMessageToAllPartyPlayers(Component.text("The party is now open", NamedTextColor.GREEN));
        } else {
            sendMessageToAllPartyPlayers(Component.text("The party is now closed", NamedTextColor.RED));
        }
    }

    public boolean isAllInvite() {
        return allInvite;
    }

    public void setAllInvite(boolean allInvite) {
        this.allInvite = allInvite;
    }

    public String getLeaderName() {
        return Bukkit.getOfflinePlayer(getPartyLeader().getUUID()).getName();
    }

    public PartyPlayer getPartyLeader() {
        return partyPlayers.stream().filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.LEADER).findFirst().get();
    }

    public List<PartyPlayer> getPartyModerators() {
        return partyPlayers.stream()
                           .filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.MODERATOR)
                           .sorted(Comparator.comparing(PartyPlayer::isOffline)
                                             .thenComparing(PartyPlayer::isAFK))
                           .toList();
    }

    public List<PartyPlayer> getPartyMembers() {
        return partyPlayers.stream()
                           .filter(partyPlayer -> partyPlayer.getPartyPlayerType() == PartyPlayerType.MEMBER)
                           .sorted(Comparator.comparing(PartyPlayer::isOffline)
                                             .thenComparing(PartyPlayer::isAFK))
                           .toList();
    }

    public PartyPlayer getPartyPlayerByUUID(UUID uuid) {
        return partyPlayers.stream().filter(partyPlayer -> partyPlayer.getUUID().equals(uuid)).findFirst().orElse(null);
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

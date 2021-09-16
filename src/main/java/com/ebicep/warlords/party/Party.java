package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class Party {

    private UUID leader; //uuid of leader
    private LinkedHashMap<UUID, Boolean> members = new LinkedHashMap<>(); //members includes leader
    private boolean isOpen;
    private List<Poll> polls = new ArrayList<>(); //in the future allow for multiple polls at once?
    private HashMap<UUID, Integer> invites = new HashMap<>();
    private BukkitTask partyTask;

    public Party(UUID leader, boolean isOpen) {
        this.leader = leader;
        this.members.put(leader, true);
        this.isOpen = isOpen;
        partyTask = new BukkitRunnable() {

            @Override
            public void run() {
                invites.forEach((uuid, integer) -> invites.put(uuid, integer - 1));
                invites.entrySet().removeIf(invite ->  {
                    if(invite.getValue() <= 0) {
                        sendMessageToAllPartyPlayers(
                                ChatColor.RED + "The party invite to " + ChatColor.AQUA + Bukkit.getOfflinePlayer(invite.getKey()).getName() + ChatColor.RED + " has expired!",
                                true,
                                true);
                    }
                    return invite.getValue() <= 0;
                });
            }
        }.runTaskTimer(Warlords.getInstance(), 0, 20);
    }

    public UUID getLeader() {
        return leader;
    }

    public String getLeaderName() {
        return Bukkit.getOfflinePlayer(leader).getName();
    }

    public LinkedHashMap<UUID, Boolean> getMembers() {
        return members;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void invite(String name) {
        Player player = Bukkit.getPlayer(name);
        invites.put(player.getUniqueId(), 60);
    }

    public void join(UUID uuid) {
        invites.remove(uuid);
        members.put(uuid, true);
        sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getPlayer(uuid).getName() + ChatColor.GREEN + " joined the party", true, true);
    }

    public void leave(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        members.remove(uuid);
        if (leader.equals(uuid)) {
            if (members.keySet().stream().findAny().isPresent()) {
                leader = members.keySet().stream().findFirst().get();
                sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", true, true);
                sendMessageToAllPartyPlayers(ChatColor.AQUA + Bukkit.getOfflinePlayer(leader).getName() + ChatColor.GREEN + " is now the new party leader", true, true);
            } else {
                Bukkit.getPlayer(leader).sendMessage(ChatColor.RED + "The party was disbanded");
                disband();
            }
        } else {
            sendMessageToAllPartyPlayers(ChatColor.AQUA + player.getName() + ChatColor.RED + " left the party", true, true);
        }
    }

    public void disband() {
        Warlords.partyManager.disbandParty(this);
        sendMessageToAllPartyPlayers(ChatColor.DARK_RED + "The party was disbanded", true, true);
        partyTask.cancel();
    }

    public String getList() {
        StringBuilder stringBuilder = new StringBuilder(ChatColor.BLUE + "-----------------------------\n")
                .append(ChatColor.GOLD + "Party Members (").append(members.size()).append(")\n \n")
                .append(ChatColor.YELLOW + "Party Leader: " + ChatColor.AQUA).append(Bukkit.getOfflinePlayer(leader).getName()).append(members.get(leader) ? ChatColor.GREEN : ChatColor.RED).append(" ● \n");
        if(members.size() > 1) {
            stringBuilder.append(ChatColor.YELLOW + "Party Members: " + ChatColor.AQUA);
        }
        members.forEach((uuid, isOnline) -> {
            if(uuid != leader) {
                stringBuilder.append(ChatColor.AQUA).append(Bukkit.getOfflinePlayer(uuid).getName()).append(isOnline ? ChatColor.GREEN : ChatColor.RED).append(" ● ");
            }
        });
        stringBuilder.append(ChatColor.BLUE + "\n-----------------------------");
        return stringBuilder.toString();
    }

    public void remove(String name) {
        for (Map.Entry<UUID, Boolean> uuidBooleanEntry : members.entrySet()) {
            String partyMemberName = Bukkit.getOfflinePlayer(uuidBooleanEntry.getKey()).getName();
            if(partyMemberName.equalsIgnoreCase(name)) {
                members.remove(uuidBooleanEntry.getKey());
                sendMessageToAllPartyPlayers(ChatColor.AQUA + partyMemberName + ChatColor.RED + " was removed from the party", true, true);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if(player.getUniqueId().equals(uuidBooleanEntry.getKey())) {
                        player.sendMessage(ChatColor.RED + "You were removed from the party");
                        break;
                    }
                }
                break;
            }
        }
    }

    public void setOpen(boolean open) {
        isOpen = open;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getUniqueId().equals(leader)) {
                if(open) {
                    sendMessageToAllPartyPlayers(ChatColor.GREEN + "The party is now open", true, true);
                } else {
                    sendMessageToAllPartyPlayers(ChatColor.RED + "The party is now closed", true, true);
                }
                return;
            }
        }

    }

    public static void sendMessageToPlayer(Player partyMember, String message, boolean withBorder, boolean centered) {
        if(centered) {
            if(withBorder) {
                Utils.sendCenteredMessage(partyMember, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            String[] messages = message.split("\n");
            for (String s : messages) {
                Utils.sendCenteredMessage(partyMember, s);
            }
            if(withBorder) {
                Utils.sendCenteredMessage(partyMember, ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        } else {
            if(withBorder) {
                partyMember.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
            partyMember.sendMessage(message);
            if(withBorder) {
                partyMember.sendMessage(ChatColor.BLUE.toString() + ChatColor.BOLD + "------------------------------------------");
            }
        }
    }

    public void sendMessageToAllPartyPlayers(String message, boolean withBorder, boolean centered) {
        getAllPartyPeoplePlayerOnline().forEach(partyMember -> {
            sendMessageToPlayer(partyMember, message, withBorder, centered);
        });
    }

    public boolean hasUUID(UUID uuid) {
        return members.containsKey(uuid);
    }

    public List<UUID> getAllPartyPeopleUUID(boolean online) {
        List<UUID> output = new ArrayList<>();
        members.forEach((uuid, isOnline) -> {
            if (isOnline == online) {
                output.add(uuid);
            }
        });
        return output;
    }

    public List<UUID> getAllPartyPeople() {
        return new ArrayList<>(members.keySet());
    }

    public boolean allOnline() {
        return members.values().stream().allMatch(b -> b);
    }

    public List<Player> getAllPartyPeoplePlayerOnline() {
        return Bukkit.getOnlinePlayers().stream()
                .filter(player -> getAllPartyPeople().contains(player.getUniqueId()))
                .collect(Collectors.toList());
    }

    public void addPoll(String question, List<String> options) {
        polls.add(new Poll(this, question, options));
    }

    public List<Poll> getPolls() {
        return polls;
    }

    public HashMap<UUID, Integer> getInvites() {
        return invites;
    }
}

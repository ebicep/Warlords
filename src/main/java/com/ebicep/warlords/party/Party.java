package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Party {

    //leader of party
    private UUID leader;
    //members includes leader
    private LinkedHashMap<UUID, Boolean> members = new LinkedHashMap<>();
    private boolean isOpen;

    public Party(UUID leader, boolean isOpen) {
        this.leader = leader;
        this.members.put(leader, true);
        this.isOpen = isOpen;
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

    public void join(UUID uuid) {
        members.put(uuid, true);
        sendMessageToAllPartyPlayers(ChatColor.GREEN + Bukkit.getPlayer(uuid).getName() + " has joined the party");
    }

    public void leave(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        members.remove(uuid);
        if (leader.equals(uuid)) {
            if (members.keySet().stream().findAny().isPresent()) {
                leader = members.keySet().stream().findFirst().get();
                sendMessageToAllPartyPlayers(ChatColor.RED + player.getName() + " has left the party");
                sendMessageToAllPartyPlayers(ChatColor.GREEN + Bukkit.getOfflinePlayer(leader).getName() + " is now the new party leader");
            } else {
                Bukkit.getPlayer(leader).sendMessage(ChatColor.RED + "The party was disbanded");
                disband();
            }
        } else {
            sendMessageToAllPartyPlayers(ChatColor.RED + player.getName() + " has left the party");
        }
    }

    public void disband() {
        Warlords.partyManager.disbandParty(this);
        sendMessageToAllPartyPlayers(ChatColor.DARK_RED + "The party was disbanded");
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
                sendMessageToAllPartyPlayers(ChatColor.RED + partyMemberName + " was removed from the party");
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
                    player.sendMessage(ChatColor.GREEN + "The party is now open!");
                } else {
                    player.sendMessage(ChatColor.RED + "The party is now closed!");
                }
                break;
            }
        }
    }

    public void sendMessageToAllPartyPlayers(String message) {
        getAllPartyPeoplePlayerOnline().forEach(partyMember -> {
            partyMember.sendMessage(message);
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
}

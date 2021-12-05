package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PartyListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        party.ifPresent(p -> {
            p.getMembers().put(player.getUniqueId(), true);
            p.getDisconnects().remove(player.getUniqueId());
        });
        if (!party.isPresent() && !Warlords.partyManager.getParties().isEmpty()) {
            StringBuilder parties = new StringBuilder(ChatColor.YELLOW + "Current parties: ");
            for (Party partyManagerParty : Warlords.partyManager.getParties()) {
                parties.append(ChatColor.AQUA).append(partyManagerParty.getLeaderName()).append(ChatColor.GRAY).append(", ");
            }
            parties.setLength(parties.length() - 2);
            player.sendMessage(parties.toString());
        }
        //queue
        Bukkit.dispatchCommand(player, "queue");
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        party.ifPresent(p -> {
            p.getMembers().put(player.getUniqueId(), false);
            p.getDisconnects().put(player.getUniqueId(), 5 * 60);
            p.sendMessageToAllPartyPlayers(
                    ChatColor.AQUA + player.getName() + ChatColor.YELLOW + " has" + ChatColor.RED + " 5 " + ChatColor.YELLOW + "minutes to rejoin before getting kicked!",
                    true,
                    true
            );
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        if(e.getMessage().equalsIgnoreCase("/pl")) {
            Bukkit.getServer().dispatchCommand(e.getPlayer(), "party list");
            e.setCancelled(true);
        }
    }

}

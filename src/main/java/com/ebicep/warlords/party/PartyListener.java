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
        party.flatMap(p -> p.getPartyPlayers().stream()
                .filter(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()))
                .findFirst()).ifPresent(partyPlayer -> {
            partyPlayer.setOnline(true);
            partyPlayer.setOfflineTimeLeft(-1);
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
        party.flatMap(p -> p.getPartyPlayers().stream()
                .filter(partyPlayer -> partyPlayer.getUuid().equals(player.getUniqueId()))
                .findFirst()).ifPresent(partyPlayer -> {
            partyPlayer.setOnline(false);
            partyPlayer.setOfflineTimeLeft(5 * 60);
        });
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().equalsIgnoreCase("/pl")) {
            Bukkit.getServer().dispatchCommand(e.getPlayer(), "party list");
            e.setCancelled(true);
        }
    }

}

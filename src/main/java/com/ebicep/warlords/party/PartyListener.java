package com.ebicep.warlords.party;

import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
        if (partyPlayerPair == null) {
            if (!PartyManager.PARTIES.isEmpty()) {
                StringBuilder parties = new StringBuilder(ChatColor.YELLOW + "Current parties: ");
                for (Party partyManagerParty : PartyManager.PARTIES) {
                    parties.append(ChatColor.AQUA).append(partyManagerParty.getLeaderName()).append(ChatColor.GRAY).append(", ");
                }
                parties.setLength(parties.length() - 2);
                player.sendMessage(parties.toString());
            }
        } else {
            partyPlayerPair.getB().setOnline(true);
            partyPlayerPair.getB().setOfflineTimeLeft(-1);
        }
        //queue
        Bukkit.dispatchCommand(player, "queue");
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
        if (partyPlayerPair != null) {
            partyPlayerPair.getB().setOnline(false);
            partyPlayerPair.getB().setOfflineTimeLeft(5 * 60);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        String message = e.getMessage();
        if (message.equalsIgnoreCase("/pl")) {
            Bukkit.getServer().dispatchCommand(e.getPlayer(), "party list");
            e.setCancelled(true);
        } else if (message.equalsIgnoreCase("/gl")) {
            Bukkit.getServer().dispatchCommand(e.getPlayer(), "guild list");
            e.setCancelled(true);
        }
    }

}

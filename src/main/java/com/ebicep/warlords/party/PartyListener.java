package com.ebicep.warlords.party;

import com.ebicep.warlords.Warlords;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class PartyListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        party.ifPresent(value -> value.getMembers().put(player.getUniqueId(), true));
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Optional<Party> party = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
        party.ifPresent(value -> value.getMembers().put(player.getUniqueId(), false));
    }

}

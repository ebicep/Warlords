package com.ebicep.warlords.party;

import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
        if (partyPlayerPair == null) {
            if (!PartyManager.PARTIES.isEmpty()) {
                TextComponent.Builder parties = Component.text().append(Component.text("Current parties: ", NamedTextColor.YELLOW));
                player.sendMessage(PartyManager.PARTIES
                        .stream()
                        .map(party -> Component.text(party.getLeaderName(), NamedTextColor.AQUA)
                                               .hoverEvent(HoverEvent.showText(party.getPartyList())))
                        .collect(Component.toComponent(Component.text(", ", NamedTextColor.GRAY))));
            }
        } else {
            partyPlayerPair.getB().setOnline(true);
            partyPlayerPair.getB().setOfflineTimeLeft(-1);
        }
        //queue
        player.performCommand("queue");
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


}

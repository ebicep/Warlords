package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

@CommandAlias("getplayers")
public class GetPlayersCommand extends co.aikar.commands.BaseCommand {

    @Default
    @Description("Get list of players in your party")
    public void getPlayers(@Conditions("party:true") Player player) {
        Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
        if (partyPlayerPair != null) {
            player.sendMessage(Component.text("CLICK ME FOR PLAYERS - AUTO COPIED TO CLIPBOARD", NamedTextColor.GREEN, TextDecoration.UNDERLINED, TextDecoration.BOLD)
                                        .clickEvent(ClickEvent.copyToClipboard(partyPlayerPair.getA()
                                                                                              .getPartyPlayers()
                                                                                              .stream()
                                                                                              .map(PartyPlayer::getUUID)
                                                                                              .map(Bukkit::getOfflinePlayer)
                                                                                              .map(OfflinePlayer::getName)
                                                                                              .collect(Collectors.joining(","))))
            );
        }
    }

}

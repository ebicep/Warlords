package com.ebicep.warlords.party.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("stream")
@CommandPermission("warlords.party.stream")
public class StreamCommand extends BaseCommand {

    @Default
    @Description("Creates a new party where anyone can join")
    public void stream(@Conditions("party:false") Player player) {
        Party party = new Party(player.getUniqueId(), true);
        PartyManager.PARTIES.add(party);

        party.sendMessageToAllPartyPlayers(
                Component.textOfChildren(
                        Component.text("You created a public party! Players can join with", NamedTextColor.GREEN),
                        Component.newline(),
                        Component.text("/party join " + player.getName(), NamedTextColor.GOLD, TextDecoration.BOLD)
                )
        );
        Bukkit.getOnlinePlayers().stream()
              .filter(p -> p.getUniqueId() != player.getUniqueId())
              .forEach(onlinePlayer -> {
                  ChatUtils.sendCenteredMessage(onlinePlayer, Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
                  ChatUtils.sendCenteredMessage(onlinePlayer,
                          Component.text(player.getName(), NamedTextColor.AQUA)
                                   .append(Component.text(" created a public party!", NamedTextColor.YELLOW))
                  );
                  ChatUtils.sendCenteredMessage(onlinePlayer, Component.text("Click here to join!", NamedTextColor.GOLD, TextDecoration.BOLD)
                                                                       .clickEvent(ClickEvent.runCommand("/party join " + player.getName()))
                  );
                  ChatUtils.sendCenteredMessage(onlinePlayer, Component.text("------------------------------------------", NamedTextColor.BLUE, TextDecoration.BOLD));
              });

        BotManager.sendMessageToStatusChannel("[PARTY] **" + player.getName() + "** created a public party! /p join " + player.getName());
    }
}

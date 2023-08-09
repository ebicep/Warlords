package com.ebicep.jda;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.party.commands.PartyPlayerWrapper;
import net.dv8tion.jda.api.entities.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("bot")
@CommandPermission("warlords.game.bot")
@Conditions("bot")
public class BotCommand extends BaseCommand {

    @Default
    public void sendBal(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, String command) {
        java.util.Optional<TextChannel> botTeams = BotManager.getTextChannelCompsByName("bot-teams");
        java.util.Optional<TextChannel> gsTeams = BotManager.getTextChannelCompsByName("gs-teams");
        if (botTeams.isEmpty()) {
            player.sendMessage(Component.text("Could not find bot-teams!", NamedTextColor.RED));
            return;
        }
        StringBuilder players = new StringBuilder();
        partyPlayerWrapper.getParty()
                          .getPartyPlayers()
                          .forEach(partyPlayer -> players.append(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName()).append(","));
        players.setLength(players.length() - 1);
        botTeams.get().sendMessage("```/" + command + " string:" + players + "```").queue();
        player.sendMessage(Component.text("Posted command in bot-teams!", NamedTextColor.GREEN));

    }

}


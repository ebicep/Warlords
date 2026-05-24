package com.ebicep.jda;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.party.commands.PartyPlayerWrapper;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("bot")
@CommandPermission("warlords.game.bot")
@Conditions("bot")
public class BotCommand extends BaseCommand {

    @Default
    public void sendBal(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, String command) {
        StringBuilder playerNamesBuilder = new StringBuilder();
        partyPlayerWrapper.getParty()
                          .getPartyPlayers()
                          .forEach(partyPlayer -> playerNamesBuilder.append(Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName()).append(","));
        playerNamesBuilder.setLength(playerNamesBuilder.length() - 1);
        String playerNames = playerNamesBuilder.toString();

        BotManager.DiscordServer comps = BotManager.getServer("comps");
        if (comps != null) {
            Optional<TextChannel> compsBotTeams = comps.getChannel(BotManager.BotChannel.BOT_TEAMS);
            if (compsBotTeams.isPresent()) {
                compsBotTeams.get().sendMessage("```/" + command + " string:" + playerNames + "```").queue();
            } else {
                player.sendMessage(Component.text("Could not find comps bot-teams channel.", NamedTextColor.YELLOW));
            }
        }

        BotManager.DiscordServer main = BotManager.getServer("main");
        if (main != null) {
            Optional<TextChannel> mainBotTeams = main.getChannel(BotManager.BotChannel.BOT_TEAMS);
            if (mainBotTeams.isPresent()) {
                mainBotTeams.get().sendMessage("```/experimental run players:" + playerNames + "```").queue();
            } else {
                player.sendMessage(Component.text("Could not find main bot-teams channel.", NamedTextColor.YELLOW));
            }
        }
    }

}

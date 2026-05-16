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

@CommandAlias("bot")
@CommandPermission("warlords.game.bot")
@Conditions("bot")
public class BotCommand extends BaseCommand {

    @Default
    public void sendBal(@Conditions("party:true") Player player, PartyPlayerWrapper partyPlayerWrapper, String command) {
        BotManager.DiscordServer comps = BotManager.getServer("comps");
        if (comps == null) {
            player.sendMessage(Component.text("Discord comps server not configured!", NamedTextColor.RED));
            return;
        }
        java.util.Optional<TextChannel> botTeams = comps.getChannel(BotManager.BotChannel.BOT_TEAMS);
        if (botTeams.isEmpty()) {
            player.sendMessage(Component.text("Could not find bot-teams channel!", NamedTextColor.RED));
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


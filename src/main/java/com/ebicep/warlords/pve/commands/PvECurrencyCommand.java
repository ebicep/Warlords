package com.ebicep.warlords.pve.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("pvecurrency")
@CommandPermission("group.administrator")
public class PvECurrencyCommand extends BaseCommand {

    @Subcommand("add")
    @Description("Add pve currency to your inventory")
    public void add(Player player, Currencies currency, @Conditions("limits:min=1") Integer amount) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().addCurrency(currency, amount);
        });
        ChatChannels.playerSendMessage(player,
                ChatChannels.DEBUG,
                Component.text("Gave yourself ", NamedTextColor.GREEN).append(currency.getCostColoredName(amount))
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

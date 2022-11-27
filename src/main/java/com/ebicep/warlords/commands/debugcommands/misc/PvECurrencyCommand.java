package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

@CommandAlias("pvecurrency")
@CommandPermission("minecraft.command.op|group.administrator")
@Conditions("database:player")
public class PvECurrencyCommand extends BaseCommand {

    @Subcommand("add")
    @Description("Add pve currency to your inventory")
    public void add(Player player, Currencies currency, @Conditions("limits:min=1") Integer amount) {
        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
            databasePlayer.getPveStats().addCurrency(currency, amount);
        });
        ChatChannels.playerSendMessage(player,
                ChatColor.GREEN + "Gave yourself " + ChatColor.YELLOW + amount + " " + ChatColor.LIGHT_PURPLE + currency.name + (amount != 1 ? "s" : ""),
                ChatChannels.DEBUG,
                true
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

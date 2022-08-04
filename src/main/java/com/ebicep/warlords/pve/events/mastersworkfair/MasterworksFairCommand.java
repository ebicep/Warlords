package com.ebicep.warlords.pve.events.mastersworkfair;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import org.bukkit.ChatColor;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.currentFair;
import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.resetFair;

@CommandAlias("masterworksfair")
@CommandPermission("group.administrator")
public class MasterworksFairCommand extends BaseCommand {

    @Subcommand("end")
    @Description("Ends the current masterworks fair event")
    public void end(CommandIssuer issuer, Boolean awardThroughRewardsInventory) {
        if (currentFair == null) {
            ChatCommand.sendDebugMessage(issuer, ChatColor.RED + "No current masterworks fair event to end", true);
            return;
        }
        ChatCommand.sendDebugMessage(issuer, ChatColor.GREEN + "Ending current masterworks fair event", true);
        resetFair(currentFair, awardThroughRewardsInventory);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}

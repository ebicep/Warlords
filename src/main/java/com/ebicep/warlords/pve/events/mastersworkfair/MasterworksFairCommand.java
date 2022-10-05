package com.ebicep.warlords.pve.events.mastersworkfair;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

import java.util.Comparator;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.currentFair;
import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.resetFair;

@CommandAlias("masterworksfair")
@CommandPermission("group.administrator")
public class MasterworksFairCommand extends BaseCommand {

    @Subcommand("end")
    @Description("Ends the current masterworks fair event")
    public void end(CommandIssuer issuer, Boolean awardThroughRewardsInventory, @Default("5") @Conditions("limits:min=1,max=5") Integer startMinuteDelay) {
        if (currentFair == null) {
            ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "No current masterworks fair event to end", true);
            return;
        }
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Ending current masterworks fair event with start delay of " + startMinuteDelay, true);
        currentFair.setEnded(true);
        resetFair(currentFair, awardThroughRewardsInventory, startMinuteDelay);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

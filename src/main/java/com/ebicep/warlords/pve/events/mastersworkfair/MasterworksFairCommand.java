package com.ebicep.warlords.pve.events.mastersworkfair;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

import java.util.Comparator;
import java.util.List;

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

    @Subcommand("resendresults")
    @Description("Resends the results of the selected masterworks fair, or the latest one if none is selected")
    public void resendResults(CommandIssuer issuer, @Optional Integer fairNumber) {
        List<MasterworksFair> fairs = DatabaseManager.masterworksFairService.findAll();
        if (fairNumber == null) {
            fairs.get(fairs.size() - 1).sendResults();
            ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Resent latest Masterworks Fair results", true);
        } else {
            java.util.Optional<MasterworksFair> fairOptional = fairs.stream()
                    .filter(fair -> fair.getFairNumber() == fairNumber)
                    .findFirst();
            if (fairOptional.isPresent()) {
                fairOptional.get().sendResults();
                ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Resent Masterworks Fair #" + fairNumber + " results", true);
            } else {
                ChatChannels.sendDebugMessage(issuer, ChatColor.RED + "Could not find fair #" + fairNumber, true);
            }
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

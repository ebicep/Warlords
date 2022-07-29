package com.ebicep.warlords.commands2.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.AFKDetectionOption;
import org.bukkit.ChatColor;

@CommandAlias("afkdetection")
@CommandPermission("warlords.game.toggleafkdetection")
public class ToggleAFKDetectionCommand extends BaseCommand {

    @Default
    @CommandCompletion("@enabledisable")
    public void toggleAFKDetection(CommandIssuer issuer, @Values("@enabledisable") String option) {
        AFKDetectionOption.enabled = option.equals("enable");
        issuer.sendMessage((AFKDetectionOption.enabled ? ChatColor.GREEN : ChatColor.RED) + "AFK Detection is now " + option + "d.");
    }

}

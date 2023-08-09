package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.freeze.AFKDetectionOption;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("afkdetection")
@CommandPermission("warlords.game.toggleafkdetection")
public class ToggleAFKDetectionCommand extends BaseCommand {

    @Default
    @CommandCompletion("@enabledisable")
    public void toggleAFKDetection(CommandIssuer issuer, @Values("@enabledisable") String option) {
        AFKDetectionOption.enabled = option.equals("enable");
        ChatChannels.sendDebugMessage(issuer, Component.text("AFK Detection is now " + option + "d.", (AFKDetectionOption.enabled ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }

}

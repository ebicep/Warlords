package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("debugvalue|dv")
@CommandPermission("group.adminisrator")
public class DebugValueCommand extends BaseCommand {

    public static int debugValue = 0;

    @Default
    public void debugValue(CommandIssuer issuer, Integer value) {
        debugValue = value;
        ChatChannels.sendDebugMessage(issuer, Component.text("Debug value set to " + value, NamedTextColor.GREEN));
    }

}

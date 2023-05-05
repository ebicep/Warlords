package com.ebicep.warlords.pve.quests;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("quest")
@CommandPermission("group.administrator")
public class QuestCommand extends BaseCommand {

    public static boolean isQuestsEnabled = true;

    @Subcommand("toggle")
    public void toggle(CommandIssuer issuer) {
        isQuestsEnabled = !isQuestsEnabled;
        ChatChannels.sendDebugMessage(issuer, Component.text(isQuestsEnabled ? "Quests Enabled" : "Quests Disabled", isQuestsEnabled ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

}

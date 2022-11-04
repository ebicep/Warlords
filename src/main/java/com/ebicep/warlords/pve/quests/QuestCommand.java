package com.ebicep.warlords.pve.quests;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

@CommandAlias("quest")
@CommandPermission("group.administrator")
public class QuestCommand extends BaseCommand {

    public static boolean isQuestsEnabled = true;

    @Subcommand("disable")
    public void disable(CommandIssuer issuer) {
        isQuestsEnabled = !isQuestsEnabled;
        ChatChannels.sendDebugMessage(issuer, isQuestsEnabled ? ChatColor.GREEN + "Quests Enabled" : ChatColor.RED + "Quests Disabled", true);
    }

}

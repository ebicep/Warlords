package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.ConfigUtil;

@CommandAlias("botconfig")
@CommandPermission("group.adminisrator")
public class BotConfigCommand extends BaseCommand {

    @Subcommand("reload")
    public void reload(CommandIssuer issuer) {
        try {
            ConfigUtil.reloadBotConfig(Warlords.getInstance());
            ChatChannels.sendDebugMessage(issuer, "Reloaded bot config");
        } catch (Exception e) {
            ChatChannels.sendDebugMessage(issuer, "Failed to reload bot config: " + e.getMessage());
            ChatUtils.MessageType.CONFIG.sendErrorMessage(e);
        }
    }

}

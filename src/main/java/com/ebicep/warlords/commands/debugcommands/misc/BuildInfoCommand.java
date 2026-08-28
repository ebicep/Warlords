package com.ebicep.warlords.commands.debugcommands.misc;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.util.BuildInfo;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("buildinfo|bi")
@CommandPermission("group.administrator")
public class BuildInfoCommand extends BaseCommand {

    @Default
    public void buildInfo(CommandIssuer issuer) {
        ChatChannels.sendDebugMessage(issuer, Component.text("Warlords Build Info", NamedTextColor.GOLD));
        ChatChannels.sendDebugMessage(issuer, Component.text("Version: " + BuildInfo.getVersion(), NamedTextColor.AQUA));
        ChatChannels.sendDebugMessage(issuer, Component.text("Commit: " + BuildInfo.getCommitShort() + " (" + BuildInfo.getCommit() + ")", NamedTextColor.AQUA));
        ChatChannels.sendDebugMessage(issuer, Component.text("Branch: " + BuildInfo.getBranch(), NamedTextColor.AQUA));
        ChatChannels.sendDebugMessage(issuer, Component.text("Commit Time: " + BuildInfo.getCommitTime(), NamedTextColor.AQUA));
        ChatChannels.sendDebugMessage(issuer, Component.text("Build Time: " + BuildInfo.getBuildTime(), NamedTextColor.AQUA));
        ChatChannels.sendDebugMessage(issuer, Component.text("Dirty: " + (BuildInfo.isDirty() ? "yes" : "no"), BuildInfo.isDirty() ? NamedTextColor.RED : NamedTextColor.GREEN));
    }
}

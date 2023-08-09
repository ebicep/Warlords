package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("stunclear")
@CommandPermission("group.administrator")
public class StunClearCommand extends BaseCommand {

    @Default
    public void clear(CommandIssuer issuer) {
        WarlordsPlayer.STUNNED_PLAYERS.clear();
        ChatChannels.sendDebugMessage(issuer, Component.text("Cleared WarlordsPlayer Stun List", NamedTextColor.GREEN));
    }

}

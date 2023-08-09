package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.freeze.GameFreezeWhenOfflineOption;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("offlinefreeze")
@CommandPermission("warlords.game.toggleofflinefreeze")
public class ToggleOfflineFreezeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@enabledisable")
    public void toggleOfflineFreeze(CommandIssuer issuer, @Values("@enabledisable") String option) {
        GameFreezeWhenOfflineOption.enabled = option.equals("enable");
        ChatChannels.sendDebugMessage(issuer,
                Component.text("Offline Freeze is now " + option + "d.", (GameFreezeWhenOfflineOption.enabled ? NamedTextColor.GREEN : NamedTextColor.RED))
        );
    }

}

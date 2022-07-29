package com.ebicep.warlords.commands2.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.option.GameFreezeWhenOfflineOption;
import org.bukkit.ChatColor;

@CommandAlias("offlinefreeze")
@CommandPermission("minecraft.command.op")
public class ToggleOfflineFreezeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@enabledisable")
    public void toggleOfflineFreeze(CommandIssuer issuer, @Values("@enabledisable") String option) {
        GameFreezeWhenOfflineOption.enabled = option.equals("enable");
        issuer.sendMessage((GameFreezeWhenOfflineOption.enabled ? ChatColor.GREEN : ChatColor.RED) + "Offline Freeze is now " + option + "d.");
    }

}

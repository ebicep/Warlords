package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.abilties.RecklessCharge;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;

@CommandAlias("recklesschargestunclear")
@CommandPermission("minecraft.command.op|group.administrator")
public class RecklessChargeStunClearCommand extends BaseCommand {

    @Default
    public void clear(CommandIssuer issuer) {
        RecklessCharge.STUNNED_PLAYERS.clear();
        ChatChannels.sendDebugMessage(issuer, ChatColor.GREEN + "Cleared Reckless Charge Stun List", true);
    }

}

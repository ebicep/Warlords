package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;


@CommandAlias("debugmode")
@CommandPermission("warlords.game.debugmode")
public class DebugModeCommand extends BaseCommand {

    @Default
    @Description("Disables energy consumption, Disables cooldowns, and Prevents damage from being taken")
    public void debugMode(WarlordsPlayer warlordsPlayer) {
        if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            ChatChannels.sendDebugMessage(warlordsPlayer, ChatColor.RED + "Debug commands are disabled in public games!", true);
            return;
        }
        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        ChatChannels.sendDebugMessage(warlordsPlayer, ChatColor.GREEN + "You now have infinite energy, no cooldowns, and take no damage!", true);
    }
}

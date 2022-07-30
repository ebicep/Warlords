package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.commands.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;


@CommandAlias("debugmode")
@CommandPermission("warlords.game.debugmode")
public class DebugModeCommand extends BaseCommand {

    @Default
    @Description("Disables energy consumption, Disables cooldowns, and Prevents damage from being taken")
    public void debugMode(WarlordsPlayer warlordsPlayer) {
        if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            ChatCommand.sendDebugMessage(warlordsPlayer, ChatColor.RED + "Debug commands are disabled in public games!");
            return;
        }
        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        ChatCommand.sendDebugMessage(warlordsPlayer, ChatColor.GREEN + "You now have infinite energy, no cooldowns, and take no damage!");
    }
}

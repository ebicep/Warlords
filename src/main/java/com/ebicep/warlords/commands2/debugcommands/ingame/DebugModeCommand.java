package com.ebicep.warlords.commands2.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands2.miscellaneouscommands.ChatCommand;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


@CommandAlias("debugmode")
@CommandPermission("warlords.game.debugmode")
public class DebugModeCommand extends BaseCommand {

    @Default
    @Description("Disables energy consumption, Disables cooldowns, and Prevents damage from being taken")
    public void debugMode(@Conditions("requireWarlordsPlayer") Player player) {
        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        if (!warlordsPlayer.getGame().getAddons().contains(GameAddon.PRIVATE_GAME)) {
            ChatCommand.sendDebugMessage(player, ChatColor.RED + "Debug commands are disabled in public games!");
            return;
        }
        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        ChatCommand.sendDebugMessage(player, ChatColor.GREEN + "You now have infinite energy, no cooldowns, and take no damage!");
    }
}

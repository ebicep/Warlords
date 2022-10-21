package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import org.bukkit.ChatColor;


@CommandAlias("debugmode")
@CommandPermission("group.administrator")
//@CommandPermission("warlords.game.debugmode")
public class DebugModeCommand extends BaseCommand {

    @Default
    @Description("Disables energy consumption, Disables cooldowns, and Prevents damage from being taken")
    public void debugMode(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            ability.setCurrentCooldown(0);
        }
        warlordsPlayer.updateItems();
        warlordsPlayer.setHorseCooldown(0);
        ChatChannels.sendDebugMessage(warlordsPlayer,
                ChatColor.GREEN + "You now have infinite energy, no cooldowns, and take no damage!",
                true
        );
    }
}

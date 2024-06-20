package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;


@CommandAlias("debugmode")
@CommandPermission("group.administrator")
//@CommandPermission("warlords.game.debugmode")
public class DebugModeCommand extends BaseCommand {

    @Default
    @Description("Disables energy consumption, Disables cooldowns, Prevents damage from being taken, and shows debug messages on attacks")
    public void debugMode(WarlordsPlayer warlordsPlayer) {
        debugMode(warlordsPlayer, false);
    }

    private static void debugMode(WarlordsPlayer warlordsPlayer, boolean gmc) {
        warlordsPlayer.setNoEnergyConsumption(true);
        warlordsPlayer.setDisableCooldowns(true);
        warlordsPlayer.setTakeDamage(false);
        warlordsPlayer.setShowDebugMessage(true);
        for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
            ability.setCurrentCooldown(0);
            ability.queueUpdateItem();
        }
        warlordsPlayer.setHorseCooldown(0.05f);
        if (gmc) {
            if (warlordsPlayer.getEntity() instanceof Player) {
                ((Player) warlordsPlayer.getEntity()).setGameMode(GameMode.CREATIVE);
            }
        }
        ChatChannels.sendDebugMessage(warlordsPlayer,
                Component.text("You now have infinite energy, no cooldowns, will take no damage, and have debug messages!", NamedTextColor.GREEN)
        );
    }

    @CommandAlias("debugmode2")
    @Description("Debugmode + creative mode")
    public void debugMode2(WarlordsPlayer warlordsPlayer) {
        debugMode(warlordsPlayer, true);
    }

}

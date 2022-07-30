package com.ebicep.warlords.commands2.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;

@CommandAlias("recordaveragedamagedone")
@CommandPermission("warlords.game.recordaverage")
public class RecordAverageDamageCommand extends BaseCommand {

    @Default
    @Description("Prints your average damage done")
    public void getAverageDamageDone(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.sendMessage(ChatColor.GREEN + "Average Damage Done = " + ChatColor.RED + warlordsPlayer.getRecordDamage().stream()
                .mapToDouble(Float::floatValue)
                .average()
                .orElse(Double.NaN));
        warlordsPlayer.getRecordDamage().clear();
    }

}

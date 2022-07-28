package com.ebicep.warlords.commands2.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("recordaveragedamagedone")
@CommandPermission("warlords.game.recordaverage")
public class RecordAverageDamageCommand extends BaseCommand {

    @Default
    @Description("Prints your average damage done")
    public void getAverageDamageDone(@Conditions("requireWarlordsPlayer") Player player) {
        WarlordsEntity warlordsPlayer = Warlords.getPlayer(player);
        warlordsPlayer.sendMessage(ChatColor.GREEN + "Average Damage Done = " + ChatColor.RED + warlordsPlayer.getRecordDamage().stream()
                .mapToDouble(Float::floatValue)
                .average()
                .orElse(Double.NaN));
        warlordsPlayer.getRecordDamage().clear();
    }

}

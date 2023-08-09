package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@CommandAlias("recordaveragedamagedone")
@CommandPermission("warlords.game.recordaverage")
public class RecordAverageDamageCommand extends BaseCommand {

    @Default
    @Description("Prints your average damage done")
    public void getAverageDamageDone(WarlordsPlayer warlordsPlayer) {
        double averageDamageDone = warlordsPlayer.getRecordDamage().stream()
                                                 .mapToDouble(Float::floatValue)
                                                 .average()
                                                 .orElse(Double.NaN);
        warlordsPlayer.sendMessage(Component.text("Average Damage Done = ", NamedTextColor.GREEN)
                                            .append(Component.text(averageDamageDone, NamedTextColor.RED)));
        warlordsPlayer.getRecordDamage().clear();
    }

}

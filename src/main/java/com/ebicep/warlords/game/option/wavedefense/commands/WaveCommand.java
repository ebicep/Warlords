package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

@CommandAlias("wave")
public class WaveCommand extends BaseCommand {

    @Subcommand("set")
    @Description("Set the wave counter")
    public void set(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        WaveDefenseOption waveDefenseOption = (WaveDefenseOption) warlordsPlayer.getGame().getOptions().stream()
                .filter(option -> option instanceof WaveDefenseOption)
                .findFirst()
                .get();
        waveDefenseOption.setWaveCounter(amount);
    }
}

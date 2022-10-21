package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveEditEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.Bukkit;

@CommandAlias("wave")
@CommandPermission("group.administrator")
public class WaveCommand extends BaseCommand {

    @Subcommand("set")
    @Description("Set the wave counter")
    public void set(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        Game game = warlordsPlayer.getGame();
        WaveDefenseOption waveDefenseOption = (WaveDefenseOption) game.getOptions().stream()
                .filter(option -> option instanceof WaveDefenseOption)
                .findFirst()
                .get();
        waveDefenseOption.setWaveCounter(amount);
        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveEditEvent(game, amount));
    }
}

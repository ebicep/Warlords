package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.game.pve.WarlordsGameWaveEditEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("wave")
@CommandPermission("group.administrator")
public class WaveCommand extends BaseCommand {

    @Subcommand("set")
    @Description("Set the wave counter")
    public void set(@Conditions("requireGame:gamemode=WAVE_DEFENSE") Player player, Integer amount) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        WaveDefenseOption waveDefenseOption = (WaveDefenseOption) game.getOptions().stream()
                .filter(option -> option instanceof WaveDefenseOption)
                .findFirst()
                .get();
        waveDefenseOption.setWaveCounter(amount);
        Bukkit.getPluginManager().callEvent(new WarlordsGameWaveEditEvent(game, amount));
    }
}

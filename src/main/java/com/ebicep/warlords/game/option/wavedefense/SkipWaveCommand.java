package com.ebicep.warlords.game.option.wavedefense;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SkipWaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        WarlordsEntity we = BaseCommand.requireWarlordsPlayer(sender);
        if (we != null) {
            if (we.getGame() != null && we.getGame().getGameMode().equals(GameMode.WAVE_DEFENSE)) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) we.getGame().getOptions().stream()
                    .filter(option -> option instanceof WaveDefenseOption)
                    .findFirst()
                    .get();

                int amount = Integer.parseInt(args[0]);
                waveDefenseOption.setWaveCounter(amount);
            }
        }
        return true;
    }

    public void register(Warlords instance) {
        instance.getCommand("setwave").setExecutor(this);
    }

}

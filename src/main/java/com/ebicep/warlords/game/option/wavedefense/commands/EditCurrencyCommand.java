package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;

@CommandAlias("currency")
public class EditCurrencyCommand extends BaseCommand {

    @Subcommand("add")
    public void add(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.addCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You gained " + amount + " currency");
    }

    @Subcommand("remove")
    public void remove(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.subtractCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You lost " + amount + " currency");
    }

    @Subcommand("set")
    public void set(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.setCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You set your currency to " + amount);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.showHelp();
    }

}

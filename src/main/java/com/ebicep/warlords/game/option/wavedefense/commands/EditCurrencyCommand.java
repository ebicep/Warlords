package com.ebicep.warlords.game.option.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;

import java.util.Comparator;

@CommandAlias("currency")
public class EditCurrencyCommand extends BaseCommand {

    @Subcommand("add")
    @Description("Adds currency to yourself")
    public void add(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.addCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You gained " + amount + " currency");
    }

    @Subcommand("remove")
    @Description("Removes your currency")
    public void remove(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.subtractCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You lost " + amount + " currency");
    }

    @Subcommand("set")
    @Description("Sets your currency to a specific amount")
    public void set(@Conditions("requireGame:gamemode=WAVE_DEFENSE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.setCurrency(amount);
        warlordsPlayer.sendMessage(ChatColor.AQUA + "You set your currency to " + amount);
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

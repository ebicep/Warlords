package com.ebicep.warlords.game.option.pve.wavedefense.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;

@CommandAlias("currency")
@CommandPermission("group.administrator")
public class EditCurrencyCommand extends BaseCommand {

    @Subcommand("add")
    @Description("Adds currency to yourself")
    public void add(
            CommandIssuer issuer,
            Integer amount,
            @Optional @Conditions("requireGame:gamemode=PVE") WarlordsPlayer warlordsPlayer
    ) {
        warlordsPlayer.addCurrency(amount);
        warlordsPlayer.sendMessage(Component.text("You gained " + amount + " currency", NamedTextColor.AQUA));
    }

    @Subcommand("addall")
    @Description("Adds currency to everyone")
    public void addAll(@Conditions("requireGame:gamemode=PVE") WarlordsPlayer warlordsPlayer, Integer amount) {
        warlordsPlayer.getGame().warlordsPlayers().forEach(wp -> {
            wp.addCurrency(amount);
            wp.sendMessage(Component.text("You gained " + amount + " currency", NamedTextColor.AQUA));
        });
    }

    @Subcommand("remove")
    @Description("Removes your currency")
    public void remove(
            CommandIssuer issuer,
            Integer amount,
            @Optional @Conditions("requireGame:gamemode=PVE") WarlordsPlayer warlordsPlayer
    ) {
        warlordsPlayer.subtractCurrency(amount);
        warlordsPlayer.sendMessage(Component.text("You lost " + amount + " currency", NamedTextColor.AQUA));
    }

    @Subcommand("set")
    @Description("Sets your currency to a specific amount")
    public void set(
            CommandIssuer issuer,
            Integer amount,
            @Optional @Conditions("requireGame:gamemode=PVE") WarlordsPlayer warlordsPlayer
    ) {
        warlordsPlayer.setCurrency(amount);
        warlordsPlayer.sendMessage(Component.text("You set your currency to " + amount, NamedTextColor.AQUA));
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

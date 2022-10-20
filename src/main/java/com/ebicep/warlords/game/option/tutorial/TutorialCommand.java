package com.ebicep.warlords.game.option.tutorial;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;

import java.util.Comparator;

@CommandAlias("tutorial")
public class TutorialCommand extends BaseCommand {

    @Subcommand("skip")
    @Description("Skips the tutorial")
    public void skipTutorial(@Conditions("requireGame:gamemode=TUTORIAL") WarlordsPlayer warlordsPlayer) {
        Game game = warlordsPlayer.getGame();
        if (game.isState(PlayingState.class)) {
            game.setNextState(new EndState(game, null));
            warlordsPlayer.sendMessage(ChatColor.GREEN + "Tutorial skipped!");
        } else {
            warlordsPlayer.sendMessage(ChatColor.RED + "You can only skip the tutorial when you are in the tutorial!");
        }
    }

    @Subcommand("abort")
    @Description("Aborts the tutorial")
    public void abortTutorial(@Conditions("requireGame:gamemode=TUTORIAL") WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().close();
        warlordsPlayer.sendMessage(ChatColor.GREEN + "Tutorial aborted!");
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

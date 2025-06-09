package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.option.PlayerCooldownDisplayOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Comparator;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

@CommandAlias("cooldowndisplay")
@CommandPermission("group.administrator")
public class PlayerCooldownDisplayCommand extends BaseCommand {

    @Subcommand("toggle")
    @Description("Toggles the ability cooldown display")
    public void respawn(CommandIssuer issuer) {
        PlayerCooldownDisplayOption.enabled = !PlayerCooldownDisplayOption.enabled;
        boolean enabled = PlayerCooldownDisplayOption.enabled;
        if (!enabled) {
            for (GameManager.GameHolder game : Warlords.getGameManager().getGames()) {
                if (game.getGame() != null) {
                    for (PlayerCooldownDisplayOption playerCooldownDisplayOption : game.getGame().getOption(PlayerCooldownDisplayOption.class)) {
                        playerCooldownDisplayOption.removeEntities();
                    }
                }
            }
        }
        sendDebugMessage(issuer, Component.text("Cooldown Display: " + (enabled ? "Enabled" : "Disabled"), enabled ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    @Subcommand("toggleseeteammates")
    @CommandCompletion("@warlordsplayers")
    @Description("Toggles the ability cooldown display for teammates")
    public void toggleSeeTeammates(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        for (PlayerCooldownDisplayOption cooldownDisplayOption : target.getGame().getOption(PlayerCooldownDisplayOption.class)) {
            PlayerCooldownDisplayOption.CooldownData cooldownData = cooldownDisplayOption.getPlayerSettings().get(target);
            if (cooldownData == null) {
                return;
            }
            cooldownData.setSeeTeammates(!cooldownData.isSeeTeammates());
            boolean seeTeammates = cooldownData.isSeeTeammates();
            sendDebugMessage(issuer,
                    Component.text("Cooldown Display for " + target.getName() + ": " + (seeTeammates ? "Enabled" : "Disabled"),
                            seeTeammates ? NamedTextColor.GREEN : NamedTextColor.RED
                    )
            );
        }
    }

    @Subcommand("toggleseeenemies")
    @CommandCompletion("@warlordsplayers")
    @Description("Toggles the ability cooldown display for enemies")
    public void toggleSeeEnemies(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        for (PlayerCooldownDisplayOption cooldownDisplayOption : target.getGame().getOption(PlayerCooldownDisplayOption.class)) {
            PlayerCooldownDisplayOption.CooldownData cooldownData = cooldownDisplayOption.getPlayerSettings().get(target);
            if (cooldownData == null) {
                return;
            }
            cooldownData.setSeeEnemies(!cooldownData.isSeeEnemies());
            boolean seeEnemies = cooldownData.isSeeEnemies();
            sendDebugMessage(issuer,
                    Component.text("Cooldown Display for " + target.getName() + ": " + (seeEnemies ? "Enabled" : "Disabled"),
                            seeEnemies ? NamedTextColor.GREEN : NamedTextColor.RED
                    )
            );
        }
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

}

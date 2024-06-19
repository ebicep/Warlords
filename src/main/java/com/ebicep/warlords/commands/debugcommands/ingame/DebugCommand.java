package com.ebicep.warlords.commands.debugcommands.ingame;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.freeze.GameFreezeOption;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.game.state.TimerDebugAble;
import com.ebicep.warlords.menu.debugmenu.DebugMenu;
import com.ebicep.warlords.menu.debugmenu.DebugMenuPlayerOptions;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Comparator;

import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

@CommandAlias("wl|debug")
@CommandPermission("warlords.game.debug")
public class DebugCommand extends BaseCommand {

    @Default
    public void openDebugMenu(Player player) {
        DebugMenu.openDebugMenu(player);
    }

    @Subcommand("printclassinfo")
    @Description("Prints class info (health, energy, etc)")
    public void printClassInfo(@Conditions("requireGame") WarlordsPlayer player) {
        AbstractPlayerClass specClass = player.getSpec();
        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Class: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getName(), NamedTextColor.WHITE)));

        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Max Health: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getMaxHealth(), NamedTextColor.WHITE)));

        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Max Energy: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getMaxEnergy(), NamedTextColor.WHITE)));

        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Energy Per Sec: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getEnergyPerSec(), NamedTextColor.WHITE)));

        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Energy Per Hit: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getEnergyPerHit(), NamedTextColor.WHITE)));

        sendDebugMessage(player, Component.empty()
                                          .append(Component.text("Damage Resistance: ", NamedTextColor.GOLD))
                                          .append(Component.text(specClass.getDamageResistance(), NamedTextColor.WHITE)));
    }

    @Subcommand("freeze")
    @Description("Freezes/Unfreezes the game")
    public void freezeGame(@Conditions("requireGame") Player player, @Optional String message) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        if (game.getState() instanceof EndState) {
            sendDebugMessage(player, Component.text("Cannot freeze game in end state", NamedTextColor.RED));
            return;
        }
        if (!game.isUnfreezeCooldown()) {
            if (game.isFrozen()) {
                GameFreezeOption.resumeGame(game);
            } else {
                if (message != null) {
                    message = message.replaceAll("&", "§");
                    game.addFrozenCause(Component.text(message, NamedTextColor.GOLD));
                } else {
                    game.addFrozenCause(Component.text("Manually paused by §c" + player.getName(), NamedTextColor.GOLD));
                }
                sendDebugMessage(player, Component.text("The game has been frozen!", NamedTextColor.GREEN));
            }
        } else {
            sendDebugMessage(player, Component.text("The game is currently unfreezing!", NamedTextColor.RED));
        }
    }

    @Subcommand("timer")
    @CommandCompletion("reset|skip")
    @Description("Resets or skips the timer")
    public void timer(@Conditions("requireGame") Player player, @Values("reset|skip") String option) {
        Game game = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).get();
        if (!(game.getState() instanceof TimerDebugAble timerDebugAble)) {
            sendDebugMessage(player, Component.text("This gamestate cannot be manipulated by the timer debug option!", NamedTextColor.RED));
            return;
        }
        switch (option) {
            case "reset" -> {
                timerDebugAble.resetTimer();
                sendDebugMessage(player, Component.text("Timer has been reset!", NamedTextColor.GREEN));
            }
            case "skip" -> {
                timerDebugAble.skipTimer();
                sendDebugMessage(player, Component.text("Timer has been skipped!", NamedTextColor.GREEN));
            }
        }
    }

    @Subcommand("respawn")
    @CommandCompletion("@warlordsplayers")
    @Description("Respawns a player or sender if there is no target")
    public void respawn(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        target.respawn();
    }

    @Subcommand("energy")
    @CommandCompletion("@enabledisable @warlordsplayers")
    @Description("Toggles ability energy usage for a player or sender if there is no target")
    public void setEnergy(CommandIssuer issuer, @Values("@enabledisable") String option, @Optional WarlordsPlayer target) {
        boolean enable = option.equals("enable");
        target.setNoEnergyConsumption(enable);
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text("'s No Energy Consumption was set to " + enable, NamedTextColor.GREEN)));
    }

    @Subcommand("cooldown")
    @CommandCompletion("@enabledisable @warlordsplayers")
    @Description("Toggles ability cooldowns for a player or sender if there is no target")
    public void setCooldown(CommandIssuer issuer, @Values("@enabledisable") String option, @Optional WarlordsPlayer target) {
        boolean disable = option.equals("disable");
        target.setDisableCooldowns(disable);
        if (disable) {
            target.resetAbilities(false);
        }
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text("'s Cooldown Timers have been " + option + "d!", NamedTextColor.GREEN)));
    }

    @Subcommand("takedamage")
    @CommandCompletion("@enabledisable @warlordsplayers")
    @Description("Toggles if a player takes damage or sender if there is no target")
    public void setTakeDamage(CommandIssuer issuer, @Values("@enabledisable") String option, @Optional WarlordsPlayer target) {
        boolean enable = option.equals("enable");
        target.setTakeDamage(enable);
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text(" will " + (!enable ? "no longer take" : "start taking") + " damage!", NamedTextColor.GREEN)));
    }

    @Subcommand("crits")
    @CommandCompletion("@enabledisable @warlordsplayers")
    @Description("Toggles if a player can crit or sender if there is no target")
    public void setCrits(CommandIssuer issuer, @Values("@enabledisable") String option, @Optional WarlordsPlayer target) {
        boolean enable = option.equals("enable");
        target.setCanCrit(enable);
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text("'s Crits have been " + option + "d!", NamedTextColor.GREEN)));
    }

    @Subcommand("heal")
    @CommandCompletion("@warlordsplayers")
    @Description("Heals a player based on the amount or sender if there is no target")
    public void heal(CommandIssuer issuer, @Default("1000") @Conditions("limits:min=0,max=100000") Integer amount, @Optional WarlordsPlayer target) {
        target.addInstance(InstanceBuilder
                .healing()
                .cause("God")
                .source(target)
                .value(amount)
        );
        target.resetRegenTimer();
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text(" was healed for " + amount + " health!", NamedTextColor.GREEN)));
    }

    @Subcommand("damage")
    @CommandCompletion("@warlordsplayers")
    @Description("Damages a player based on the amount or sender if there is no target")
    public void damage(CommandIssuer issuer, @Default("1000") @Conditions("limits:min=0,max=100000") Integer amount, @Optional WarlordsPlayer target) {
        target.addInstance(InstanceBuilder
                .damage()
                .cause("God")
                .source(target)
                .value(amount)
        );
        target.resetRegenTimer();
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text(" took " + amount + " damage!", NamedTextColor.GREEN)));
    }

    @Subcommand("debugmessage")
    @CommandCompletion("@warlordsplayers")
    @Description("Toggle debug messages for a player or sender if there is no target")
    public void damage(CommandIssuer issuer, @Values("@enabledisable") String option, @Optional WarlordsPlayer target) {
        boolean enable = option.equals("enable");
        target.setShowDebugMessage(enable);

        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text(" will " + (!enable ? "no longer see" : "start seeing") + " debug messages!", NamedTextColor.GREEN)));
    }

    @Subcommand("resetspec")
    @CommandCompletion("@warlordsplayers")
    @Description("Resets player spec to their spec again")
    public void resetSpec(Player player, @Optional WarlordsPlayer target) {
        Specializations spec = target.getSpecClass();
        DebugMenuPlayerOptions.setSpec(player, target, spec, spec.skillBoosts.get(0));
    }

    @Subcommand("clearcooldowns")
    @CommandCompletion("@warlordsplayers")
    @Description("Clears cooldown of player")
    public void clearCooldowns(CommandIssuer issuer, @Optional WarlordsPlayer target) {
        target.getCooldownManager().clearCooldowns();
        sendDebugMessage(issuer, Component.empty()
                                          .append(target.getColoredName())
                                          .append(Component.text("'s cooldowns have been cleared!", NamedTextColor.GREEN)));
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }
}

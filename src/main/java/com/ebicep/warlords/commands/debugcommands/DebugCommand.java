package com.ebicep.warlords.commands.debugcommands;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.BaseCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.state.TimerDebugAble;
import com.ebicep.warlords.menu.debugmenu.DebugMenu;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ebicep.warlords.commands.BaseCommand.requireGame;
import static com.ebicep.warlords.commands.BaseCommand.requireWarlordsPlayerInPrivateGame;
import static com.ebicep.warlords.game.option.GameFreezeWhenOfflineOption.UNFREEZE_TIME;

public class DebugCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if (!sender.hasPermission("warlords.game.debug")) {
            sender.sendMessage("§cYou do not have permission to do that.");
            return true;
        }

        if (args.length < 1) {
            DebugMenu.openDebugMenu((Player) sender);
            //sender.sendMessage("§cYou need to pass an argument, valid arguments: [timer, energy, cooldown, cooldownmode, takedamage]");
            return true;
        }
        String input = args[0];
        switch (input.toLowerCase(Locale.ROOT)) {
            case "respawn": {
                WarlordsPlayer wp = requireWarlordsPlayerInPrivateGame(sender, args.length > 1 ? args[1] : null);
                if (wp == null) {
                    return true;
                }
                wp.respawn();
                return true;
            }
            case "energy": {
                WarlordsPlayer wp = requireWarlordsPlayerInPrivateGame(sender, args.length > 2 ? args[2] : null);
                if (wp == null) {
                    return true;
                }
                switch (args.length > 1 ? args[1] : "") {
                    case "disable":
                        wp.setInfiniteEnergy(true);
                        sender.sendMessage(ChatColor.RED + "DEV: " + wp.getColoredName() + "'s §aEnergy consumption has been disabled!");
                        return true;
                    case "enable":
                        wp.setInfiniteEnergy(false);
                        sender.sendMessage(ChatColor.RED + "DEV: " + wp.getColoredName() + "'s §aEnergy consumption has been enabled!");
                        return true;
                    default:
                        sender.sendMessage("§cInvalid option!");
                        return false;
                }
            }
            case "cooldown": {
                WarlordsPlayer wp = requireWarlordsPlayerInPrivateGame(sender, args.length > 2 ? args[2] : null);
                if (wp == null) {
                    return true;
                }
                switch (args.length > 1 ? args[1] : "") {
                    case "disable":
                        wp.setDisableCooldowns(true);
                        sender.sendMessage(ChatColor.RED + "DEV: " + wp.getColoredName() + "'s §aCooldown timers have been disabled!");
                        return true;
                    case "enable":
                        wp.setDisableCooldowns(false);
                        sender.sendMessage(ChatColor.RED + "DEV: " + wp.getColoredName() + "'s §aCooldown timers have been enabled!");
                        return true;
                    default:
                        sender.sendMessage("§cInvalid option!");
                        return false;
                }
            }
            case "damage": {
                WarlordsPlayer wp = BaseCommand.requireWarlordsPlayerInPrivateGame(sender, args.length > 2 ? args[2] : null);
                if (wp == null) {
                    return true;
                }
                switch (args.length > 1 ? args[1] : "") {
                    case "disable":
                        wp.setTakeDamage(false);
                        sender.sendMessage(ChatColor.RED + "§cDEV: " + wp.getColoredName() + "'s §aTaking damage has been disabled!");
                        return true;
                    case "enable":
                        wp.setTakeDamage(true);
                        sender.sendMessage(ChatColor.RED + "§cDEV: " + wp.getColoredName() + "'s §aTaking damage has been enabled!");
                        return true;
                    default:
                        sender.sendMessage("§cInvalid option!");
                        return false;
                }
            }
            case "heal":
            case "takedamage": {
                if (args.length < 2) {
                    sender.sendMessage("§c" + (input.equals("takedamage") ? "Take Damage" : "Heal") + " requires more arguments, valid arguments: [1000, 2000, 3000, 4000, 5000]");
                    return true;
                }
                if (NumberUtils.isNumber(args[1])) {
                    int amount = Integer.parseInt(args[1]);

                    if (amount > 5000 || amount % 1000 != 0) {
                        sender.sendMessage("§cInvalid option! [Options: 1000, 2000, 3000, 4000, 5000]");
                        return true;
                    }

                    String endMessage = input.equals("takedamage") ? "took " + amount + " damage!" : "got " + amount + " heath!";

                    WarlordsPlayer wp = BaseCommand.requireWarlordsPlayerInPrivateGame(sender, args.length > 2 ? args[2] : null);
                    if (wp == null) {
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "§cDEV: " + wp.getColoredName() + " §a" + endMessage);

                    if (input.equals("takedamage")) {
                        wp.addDamageInstance(wp, "debug", amount, amount, -1, 100, false);
                        wp.setRegenTimer(10);
                    } else {
                        wp.addHealingInstance(wp, "debug", amount, amount, -1, 100, false, false);
                        wp.setRegenTimer(10);
                    }

                    return true;
                }
                sender.sendMessage("§cInvalid option! [Options: 1000, 2000, 3000, 4000, 5000]");
                return true;
            }
            case "crits": {
                WarlordsPlayer wp = requireWarlordsPlayerInPrivateGame(sender, args.length > 2 ? args[2] : null);
                if (wp == null) {
                    return true;
                }
                switch (args.length > 1 ? args[1] : "") {
                    case "disable":
                        wp.setCanCrit(false);
                        sender.sendMessage(ChatColor.RED + "§cDEV: " + wp.getColoredName() + "'s §aCrits has been disabled!");
                        return true;
                    case "enable":
                        wp.setCanCrit(true);
                        sender.sendMessage(ChatColor.RED + "§cDEV: " + wp.getColoredName() + "'s §aCrits has been enabled!");
                        return true;
                    default:
                        sender.sendMessage("§cInvalid option!");
                        return false;
                }
            }
            case "freeze": {
                Game game = requireGame(sender, args.length > 1 ? args[1] : null);
                if (game == null) {
                    return true;
                }
                if (game.isFrozen()) {
                    new GameRunnable(game, true) {
                        int timer = 0;
                        @Override
                        public void run() {
                            timer++;
                            if (timer < UNFREEZE_TIME) {
                                game.forEachOnlinePlayerWithoutSpectators((p, team) -> {
                                    PacketUtils.sendTitle(p, ChatColor.BLUE + "Resuming in... " + ChatColor.GREEN + (UNFREEZE_TIME - timer), "", 0, 40, 0);
                                });
                            } else {
                                game.removeFrozenCause(game.getFrozenCauses().get(0));
                                sender.sendMessage(ChatColor.RED + "§cDEV: §aThe game has been unfrozen!");
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(10, 20);
                } else {
                    game.addFrozenCause(ChatColor.GOLD + "Manually paused by §c" + sender.getName());
                    sender.sendMessage(ChatColor.RED + "§cDEV: §aThe game has been frozen!");
                }
                return true;
            }
            case "timer": {
                Game game = requireGame(sender, args.length > 2 ? args[2] : null);
                if (game == null) {
                    return true;
                }
                if (!(game.getState() instanceof TimerDebugAble)) {
                    sender.sendMessage("§cThis gamestate cannot be manipulated by the timer debug option");
                    return true;
                }
                TimerDebugAble timerDebugAble = (TimerDebugAble) game.getState();
                if (args.length < 2) {
                    sender.sendMessage("§cTimer requires 2 or more arguments, valid arguments: [skip, reset]");
                    return true;
                }
                switch (args[1]) {
                    case "reset":
                        timerDebugAble.resetTimer();
                        sender.sendMessage(ChatColor.RED + "DEV: §aTimer has been reset!");
                        return true;
                    case "skip":
                        timerDebugAble.skipTimer();
                        sender.sendMessage(ChatColor.RED + "DEV: §aTimer has been skipped!");
                        return true;
                    default:
                        sender.sendMessage("§cInvalid option! [reset, skip]");
                        return true;
                }
            }
            default:
                sender.sendMessage("§cInvalid option! valid args: [cooldownmode, cooldown, energy, damage, takedamage, freeze, timer");
                return true;
        }
    }

    public void register(Warlords instance) {
        instance.getCommand("wl").setExecutor(this);
        instance.getCommand("wl").setTabCompleter(this);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        String lastArg = args[args.length - 1];
        if (args.length > 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .filter(e -> e.getName().toLowerCase().startsWith(lastArg.toLowerCase()))
                    .map(e -> e.getName().charAt(0) + e.getName().substring(1))
                    .collect(Collectors.toList());
        }
        return Stream.of("respawn",
                        "energy",
                        "cooldown",
                        "damage",
                        "heal",
                        "takedamage",
                        "crits",
                        "freeze",
                        "timer")
                .filter(e -> e.startsWith(lastArg.toLowerCase(Locale.ROOT)))
                .map(e -> e.charAt(0) + e.substring(1).toLowerCase(Locale.ROOT))
                .collect(Collectors.toList());
    }
}

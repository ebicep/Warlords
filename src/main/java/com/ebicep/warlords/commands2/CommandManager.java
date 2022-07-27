package com.ebicep.warlords.commands2;

import co.aikar.commands.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands2.debugcommands.ingame.DebugCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.stream.Collectors;

public class CommandManager {

    public static PaperCommandManager manager;

    public static void init(Warlords instance) {
        manager = new PaperCommandManager(instance);
        manager.enableUnstableAPI("help");
        registerContexts();
        registerCompletions();
        registerConditions();

        registerCommands();
    }

    public static void registerCommands() {
        manager.registerCommand(new DebugCommand());
    }

    public static void registerConditions() {
        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayer", (command, exec, player) -> {
            WarlordsEntity issuerWarlordsPlayer = Warlords.getPlayer(command.getIssuer().getPlayer());
            if (issuerWarlordsPlayer == null) {
                throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
            }
        });

        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayerTarget", (command, exec, player) -> {
            if (!command.getIssuer().isPlayer()) {
                throw new ConditionFailedException(ChatColor.RED + "This command requires a player!");
            }
            WarlordsEntity issuerWarlordsPlayer = Warlords.getPlayer(command.getIssuer().getPlayer());
            if (issuerWarlordsPlayer == null) {
                throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
            }
            //target is arg else target is self
            if (player != null) {
                WarlordsEntity targetWarlordsPlayer = Warlords.getPlayer(player);
                if (targetWarlordsPlayer == null) {
                    throw new ConditionFailedException(ChatColor.RED + "Target must be in an active game to use this command!");
                }
                //make sure target is in the same game as the issuer
//                if(!issuerWarlordsPlayer.getGame().equals(targetWarlordsPlayer.getGame())) {
//                    throw new ConditionFailedException(ChatColor.RED + "You cannot use this command on players in different games!");
//                }
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "requireGame", (command, exec, player) -> {
            Optional<Game> playerGame = Warlords.getGameManager().getPlayerGame(command.getIssuer().getPlayer().getUniqueId());
            if (!playerGame.isPresent()) {
                throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
            }
        });
        manager.getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
                throw new ConditionFailedException("Min value must be " + c.getConfigValue("min", 0));
            }
            if (c.hasConfig("max") && c.getConfigValue("max", 3) < value) {
                throw new ConditionFailedException("Max value must be " + c.getConfigValue("max", 3));
            }
        });
    }

    public static void registerCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = manager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("warlordsplayerssamegame", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player) {
                Player player = (Player) sender;
                WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
                if (warlordsEntity != null) {
                    return warlordsEntity.getGame().warlordsPlayers().map(WarlordsEntity::getName).collect(Collectors.toList());
                }
            }
            return null;
        });
        commandCompletions.registerAsyncCompletion("warlordsplayers", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player) {
                return Warlords.getPlayers().values()
                        .stream()
                        .filter(WarlordsPlayer.class::isInstance)
                        .map(WarlordsPlayer.class::cast)
                        .map(WarlordsPlayer::getName)
                        .collect(Collectors.toList());
            }
            return null;
        });

    }

    public static void registerContexts() {
        manager.getCommandContexts().registerContext(WarlordsPlayer.class, command -> {
            String target = command.popFirstArg();
            boolean checkSelf = target.equals(DebugCommand.SELF);
            if (checkSelf) {
                target = command.getSender().getName();
            }
            String finalTarget = target;

            Optional<WarlordsPlayer> optionalWarlordsPlayer = Warlords.getPlayers().values()
                    .stream()
                    .filter(WarlordsPlayer.class::isInstance)
                    .map(WarlordsPlayer.class::cast)
                    .filter(warlordsPlayer -> warlordsPlayer.getName().equalsIgnoreCase(finalTarget))
                    .findAny();
            if (!optionalWarlordsPlayer.isPresent()) {
                if (checkSelf) {
                    throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
                } else {
                    throw new InvalidCommandArgument("Could not find WarlordsPlayer with name " + target);
                }
            }
            return optionalWarlordsPlayer.get();
        });
    }

    public static void requirePlayer(BukkitCommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            throw new ConditionFailedException(ChatColor.RED + "This command requires a player!");
        }
    }

}

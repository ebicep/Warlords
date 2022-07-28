package com.ebicep.warlords.commands2;

import co.aikar.commands.*;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands2.debugcommands.game.GameKillCommand;
import com.ebicep.warlords.commands2.debugcommands.game.GameListCommand;
import com.ebicep.warlords.commands2.debugcommands.ingame.DebugCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CommandManager {

    public static final String SELF = "*";
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
        manager.registerCommand(new GameKillCommand());
        manager.registerCommand(new GameListCommand());
    }

    public static void registerConditions() {
        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayer", (command, exec, player) -> {
            requireWarlordsPlayer(command.getIssuer());
        });

        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayerTarget", (command, exec, player) -> {
            requirePlayer(command.getIssuer());
            requireWarlordsPlayer(command.getIssuer());
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
        commandCompletions.registerAsyncCompletion("warlordsplayers", command ->
                Warlords.getPlayers().values()
                        .stream()
                        .filter(WarlordsPlayer.class::isInstance)
                        .map(WarlordsPlayer.class::cast)
                        .map(WarlordsPlayer::getName)
                        .collect(Collectors.toList())
        );
        commandCompletions.registerAsyncCompletion("maps", command ->
                Arrays.stream(GameMap.values())
                        .map(GameMap::name)
                        .collect(Collectors.toList()));
        commandCompletions.registerAsyncCompletion("gamemodes", command ->
                Arrays.stream(GameMode.values())
                        .map(GameMode::name)
                        .collect(Collectors.toList()));
        commandCompletions.registerAsyncCompletion("gameids", command ->
                Warlords.getGameManager().getGames()
                        .stream()
                        .map(GameManager.GameHolder::getGame)
                        .filter(Objects::nonNull)
                        .map(Game::getGameId)
                        .map(String::valueOf)
                        .collect(Collectors.toList())
        );

    }

    public static void registerContexts() {
        manager.getCommandContexts().registerContext(WarlordsPlayer.class, command -> {
            String target = command.popFirstArg();
            boolean checkSelf = target.equals(SELF);
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
        manager.getCommandContexts().registerContext(GameManager.GameHolder.class, command -> {
            String target = command.popFirstArg();
            boolean checkSelf = target.equals(SELF);
            if (checkSelf) {
                WarlordsEntity warlordsPlayer = requireWarlordsPlayer(command.getIssuer());
                return Warlords.getGameManager().getGames()
                        .stream()
                        .filter(game -> {
                            if (game.getGame() != null) {
                                return game.getGame().equals(warlordsPlayer.getGame());
                            }
                            return false;
                        })
                        .findAny()
                        .get();
            }
            Optional<GameManager.GameHolder> optionalGameHolder = Warlords.getGameManager().getGames().stream()
                    .filter(game -> game.getName().equals(target))
                    .findAny();
            if (!optionalGameHolder.isPresent()) {
                throw new InvalidCommandArgument("Could not find GameHolder with name " + target);
            }
            return optionalGameHolder.get();
        });
        manager.getCommandContexts().registerContext(UUID.class, command -> UUID.fromString(command.popFirstArg()));
    }

    public static void requirePlayer(BukkitCommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            throw new ConditionFailedException(ChatColor.RED + "This command requires a player!");
        }
    }

    public static WarlordsEntity requireWarlordsPlayer(BukkitCommandIssuer issuer) {
        WarlordsEntity issuerWarlordsPlayer = Warlords.getPlayer(issuer.getPlayer());
        if (issuerWarlordsPlayer == null) {
            throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
        }
        return issuerWarlordsPlayer;
    }

}

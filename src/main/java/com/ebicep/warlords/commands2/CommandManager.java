package com.ebicep.warlords.commands2;

import co.aikar.commands.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands2.debugcommands.game.GameKillCommand;
import com.ebicep.warlords.commands2.debugcommands.game.GameListCommand;
import com.ebicep.warlords.commands2.debugcommands.game.GameTerminateCommand;
import com.ebicep.warlords.commands2.debugcommands.game.PrivateGameTerminateCommand;
import com.ebicep.warlords.commands2.debugcommands.ingame.*;
import com.ebicep.warlords.commands2.debugcommands.misc.*;
import com.ebicep.warlords.commands2.miscellaneouscommands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
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
        manager.registerCommand(new GameTerminateCommand());
        manager.registerCommand(new PrivateGameTerminateCommand());

        manager.registerCommand(new DebugModeCommand());
        manager.registerCommand(new ImposterCommand());
        manager.registerCommand(new RecordAverageDamageCommand());
        manager.registerCommand(new SpawnTestDummyCommand());
        manager.registerCommand(new ToggleAFKDetectionCommand());
        manager.registerCommand(new ToggleOfflineFreezeCommand());
        manager.registerCommand(new UnstuckCommand(), true);

        manager.registerCommand(new ExperienceCommand());
        manager.registerCommand(new FindPlayerCommand());
        manager.registerCommand(new GamesCommand());
        manager.registerCommand(new GetPlayerLastAbilityStatsCommand());
        manager.registerCommand(new GetPlayersCommand());
        manager.registerCommand(new MuteCommand());
        manager.registerCommand(new MyLocationCommand());
        manager.registerCommand(new RecordGamesCommand());
        manager.registerCommand(new ServerStatusCommand());
        manager.registerCommand(new TestCommand());

        //manager.registerCommand(new AchievementsCommand());
        manager.registerCommand(new ChatCommand());
        manager.registerCommand(new ClassCommand());
        manager.registerCommand(new DiscordCommand());
        manager.registerCommand(new HotkeyModeCommand());
        manager.registerCommand(new LobbyCommand());
        manager.registerCommand(new ParticleQualityCommand());
        manager.registerCommand(new ResourcePackCommand());
        manager.registerCommand(new ShoutCommand());
        manager.registerCommand(new SpectateCommand());
    }

    public static void registerContexts() {
        manager.getCommandContexts().registerContext(DatabasePlayerFuture.class, command -> {
            String name = command.popFirstArg();
            OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
            return new DatabasePlayerFuture(CompletableFuture.supplyAsync(() -> {
                for (OfflinePlayer offlinePlayer : offlinePlayers) {
                    //~50ms
                    if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(name)) {
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(offlinePlayer.getUniqueId());
                        if (databasePlayer == null) {
                            throw new ConditionFailedException("Could not find DatabasePlayer with UUID " + offlinePlayer.getUniqueId() + " (" + offlinePlayer.getName() + ")");
                        }
                        return databasePlayer;
                    }
                }
                throw new ConditionFailedException("Could not find player with name " + name);
            }));
        });
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
                WarlordsEntity warlordsPlayer = requireWarlordsPlayer(command.getIssuer().getPlayer());
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
        manager.getCommandContexts().registerContext(Boolean.class, command -> {
            String arg = command.popFirstArg();
            return arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("enable");
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
        commandCompletions.registerAsyncCompletion("gameplayers", command ->
                Warlords.getGameManager().getGames().stream()
                        .map(GameManager.GameHolder::getGame)
                        .filter(Objects::nonNull)
                        .map(Game::getPlayers)
                        .map(Map::keySet)
                        .flatMap(Collection::stream)
                        .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                        .collect(Collectors.toList())

        );
        commandCompletions.registerAsyncCompletion("enabledisable", command -> Arrays.asList("enable", "disable"));
        commandCompletions.registerAsyncCompletion("boolean", command -> Arrays.asList("true", "false"));
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
        commandCompletions.registerAsyncCompletion("gameteams", command -> TeamMarker.getTeams(Warlords.getPlayer(command.getPlayer()).getGame()).stream().map(Team::getName).collect(Collectors.toList()));
        commandCompletions.registerAsyncCompletion("playerabilitystats", command -> GetPlayerLastAbilityStatsCommand.playerLastAbilityStats.keySet().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.toList()));
        commandCompletions.registerAsyncCompletion("chatchannels", command -> Arrays.asList("a", "all", "p", "party", "g", "guild"));

    }

    public static void registerConditions() {
        manager.getCommandConditions().addCondition("database", command -> {
            if (!DatabaseManager.enabled) {
                throw new ConditionFailedException(ChatColor.RED + "The database is not enabled!");
            }
            if (command.hasConfig("player") && DatabaseManager.playerService == null) {
                throw new ConditionFailedException(ChatColor.RED + "playerService is null");
            }
            if (command.hasConfig("game") && DatabaseManager.gameService == null) {
                throw new ConditionFailedException(ChatColor.RED + "gameService is null");
            }
        });
        manager.getCommandConditions().addCondition("bot", command -> {
            if (BotManager.jda == null) {
                throw new ConditionFailedException(ChatColor.RED + "The bot is not enabled!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayer", (command, exec, player) -> requireWarlordsPlayer(player));

//        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayerTarget", (command, exec, player) -> {
//            requirePlayer(command.getIssuer());
//            requireWarlordsPlayer(player);
//            //target is arg else target is self
//            if (player != null) {
//                WarlordsEntity targetWarlordsPlayer = Warlords.getPlayer(player);
//                if (targetWarlordsPlayer == null) {
//                    throw new ConditionFailedException(ChatColor.RED + "Target must be in an active game to use this command!");
//                }
//                //make sure target is in the same game as the issuer
////                if(!issuerWarlordsPlayer.getGame().equals(targetWarlordsPlayer.getGame())) {
////                    throw new ConditionFailedException(ChatColor.RED + "You cannot use this command on players in different games!");
////                }
//            }
//        });
        manager.getCommandConditions().addCondition(Player.class, "requireGame", (command, exec, player) -> {
            Optional<Game> game = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
            if (!game.isPresent()) {
                BukkitCommandIssuer issuer = command.getIssuer();
                if (issuer.isPlayer() && issuer.getPlayer().equals(player)) {
                    throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
                } else {
                    throw new ConditionFailedException(ChatColor.RED + "Target player must be in an active game to use this command!");
                }
            }
            if (command.hasConfig("withAddon")) {
                GameAddon addon = GameAddon.valueOf(command.getConfigValue("withAddon", ""));
                if (!game.get().getAddons().contains(addon)) {
                    throw new ConditionFailedException(ChatColor.RED + "Game does not contain addon " + addon.name());
                }
            }
            if (command.hasConfig("unfrozen")) {
                if (game.get().isFrozen()) {
                    throw new ConditionFailedException(ChatColor.RED + "You cannot use this command while the game is frozen!");
                }
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "outsideGame", (command, exec, player) -> {
            if (Warlords.hasPlayer(player)) {
                throw new ConditionFailedException(ChatColor.RED + "You cannot use this command while in an active game!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "requireParty", (command, exec, player) -> {
            Optional<Party> optionalParty = Warlords.partyManager.getPartyFromAny(player.getUniqueId());
            if (!optionalParty.isPresent()) {
                throw new ConditionFailedException(ChatColor.RED + "You must be in a party to use this command!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "requireGuild", (command, exec, player) -> {
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair == null) {
                throw new ConditionFailedException(ChatColor.RED + "You must be in a guild to use this command!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "otherChatChannel", (command, exec, player) -> {
            ChatChannels selectedChatChannel = Warlords.playerChatChannels.get(player.getUniqueId());
            if (command.hasConfig("target")) {
                ChatChannels currentChatChannel = ChatChannels.valueOf(command.getConfigValue("target", ""));
                if (selectedChatChannel == currentChatChannel) {
                    throw new ConditionFailedException(ChatColor.RED + "You are already in this channel");
                }
            }
        });
        manager.getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
            if (value == null) {
                return;
            }
            if (c.hasConfig("previousGames")) {
                int size = DatabaseGameBase.previousGames.size();
                if (size == 0) {
                    throw new ConditionFailedException("No previous games found!");
                }
                if (value < 0 || value > size) {
                    throw new ConditionFailedException("Game must be an index in the previous games list!");
                }
                return;
            }

            Integer min = c.getConfigValue("min", 0);
            Integer max = c.getConfigValue("max", 3);

            if (c.hasConfig("min") && min > value) {
                throw new ConditionFailedException("Min value must be " + min);
            }
            if (c.hasConfig("max") && max < value) {
                throw new ConditionFailedException("Max value must be " + max);
            }
        });
    }

    public static void requirePlayer(BukkitCommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            throw new ConditionFailedException(ChatColor.RED + "This command requires a player!");
        }
    }

    public static WarlordsEntity requireWarlordsPlayer(Player player) {
        WarlordsEntity issuerWarlordsPlayer = Warlords.getPlayer(player);
        if (issuerWarlordsPlayer == null) {
            throw new ConditionFailedException(ChatColor.RED + "You must be in an active game to use this command!");
        }
        return issuerWarlordsPlayer;
    }
}

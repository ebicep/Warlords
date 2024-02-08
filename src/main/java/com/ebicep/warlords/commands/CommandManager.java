package com.ebicep.warlords.commands;

import co.aikar.commands.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.ebicep.customentities.npc.NPCCommand;
import com.ebicep.jda.BotCommand;
import com.ebicep.jda.BotManager;
import com.ebicep.jda.queuesystem.QueueCommand;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.game.*;
import com.ebicep.warlords.commands.debugcommands.ingame.*;
import com.ebicep.warlords.commands.debugcommands.misc.*;
import com.ebicep.warlords.commands.miscellaneouscommands.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.*;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.game.option.pve.onslaught.OnslaughtCommand;
import com.ebicep.warlords.game.option.pve.tutorial.TutorialCommand;
import com.ebicep.warlords.game.option.pve.wavedefense.commands.EditCurrencyCommand;
import com.ebicep.warlords.game.option.pve.wavedefense.commands.WaveCommand;
import com.ebicep.warlords.game.option.towerdefense.TowerDefenseCommand;
import com.ebicep.warlords.guilds.Guild;
import com.ebicep.warlords.guilds.GuildManager;
import com.ebicep.warlords.guilds.GuildPermissions;
import com.ebicep.warlords.guilds.GuildPlayer;
import com.ebicep.warlords.guilds.commands.GuildCommand;
import com.ebicep.warlords.guilds.commands.GuildDebugCommand;
import com.ebicep.warlords.guilds.commands.GuildPlayerWrapper;
import com.ebicep.warlords.menu.generalmenu.WarlordsNewHotbarMenu;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.PartyPlayerType;
import com.ebicep.warlords.party.commands.PartyCommand;
import com.ebicep.warlords.party.commands.PartyPlayerWrapper;
import com.ebicep.warlords.party.commands.StreamCommand;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.poll.AbstractPoll;
import com.ebicep.warlords.poll.PollCommand;
import com.ebicep.warlords.pve.bountysystem.BountyCommand;
import com.ebicep.warlords.pve.commands.*;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairCommand;
import com.ebicep.warlords.pve.gameevents.libraryarchives.PlayerCodexCommand;
import com.ebicep.warlords.pve.items.ItemsCommand;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.quests.QuestCommand;
import com.ebicep.warlords.pve.weapons.WeaponCommand;
import com.ebicep.warlords.sr.BalancerCommand;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.Year;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static co.aikar.commands.ACFBukkitUtil.isValidName;

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

    public static void registerContexts() {
        //Issuer aware contexts
        manager.getCommandContexts().registerIssuerAwareContext(Player.class, (c) -> {
            boolean isOptional = c.isOptional();
            CommandSender sender = c.getSender();
            boolean isPlayerSender = sender instanceof Player;
            if (!c.hasFlag("other")) {
                Player player = isPlayerSender ? (Player) sender : null;
                if (player == null && !isOptional) {
                    throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
                }
//                PlayerInventory inventory = player != null ? player.getInventory() : null;
//                if (inventory != null && c.hasFlag("itemheld") && !ACFBukkitUtil.isValidItem(inventory.getItem(inventory.getHeldItemSlot()))) {
//                    throw new InvalidCommandArgument(MinecraftMessageKeys.YOU_MUST_BE_HOLDING_ITEM, false);
//                }
                return player;
            } else {
                String arg = c.popFirstArg();
                if (arg == null && isOptional) {
                    if (c.hasFlag("defaultself")) {
                        if (isPlayerSender) {
                            return (Player) sender;
                        } else {
                            throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
                        }
                    } else {
                        return null;
                    }
                } else if (arg == null) {
                    throw new InvalidCommandArgument();
                }

                OnlinePlayer onlinePlayer = getOnlinePlayer(c.getIssuer(), arg, isOptional);
                return onlinePlayer != null ? onlinePlayer.getPlayer() : null;
            }
        });
        //TODO fix syntax/completion when this is issuer
        manager.getCommandContexts().registerIssuerAwareContext(WarlordsPlayer.class, command -> {
            String target;
            String name = command.getSender().getName();
            if (command.getIndex() == 0) {
                target = name;
            } else {
                String arg = command.popFirstArg();
                target = arg == null ? name : arg;
            }
            Optional<WarlordsPlayer> optionalWarlordsPlayer = Warlords.getPlayers().values()
                                                                      .stream()
                                                                      .filter(WarlordsPlayer.class::isInstance)
                                                                      .map(WarlordsPlayer.class::cast)
                                                                      .filter(warlordsPlayer -> warlordsPlayer.getName().equalsIgnoreCase(target))
                                                                      .findAny();
            if (optionalWarlordsPlayer.isEmpty()) {
                if (target.equals(name)) {
                    throw new ConditionFailedException("You must be in an active game to use this command!");
                } else {
                    throw new InvalidCommandArgument("Could not find WarlordsPlayer with name " + target);
                }
            }
            return optionalWarlordsPlayer.get();
        });
        //Issuer only contexts
        manager.getCommandContexts().registerIssuerOnlyContext(PartyPlayerWrapper.class, command -> {
            Player player = command.getPlayer();
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (partyPlayerPair != null) {
                if (command.hasFlag("leader") && partyPlayerPair.getB().getPartyPlayerType() != PartyPlayerType.LEADER) {
                    Party.sendPartyMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
                    throw new ConditionFailedException();
                }
                return new PartyPlayerWrapper(partyPlayerPair);
            }
            throw new ConditionFailedException("You must be in a party to use this command!");
        });
        manager.getCommandContexts().registerIssuerOnlyContext(GuildPlayerWrapper.class, command -> {
            Player player = command.getPlayer();
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair != null) {
                if (command.hasFlag("master") && !guildPlayerPair.getA().getCurrentMaster().equals(player.getUniqueId())) {
                    Guild.sendGuildMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
                    throw new ConditionFailedException();
                }
                return new GuildPlayerWrapper(guildPlayerPair);
            }
            throw new ConditionFailedException("You must be in a guild to use this command!");
        });
        //Contexts
        manager.getCommandContexts().registerContext(WarlordsNPC.class, command -> {
            String target = command.popFirstArg();
            Optional<WarlordsNPC> optionalWarlordsNPC = Warlords.getPlayers().values()
                                                                .stream()
                                                                .filter(WarlordsNPC.class::isInstance)
                                                                .map(WarlordsNPC.class::cast)
                                                                .filter(warlordsNPC -> warlordsNPC.getUuid().equals(UUID.fromString(target)))
                                                                .findAny();
            if (optionalWarlordsNPC.isEmpty()) {
                throw new InvalidCommandArgument("Could not find WarlordsNPC with UUID " + target);
            }
            return optionalWarlordsNPC.get();
        });
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
        manager.getCommandContexts().registerContext(UUID.class, command -> UUID.fromString(command.popFirstArg()));
        manager.getCommandContexts().registerContext(Boolean.class, command -> {
            String arg = command.popFirstArg();
            return arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("enable");
        });
        manager.getCommandContexts().registerContext(PartyPlayer.class, command -> {
            String arg = command.popFirstArg();
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(command.getPlayer().getUniqueId());
            if (partyPlayerPair == null) {
                throw new ConditionFailedException("You must be in a party to use this command!");
            }
            for (PartyPlayer partyPlayer : partyPlayerPair.getA().getPartyPlayers()) {
                if (Bukkit.getOfflinePlayer(partyPlayer.getUUID()).getName().equalsIgnoreCase(arg)) {
                    return partyPlayer;
                }
            }
            throw new InvalidCommandArgument("Could not find a player in your party with the name " + arg);
        });
        manager.getCommandContexts().registerContext(GuildPlayer.class, command -> {
            String arg = command.popFirstArg();
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(command.getPlayer());
            if (guildPlayerPair == null) {
                throw new ConditionFailedException("You must be in a guild to use this command!");
            }
            for (GuildPlayer guildPlayer : guildPlayerPair.getA().getPlayers()) {
                if (guildPlayer.getName().equalsIgnoreCase(arg)) {
                    return guildPlayer;
                }
            }
            throw new InvalidCommandArgument("Could not find a player in your guild with the name " + arg);
        });
        manager.getCommandContexts().registerContext(AbstractPoll.class, command -> {
            Optional<AbstractPoll<?>> optionalPoll = AbstractPoll.getPoll(command.popFirstArg());
            if (optionalPoll.isEmpty()) {
                throw new InvalidCommandArgument("Could not find a poll with that ID");
            }
            if (!optionalPoll.get().getUUIDsAllowedToVote().contains(command.getPlayer().getUniqueId())) {
                throw new ConditionFailedException("You can't vote in this poll!");
            }
            return optionalPoll.get();
        });
        manager.getCommandContexts().registerContext(Game.class, command -> {
            String gameID = command.popFirstArg();
            Optional<Game> game = Warlords.getGameManager()
                                          .getGames()
                                          .stream()
                                          .map(GameManager.GameHolder::getGame)
                                          .filter(Objects::nonNull)
                                          .filter(gameHolderGame -> gameHolderGame.getGameId().toString().equalsIgnoreCase(gameID))
                                          .findAny();
            if (game.isPresent()) {
                return game.get();
            } else {
                throw new InvalidCommandArgument("Could not find a game with that ID");
            }
        });
        manager.getCommandContexts().registerContext(Instant.class, command -> Instant.parse(command.popFirstArg()));
        manager.getCommandContexts().registerContext(Year.class, command -> Year.parse(command.popFirstArg()));
        manager.getCommandContexts().registerContext(NamedTextColor.class, command -> {
            NamedTextColor namedTextColor = NamedTextColor.NAMES.value(command.popFirstArg().toLowerCase());
            if (namedTextColor == null) {
                throw new InvalidCommandArgument("Could not find a color with that name");
            }
            return namedTextColor;
        });
        manager.getCommandContexts().registerContext(GameMap.class, command -> {
            String map = command.popFirstArg();
            for (GameMap value : GameMap.VALUES) {
                if (value.getMapName().equalsIgnoreCase(map)) {
                    return value;
                }
            }
            throw new InvalidCommandArgument("Could not find a game map with that name");
        });
    }

    public static void registerCompletions() {
        CommandCompletions<BukkitCommandCompletionContext> commandCompletions = manager.getCommandCompletions();
        commandCompletions.registerAsyncCompletion("warlordsplayerssamegame", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player player) {
                WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
                if (warlordsEntity != null) {
                    return warlordsEntity.getGame().warlordsPlayers().map(WarlordsEntity::getName).toList();
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
                        .toList()
        );
        commandCompletions.registerAsyncCompletion("warlordsnpcs", command ->
                Warlords.getPlayers().values()
                        .stream()
                        .filter(WarlordsNPC.class::isInstance)
                        .map(WarlordsNPC.class::cast)
                        .map(WarlordsNPC::getUuid)
                        .map(UUID::toString)
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
                        .toList()

        );
        commandCompletions.registerAsyncCompletion("enabledisable", command -> Arrays.asList("enable", "disable"));
        commandCompletions.registerAsyncCompletion("boolean", command -> Arrays.asList("true", "false"));
        commandCompletions.registerAsyncCompletion("maps", command ->
                Arrays.stream(GameMap.VALUES)
                      .map(GameMap::getMapName)
                      .toList());
        commandCompletions.registerAsyncCompletion("gamemodes", command ->
                Arrays.stream(GameMode.VALUES)
                      .map(GameMode::name)
                      .toList());
        commandCompletions.registerAsyncCompletion("gameids", command ->
                Warlords.getGameManager().getGames()
                        .stream()
                        .map(GameManager.GameHolder::getGame)
                        .filter(Objects::nonNull)
                        .map(Game::getGameId)
                        .map(String::valueOf)
                        .toList()
        );
        commandCompletions.registerAsyncCompletion("gameteams",
                command -> TeamMarker.getTeams(Warlords.getPlayer(command.getPlayer()).getGame()).stream().map(Team::getName).toList()
        );
        commandCompletions.registerAsyncCompletion("playerabilitystats",
                command -> GetPlayerLastAbilityStatsCommand.PLAYER_LAST_ABILITY_STATS.keySet()
                                                                                     .stream()
                                                                                     .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                                                                     .toList()
        );
        commandCompletions.registerAsyncCompletion("chatchannels", command -> Arrays.asList("a", "all", "p", "party", "g", "guild"));
        commandCompletions.registerAsyncCompletion("partymembers", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player) {
                return PartyManager.PARTIES.stream()
                                           .filter(party -> party.hasUUID(((Player) sender).getUniqueId()))
                                           .map(Party::getPartyPlayers)
                                           .flatMap(Collection::stream)
                                           .map(PartyPlayer::getUUID)
                                           .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                           .toList();
            }
            return null;
        });
        commandCompletions.registerAsyncCompletion("guildmembers", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player) {
                return GuildManager.GUILDS.stream()
                                          .filter(guild -> guild.hasUUID(((Player) sender).getUniqueId()))
                                          .map(Guild::getPlayers)
                                          .flatMap(Collection::stream)
                                          .map(GuildPlayer::getUUID)
                                          .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                                          .toList();
            }
            return null;
        });
        commandCompletions.registerAsyncCompletion("guildnames", command -> {
            CommandSender sender = command.getSender();
            if (sender instanceof Player) {
                return GuildManager.GUILDS.stream()
                                          .map(Guild::getName)
                                          .toList();
            }
            return null;
        });
        commandCompletions.registerAsyncCompletion("pvemobs", command -> Arrays.stream(Mob.values())
                                                                               .map(Mob::name)
                                                                               .toList());
        commandCompletions.registerAsyncCompletion("classesalias", command -> Classes.NAMES);
        commandCompletions.registerAsyncCompletion("specsalias", command -> Specializations.NAMES);
        commandCompletions.registerAsyncCompletion("textcolors", command -> NamedTextColor.NAMES.keys());
        commandCompletions.registerAsyncCompletion("floatmodifiabletype", command -> Arrays.stream(PrintFloatModifiableCommand.Type.VALUES)
                                                                                           .map(Enum::name)
                                                                                           .toList());

    }

    public static void registerConditions() {
        manager.getCommandConditions().addCondition("database", command -> {
            if (!DatabaseManager.enabled) {
                throw new ConditionFailedException("The database is currently disabled!");
            }
            if (command.hasConfig("player") && DatabaseManager.playerService == null) {
                throw new ConditionFailedException("Player database is currently disabled!");
            }
            if (command.hasConfig("game") && DatabaseManager.gameService == null) {
                throw new ConditionFailedException("Games are currently disabled!");
            }
            if (command.hasConfig("guild") && DatabaseManager.guildService == null) {
                throw new ConditionFailedException("Guilds are current disabled!");
            }
        });
        manager.getCommandConditions().addCondition("bot", command -> {
            if (BotManager.jda == null) {
                throw new ConditionFailedException("The bot is not enabled!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "requireWarlordsPlayer", (command, exec, player) -> requireWarlordsPlayer(player));

        manager.getCommandConditions().addCondition(Player.class, "requireGame", (command, exec, player) -> {
            Optional<Game> game = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
            if (game.isEmpty()) {
                BukkitCommandIssuer issuer = command.getIssuer();
                if (issuer.isPlayer() && issuer.getPlayer().equals(player)) {
                    throw new ConditionFailedException("You must be in an active game to use this command!");
                } else {
                    throw new ConditionFailedException("Target player must be in an active game to use this command!");
                }
            }
            requireGameConfig(command, game.get());
        });
        manager.getCommandConditions().addCondition(WarlordsPlayer.class, "requireGame", (command, exec, warlordsPlayer) -> {
            Game game = warlordsPlayer.getGame();
            if (game == null) {
                throw new ConditionFailedException("You must be in an active game to use this command!");
            }
            requireGameConfig(command, game);
        });
        manager.getCommandConditions().addCondition(Game.class, "filter", (command, exec, game) -> {
            requireGameConfig(command, game);
        });

        manager.getCommandConditions().addCondition(Player.class, "outsideGame", (command, exec, player) -> {
            if (Warlords.hasPlayer(player)) {
                throw new ConditionFailedException("You cannot use this command while in an active game!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "party", (command, exec, player) -> {
            Pair<Party, PartyPlayer> optionalParty = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (optionalParty == null && command.hasConfig("true")) {
                throw new ConditionFailedException("You must be in a party to use this command!");
            }
            if (optionalParty != null && command.hasConfig("false")) {
                throw new ConditionFailedException("You cannot be in a party to use this command!");

            }
        });
        manager.getCommandConditions().addCondition(Player.class, "guild", (command, exec, player) -> {
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair == null && command.hasConfig("true")) {
                throw new ConditionFailedException("You must be in a guild to use this command!");
            }
            if (guildPlayerPair != null && command.hasConfig("false")) {
                throw new ConditionFailedException("You cannot be in a guild to use this command!");
            }
        });
        manager.getCommandConditions().addCondition(Player.class, "otherChatChannel", (command, exec, player) -> {
            ChatChannels selectedChatChannel = ChatChannels.PLAYER_CHAT_CHANNELS.get(player.getUniqueId());
            if (command.hasConfig("target")) {
                ChatChannels currentChatChannel = ChatChannels.valueOf(command.getConfigValue("target", ""));
                if (selectedChatChannel == currentChatChannel) {
                    throw new ConditionFailedException("You are already in this channel");
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
        manager.getCommandConditions().addCondition(Double.class, "limits", (c, exec, value) -> {
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

            double min = Double.parseDouble(c.getConfigValue("min", ""));
            double max = Double.parseDouble(c.getConfigValue("max", ""));

            if (c.hasConfig("min") && min > value) {
                throw new ConditionFailedException("Min value must be " + min);
            }
            if (c.hasConfig("max") && max < value) {
                throw new ConditionFailedException("Max value must be " + max);
            }
        });
        manager.getCommandConditions().addCondition(PartyPlayer.class, "lowerRank", (command, exec, partyPlayer) -> {
            Player player = command.getIssuer().getPlayer();
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUniqueId());
            if (partyPlayerPair == null) {
                throw new ConditionFailedException("You must be in a party to use this command!");
            }
            if (partyPlayerPair.getB().getPartyPlayerType().ordinal() >= partyPlayer.getPartyPlayerType().ordinal()) {
                Party.sendPartyMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
                throw new ConditionFailedException();
            }
        });
        manager.getCommandConditions().addCondition(GuildPlayer.class, "lowerRank", (command, exec, guildPlayer) -> {
            Player player = command.getIssuer().getPlayer();
            Pair<Guild, GuildPlayer> guildPlayerPair = GuildManager.getGuildAndGuildPlayerFromPlayer(player);
            if (guildPlayerPair == null) {
                throw new ConditionFailedException("You must be in a party to use this command!");
            }
            Guild guild = guildPlayerPair.getA();
            if (guild.getRoleLevel(guildPlayerPair.getB()) >= guild.getRoleLevel(guildPlayer)) {
                Guild.sendGuildMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
                throw new ConditionFailedException();
            }
        });
        manager.getCommandConditions().addCondition(GuildPlayerWrapper.class, "requirePerm", (command, exec, guildPlayerWrapper) -> {
            Player player = command.getIssuer().getPlayer();
            GuildPermissions perm = GuildPermissions.valueOf(command.getConfigValue("perm", ""));
            if (!guildPlayerWrapper.getGuild().playerHasPermission(guildPlayerWrapper.getGuildPlayer(), perm)) {
                Guild.sendGuildMessage(player, Component.text("Insufficient Permissions!", NamedTextColor.RED));
                throw new ConditionFailedException();
            }
        });
    }

    public static void registerCommands() {
        manager.registerCommand(new NPCCommand());

        manager.registerCommand(new GameDebugCommand());
        manager.registerCommand(new GameInfoCommand());
        manager.registerCommand(new GameKillCommand());
        manager.registerCommand(new GameListCommand());
        manager.registerCommand(new GameTerminateCommand());
        manager.registerCommand(new PrivateGameTerminateCommand());

        manager.registerCommand(new DebugCommand());
        manager.registerCommand(new DebugModeCommand());
        manager.registerCommand(new AbilityCommand());
        manager.registerCommand(new GameLeaveCommand());
        manager.registerCommand(new ImposterCommand());
        manager.registerCommand(new PrintFloatModifiableCommand());
        manager.registerCommand(new PrintSpecStatsCommand());
        manager.registerCommand(new StunClearCommand());
        manager.registerCommand(new RecordAverageDamageCommand());
        manager.registerCommand(new SpawnTestDummyCommand());
        manager.registerCommand(new ToggleAFKDetectionCommand());
        manager.registerCommand(new ToggleOfflineFreezeCommand());
        manager.registerCommand(new UnstuckCommand(), true);

        manager.registerCommand(new AdminCommand());
        manager.registerCommand(new DatabaseCommand());
        manager.registerCommand(new DebugValueCommand());
        manager.registerCommand(new EditStatsCommand());
        manager.registerCommand(new ExperienceCommand());
        manager.registerCommand(new FindPlayerCommand());
        manager.registerCommand(new FlyCommand());
        manager.registerCommand(new GamesCommand());
        manager.registerCommand(new GetPlayerLastAbilityStatsCommand());
        manager.registerCommand(new GetPlayersCommand());
        manager.registerCommand(new MuteCommand());
        manager.registerCommand(new PatchCommand());
        manager.registerCommand(new LocationCommand());
        manager.registerCommand(new PatreonCommand());
        manager.registerCommand(new PvECurrencyCommand());
        manager.registerCommand(new RecordGamesCommand());
        manager.registerCommand(new SeeAllChatsCommand());
        manager.registerCommand(new ServerStatusCommand());
        manager.registerCommand(new SudoCommand());
        manager.registerCommand(new TestCommand());
        manager.registerCommand(new TournamentCommand());
        manager.registerCommand(new WarlordsPlusCommand());

        manager.registerCommand(new AchievementsCommand(), true);
        manager.registerCommand(new ChatCommand());
        manager.registerCommand(new ClassCommand());
        manager.registerCommand(new DiscordCommand());
        manager.registerCommand(new FastWaveModeCommand());
        manager.registerCommand(new FlagMessageModeCommand());
        manager.registerCommand(new GlowingModeCommand());
        manager.registerCommand(new HotkeyModeCommand());
        manager.registerCommand(new IgnoreCommand());
        manager.registerCommand(new LobbyCommand());
        manager.registerCommand(new ParticleQualityCommand());
        manager.registerCommand(new ResourcePackCommand());
        manager.registerCommand(new ShoutCommand());
        manager.registerCommand(new SpectateCommand());
        manager.registerCommand(new StreamChaptersCommand());
        manager.registerCommand(new MessageCommand());

        manager.registerCommand(new StatsLeaderboardCommand());

        manager.registerCommand(new PartyCommand(), true);
        manager.registerCommand(new StreamCommand());

        manager.registerCommand(new PollCommand());

        manager.registerCommand(new BotCommand());
        manager.registerCommand(new QueueCommand());

        manager.registerCommand(new EditCurrencyCommand());
        manager.registerCommand(new MobCommand());
        manager.registerCommand(new WaveCommand());
        manager.registerCommand(new OnslaughtCommand());

        manager.registerCommand(new PlayerCodexCommand());

        manager.registerCommand(new GuildCommand());
        manager.registerCommand(new GuildDebugCommand());

        manager.registerCommand(new MyPositionCommand());
        manager.registerCommand(new CompensateCommand());

        manager.registerCommand(new QuestCommand());
        manager.registerCommand(new WeaponCommand());
        manager.registerCommand(new MasterworksFairCommand());
        manager.registerCommand(new ItemsCommand());

        manager.registerCommand(new TutorialCommand());

        manager.registerCommand(new WarlordsNewHotbarMenu.SettingsMenu());

        manager.registerCommand(new AbilityTreeCommand());

        manager.registerCommand(new GameJoinCommand());

        manager.registerCommand(new BountyCommand());

        manager.registerCommand(new BalancerCommand());

        manager.registerCommand(new TowerDefenseCommand());
    }

    @Nullable
    public static OnlinePlayer getOnlinePlayer(BukkitCommandIssuer issuer, String lookup, boolean allowMissing) throws InvalidCommandArgument {
        Player player = findPlayerSmart(issuer, lookup);
        if (player == null) {
            if (allowMissing) {
                return null;
            }
            throw new InvalidCommandArgument(false);
        }
        return new OnlinePlayer(player);
    }

    public static void requireWarlordsPlayer(Player player) {
        WarlordsEntity issuerWarlordsPlayer = Warlords.getPlayer(player);
        if (issuerWarlordsPlayer == null) {
            throw new ConditionFailedException("You must be in an active game to use this command!");
        }
    }

    public static void requireGameConfig(ConditionContext<BukkitCommandIssuer> command, Game game) {
        if (command.hasConfig("gamemode")) {
            String configValue = command.getConfigValue("gamemode", "");
            String[] gameModes = configValue.split("/");
            boolean hasGameMode = false;
            for (String gameMode : gameModes) {
                if (gameMode.equals("PVE") && GameMode.isPvE(game.getGameMode())) {
                    hasGameMode = true;
                    break;
                }
                GameMode gamemode = GameMode.valueOf(gameMode);
                if (game.getGameMode() == gamemode) {
                    hasGameMode = true;
                    break;
                }
            }
            if (!hasGameMode) {
                throw new ConditionFailedException("Game does not contain gamemode " + configValue);
            }
        }
        if (command.hasConfig("withAddon")) {
            GameAddon addon = GameAddon.valueOf(command.getConfigValue("withAddon", ""));
            if (!game.getAddons().contains(addon)) {
                throw new ConditionFailedException("Game does not contain addon " + addon.name());
            }
        }
        if (command.hasConfig("unfrozen")) {
            if (game.isFrozen()) {
                throw new ConditionFailedException("You cannot use this command while the game is frozen!");
            }
        }
    }

    public static Player findPlayerSmart(CommandIssuer issuer, String search) {
        CommandSender requester = issuer.getIssuer();
        if (search == null) {
            return null;
        }
        String name = ACFUtil.replace(search, ":confirm", "");

        if (!isValidName(name)) {
            issuer.sendError(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, "{name}", name);
            return null;
        }

        List<Player> matches = Bukkit.getServer().matchPlayer(name);
        List<Player> confirmList = new ArrayList<>();
        //findMatches(search, requester, matches, confirmList);


        if (matches.size() > 1 || confirmList.size() > 1) {
            String allMatches = matches.stream().map(Player::getName).collect(Collectors.joining(", "));
            issuer.sendError(MinecraftMessageKeys.MULTIPLE_PLAYERS_MATCH,
                    "{search}", name, "{all}", allMatches
            );
            return null;
        }

        if (matches.isEmpty()) {
            Player player = ACFUtil.getFirstElement(confirmList);
            if (player == null) {
                issuer.sendError(MinecraftMessageKeys.NO_PLAYER_FOUND_SERVER, "{search}", name);
            } else {
                issuer.sendInfo(MinecraftMessageKeys.PLAYER_IS_VANISHED_CONFIRM, "{vanished}", player.getName());
            }
            return null;
        }

        return matches.get(0);
    }

    public static void requirePlayer(BukkitCommandIssuer issuer) {
        if (!issuer.isPlayer()) {
            throw new ConditionFailedException("This command requires a player!");
        }
    }
}

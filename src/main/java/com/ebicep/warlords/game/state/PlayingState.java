package com.ebicep.warlords.game.state;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.commands.debugcommands.misc.RecordGamesCommand;
import com.ebicep.warlords.commands.debugcommands.misc.WarlordsPlusCommand;
import com.ebicep.warlords.commands.miscellaneouscommands.StreamChaptersCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.LocationMarker;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.PlayerStatisticsMinute;
import com.ebicep.warlords.player.ingame.PlayerStatisticsSecond;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.RemoveEntities;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

public class PlayingState implements State, TimerDebugAble {

    private final Game game;
    private WarlordsGameTriggerWinEvent winEvent;
    private int counter = 0;
    private int timer = 0;

    private AtomicBoolean gameAdded = new AtomicBoolean(false);

    public PlayingState(@Nonnull Game game) {
        this.game = game;
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Game " + game.getGameId() + " has started");
        Warlords.getGameManager().getGames().stream()
                .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().equals(game))
                .findAny()
                .ifPresent(gameHolder -> {
                    ChatChannels.sendDebugMessage((CommandIssuer) null,
                            ChatColor.LIGHT_PURPLE + "Started Game: " + game.getGameMode() + " - " + gameHolder.getName()
                    );
                });
        this.game.setAcceptsSpectators(true);
        this.game.setAcceptsPlayers(false);
        this.resetTimer();
        RemoveEntities.doRemove(this.game);
        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Adding game options");
        for (Option option : game.getOptions()) {
            option.start(game);
        }
        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Game options added");

        List<UUID> toRemove = new ArrayList<>();
        AtomicBoolean closeGame = new AtomicBoolean(false);
        this.game.forEachOfflinePlayer((player, team) -> {
            Player p = player.getPlayer();
            if (team != null && (!com.ebicep.warlords.game.GameMode.isWaveDefense(game.getGameMode()) || player.isOnline())) {
                if (com.ebicep.warlords.game.GameMode.isWaveDefense(game.getGameMode())) {
                    PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                    Specializations selectedSpec = playerSettings.getSelectedSpec();
                    if (selectedSpec.isBanned()) {
                        for (Specializations value : Specializations.VALUES) {
                            if (value.isBanned()) {
                                continue;
                            }
                            if (p != null) {
                                p.sendMessage(ChatColor.RED + selectedSpec.name + " is currently disabled. Your specialization has been changed.");
                            }
                            playerSettings.setSelectedSpec(value);
                            break;
                        }
                        if (playerSettings.getSelectedSpec().isBanned()) {
                            if (p != null) {
                                p.sendMessage(ChatColor.RED + "All specializations are currently disabled. Game closing.");
                            }
                            closeGame.set(true);
                        }
                    }
                }
                Warlords.addPlayer(new WarlordsPlayer(
                        player,
                        this.getGame(),
                        team
                ));
            } else {
                toRemove.add(player.getUniqueId());
                return;
            }
            if (p != null) {
                p.getInventory().setHeldItemSlot(0);
                Utils.resetPlayerMovementStatistics(p);
            }
        });
        toRemove.forEach(this.game::removePlayer);

        if (closeGame.get()) {
            new BukkitRunnable() {

                @Override
                public void run() {
                    game.close();
                }
            }.runTaskLater(Warlords.getInstance(), 20);
        }

        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                game.setNextState(new EndState(game, event, gameAdded));
                winEvent = event;
            }
        });
        new GameRunnable(game) {

            @Override
            public void run() {
                game.forEachOnlinePlayer((player, team) -> {
                    updateBasedOnGameState(CustomScoreboard.getPlayerScoreboard(player), (WarlordsPlayer) Warlords.getPlayer(player));
                });
            }
        }.runTaskTimer(0, 10);

        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Started recording timed stats");

        new GameRunnable(game) {

            @Override
            public void run() {
                counter++;
                timer += GameRunnable.SECOND;
                if (counter >= 60) {
                    counter -= 60;
                    PlayerFilter.playingGame(game).forEach(wp -> {
                        PlayerStatisticsMinute minuteStats = wp.getMinuteStats();
                        minuteStats.advanceMinute();
                        //remove minute stats if over 30 minutes for memory
                        if (minuteStats.getEntries().size() > 30) {
                            minuteStats.getEntries().remove(0);
                        }
                    });
                }
                PlayerFilter.playingGame(game).forEach(wp -> {
                    PlayerStatisticsSecond secondStats = wp.getSecondStats();
                    secondStats.advanceSecond();
                    //remove second stats if over 10 minutes for memory
                    if (secondStats.getEntries().size() > 60 * 10) {
                        secondStats.getEntries().remove(0);
                    }
                });
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delay) -> {
            counter += delay / GameRunnable.SECOND;
            timer += delay;
        });

        this.game.forEachOfflineWarlordsPlayer(wp -> {
            if (StreamChaptersCommand.GAME_TIMES.containsKey(wp.getUuid())) {
                StreamChaptersCommand.GAME_TIMES.get(wp.getUuid())
                                                .add(new StreamChaptersCommand.GameTime(Instant.now(), game.getMap(), wp.getSpecClass(), game.playersCount()));
            }
        });

        Warlords.getInstance().hideAndUnhidePeople();
        Game.reopenGameReferencedMenus();

        ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Game start done");
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    private void updateBasedOnGameState(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        this.updateHealth(customScoreboard);
        this.updateNames(customScoreboard);
        this.updateBasedOnGameScoreboards(customScoreboard, warlordsPlayer);
    }

    private void updateHealth(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective health = customScoreboard.getHealth();
        if (health == null || scoreboard.getObjective("health") == null) {
            health = scoreboard.registerNewObjective("health", "dummy");
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            health.setDisplayName(ChatColor.RED + "❤");
            customScoreboard.setHealth(health);
        }
        Objective finalHealth = health;
        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
            if (warlordsEntity != null) {
                finalHealth.getScore(warlordsEntity.getName()).setScore(Math.round(warlordsEntity.getHealth()));
            }
        });
    }

    private void updateNames(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        this.getGame().forEachOfflineWarlordsPlayer((player, team) -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
            if (warlordsEntity == null) {
                return;
            }
            String name = warlordsEntity.getName();
            UUID uuid = warlordsEntity.getUuid();
            String levelString = ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(uuid, warlordsEntity.getSpecClass()));
            if (scoreboard.getTeam(name) == null) {
                org.bukkit.scoreboard.Team temp = scoreboard.registerNewTeam(name);
                temp.setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsEntity.getSpec()
                                                                                          .getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                temp.addEntry(name);
                temp.setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv" + levelString + ChatColor.DARK_GRAY + "]");
            } else {
                scoreboard.getTeam(name).setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsEntity.getSpec()
                                                                                                              .getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                if (warlordsEntity.getCarriedFlag() != null) {
                    scoreboard.getTeam(name).setSuffix(
                            ChatColor.DARK_GRAY + "[" +
                                    ChatColor.GRAY + "Lv" +
                                    levelString +
                                    ChatColor.DARK_GRAY + "]" +
                                    ChatColor.WHITE + "⚑"
                    );
                } else {
                    String s = ChatColor.GRAY + " - " + ChatColor.RED + "⚔ " + warlordsEntity.getMinuteStats().total().getKills();
                    scoreboard.getTeam(name).setSuffix(
                            ChatColor.DARK_GRAY + " [" +
                                    ChatColor.GRAY + "Lv" +
                                    levelString +
                                    ChatColor.DARK_GRAY + "]"
                    );
                }
            }
        });
    }

    private void updateBasedOnGameScoreboards(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        List<String> scoreboard = new ArrayList<>();

        ScoreboardHandler lastHandler = null;
        String lastGroup = null;
        boolean lastWasEmpty = true;
        for (ScoreboardHandler handler : Utils.iterable(game
                .getScoreboardHandlers()
                .stream()
                .sorted(Comparator.comparing((ScoreboardHandler sh) -> sh.getPriority(warlordsPlayer)))
        )) {
            String group = handler.getGroup();
            if ((lastGroup == null || !lastGroup.equals(group)) && !lastWasEmpty && handler.emptyLinesBetween() && lastHandler.emptyLinesBetween()) {
                scoreboard.add("");
                lastWasEmpty = true;
            }
            lastHandler = handler;
            lastGroup = group;
            List<String> handlerContents = handler.computeLines(warlordsPlayer);
            if (!handlerContents.isEmpty()) {
                lastWasEmpty = false;
                scoreboard.addAll(handlerContents);
            }
        }
        customScoreboard.giveNewSideBar(false, scoreboard);
    }

    @Override
    public State run() {
        if (WarlordsPlusCommand.enabled) {
            game.warlordsPlayers().forEach(wp -> {
                ByteArrayDataOutput byteArrayDataOutput = ByteStreams.newDataOutput();
                if (wp != null) {
                    byteArrayDataOutput.writeUTF(wp.getName());
                    byteArrayDataOutput.writeInt((int) wp.getEnergy());
                    byteArrayDataOutput.writeInt((int) wp.getMaxEnergy());
                    AbstractPlayerClass spec = wp.getSpec();
                    byteArrayDataOutput.writeInt(spec.getRed().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getRed()
                                                                                                                    .getCurrentCooldown() + .5));
                    byteArrayDataOutput.writeInt(spec.getPurple().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getPurple()
                                                                                                                       .getCurrentCooldown() + .5));
                    byteArrayDataOutput.writeInt(spec.getBlue().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getBlue()
                                                                                                                     .getCurrentCooldown() + .5));
                    byteArrayDataOutput.writeInt(spec.getOrange().getCurrentCooldown() == 0 ? 0 : (int) Math.round(spec.getOrange()
                                                                                                                       .getCurrentCooldown() + .5));
                    if (com.ebicep.warlords.game.GameMode.isWaveDefense(game.getGameMode())) {
                        game.onlinePlayers().forEach(playerTeamEntry -> {
                            playerTeamEntry.getKey().sendPluginMessage(Warlords.getInstance(), "warlords:warlords", byteArrayDataOutput.toByteArray());
                        });
                    } else {
                        game.spectators().forEach(uuid -> {
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null && WarlordsPlusCommand.UUIDS.contains(player.getUniqueId())) {
                                player.sendPluginMessage(Warlords.getInstance(), "warlords:warlords", byteArrayDataOutput.toByteArray());
                            }
                        });
                    }
                }
            });
        }

        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {
        this.getGame().forEachOfflineWarlordsEntity(e -> e.setActive(false));
        System.out.println(" ----- GAME END ----- ");
        System.out.println("RecordGames = " + RecordGamesCommand.recordGames);
        System.out.println("Force End = " + (winEvent == null));
        System.out.println("Player Count = " + game.warlordsPlayers().count());
        System.out.println("Players = " + game.warlordsPlayers().toList());
        System.out.println("Timer = " + timer);
        System.out.println("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        System.out.println("GameMode = " + game.getGameMode());
        System.out.println("Map = " + game.getMap());
        System.out.println("Game Addons = " + game.getAddons());
        System.out.println(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = PlayerFilterGeneric.playingGameWarlordsPlayers(game).toList();
        if (players.isEmpty()) {
            return;
        }

        if (winEvent != null) {
            boolean isCompGame = game.getAddons().contains(GameAddon.PRIVATE_GAME) &&
                    !com.ebicep.warlords.game.GameMode.isWaveDefense(game.getGameMode()) &&
                    players.size() >= game.getGameMode().minPlayersToAddToDatabase &&
                    timer >= 6000;
            //comps
            if (isCompGame) {
                ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Adding comp game");
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, RecordGamesCommand.recordGames));
                ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Done adding comp game");
            }
            //pubs or pve
            else if (players.size() >= game.getMap().getMinPlayers()) {
                ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Adding pub game");
                if (DatabaseManager.playerService == null) {
                    return;
                }
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, true));
                ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("Done adding pub game");
                if (!com.ebicep.warlords.game.GameMode.isWaveDefense(game.getGameMode())) {
//                    Warlords.newChain()
//                            .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5))
//                            .syncLast(databasePlayers -> {
//                                SRCalculator.databasePlayerCache = new HashSet<>(databasePlayers);
//                                SRCalculator.recalculateSR();
//                            })
//                            .execute();
                }
            }
        } else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME) && players.size() >= 6 && timer >= 6000) {
                DatabaseGameBase.addGame(game, null, false);
            } else {
                ChatUtils.MessageTypes.WARLORDS.sendMessage(
                        "This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
    }

    @Override
    public void onPlayerReJoinGame(@Nonnull Player player) {
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            // Spectator
            player.setGameMode(GameMode.SPECTATOR);
            Location spawn = Stream.concat(
                    getGame().getMarkers(SpawnLocationMarker.class).stream(),
                    getGame().getMarkers(LobbyLocationMarker.class).stream()
            ).map(LocationMarker::getLocation).collect(Utils.randomElement());
            player.teleport(spawn);
        }
        if (wp instanceof WarlordsPlayer) {
            CustomScoreboard sb = CustomScoreboard.getPlayerScoreboard(player);
            updateBasedOnGameState(sb, (WarlordsPlayer) wp);
        }
    }

    @Override
    public int getTicksElapsed() {
        return this.timer;
    }

    @Override
    public void skipTimer() {
        // TODO loop over options and decrement them is needed
        int maxSkip = Integer.MAX_VALUE;
        for (TimerSkipAbleMarker marker : game.getMarkers(TimerSkipAbleMarker.class)) {
            if (marker.getDelay() > 0) {
                maxSkip = Math.min(marker.getDelay(), maxSkip);
            }
        }
        for (TimerSkipAbleMarker marker : game.getMarkers(TimerSkipAbleMarker.class)) {
            marker.skipTimer(maxSkip);
        }
    }

    @Override
    public void resetTimer() throws IllegalStateException {
    }

    /**
     * Updates the names of the player on the scoreboard. To be used when the spec of a warlord player changes
     *
     * @param we the player changing
     */
    public void updatePlayerName(@Nonnull WarlordsEntity we) {
        this.getGame().forEachOfflineWarlordsPlayer((player, team) -> {
            Scoreboard scoreboard = CustomScoreboard.getPlayerScoreboard(player.getUniqueId()).getScoreboard();
            int level = ExperienceManager.getLevelForSpec(we.getUuid(), we.getSpecClass());
            //System.out.println("Updating scorebopard for " + player + " setting " + warlordsPlayer + " to team " + warlordsPlayer.getTeam());
            scoreboard.getTeam(we.getName())
                      .setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + we.getSpec()
                                                                                .getClassNameShort() + ChatColor.DARK_GRAY + "] " + we.getTeam().teamColor());
            scoreboard.getTeam(we.getName())
                      .setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + (level < 10 ? "0" : "") + level + ChatColor.DARK_GRAY + "]");

        });
    }

}

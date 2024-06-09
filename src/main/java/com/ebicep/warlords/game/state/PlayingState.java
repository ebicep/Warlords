package com.ebicep.warlords.game.state;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
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
import com.ebicep.warlords.game.option.marker.*;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.*;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.instances.PlayerNameInstance;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.bukkit.RemoveEntities;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.JavaUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import com.ebicep.warlords.util.warlords.Utils;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

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
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game " + game.getGameId() + " has started");
        Warlords.getGameManager().getGames().stream()
                .filter(gameHolder -> gameHolder.getGame() != null && gameHolder.getGame().equals(game))
                .findAny()
                .ifPresent(gameHolder -> {
                    ChatChannels.sendDebugMessage((CommandIssuer) null,
                            Component.text("Started Game: " + game.getGameMode() + " - " + gameHolder.getName(), NamedTextColor.LIGHT_PURPLE)
                    );
                });
        this.game.setAcceptsSpectators(true);
        this.game.setAcceptsPlayers(false);
        this.resetTimer();
        RemoveEntities.doRemove(this.game);
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding game options");
        for (Option option : game.getOptions()) {
            option.start(game);
        }
        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game options added");

        List<WarlordsEntity> warlordsEntities = new ArrayList<>();
        this.game.forEachOfflinePlayer((player, team) -> {
            Player p = player.getPlayer();
            if (team == null || (!player.isOnline() && com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode()))) {
                return;
            }
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            if (selectedSpec.isBanned()) {
                for (Specializations value : Specializations.VALUES) {
                    if (value.isBanned()) {
                        continue;
                    }
                    if (p != null) {
                        p.sendMessage(Component.text(selectedSpec.name + " is currently disabled. Your specialization has been changed.", NamedTextColor.RED));
                    }
                    playerSettings.setSelectedSpec(value);
                    break;
                }
                if (playerSettings.getSelectedSpec().isBanned()) {
                    if (p != null) {
                        p.sendMessage(Component.text("All specializations are currently disabled. Game closing.", NamedTextColor.RED));
                    }
                }
            }
            WarlordsPlayer warlordsEntity = new WarlordsPlayer(
                    player,
                    this.getGame(),
                    team
            );
            Warlords.addPlayer(warlordsEntity);
            warlordsEntities.add(warlordsEntity);
            if (p != null) {
                p.getInventory().setHeldItemSlot(0);
            }
            Utils.resetPlayerMovementStatistics(player);
        });
        for (Option option : game.getOptions()) {
            option.afterAllWarlordsEntitiesCreated(warlordsEntities);
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
                this.getGame().forEachOnlineWarlordsPlayer(warlordsPlayer -> {
                    if (!warlordsPlayer.isUpdateTabName()) {
                        return;
                    }
                    UUID uuid = warlordsPlayer.getUuid();
                    String levelString = ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(uuid, warlordsPlayer.getSpecClass()));
                    TextComponent.Builder playerTabName = Component.text()
                                                                   .append(Component.text("[", NamedTextColor.DARK_GRAY))
                                                                   .append(Component.text(warlordsPlayer.getSpec().getClassNameShort(), NamedTextColor.GOLD))
                                                                   .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                                                   .append(Component.text(warlordsPlayer.getName(), warlordsPlayer.getTeam().teamColor))
                                                                   .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                                                   .append(Component.text("Lv" + levelString, NamedTextColor.GRAY))
                                                                   .append(Component.text("] ", NamedTextColor.DARK_GRAY));
                    if (warlordsPlayer.getCarriedFlag() != null) {
                        playerTabName.append(Component.text("⚑", NamedTextColor.WHITE));
                    }
                    if (warlordsPlayer.getEntity() instanceof Player player) {
                        player.playerListName(playerTabName.build());
                    }
                });
            }
        }.runTaskTimer(0, 10);

        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Started recording timed stats");

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

        ChatUtils.MessageType.GAME_DEBUG.sendMessage("Game start done");
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
                    List<AbstractAbility> abilities = spec.getAbilities();
                    for (int i = 1; i < abilities.size() && i < 5; i++) {
                        AbstractAbility ability = abilities.get(i);
                        byteArrayDataOutput.writeInt(ability.getCurrentCooldown() == 0 ? 0 : (int) Math.round(ability.getCurrentCooldown() + .5));
                    }
                    if (com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
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
        ChatUtils.MessageType.WARLORDS.sendMessage(" ----- GAME END ----- ");
        ChatUtils.MessageType.WARLORDS.sendMessage("RecordGames = " + RecordGamesCommand.recordGames);
        ChatUtils.MessageType.WARLORDS.sendMessage("Force End = " + (winEvent == null));
        ChatUtils.MessageType.WARLORDS.sendMessage("Player Count = " + game.warlordsPlayers().count());
        ChatUtils.MessageType.WARLORDS.sendMessage("Players = " + game.warlordsPlayers().toList());
        ChatUtils.MessageType.WARLORDS.sendMessage("Timer = " + timer);
        ChatUtils.MessageType.WARLORDS.sendMessage("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        ChatUtils.MessageType.WARLORDS.sendMessage("GameMode = " + game.getGameMode());
        ChatUtils.MessageType.WARLORDS.sendMessage("Map = " + game.getMap());
        ChatUtils.MessageType.WARLORDS.sendMessage("Game Addons = " + game.getAddons());
        ChatUtils.MessageType.WARLORDS.sendMessage("Win Event = " + (winEvent == null ? null : winEvent.getCause()));
        ChatUtils.MessageType.WARLORDS.sendMessage(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = PlayerFilterGeneric.playingGameWarlordsPlayers(game).toList();
        if (players.isEmpty()) {
            ChatUtils.MessageType.GAME_DEBUG.sendMessage("No players in game, not adding game");
            return;
        }

        if (winEvent != null) {
            boolean isCompGame = game.getAddons().contains(GameAddon.PRIVATE_GAME) &&
                    !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode()) &&
                    players.size() >= game.getGameMode().minPlayersToAddToDatabase &&
                    timer >= 6000;
            //comps
            if (isCompGame) {
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding comp game");
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, RecordGamesCommand.recordGames));
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Done adding comp game");
            }
            //pubs or pve
            else if (players.size() >= game.getMap().getMinPlayers()) {
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Adding pub/pve game");
                if (DatabaseManager.playerService == null) {
                    return;
                }
                gameAdded.set(DatabaseGameBase.addGame(game, winEvent, true));
                ChatUtils.MessageType.GAME_DEBUG.sendMessage("Done adding pub/pve game");
                if (!com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                    SRCalculator.recalculateSR();
                }
            }
        } else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME) && players.size() >= 6 && timer >= 6000) {
                DatabaseGameBase.addGame(game, null, false);
                ChatUtils.MessageType.WARLORDS.sendMessage("SOME CASE");
            } else {
                ChatUtils.MessageType.WARLORDS.sendMessage("This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
    }

    @Override
    public void onPlayerReJoinGame(@Nonnull Player player) {
        WarlordsEntity wp = Warlords.getPlayer(player);
        if (wp == null) {
            Location spawn = Stream.concat(
                    getGame().getMarkers(SpawnLocationMarker.class).stream(),
                    getGame().getMarkers(LobbyLocationMarker.class).stream()
            ).map(LocationMarker::getLocation).collect(Utils.randomElement());
            player.teleport(spawn);
            // Spectator - delay because multiverse is dumb
            new BukkitRunnable() {

                @Override
                public void run() {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }.runTaskLater(Warlords.getInstance(), 1);
        }
        if (wp instanceof WarlordsPlayer) {
            player.setFlying(false);
            player.setAllowFlight(false);
            CustomScoreboard sb = CustomScoreboard.getPlayerScoreboard(player);
            updateBasedOnGameState(sb, (WarlordsPlayer) wp);
        }
    }

    @Override
    public int getTicksElapsed() {
        return this.timer;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    private void updateBasedOnGameState(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        this.updateHealth(customScoreboard);
        this.updateNames(customScoreboard, warlordsPlayer);
        this.updateBasedOnGameScoreboards(customScoreboard, warlordsPlayer);
    }

    private void updateHealth(@Nonnull CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        Objective health = customScoreboard.getHealth();
        if (health == null || scoreboard.getObjective("health") == null) {
            health = scoreboard.registerNewObjective("health", Criteria.DUMMY, Component.text("❤", NamedTextColor.RED));
            health.setDisplaySlot(DisplaySlot.BELOW_NAME);
            customScoreboard.setHealth(health);
        }
        Objective finalHealth = health;
        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsEntity warlordsEntity = Warlords.getPlayer(player);
            if (warlordsEntity != null) {
                finalHealth.getScore(warlordsEntity.getName()).setScore(Math.round(warlordsEntity.getCurrentHealth()));
            }
        });
    }

    public void updateNames(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsEntity warlordsPlayer) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        List<AbstractCooldown<?>> cooldowns;
        if (warlordsPlayer != null) {
            cooldowns = warlordsPlayer.getCooldownManager().getCooldowns();
        } else {
            cooldowns = new ArrayList<>();
        }
        this.getGame().forEachOfflineWarlordsEntity(otherPlayer -> {
            if (otherPlayer instanceof WarlordsPlayerDisguised || otherPlayer instanceof WarlordsNPC) {
                return;
            }
            Entity entity = otherPlayer.getEntity();
            UUID uuid = otherPlayer.getUuid();
            List<AbstractCooldown<?>> otherPlayerCooldowns = otherPlayer.getCooldownManager().getCooldowns();
            String levelString = ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(uuid, otherPlayer.getSpecClass()));
            Team playerTeam = scoreboard.getEntityTeam(entity);
            if (playerTeam == null) {
                playerTeam = scoreboard.registerNewTeam(((CraftEntity) entity).getHandle().getScoreboardName());
                playerTeam.addEntity(entity);
            }
            playerTeam.color(otherPlayer.getTeam().teamColor());

            //tab name
            //prefix
            TextComponent.Builder prefix = Component.text();
            if (warlordsPlayer != null) {
                cooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData prefixFromSelf = cd.addPrefixFromSelf();
                    if (prefixFromSelf != null && prefixFromSelf.displayPredicate().test(otherPlayer)) {
                        prefix.append(prefixFromSelf.text().append(Component.space()));
                    }
                });
                otherPlayerCooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData prefixFromEnemy = cd.addPrefixFromOther();
                    if (prefixFromEnemy != null && prefixFromEnemy.displayPredicate().test(warlordsPlayer)) {
                        prefix.append(prefixFromEnemy.text().append(Component.space()));
                    }
                });
            }
            if (otherPlayer instanceof WarlordsPlayer) {
                TextComponent.Builder basePrefix = Component.text()
                                                            .append(Component.text("[", NamedTextColor.DARK_GRAY))
                                                            .append(Component.text(otherPlayer.getSpec().getClassNameShort(), NamedTextColor.GOLD))
                                                            .append(Component.text("] ", NamedTextColor.DARK_GRAY));
                prefix.append(basePrefix);
            }
            playerTeam.prefix(prefix.build());


            //suffix
            TextComponent.Builder suffix = Component.text();
            if (otherPlayer instanceof WarlordsPlayer) {
                TextComponent.Builder baseSuffix = Component.text()
                                                            .append(Component.text("[", NamedTextColor.DARK_GRAY))
                                                            .append(Component.text("Lv" + levelString, NamedTextColor.GRAY))
                                                            .append(Component.text("]", NamedTextColor.DARK_GRAY));
                if (otherPlayer.getCarriedFlag() != null) {
                    baseSuffix.append(Component.text(" ⚑", NamedTextColor.WHITE));
                }
                suffix.append(baseSuffix);
            }
            if (warlordsPlayer != null) {
                cooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData suffixFromSelf = cd.addSuffixFromSelf();
                    if (suffixFromSelf != null && suffixFromSelf.displayPredicate().test(otherPlayer)) {
                        suffix.append(Component.space().append(suffixFromSelf.text()));
                    }
                });
                otherPlayerCooldowns.forEach(cd -> {
                    PlayerNameInstance.PlayerNameData suffixFromEnemy = cd.addSuffixFromOther();
                    if (suffixFromEnemy != null && suffixFromEnemy.displayPredicate().test(warlordsPlayer)) {
                        suffix.append(Component.space().append(suffixFromEnemy.text()));
                    }
                });
            }
            playerTeam.suffix(suffix.build());
        });
    }

    private void updateBasedOnGameScoreboards(@Nonnull CustomScoreboard customScoreboard, @Nullable WarlordsPlayer warlordsPlayer) {
        List<Component> scoreboard = new ArrayList<>();

        ScoreboardHandler lastHandler = null;
        String lastGroup = null;
        boolean lastWasEmpty = true;
        for (ScoreboardHandler handler : JavaUtils.iterable(game
                .getScoreboardHandlers()
                .stream()
                .sorted(Comparator.comparing((ScoreboardHandler sh) -> sh.getPriority(warlordsPlayer)))
        )) {
            String group = handler.getGroup();
            if ((lastGroup == null || !lastGroup.equals(group)) && !lastWasEmpty && handler.emptyLinesBetween() && lastHandler.emptyLinesBetween()) {
                scoreboard.add(Component.empty());
                lastWasEmpty = true;
            }
            lastHandler = handler;
            lastGroup = group;
            List<Component> handlerContents = handler.computeLines(warlordsPlayer);
            if (!handlerContents.isEmpty()) {
                lastWasEmpty = false;
                scoreboard.addAll(handlerContents);
            }
        }
        customScoreboard.giveNewSideBar(false, scoreboard);
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
        for (TimerResetAbleMarker marker : game.getMarkers(TimerResetAbleMarker.class)) {
            marker.reset();
        }
    }

}

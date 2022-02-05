package com.ebicep.warlords.maps.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.RecordGamesCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.Option;
import com.ebicep.warlords.maps.option.marker.TimerSkipAbleMarker;
import com.ebicep.warlords.maps.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.player.CustomScoreboard;
import com.ebicep.warlords.player.ExperienceManager;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.PlayerFilter;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayingState implements State, TimerDebugAble {

    private final Game game;
    private WarlordsGameTriggerWinEvent winEvent;
    private int counter = 0;
    private int timer = 0;

    public PlayingState(@Nonnull Game game) {
        this.game = game;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        this.game.setAcceptsSpectators(true);
        this.game.setAcceptsPlayers(false);
        this.resetTimer();
        RemoveEntities.doRemove(this.game);
        for (Option option : game.getOptions()) {
            option.start(game);
        }

        this.game.forEachOfflinePlayer((player, team) -> {
            if (team != null) {
                PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                Warlords.addPlayer(new WarlordsPlayer(
                        player,
                        this,
                        team,
                        playerSettings
                ));
            }
        });
        this.game.forEachOfflineWarlordsPlayer(wp -> {
            CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(wp.getUuid());
            updateBasedOnGameState(true, customScoreboard, wp);
            if (wp.getEntity() instanceof Player) {
                wp.applySkillBoost((Player) wp.getEntity());
            }
        });

        Warlords.newChain().async(() -> game.forEachOfflinePlayer((player, team) -> {
            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
            DatabaseManager.updatePlayerAsync(databasePlayer);
            DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.SEASON_5, () -> {
            });
            DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.WEEKLY, () -> {
            });
            DatabaseManager.loadPlayer(player.getUniqueId(), PlayersCollections.DAILY, () -> {
            });
        })).execute();
        game.setAcceptsPlayers(true);
        game.setAcceptsSpectators(false);
        game.registerEvents(new Listener() {
            @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
            public void onWin(WarlordsGameTriggerWinEvent event) {
                game.setNextState(new EndState(game, event));
                winEvent = event;
            }
        });
        new GameRunnable(game) {
            @Override
            public void run() {
                giveScoreboard();
            }

        }.runTaskTimer(0, 10);
        new GameRunnable(game) {
            @Override
            public void run() {
                counter++;
                timer += GameRunnable.SECOND;
                if (counter >= 60) {
                    counter -= 60;
                    PlayerFilter.playingGame(game).forEach(wp -> wp.getStats().advanceMinute());
                }
            }
        }.runTaskTimer(0, GameRunnable.SECOND);
        game.registerGameMarker(TimerSkipAbleMarker.class, (delay) -> {
            counter += delay / GameRunnable.SECOND;
            timer += delay;
        });
    }

    @Override
    public State run() {
        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {

        System.out.println(" ----- GAME END ----- ");
        System.out.println("RecordGames = " + RecordGamesCommand.recordGames);
        System.out.println("Imposter = " + game.getAddons().contains(GameAddon.IMPOSTER_MODE));
        System.out.println("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        System.out.println("Force End = " + (winEvent == null));
        System.out.println("Player Count = " + game.playersCount());
        System.out.println("Players = " + game.getPlayers().keySet());
        System.out.println("Timer = " + timer);
        System.out.println(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = PlayerFilter.playingGame(game).toList();
        if (players.isEmpty()) {
            return;
        }
        float highestDamage = players.stream().sorted(Comparator.comparing((WarlordsPlayer wp) -> wp.getStats().total().getDamage()).reversed()).findFirst().get().getStats().total().getDamage();
        float highestHealing = players.stream().sorted(Comparator.comparing((WarlordsPlayer wp) -> wp.getStats().total().getHealing()).reversed()).findFirst().get().getStats().total().getHealing();
        //PUBS
        if (!game.getAddons().contains(GameAddon.PRIVATE_GAME) && !game.getAddons().contains(GameAddon.IMPOSTER_MODE) && winEvent != null && game.playersCount() >= 12) {
            String gameEnd = "[GAME] A Public game ended with ";
            // TODO parse winEvent better here
            if (winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + game.getPoints(Team.BLUE) + " to " + game.getPoints(Team.RED), false, true);
            } else if (winEvent != null && winEvent.getDeclaredWinner() == Team.RED) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + game.getPoints(Team.RED) + " to " + game.getPoints(Team.BLUE), false, true);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", false, true);
            }
            if (highestDamage <= 750000 && highestHealing <= 750000) {
                DatabaseGame.addGame(game, winEvent, true);
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB game was added to the database and player information was changed");
            } else {
                DatabaseGame.addGame(game, winEvent, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB game was added to the database (INVALID DAMAGE/HEALING) but player information remained the same");
            }

            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5))
                    .syncLast(databasePlayers -> {
                        SRCalculator.databasePlayerCache = databasePlayers;
                        SRCalculator.recalculateSR();
                    })
                    .execute();
        } //COMPS
        else if (RecordGamesCommand.recordGames && !game.getAddons().contains(GameAddon.IMPOSTER_MODE) && winEvent != null && game.playersCount() >= 16 && timer <= 12000) {
            String gameEnd = "[GAME] A game ended with ";
            if (winEvent != null && winEvent.getDeclaredWinner() == Team.BLUE) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + game.getPoints(Team.BLUE) + " to " + game.getPoints(Team.RED), true, false);
            } else if (winEvent != null && winEvent.getDeclaredWinner() == Team.RED) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + game.getPoints(Team.RED) + " to " + game.getPoints(Team.BLUE), true, false);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", true, false);
            }
            if (highestDamage <= 750000 && highestHealing <= 750000) {
                DatabaseGame.addGame(game, winEvent, true);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database and player information was changed");
            } else {
                DatabaseGame.addGame(game, winEvent, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database (INVALID DAMAGE/HEALING) but player information remained the same");
            }
        } //END GAME
        else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME) && game.playersCount() >= 6 && timer <= 12000) {
                DatabaseGame.addGame(game, winEvent, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database but player information remained the same");
            } else {
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
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

    private void giveScoreboard() {
        game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
            this.onPlayerReJoinGame(player);
        });
    }

    public void updateHealth(CustomScoreboard customScoreboard) {
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
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null) {
                finalHealth.getScore(warlordsPlayer.getName()).setScore(warlordsPlayer.getHealth());
            }
        });
    }

    public void updateNames(CustomScoreboard customScoreboard) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();
        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
            if (warlordsPlayer != null) {
                if (scoreboard.getTeam(warlordsPlayer.getName()) == null) {
                    org.bukkit.scoreboard.Team temp = scoreboard.registerNewTeam(warlordsPlayer.getName());
                    temp.setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                    temp.addEntry(warlordsPlayer.getName());
                    temp.setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]");
                } else {
                    scoreboard.getTeam(warlordsPlayer.getName()).setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + team.teamColor());
                    if (warlordsPlayer.getCarriedFlag() != null) {
                        scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]" + ChatColor.WHITE + "⚑");
                    } else {
                        scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(warlordsPlayer.getUuid(), warlordsPlayer.getSpecClass())) + ChatColor.DARK_GRAY + "]");
                    }
                }
            }
        });
    }

    public void updatePlayerName(CustomScoreboard customScoreboard, WarlordsPlayer warlordsPlayer) {
        Scoreboard scoreboard = customScoreboard.getScoreboard();

        this.getGame().forEachOfflinePlayer((player, team) -> {
            WarlordsPlayer wp = Warlords.getPlayer(player);
            if (wp != null) {
                int level = ExperienceManager.getLevelForSpec(wp.getUuid(), wp.getSpecClass());
                scoreboard.getTeam(warlordsPlayer.getName()).setPrefix(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + warlordsPlayer.getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] " + warlordsPlayer.getTeam().teamColor());
                scoreboard.getTeam(warlordsPlayer.getName()).setSuffix(ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + (level < 10 ? "0" : "") + level + ChatColor.DARK_GRAY + "]");
            }
        });
    }

    public void updateBasedOnGameState(boolean init, CustomScoreboard customScoreboard, WarlordsPlayer warlordsPlayer) {
        this.updateHealth(customScoreboard);
        this.updateNames(customScoreboard);

        List<String> scoreboard = new ArrayList<>();

        String lastGroup = null;
        boolean lastWasEmpty = true;
        for (ScoreboardHandler handler : Utils.iterable(game
                .getScoreboardHandlers()
                .stream()
                .sorted(Comparator.comparing((ScoreboardHandler sh) -> sh.getPriority(warlordsPlayer)))
        )) {
            String group = handler.getGroup();
            if ((lastGroup == null || !lastGroup.equals(group)) && !lastWasEmpty) {
                scoreboard.add("");
                lastWasEmpty = true;
            }
            lastGroup = group;
            List<String> handlerContents = handler.computeLines(warlordsPlayer);
            if (!handlerContents.isEmpty()) {
                lastWasEmpty = false;
                scoreboard.addAll(handlerContents);
            }
        }

        customScoreboard.giveNewSideBar(init, scoreboard);
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        WarlordsPlayer wp = Warlords.getPlayer(player);
        updateBasedOnGameState(false, Warlords.playerScoreboards.get(player.getUniqueId()), wp);
    }

    public int getTicksElapsed() {
        return this.timer;
    }

}

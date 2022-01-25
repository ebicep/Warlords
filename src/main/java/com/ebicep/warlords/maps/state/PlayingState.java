package com.ebicep.warlords.maps.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.ImposterCommand;
import com.ebicep.warlords.commands.debugcommands.RecordGamesCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGame;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.maps.Game.Stats;
import com.ebicep.warlords.maps.GameAddon;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.option.Option;
import com.ebicep.warlords.player.CustomScoreboard;
import com.ebicep.warlords.player.ExperienceManager;
import com.ebicep.warlords.player.PlayerSettings;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.RemoveEntities;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PlayingState implements State, TimerDebugAble {
    private static final int OVERTIME_TIME = 60 * 20;

    private static final int ENDING_SCORE_LIMIT = 1000;

    private boolean overTimeActive = false;
    private int pointLimit = 0;
    private final Game game;

    public PlayingState(@Nonnull Game game) {
        this.game = game;
    }

    @Deprecated
    public void addKill(@Nonnull Team victim, boolean isSuicide) {
        game.addKill(victim, victim.enemy());
    }

    @Nonnull
    @Deprecated
    public Stats getStats(@Nonnull Team team) {
        return game.getStats(team);
    }

    @Deprecated
    public void addPoints(@Nonnull Team team, int i) {
        game.addPoints(team, i);
    }

    @Deprecated
    public int getBluePoints() {
        return game.getStats(Team.BLUE).points();
    }

    @Deprecated
    public void addBluePoints(int i) {
        this.addPoints(Team.BLUE, i);
    }

    @Deprecated
    public int getRedPoints() {
        return game.getStats(Team.RED).points();
    }

    @Deprecated
    public void addRedPoints(int i) {
        this.addPoints(Team.RED, i);
    }

    public boolean isOvertime() {
        return this.overTimeActive;
    }

    @Nonnull
    public Game getGame() {
        return game;
    }

    @Override
    @SuppressWarnings("null")
    public void begin() {
        for (Option option : game.getOptions()) {
            option.start(game);
        }
        this.resetTimer();
        RemoveEntities.doRemove(this.game);

        this.game.forEachOfflinePlayer((player, team) -> {
            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Warlords.addPlayer(new WarlordsPlayer(
                    player,
                    this,
                    team,
                    playerSettings
            ));
        });
        this.game.forEachOfflineWarlordsPlayer(wp -> {
            CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(wp.getUuid());
            updateBasedOnGameState(true, customScoreboard, wp);
            if (wp.getEntity() instanceof Player) {
                wp.applySkillBoost((Player) wp.getEntity());
                PacketUtils.sendTitle((Player) wp.getEntity(), ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
            }
        });

        Warlords.newChain()
                .async(() -> game.forEachOfflinePlayer((player, team) -> {
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
            }
        });
    }

    @Override
    public State run() {
        this.timer--;
        if (forceEnd) {
            return getEndState(null);
        }
        if (this.timer <= 0) {
            if (this.overTimeActive) {
                State next = nextStateByPoints();
                return next == null ? getEndState(null) : next;
            } else {
                State next = nextStateByPoints();
                if (next == null) {
                    this.timer = OVERTIME_TIME;
                    this.overTimeActive = true;
                    assert getStats(Team.BLUE).points == getStats(Team.RED).points;
                } else {
                    return next;
                }
            }
        }
        if (timer % 10 == 0) {
            giveScoreboard();
        }

        //update every 5 seconds
        if (timer % 100 == 0) {
            BotManager.sendStatusMessage(false);
        }

        int redPoints = getStats(Team.RED).points;
        int bluePoints = getStats(Team.BLUE).points;
        if (redPoints >= this.pointLimit || bluePoints >= this.pointLimit || (Math.abs(redPoints - bluePoints) >= MERCY_LIMIT && this.timer < game.getMap().getGameTimerInTicks() - 20 * 60 * 5)) {
            giveScoreboard();
            return nextStateByPoints();
        }
        return null;
    }

    @Override
    @SuppressWarnings("null")
    public void end() {

        Warlords.getPlayers().forEach(((uuid, warlordsPlayer) -> warlordsPlayer.removeGrave()));

        System.out.println(" ----- GAME END ----- ");
        System.out.println("RecordGames = " + RecordGamesCommand.recordGames);
        System.out.println("Imposter = " + ImposterCommand.enabled);
        System.out.println("Private = " + game.getAddons().contains(GameAddon.PRIVATE_GAME));
        System.out.println("Force End = " + forceEnd);
        System.out.println("Player Count = " + game.playersCount());
        System.out.println("Timer = " + timer);
        System.out.println(" ----- GAME END ----- ");

        List<WarlordsPlayer> players = new ArrayList<>(Warlords.getPlayers().values());
        float highestDamage = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalDamage).reversed()).collect(Collectors.toList()).get(0).getTotalDamage();
        float highestHealing = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalHealing).reversed()).collect(Collectors.toList()).get(0).getTotalHealing();
        //PUBS
        if (!game.getAddons().contains(GameAddon.PRIVATE_GAME) && !ImposterCommand.enabled && !forceEnd && game.playersCount() >= 12) {
            String gameEnd = "[GAME] A Public game ended with ";
            if (getBluePoints() > getRedPoints()) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + getBluePoints() + " to " + getRedPoints(), true);
            } else if (getBluePoints() < getRedPoints()) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + getRedPoints() + " to " + getBluePoints(), true);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", true);
            }
            if (highestDamage <= 750000 && highestHealing <= 750000) {
                DatabaseGame.addGame(PlayingState.this, true);
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB game was added to the database and player information was changed");
            } else {
                DatabaseGame.addGame(PlayingState.this, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB game was added to the database (INVALID DAMAGE/HEALING) but player information remained the same");
            }

            Warlords.newChain()
                    .asyncFirst(() -> DatabaseManager.playerService.findAll(PlayersCollections.SEASON_5))
                    .syncLast(databasePlayers -> {
                        SRCalculator.databasePlayerCache = databasePlayers;
                        SRCalculator.recalculateSR();
                    })
                    .execute();
        }
        //COMPS
        else if (RecordGamesCommand.recordGames && !ImposterCommand.enabled && !forceEnd && game.playersCount() >= 16 && timer <= 12000) {
            String gameEnd = "[GAME] A game ended with ";
            if (getBluePoints() > getRedPoints()) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**BLUE** winning " + getBluePoints() + " to " + getRedPoints(), true);
            } else if (getBluePoints() < getRedPoints()) {
                BotManager.sendMessageToNotificationChannel(gameEnd + "**RED** winning " + getRedPoints() + " to " + getBluePoints(), true);
            } else {
                BotManager.sendMessageToNotificationChannel(gameEnd + "a **DRAW**", true);
            }
            if (highestDamage <= 750000 && highestHealing <= 750000) {
                DatabaseGame.addGame(PlayingState.this, true);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database and player information was changed");
            } else {
                DatabaseGame.addGame(PlayingState.this, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database (INVALID DAMAGE/HEALING) but player information remained the same");
            }
        }
        //END GAME
        else {
            if (game.getAddons().contains(GameAddon.PRIVATE_GAME)&& game.playersCount() >= 6 && timer <= 12000) {
                DatabaseGame.addGame(PlayingState.this, false);
                System.out.println(ChatColor.GREEN + "[Warlords] This COMP game was added to the database but player information remained the same");
            } else {
                System.out.println(ChatColor.GREEN + "[Warlords] This PUB/COMP game was not added to the database and player information remained the same");
            }
        }
    }

    @Override
    public void skipTimer() {
        // TODO loop over options and decrement them is needed
        this.timer = 0;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = game.getMap().getGameTimerInTicks();
        this.pointLimit = ENDING_SCORE_LIMIT;
        this.overTimeActive = false;
    }

    private EndState getEndState(@Nullable Team winner) {
        return new EndState(this.game, winner, this.getStats(Team.RED), this.getStats(Team.BLUE));
    }

    @Nullable
    public Team calculateWinnerByPoints() {
        int redPoints = getStats(Team.RED).points();
        int bluePoints = getStats(Team.BLUE).points();
        if (redPoints > bluePoints) {
            return Team.RED;
        }
        if (bluePoints > redPoints) {
            return Team.BLUE;
        }
        return null;
    }

    @Nullable
    private State nextStateByPoints() {
        Team winner = calculateWinnerByPoints();
        if (winner != null) {
            return getEndState(winner);
        }
        return null;
    }

    public void resetStats() {
        for (Team team : Team.values()) {
            stats.put(team, new Stats(team));
        }
    }

    public void addCapture(WarlordsPlayer capper) {
        getStats(capper.getTeam()).captures++;
        addPoints(capper.getTeam(), SCORE_CAPTURE_POINTS);
    }

    public void endGame() {
        this.forceEnd = true;
    }

    public boolean isForceEnd() {
        return forceEnd;
    }

    public int getPointLimit() {
        return pointLimit;
    }

    private void giveScoreboard() {
        // TODO
        for (WarlordsPlayer value : Warlords.getPlayers().values()) {
            if (Warlords.playerScoreboards.get(value.getUuid()) != null) {
                updateBasedOnGameState(false, Warlords.playerScoreboards.get(value.getUuid()), value);
            }
        }
        for (UUID spectator : game.spectators()) {
            updateBasedOnGameState(false, Warlords.playerScoreboards.get(spectator), null);
        }
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
                    if (warlordsPlayer.getGameState().flags().hasFlag(warlordsPlayer)) {
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

        String[] entries = new String[15];


        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy - kk:mm");
        format.setTimeZone(TimeZone.getTimeZone("EST"));

        //date
        entries[14] = ChatColor.GRAY + format.format(new Date());

        // Points
        entries[12] = ChatColor.BLUE + "BLU: " + ChatColor.AQUA + this.getBluePoints() + ChatColor.GOLD + "/" + this.getPointLimit();
        entries[11] = ChatColor.RED + "RED: " + ChatColor.AQUA + this.getRedPoints() + ChatColor.GOLD + "/" + this.getPointLimit();

        // Timer
        com.ebicep.warlords.maps.Team team = this.calculateWinnerByPoints();
        if (team != null) {
            entries[9] = team.coloredPrefix() + ChatColor.GOLD + " Wins in: " + ChatColor.GREEN + getTimeLeftString();
        } else {
            entries[9] = ChatColor.WHITE + "Time Left: " + ChatColor.GREEN + getTimeLeftString();
        }

        if (warlordsPlayer != null) {
            entries[4] = ChatColor.WHITE + "Spec: " + ChatColor.GREEN + warlordsPlayer.getSpec().getClass().getSimpleName();
            if (game.getAddons().contains(GameAddon.IMPOSTER_MODE)) {
                if ((ImposterCommand.blueImposterName != null && ImposterCommand.blueImposterName.equalsIgnoreCase(warlordsPlayer.getName())) ||
                        (ImposterCommand.redImposterName != null && ImposterCommand.redImposterName.equals(warlordsPlayer.getName()))
                ) {
                    entries[3] = ChatColor.WHITE + "Role: " + ChatColor.RED + "IMPOSTER";
                } else {
                    if (ImposterCommand.blueImposterName != null && ImposterCommand.redImposterName != null) {
                        entries[3] = ChatColor.WHITE + "Role: " + ChatColor.GREEN + "INNOCENT";
                    }
                }
            }
            entries[2] = ChatColor.GREEN.toString() + warlordsPlayer.getTotalKills() + ChatColor.RESET + " Kills " +
                    ChatColor.GREEN + warlordsPlayer.getTotalAssists() + ChatColor.RESET + " Assists";
        }

        entries[0] = ChatColor.YELLOW + Warlords.VERSION;

        Collections.reverse(Arrays.asList(entries));

        customScoreboard.giveNewSideBar(init, entries);
    }

    private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet())
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            return m1;
        };
    }

    // We have to copy this to allow null keys
    public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(
            Function<? super T, ? extends K> classifier,
            Supplier<M> mapFactory,
            Collector<? super T, A, D> downstream
    ) {
        Supplier<A> downstreamSupplier = downstream.supplier();
        BiConsumer<A, ? super T> downstreamAccumulator = downstream.accumulator();
        BiConsumer<Map<K, A>, T> accumulator = (m, t) -> {
            K key = classifier.apply(t);
            A container = m.computeIfAbsent(key, k -> downstreamSupplier.get());
            downstreamAccumulator.accept(container, t);
        };
        BinaryOperator<Map<K, A>> merger = PlayingState.<K, A, Map<K, A>>mapMerger(downstream.combiner());
        @SuppressWarnings("unchecked")
        Supplier<Map<K, A>> mangledFactory = (Supplier<Map<K, A>>) mapFactory;
        @SuppressWarnings("unchecked")
        Function<A, A> downstreamFinisher = (Function<A, A>) downstream.finisher();
        Function<Map<K, A>, M> finisher = intermediate -> {
            intermediate.replaceAll((k, v) -> downstreamFinisher.apply(v));
            @SuppressWarnings("unchecked")
            M castResult = (M) intermediate;
            return castResult;
        };
        if (downstream.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)) {
            return Collector.of(mangledFactory, accumulator, merger, finisher, Collector.Characteristics.IDENTITY_FINISH);
        } else {
            return Collector.of(mangledFactory, accumulator, merger, finisher);
        }
    }
}

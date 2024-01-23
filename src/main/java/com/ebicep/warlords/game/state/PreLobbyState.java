package com.ebicep.warlords.game.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.player.general.CustomScoreboard;
import com.ebicep.warlords.player.general.ExperienceManager;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.sr.Balancer;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {

    private final Game game;
    private final Map<UUID, TeamPreference> teamPreferences = new HashMap<>();
    private final PreGameItemOption[] items = new PreGameItemOption[9];
    private int timer = 0;
    private int maxTimer = 0;
    private boolean timerHasBeenSkipped = false;

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        if (game.getGameMode() == com.ebicep.warlords.game.GameMode.TUTORIAL) {
            return;
        }
        this.maxTimer = game.getMap().getLobbyCountdown();
        this.resetTimer();
        game.setAcceptsPlayers(true);
        game.setAcceptsSpectators(false);
        for (Option option : game.getOptions()) {
            if (option instanceof PreGameItemOption preGameItemOption) {
                items[preGameItemOption.getSlot()] = preGameItemOption;
            }
        }
    }

    @Override
    public State run() {
        if (game.getGameMode() == com.ebicep.warlords.game.GameMode.TUTORIAL) {
            return new SyncTimerState(game);
        }
        if (hasEnoughPlayers() || timerHasBeenSkipped) {
            timerHasBeenSkipped = false;
            if (timer % 20 == 0) {
                int time = timer / 20;
                game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                    giveLobbyScoreboard(false, player);
                    player.setAllowFlight(false);
                });
                if (time == 30) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text("30 ", NamedTextColor.GREEN))
                                                            .append(Component.text("seconds!"))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in 20 seconds!", NamedTextColor.YELLOW));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text("10 ", NamedTextColor.GOLD))
                                                            .append(Component.text("seconds!"))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, Component.text("The game starts in ", NamedTextColor.YELLOW)
                                                            .append(Component.text(time, NamedTextColor.RED))
                                                            .append(Component.text(" second" + s))
                        );
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                        player.setAllowFlight(false);
                    });
                }
            }

            if (timer % 200 == 0) {
                String bannedSpecs = Arrays.stream(Specializations.VALUES)
                                           .filter(Specializations::isBanned)
                                           .map(specializations -> specializations.name)
                                           .collect(Collectors.joining(", "));
                if (!bannedSpecs.isEmpty()) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player,
                                false,
                                Component.text("WARNING: Currently disabled specs: " + bannedSpecs + ". Your spec will be automatically switched if any of those are selected.",
                                        NamedTextColor.RED
                                )
                        );
                    });
                }
            }

            if (timer <= 0) {

                if (!game.getAddons().contains(GameAddon.PRIVATE_GAME) && !com.ebicep.warlords.game.GameMode.isPvE(game.getGameMode())) {
                    Balancer.balance(game);
                }

                if (game.getPlayers().size() >= 14) {
                    boolean isPrivate = game.getAddons().contains(GameAddon.PRIVATE_GAME);
                    BotManager.sendMessageToStatusChannel(
                            "[GAME] A " + (isPrivate ? "" : "Public ") + "**" + game.getMap().getMapName() + "** started with **" + game.getPlayers()
                                                                                                                                        .size() + (game.getPlayers()
                                                                                                                                                       .size() == 1 ? "** player!" : "** players!")
                    );
                }

                return new SyncTimerState(game);
            }
            timer--;
        } else {
            resetTimer();
            game.forEachOnlinePlayerWithoutSpectators((player, team) -> giveLobbyScoreboard(false, player));
        }
        return null;
    }

    public boolean hasEnoughPlayers() {
        int players = game.playersCount();
        return players >= game.getMinPlayers();
    }

    public void giveLobbyScoreboard(boolean init, Player player) {
        CustomScoreboard customScoreboard = CustomScoreboard.getPlayerScoreboard(player);

        Component date = Component.text(DateUtil.formatCurrentDateEST("MM/dd/yyyy"), NamedTextColor.GRAY);
        Component map = Component.text("Map: ", NamedTextColor.WHITE).append(Component.text(game.getMap().getMapName(), NamedTextColor.GREEN));
        Component players = Component.text("Players: ", NamedTextColor.WHITE)
                                     .append(Component.text(game.playersCount() + "/" + game.getMaxPlayers(), NamedTextColor.GREEN));
        Specializations specializations = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        Component level = Component.text("Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), specializations)) + " ",
                                           NamedTextColor.GRAY
                                   )
                                   .append(Component.text(Specializations.getClass(specializations).name, NamedTextColor.GOLD));
        Component spec = Component.text("Spec: ", NamedTextColor.WHITE).append(Component.text(specializations.name, NamedTextColor.GREEN));
        Component version = Component.text(Warlords.VERSION, NamedTextColor.YELLOW);
        if (hasEnoughPlayers()) {
            customScoreboard.giveNewSideBar(init,
                    date,
                    Component.empty(),
                    map,
                    players,
                    Component.empty(),
                    Component.text("Starting in: ", NamedTextColor.WHITE)
                             .append(Component.text(getTimeLeftString(), NamedTextColor.GREEN)),
                    Component.empty(),
                    level,
                    spec,
                    Component.empty(),
                    version
            );
        } else {
            customScoreboard.giveNewSideBar(init,
                    date,
                    Component.empty(),
                    map,
                    players,
                    Component.empty(),
                    Component.text("Starting if ", NamedTextColor.WHITE)
                             .append(Component.text((game.getMap().getMinPlayers() - game.playersCount()), NamedTextColor.GREEN))
                             .append(Component.text(" more")),
                    Component.text("players join ", NamedTextColor.WHITE),
                    Component.empty(),
                    level,
                    spec,
                    Component.empty(),
                    version
            );
        }
    }

    public String getTimeLeftString() {
        int seconds = timer / 20;
        int minutes = seconds / 60;
        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds % 60 < 10 ? "0" : "") + seconds % 60;
    }

    @Override
    public void end() {
//        updateTeamPreferences();
//        distributePeopleOverTeams();
    }

    @Override
    public void onPlayerJoinGame(OfflinePlayer op, boolean asSpectator) {
        if (!asSpectator) {
            Team team = PlayerSettings.getPlayerSettings(op.getUniqueId()).getWantedTeam();
            Team finalTeam = team == null ? Team.BLUE : team;
            game.setPlayerTeam(op, finalTeam);
            List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
            LobbyLocationMarker location = lobbies.stream().filter(e -> e.matchesTeam(finalTeam)).collect(Utils.randomElement());
            if (location == null) {
                location = lobbies.stream().collect(Utils.randomElement());
            }
            if (location != null) {
                Warlords.setRejoinPoint(op.getUniqueId(), location.getLocation());
            }
        }
    }

    @Override
    public void onPlayerReJoinGame(Player player) {
        State.super.onPlayerReJoinGame(player);
        Team team = game.getPlayerTeam(player.getUniqueId());
        player.getActivePotionEffects().clear();
        player.getInventory().clear();

        player.setAllowFlight(team == null);
        if (team != null) {
            for (PreGameItemOption item : items) {
                if (item != null) {
                    player.getInventory().setItem(item.getSlot(), item.getItem(game, player));
                }
            }
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setGameMode(team == null ? GameMode.SPECTATOR : GameMode.ADVENTURE);
            }
        }.runTaskLater(Warlords.getInstance(), 1);

        LobbyLocationMarker location = LobbyLocationMarker.getRandomLobbyLocation(game, team);
        if (location != null) {
            player.teleport(location.getLocation());
            Warlords.setRejoinPoint(player.getUniqueId(), location.getLocation());
        } else {
            ChatUtils.MessageType.WARLORDS.sendErrorMessage("Unable to warp player to lobby!, no lobby marker found");
        }
    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }

    private void updateTeamPreferences() {
        this.game.offlinePlayersWithoutSpectators().forEach((e) -> {
            if (e.getValue() == null) {
                return; // skip spectators
            }
            Team selectedTeam = PlayerSettings.getPlayerSettings(e.getKey().getUniqueId()).getWantedTeam();
            if (selectedTeam == null) {
                Bukkit.broadcast(Component.text(Objects.requireNonNull(e.getKey().getName()), NamedTextColor.GOLD)
                                          .append(Component.text("did not choose a team!", NamedTextColor.GRAY)));
            }
            TeamPreference newPref = new TeamPreference(
                    e.getValue(),
                    selectedTeam == null ? e.getValue() : selectedTeam,
                    selectedTeam == null ? TeamPriority.NO_PREFERENCE : TeamPriority.PLAYER_PREFERENCE
            );
            teamPreferences.compute(e.getKey().getUniqueId(), (k, oldPref) ->
                    oldPref == null || oldPref.priority() < newPref.priority() ? newPref : oldPref
            );
        });
    }

    private void distributePeopleOverTeams() {
        List<Map.Entry<UUID, TeamPreference>> prefs = new ArrayList<>(teamPreferences.entrySet());

        if (!prefs.isEmpty()) {
            int redIndex = 0;
            int blueIndex = prefs.size() - 1;

            boolean canPickRed = true;
            boolean canPickBlue = true;

            do {
                if (redIndex == blueIndex && canPickBlue && canPickRed) {
                    //We have 1 person remaining, and both teams still have room, put the player at its wanted team
                    tryMovePeep(prefs.get(redIndex), prefs.get(redIndex).getValue().wantedTeam);
                    canPickBlue = false;
                    canPickRed = false;
                }
                if (canPickRed) {
                    if (tryMovePeep(prefs.get(redIndex), Team.RED)) {
                        redIndex++;
                        canPickRed = redIndex < prefs.size();
                    } else {
                        canPickRed = false;
                    }
                }
                if (canPickBlue && redIndex <= blueIndex) {
                    if (tryMovePeep(prefs.get(blueIndex), Team.BLUE)) {
                        blueIndex--;
                        canPickBlue = blueIndex >= 0;
                    } else {
                        canPickBlue = false;
                    }
                }
            } while ((canPickRed || canPickBlue) && redIndex <= blueIndex);
        }
    }

    private boolean tryMovePeep(Map.Entry<UUID, TeamPreference> entry, Team target) {
        boolean canSwitchPeepTeam;
        if (entry.getValue().wantedTeam != target) {
            canSwitchPeepTeam = switch (entry.getValue().priority) {
                case FORCED_PREFERENCE, PLAYER_PREFERENCE -> false;
                // Always enforce teams people manually have picked, probably set to true in the future
                default -> true;
            };
        } else {
            canSwitchPeepTeam = true;
        }

        if (canSwitchPeepTeam) {
            this.game.setPlayerTeam(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue().wantedTeam);
            return true;
        }
        return false;
    }

    @Override
    public void skipTimer() {
        this.timer = 0;
        this.timerHasBeenSkipped = true;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = this.maxTimer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getMaxTimer() {
        return maxTimer;
    }

    public void setMaxTimer(int maxTimer) {
        this.maxTimer = maxTimer;
    }

    public void interactEvent(Player player, int heldItemSlot) {
        if (heldItemSlot >= 0 && heldItemSlot < 9 && this.items[heldItemSlot] != null) {
            this.items[heldItemSlot].runOnClick(this.game, player);
        }
    }

    private enum TeamPriority {
        FORCED_PREFERENCE,
        PLAYER_PREFERENCE,
        NO_PREFERENCE,
    }

    private static final class TeamPreference implements Comparable<TeamPreference> {
        final Team wantedTeam;
        final TeamPriority priority;
        final Team currentTeam;

        public TeamPreference(Team currentTeam, Team wantedTeam, TeamPriority priority) {
            this.currentTeam = currentTeam;
            this.wantedTeam = wantedTeam;
            this.priority = priority;
        }

        @Override
        public int compareTo(TeamPreference o) {
            return Integer.compare(toInt(), o.toInt());
        }

        // Team red is negative, blue is positive
        public int toInt() {
            return (wantedTeam == Team.RED ? -1 : 1) * priority();
        }

        public int priority() {
            return this.priority.ordinal() * 2 + 1 + (wantedTeam == currentTeam ? 1 : 0);
        }

    }
}

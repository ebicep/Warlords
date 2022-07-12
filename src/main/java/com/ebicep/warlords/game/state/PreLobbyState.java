package com.ebicep.warlords.game.state;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameAddon;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.PreGameItemOption;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.sr.SRCalculator;
import com.ebicep.warlords.util.java.DateUtil;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

import static com.ebicep.warlords.util.chat.ChatUtils.sendMessage;

public class PreLobbyState implements State, TimerDebugAble {
    public static final String WARLORDS_DATABASE_MESSAGEFEED = "warlords.database.messagefeed";

    private final Game game;
    private final Map<UUID, TeamPreference> teamPreferences = new HashMap<>();
    private int timer = 0;
    private int maxTimer = 0;
    private boolean timerHasBeenSkipped = false;
    private final PreGameItemOption[] items = new PreGameItemOption[9];

    public PreLobbyState(Game game) {
        this.game = game;
    }

    @Override
    public void begin() {
        this.maxTimer = game.getMap().getLobbyCountdown();
        this.resetTimer();
        game.setAcceptsPlayers(true);
        game.setAcceptsSpectators(false);
        for (Option option : game.getOptions()) {
            if (option instanceof PreGameItemOption) {
                PreGameItemOption preGameItemOption = (PreGameItemOption) option;
                items[preGameItemOption.getSlot()] = preGameItemOption;
            }
        }
    }
    
    public boolean hasEnoughPlayers() {
        int players = game.playersCount();
        return players >= game.getMinPlayers();
    }

    @Override
    public State run() {
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
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 20) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in 20 seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 10) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!");
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time <= 5 && time != 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        String s = time == 1 ? "!" : "s!";
                        sendMessage(player, false, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second" + s);
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, 1);
                    });
                } else if (time == 0) {
                    game.forEachOnlinePlayerWithoutSpectators((player, team) -> {
                        player.playSound(player.getLocation(), "gamestart", 1, 1);
                        player.setAllowFlight(false);
                    });
                }
            }

            if (timer <= 0) {
                // TODO update balancing system to read a games Team Markers,
                // this is needed for when we support more teams in the future
                if (!game.getAddons().contains(GameAddon.PRIVATE_GAME)) {
                    //separating internalPlayers into even teams because it might be uneven bc internalPlayers couldve left

                    //balancing based on specs

                    //parties first
                    int sameTeamPartyLimit = 2;
                    HashMap<Team, List<Player>> partyMembers = new HashMap<Team, List<Player>>() {{
                        put(Team.BLUE, new ArrayList<>());
                        put(Team.RED, new ArrayList<>());
                    }};
                    game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                        Player player = e.getKey();
                        Team team = e.getValue();
                        //check if player already is recorded
                        // TODO Test this logic if player are not online if this happens (we do not have player objects in this case)
                        if (partyMembers.values().stream().anyMatch(list -> list.contains(player))) {
                            return;
                        }
                        Warlords.partyManager.getPartyFromAny(player.getUniqueId()).ifPresent(party -> {
                            List<Player> partyPlayersInGame = party.getAllPartyPeoplePlayerOnline().stream().filter(p -> game.getPlayers().containsKey(p.getUniqueId())).collect(Collectors.toList());
                            //check if party has more than limit to get on one team, if so then skip party, they get normally balanced
                            if (partyPlayersInGame.size() > sameTeamPartyLimit) {
                                return;
                            }
                            List<Player> bluePlayers = partyMembers.get(Team.BLUE);
                            List<Player> redPlayers = partyMembers.get(Team.RED);
                            List<Player> partyPlayers = new ArrayList<>(partyPlayersInGame);
                            Collections.shuffle(partyPlayers);
                            int teamSizeDiff = Math.abs(bluePlayers.size() - redPlayers.size());
                            //check if whole party can go on the same team to get an even amount of internalPlayers on each team
                            if (teamSizeDiff > partyPlayers.size()) {
                                if (bluePlayers.size() > redPlayers.size())
                                    bluePlayers.addAll(partyPlayers);
                                else
                                    redPlayers.addAll(partyPlayers);
                            } else {
                                bluePlayers.addAll(partyPlayers);
                            }
                        });
                    });

                    HashMap<String, Integer> playersSR = new HashMap<>();
                    SRCalculator.playersSR.forEach((key, value1) -> playersSR.put(key.getUuid(), value1 == null ? 500 : value1));

                    HashMap<Player, Team> bestTeam = new HashMap<>();
                    int bestBlueSR = 0;
                    int bestRedSR = 0;
                    int bestTeamSRDifference = Integer.MAX_VALUE;

                    int maxSRDiff = 200;
                    for (int i = 0; i < 5000; i++) {
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Specializations, List<Player>> playerSpecs = new HashMap<>();
                        game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                            Player player = e.getKey();
                            Team team = e.getValue();
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player);
                        });
                        //specs that dont have an even amount of players to redistribute later
                        List<Player> playersLeft = new ArrayList<>();
                        //distributing specs evenly
                        playerSpecs.forEach((classes, playerList) -> {
                            int amountOfTargetSpecsOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec() == classes).count();
                            int amountOfTargetSpecsOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec() == classes).count();
                            for (Player player : playerList) {
                                //add to red team
                                if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                                    teams.put(player, Team.RED);
                                    amountOfTargetSpecsOnRed++;
                                }
                                //add to blue team
                                else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                                    teams.put(player, Team.BLUE);
                                    amountOfTargetSpecsOnBlue++;
                                }
                                //same amount on each team - add to playersleft to redistribute
                                else {
                                    playersLeft.add(player);
                                }
                            }
                        });
                        //start on team with least amount of players
                        int blueSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        int redSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();

//                        playersLeft = playersLeft.stream()
//                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedClass().specType))
//                                .collect(Collectors.toList());
                        for (Player player : playersLeft) {
                            if (redSR > blueSR) {
                                teams.put(player, Team.BLUE);
                                blueSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                            } else {
                                teams.put(player, Team.RED);
                                redSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                            }
                        }

                        int bluePlayers = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                        int redPlayers = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
                        //uneven teams
                        if ((bluePlayers + redPlayers) % 2 != 0) {
                            int srDiffRed = Math.abs(redSR - 3500 - blueSR);
                            int srDiffBlue = Math.abs(blueSR - 3500 - redSR);
                            if ((bluePlayers > redPlayers && srDiffRed > maxSRDiff && srDiffRed > 0) || (redPlayers > bluePlayers && srDiffBlue > maxSRDiff && srDiffBlue > 0)) {
                                maxSRDiff++;
                                continue;
                            }
                        } else {
                            if (Math.abs(redSR - blueSR) > maxSRDiff) {
                                maxSRDiff++;
                                continue;
                            }
                        }
                        if (Math.abs(bluePlayers - redPlayers) > 1) {
                            maxSRDiff++;
                            continue;
                        }

                        if (Math.abs(redSR - blueSR) < bestTeamSRDifference) {
                            bestTeam = teams;
                            bestBlueSR = blueSR;
                            bestRedSR = redSR;
                            bestTeamSRDifference = Math.abs(redSR - blueSR);
                        }
                    }
                    boolean failSafeActive = false;
                    boolean secondFailSafeActive = false;

                    //INCASE COULDNT BALANCE
                    if (bestTeam.isEmpty()) {
                        failSafeActive = true;
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Specializations, List<Player>> playerSpecs = new HashMap<>();
                        //all players are online or else they wouldve been removed from queue
                        game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                            Player player = e.getKey();
                            Team team = e.getValue();
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player);
                        });
                        List<Player> playersLeft = new ArrayList<>();
                        //distributing specs evenly
                        playerSpecs.forEach((classes, playerList) -> {
                            int amountOfTargetSpecsOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec() == classes).count();
                            int amountOfTargetSpecsOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec() == classes).count();
                            for (Player player : playerList) {
                                //add to red team
                                if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                                    teams.put(player, Team.RED);
                                    amountOfTargetSpecsOnRed++;
                                }
                                //add to blue team
                                else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                                    teams.put(player, Team.BLUE);
                                    amountOfTargetSpecsOnBlue++;
                                }
                                //same amount on each team - add to playersleft to redistribute
                                else {
                                    playersLeft.add(player);
                                }
                            }
                        });

                        //start on team with least amount of players
                        int amountOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                        int amountOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
                        final boolean[] toBlueTeam = {amountOnBlue <= amountOnRed};
                        playersLeft.stream()
                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedSpec().specType))
                                .forEachOrdered(player -> {
                                    if (toBlueTeam[0]) {
                                        teams.put(player, Team.BLUE);
                                    } else {
                                        teams.put(player, Team.RED);
                                    }
                                    toBlueTeam[0] = !toBlueTeam[0];
                                });

                        bestTeam = teams;
                        bestBlueSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        bestRedSR = teams.entrySet().stream()
                                .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                                .mapToInt(value -> playersSR.getOrDefault(value.getKey().getUniqueId().toString(), 500))
                                .sum();
                        bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
                    }

                    if (bestTeamSRDifference > 5000) {
                        secondFailSafeActive = true;
                        HashMap<Player, Team> teams = new HashMap<>();
                        HashMap<Specializations, List<Player>> playerSpecs = new HashMap<>();
                        game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                            Player player = e.getKey();
                            Team team = e.getValue();
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player);
                        });
                        int blueSR = 0;
                        int redSR = 0;
                        for (List<Player> value : playerSpecs.values()) {
                            for (Player player : value) {
                                if (blueSR > redSR) {
                                    teams.put(player, Team.RED);
                                    redSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                                } else {
                                    teams.put(player, Team.BLUE);
                                    blueSR += playersSR.getOrDefault(player.getUniqueId().toString(), 500);
                                }
                            }
                        }
                        bestTeam = teams;
                        bestBlueSR = blueSR;
                        bestRedSR = redSR;
                        bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
                    }

                    bestTeam.forEach((player, team) -> {
                        game.setPlayerTeam(player, team);
                        ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec(), team);
                        LobbyLocationMarker location = LobbyLocationMarker.getFirstLobbyLocation(game, team);
                        if (location != null) {
                            player.teleport(location.getLocation());
                        }
                    });

                    int bluePlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
                    int redPlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();

                    for (Map.Entry<UUID, Team> uuidTeamEntry : game.getPlayers().entrySet()) {
                        Player value = Bukkit.getPlayer(uuidTeamEntry.getKey());
                        if (value == null) continue;
                        if (value.hasPermission(WARLORDS_DATABASE_MESSAGEFEED)) {
                            value.sendMessage(ChatColor.DARK_AQUA + "----- BALANCE INFORMATION -----");
                            value.sendMessage(ChatColor.GREEN + "Max SR Diff: " + maxSRDiff);
                            value.sendMessage(ChatColor.GREEN + "SR Diff: " + bestTeamSRDifference);
                            value.sendMessage(ChatColor.BLUE + "Blue Players: " + ChatColor.GOLD + bluePlayers + ChatColor.GRAY + " - " + ChatColor.BLUE + "SR: " + ChatColor.GOLD + bestBlueSR);
                            value.sendMessage(ChatColor.RED + "Red Players: " + ChatColor.GOLD + redPlayers + ChatColor.GRAY + " - " + ChatColor.RED + "SR: " + ChatColor.GOLD + bestRedSR);
                            value.sendMessage(ChatColor.GREEN + "Fail Safe: " + ChatColor.GOLD + failSafeActive);
                            value.sendMessage(ChatColor.GREEN + "Second Fail Safe: " + ChatColor.GOLD + secondFailSafeActive);
                            value.sendMessage(ChatColor.DARK_AQUA + "-------------------------------");
                            bestTeam.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(playerTeamEntry -> {
                                Specializations specializations = Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec();
                                value.sendMessage(playerTeamEntry.getValue().teamColor() + playerTeamEntry.getKey().getName() + ChatColor.GRAY + " - " +
                                        specializations.specType.chatColor + specializations.name + ChatColor.GRAY + " - " +
                                        ChatColor.GOLD + playersSR.get(playerTeamEntry.getKey().getUniqueId().toString()));
                            });

                            value.sendMessage(ChatColor.DARK_AQUA + "-------------------------------");
                        }
                    }

                    System.out.println(ChatColor.DARK_AQUA + "----- BALANCE INFORMATION -----");
                    System.out.println(ChatColor.GREEN + "Max SR Diff: " + maxSRDiff);
                    System.out.println(ChatColor.GREEN + "SR Diff: " + bestTeamSRDifference);
                    System.out.println(ChatColor.BLUE + "Blue Players: " + ChatColor.GOLD + bluePlayers + ChatColor.GRAY + " - " + ChatColor.BLUE + "SR: " + ChatColor.GOLD + bestBlueSR);
                    System.out.println(ChatColor.RED + "Red Players: " + ChatColor.GOLD + redPlayers + ChatColor.GRAY + " - " + ChatColor.RED + "SR: " + ChatColor.GOLD + bestRedSR);
                    System.out.println(ChatColor.GREEN + "Fail Safe: " + ChatColor.GOLD + failSafeActive);
                    System.out.println(ChatColor.GREEN + "Second Fail Safe: " + ChatColor.GOLD + secondFailSafeActive);
                    System.out.println(ChatColor.DARK_AQUA + "-------------------------------");
                    bestTeam.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(playerTeamEntry -> {
                        Specializations specializations = Warlords.getPlayerSettings(playerTeamEntry.getKey().getUniqueId()).getSelectedSpec();
                        System.out.println(playerTeamEntry.getValue().teamColor() + playerTeamEntry.getKey().getName() + ChatColor.GRAY + " - " +
                                specializations.specType.chatColor + specializations.name + ChatColor.GRAY + " - " +
                                ChatColor.GOLD + playersSR.get(playerTeamEntry.getKey().getUniqueId().toString()));
                    });

                    System.out.println(ChatColor.DARK_AQUA + "-------------------------------");
                }

                if (game.getPlayers().size() >= 14) {
                    boolean isPrivate = game.getAddons().contains(GameAddon.PRIVATE_GAME);
                    BotManager.sendMessageToNotificationChannel(
                            "[GAME] A " + (isPrivate ? "" : "Public ") + "**" + game.getMap().getMapName() + "** started with **" + game.getPlayers().size() + (game.getPlayers().size() == 1 ? "** player!" : "** players!"),
                            isPrivate,
                            !isPrivate
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

    @Override
    public void end() {
//        updateTeamPreferences();
//        distributePeopleOverTeams();
    }

    public String getTimeLeftString() {
        int time = timer / 20;
        return (time < 10 ? "00:0" : "00:") + time;
    }

    public void giveLobbyScoreboard(boolean init, Player player) {
        CustomScoreboard customScoreboard = Warlords.playerScoreboards.get(player.getUniqueId());

        String dateString = DateUtil.formatCurrentDateEST("MM/dd/yyyy");

        String mapPrefix = ChatColor.WHITE + "Map: " + ChatColor.GREEN;
        String mapSuffix;
        if (game.getMap().getMapName().length() >= 16) {
            mapPrefix += game.getMap().getMapName().substring(0, 7);
            mapSuffix = game.getMap().getMapName().substring(7);
        } else {
            mapSuffix = game.getMap().getMapName();
        }

        Specializations specializations = Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        if (hasEnoughPlayers()) {
            customScoreboard.giveNewSideBar(init,
                    ChatColor.GRAY + dateString,
                    "  ",
                    mapPrefix + mapSuffix,
                    ChatColor.WHITE + "Players: " + ChatColor.GREEN + game.playersCount() + "/" + game.getMaxPlayers(),
                    "   ",
                    ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + getTimeLeftString() + ChatColor.WHITE,
                    "    ",
                    ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), specializations)) + " " + ChatColor.GOLD + Specializations.getClass(specializations).name,
                    ChatColor.WHITE + "Spec: " + ChatColor.GREEN + specializations.name,
                    "     ",
                    ChatColor.YELLOW + Warlords.VERSION);
        } else {
            customScoreboard.giveNewSideBar(init,
                    ChatColor.GRAY + dateString,
                    "  ",
                    mapPrefix + mapSuffix,
                    ChatColor.WHITE + "Players: " + ChatColor.GREEN + game.playersCount() + "/" + game.getMaxPlayers(),
                    "   ",
                    ChatColor.WHITE + "Starting if " + ChatColor.GREEN + (game.getMap().getMinPlayers() - game.playersCount()) + ChatColor.WHITE + " more",
                    ChatColor.WHITE + "players join ",
                    "    ",
                    ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), specializations)) + " " + ChatColor.GOLD + Specializations.getClass(specializations).name,
                    ChatColor.WHITE + "Spec: " + ChatColor.GREEN + specializations.name,
                    "     ",
                    ChatColor.YELLOW + Warlords.VERSION);
        }
    }

    private void updateTeamPreferences() {
        this.game.offlinePlayersWithoutSpectators().forEach((e) -> {
            if (e.getValue() == null) {
                return; // skip spectators
            }
            Team selectedTeam = Warlords.getPlayerSettings(e.getKey().getUniqueId()).getWantedTeam();
            if (selectedTeam == null) {
                Bukkit.broadcastMessage(ChatColor.GOLD + e.getKey().getName() + " §7did not choose a team!");
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

    private boolean tryMovePeep(Map.Entry<UUID, TeamPreference> entry, Team target) {
        boolean canSwitchPeepTeam;
        if (entry.getValue().wantedTeam != target) {
            switch (entry.getValue().priority) {
                case FORCED_PREFERENCE:
                    canSwitchPeepTeam = false;
                    break;
                case PLAYER_PREFERENCE:
                    canSwitchPeepTeam = false; // Always enforce teams people manually have picked, probably set to true in the future
                    break;
                default:
                    canSwitchPeepTeam = true;
            }
        } else {
            canSwitchPeepTeam = true;
        }

        if (canSwitchPeepTeam) {
            this.game.setPlayerTeam(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue().wantedTeam);
            return true;
        }
        return false;
    }

    private void distributePeopleOverTeams() {
        List<Map.Entry<UUID, TeamPreference>> prefs = teamPreferences.entrySet().stream().collect(Collectors.toList());

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

    @Override
    public void skipTimer() {
        this.timer = 0;
        this.timerHasBeenSkipped = true;
    }

    @Override
    public void resetTimer() throws IllegalStateException {
        this.timer = this.maxTimer;
    }

    @Override
    public void onPlayerJoinGame(OfflinePlayer op, boolean asSpectator) {
        if (!asSpectator) {
            Team team = Warlords.getPlayerSettings(op.getUniqueId()).getWantedTeam();
            Team finalTeam = team == null ? Team.BLUE : team;
            game.setPlayerTeam(op, finalTeam );
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
        
        if (team == null) {
            player.getInventory().clear();
            player.setAllowFlight(true);
            player.setGameMode(GameMode.SPECTATOR);
        } else {
            player.getInventory().clear();
            player.setAllowFlight(false);
            player.setGameMode(GameMode.ADVENTURE);
            
            for (PreGameItemOption item : items) {
                if (item != null) {
                    player.getInventory().setItem(item.getSlot(), item.getItem(game, player));
                }
            }

            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec(), team);
        }
        
        LobbyLocationMarker location = LobbyLocationMarker.getRandomLobbyLocation(game, team);
        if (location != null) {
            player.teleport(location.getLocation());
            Warlords.setRejoinPoint(player.getUniqueId(), location.getLocation());
        } else {
            System.out.println("Unable to warp player to lobby!, no lobby marker found");
        }
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

    private final class TeamPreference implements Comparable<TeamPreference> {
        final Team wantedTeam;
        final TeamPriority priority;
        final Team currentTeam;

        public TeamPreference(Team currentTeam, Team wantedTeam, TeamPriority priority) {
            this.currentTeam = currentTeam;
            this.wantedTeam = wantedTeam;
            this.priority = priority;
        }

        public int priority() {
            return this.priority.ordinal() * 2 + 1 + (wantedTeam == currentTeam ? 1 : 0);
        }

        // Team red is negative, blue is positive
        public int toInt() {
            return (wantedTeam == Team.RED ? -1 : 1) * priority();
        }

        @Override
        public int compareTo(TeamPreference o) {
            return Integer.compare(toInt(), o.toInt());
        }

    }

    @Override
    public int getTicksElapsed() {
        return 0;
    }
}

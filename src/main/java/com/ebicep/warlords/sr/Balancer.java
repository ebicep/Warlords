package com.ebicep.warlords.sr;

import co.aikar.commands.CommandIssuer;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.TeamMarker;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.player.general.SpecType;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.chat.ChatChannels;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Balancer {

    private static final int SAME_TEAM_PARTY_LIMIT = 2;
    private final Set<UUID> players;
    private final EnumSet<Team> teams;
    private final Map<Team, TeamInfo> bestTeam = new HashMap<>();

    public Balancer(Game game) {
        this(game.onlinePlayersWithoutSpectators()
                 .filter(e -> e.getValue() != null)
                 .map(Map.Entry::getKey)
                 .map(Player::getUniqueId)
                 .collect(Collectors.toSet()),
                TeamMarker.getTeams(game).clone()
        );
    }

    public Balancer(Set<UUID> players, EnumSet<Team> teams) {
        this.players = players;
        this.teams = teams;
        for (Team team : this.teams) {
            bestTeam.put(team, new TeamInfo());
        }
    }

    private void preassignParties() {
        Map<Party, List<DatabasePlayer>> partiedPlayers = new HashMap<>();
        forEachPlayer(player -> {
            // check if player already is recorded
            if (bestTeam.values().stream().anyMatch(list -> list.getPlayersSpecs().containsKey(player.getUuid()))) {
                return;
            }
            Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(player.getUuid());
            if (partyPlayerPair == null) {
                return;
            }
            Party party = partyPlayerPair.getA();
            List<UUID> partyPlayersInGame = party.getAllPartyPeoplePlayerOnline()
                                                 .stream()
                                                 .map(Player::getUniqueId)
                                                 .filter(players::contains)
                                                 .toList();
            // check if party has more than limit to get on one team, if so then skip party, they get normally balanced
            if (partyPlayersInGame.size() > SAME_TEAM_PARTY_LIMIT) {
                return;
            }
            partiedPlayers.computeIfAbsent(party, v -> new ArrayList<>()).add(player);
        });
        partiedPlayers.forEach((party, databasePlayers) -> {
            // add to team with lower amount of preassigned players
            Team teamWithLowerAmountOfPlayers = getTeamWithLeastPlayers(bestTeam);
            databasePlayers.forEach(databasePlayer -> addPlayerToTeam(databasePlayer, teamWithLowerAmountOfPlayers));
        });
    }

    private void forEachPlayer(Consumer<DatabasePlayer> biConsumer) {
        players.forEach(uuid -> DatabaseManager.getPlayer(uuid, biConsumer));
    }

    private Team getTeamWithLeastPlayers(Map<Team, TeamInfo> teamListMap) {
        return getTeam(teamListMap, Comparator.comparingInt(o -> o.getPlayersSpecs().size()));
    }

    private void addPlayerToTeam(DatabasePlayer databasePlayer, Team team) {
        bestTeam.computeIfAbsent(team, v -> new TeamInfo()).getPlayersSpecs().put(databasePlayer.getUuid(), databasePlayer.getLastSpec());
    }

    @Nullable
    private Team getTeam(Map<Team, TeamInfo> teamListMap, Comparator<TeamInfo> comparator) {
        // use comparator to get team with least whatever
        AtomicReference<Team> team = new AtomicReference<>(null);
        teamListMap.entrySet().stream().min(Map.Entry.comparingByValue(comparator)).ifPresent(e -> team.set(e.getKey()));
        return team.get();
    }

    public void balance(boolean useSR) {
        // useSR ignored for now
        preassignParties();
        // balance based on spec type only then swap stacking specs maybe
        for (SpecType value : SpecType.VALUES) {
            balanceSpecType(value);
        }
    }

    private void balanceSpecType(SpecType matchingSpecType) {
        Set<UUID> playersToRemove = new HashSet<>();
        forEachPlayer(databasePlayer -> {
            Specializations spec = databasePlayer.getLastSpec();
            SpecType specType = spec.specType;
            if (specType != matchingSpecType) {
                return;
            }
            Team teamWithLeastPlayers = getTeamWithLeastSpec(bestTeam, spec);
            addPlayerToTeam(databasePlayer.getUuid(), spec, teamWithLeastPlayers);
            playersToRemove.add(databasePlayer.getUuid());
        });
        players.removeAll(playersToRemove);
    }

    private Team getTeamWithLeastSpec(Map<Team, TeamInfo> teamListMap, Specializations spec) {
        // compare spec type
        // then compare spec so that stacking specs are swapped
        // then compare size so that teams with less players are prioritized
        SpecType specType = spec.specType;
        return getTeam(teamListMap, ((Comparator<TeamInfo>) (o1, o2) -> Integer.compare(
                        Math.toIntExact(o1.getPlayersSpecs()
                                          .values()
                                          .stream()
                                          .filter(specializations -> specializations.specType == specType)
                                          .count()),
                        Math.toIntExact(o2.getPlayersSpecs()
                                          .values()
                                          .stream()
                                          .filter(specializations -> specializations.specType == specType)
                                          .count())
                ))
                        .thenComparing(o -> o.getPlayersSpecs().values().stream().filter(specializations -> specializations == spec).count())
                        .thenComparing(o -> o.getPlayersSpecs().size())
        );
    }

    private void addPlayerToTeam(UUID uuid, Specializations spec, Team team) {
        bestTeam.computeIfAbsent(team, v -> new TeamInfo()).getPlayersSpecs().put(uuid, spec);
    }

    public void printDebugInfo() {
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("----- BALANCE INFORMATION -----", NamedTextColor.DARK_AQUA));
//        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Max SR Diff: " + maxSRDiff, NamedTextColor.GREEN));
//        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("SR Diff: " + bestTeamSRDifference, NamedTextColor.GREEN));
        for (Team team : teams) {
            TeamInfo teamInfo = bestTeam.get(team);
            if (teamInfo == null) {
                continue;
            }
            int players = teamInfo.getPlayersSpecs().size();
            int sr = 0;//teamInfo.getPlayersSpecs().values().stream().mapToInt(value -> value.sr).sum();
            ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text(team.name + " Players: ", team.getTeamColor())
                                                                         .append(Component.text(players, NamedTextColor.GOLD))
                                                                         .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                         .append(Component.text("SR: "))
                                                                         .append(Component.text(sr, NamedTextColor.GOLD)));
        }
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.empty());
//        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Fail Safe: ", NamedTextColor.GREEN)
//                                                                     .append(Component.text(failSafeActive, NamedTextColor.GOLD)));
//        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Second Fail Safe: ", NamedTextColor.GREEN)
//                                                                     .append(Component.text(secondFailSafeActive, NamedTextColor.GOLD)));
        bestTeam.keySet()
                .stream()
                .sorted(Comparator.comparing(Enum::ordinal))
                .forEachOrdered(team -> {
                    TeamInfo teamInfo = bestTeam.get(team);
                    Map<UUID, Specializations> playersSpecs = teamInfo.getPlayersSpecs();
                    playersSpecs.keySet()
                                .stream()
                                .sorted(Comparator.comparingInt(o -> playersSpecs.get(o).specType.ordinal()))
                                .forEachOrdered(uuid -> {
                                    Specializations specializations = playersSpecs.get(uuid);
                                    Player player = Bukkit.getPlayer(uuid);
                                    String name = uuid.toString();
                                    if (player != null) {
                                        name = player.getName();
                                    }
                                    ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text(name, team.getTeamColor())
                                                                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                                                 .append(Component.text(specializations.name,
                                                                                                         specializations.specType.getTextColor()
                                                                                                 )));
                                });
                });
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("-------------------------------", NamedTextColor.DARK_AQUA));
    }

    public Map<Team, TeamInfo> getBestTeam() {
        return bestTeam;
    }

    public static class TeamInfo {
        private final Map<UUID, Specializations> playersSpecs = new HashMap<>();

        public Map<UUID, Specializations> getPlayersSpecs() {
            return playersSpecs;
        }
    }


    /*
    // TODO update balancing system to read a games Team Markers,
    // this is needed for when we support more teams in the future
    public void balance() {
        //separating internalPlayers into even teams because it might be uneven bc internalPlayers couldve left

        //balancing based on specs

        //parties first


        HashMap<UUID, Integer> playersSR = new HashMap<>();
        SRCalculator.PLAYERS_SR.forEach((key, value1) -> playersSR.put(key.getUuid(), value1 == null ? 500 : value1));

        HashMap<UUID, Team> bestTeam = new HashMap<>();
        int bestBlueSR = 0;
        int bestRedSR = 0;
        int bestTeamSRDifference = Integer.MAX_VALUE;

        int maxSRDiff = 200;
        for (int i = 0; i < 5000; i++) {
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            //specs that dont have an even amount of players to redistribute later
            List<UUID> playersLeft = new ArrayList<>();
            //distributing specs evenly
            playerSpecs.forEach((specs, playerList) -> {
                int amountOfTargetSpecsOnBlue = (int) teams.entrySet()
                                                           .stream()
                                                           .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && PlayerSettings.getPlayerSettings(
                                                                   playerTeamEntry.getKey()).getSelectedSpec() == specs)
                                                           .count();
                int amountOfTargetSpecsOnRed = (int) teams.entrySet()
                                                          .stream()
                                                          .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && PlayerSettings.getPlayerSettings(
                                                                  playerTeamEntry.getKey()).getSelectedSpec() == specs)
                                                          .count();
                Collections.shuffle(playerList);
                for (UUID uuid : playerList) {
                    //add to red team
                    if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                        teams.put(uuid, Team.RED);
                        amountOfTargetSpecsOnRed++;
                    }
                    //add to blue team
                    else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                        teams.put(uuid, Team.BLUE);
                        amountOfTargetSpecsOnBlue++;
                    }
                    //same amount on each team - add to playersleft to redistribute
                    else {
                        playersLeft.add(uuid);
                    }
                }
            });
            //start on team with least amount of players
            int blueSR = teams.entrySet().stream()
                              .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE)
                              .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                              .sum();
            int redSR = teams.entrySet().stream()
                             .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                             .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                             .sum();

//                        playersLeft = playersLeft.stream()
//                                .sorted(Comparator.comparing(o -> Warlords.getPlayerSettings(o.getUniqueId()).getSelectedClass().specType))
//                                .toList();
            for (UUID uuid : playersLeft) {
                if (redSR > blueSR) {
                    teams.put(uuid, Team.BLUE);
                    blueSR += playersSR.getOrDefault(uuid, 500);
                } else {
                    teams.put(uuid, Team.RED);
                    redSR += playersSR.getOrDefault(uuid, 500);
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
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            //all players are online or else they wouldve been removed from queue
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            List<UUID> playersLeft = new ArrayList<>();
            //distributing specs evenly
            playerSpecs.forEach((classes, playerList) -> {
                int amountOfTargetSpecsOnBlue = (int) teams.entrySet()
                                                           .stream()
                                                           .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE && PlayerSettings.getPlayerSettings(
                                                                   playerTeamEntry.getKey()).getSelectedSpec() == classes)
                                                           .count();
                int amountOfTargetSpecsOnRed = (int) teams.entrySet()
                                                          .stream()
                                                          .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED && PlayerSettings.getPlayerSettings(
                                                                  playerTeamEntry.getKey()).getSelectedSpec() == classes)
                                                          .count();
                for (UUID uuid : playerList) {
                    //add to red team
                    if (amountOfTargetSpecsOnBlue > amountOfTargetSpecsOnRed) {
                        teams.put(uuid, Team.RED);
                        amountOfTargetSpecsOnRed++;
                    }
                    //add to blue team
                    else if (amountOfTargetSpecsOnRed > amountOfTargetSpecsOnBlue) {
                        teams.put(uuid, Team.BLUE);
                        amountOfTargetSpecsOnBlue++;
                    }
                    //same amount on each team - add to playersleft to redistribute
                    else {
                        playersLeft.add(uuid);
                    }
                }
            });

            //start on team with least amount of players
            int amountOnBlue = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
            int amountOnRed = (int) teams.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();
            final boolean[] toBlueTeam = {amountOnBlue <= amountOnRed};
            playersLeft.stream()
                       .sorted(Comparator.comparing(o -> PlayerSettings.getPlayerSettings(o).getSelectedSpec().specType))
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
                              .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                              .sum();
            bestRedSR = teams.entrySet().stream()
                             .filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED)
                             .mapToInt(value -> playersSR.getOrDefault(value.getKey(), 500))
                             .sum();
            bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
        }

        if (bestTeamSRDifference > 5000) {
            secondFailSafeActive = true;
            HashMap<UUID, Team> teams = new HashMap<>();
            HashMap<Specializations, List<UUID>> playerSpecs = new HashMap<>();
            game.onlinePlayersWithoutSpectators().filter(e -> e.getValue() != null).forEach(e -> {
                Player player = e.getKey();
                Team team = e.getValue();
                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                playerSpecs.computeIfAbsent(playerSettings.getSelectedSpec(), v -> new ArrayList<>()).add(player.getUniqueId());
            });
            int blueSR = 0;
            int redSR = 0;
            for (List<UUID> value : playerSpecs.values()) {
                for (UUID uuid : value) {
                    if (blueSR > redSR) {
                        teams.put(uuid, Team.RED);
                        redSR += playersSR.getOrDefault(uuid, 500);
                    } else {
                        teams.put(uuid, Team.BLUE);
                        blueSR += playersSR.getOrDefault(uuid, 500);
                    }
                }
            }
            bestTeam = teams;
            bestBlueSR = blueSR;
            bestRedSR = redSR;
            bestTeamSRDifference = Math.abs(bestBlueSR - bestRedSR);
        }

        bestTeam.forEach((uuid, team) -> {
            PlayerSettings.getPlayerSettings(uuid).setWantedTeam(team);
            game.setPlayerTeam(uuid, team);
            List<LobbyLocationMarker> lobbies = game.getMarkers(LobbyLocationMarker.class);
            LobbyLocationMarker location = lobbies.stream().filter(e -> e.matchesTeam(team)).collect(Utils.randomElement());
            if (location != null) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) {
                    player.teleport(location.getLocation());
                    Warlords.setRejoinPoint(player.getUniqueId(), location.getLocation());
                }
            }
        });

        int bluePlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.BLUE).count();
        int redPlayers = (int) bestTeam.entrySet().stream().filter(playerTeamEntry -> playerTeamEntry.getValue() == Team.RED).count();

        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("----- BALANCE INFORMATION -----", NamedTextColor.DARK_AQUA));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Max SR Diff: " + maxSRDiff, NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("SR Diff: " + bestTeamSRDifference, NamedTextColor.GREEN));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Blue Players: ", NamedTextColor.BLUE)
                                                                     .append(Component.text(bluePlayers, NamedTextColor.GOLD))
                                                                     .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                     .append(Component.text("SR: "))
                                                                     .append(Component.text(bestBlueSR, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Red Players: ", NamedTextColor.RED)
                                                                     .append(Component.text(redPlayers, NamedTextColor.GOLD))
                                                                     .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                     .append(Component.text("SR: "))
                                                                     .append(Component.text(bestRedSR, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Fail Safe: ", NamedTextColor.GREEN)
                                                                     .append(Component.text(failSafeActive, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("Second Fail Safe: ", NamedTextColor.GREEN)
                                                                     .append(Component.text(secondFailSafeActive, NamedTextColor.GOLD)));
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("-------------------------------", NamedTextColor.DARK_AQUA));
        bestTeam.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(playerTeamEntry -> {
                    UUID uuid = playerTeamEntry.getKey();
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        return;
                    }
                    Specializations specializations = PlayerSettings.getPlayerSettings(uuid).getSelectedSpec();
                    ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text(player.getName(), playerTeamEntry.getValue().teamColor())
                                                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                                 .append(Component.text(specializations.name, specializations.specType.getTextColor()))
                                                                                 .append(Component.text(" - ", NamedTextColor.GRAY))
                                                                                 .append(Component.text(playersSR.getOrDefault(uuid, 500),
                                                                                         NamedTextColor.GOLD
                                                                                 )));
                });
        ChatChannels.sendDebugMessage((CommandIssuer) null, Component.text("-------------------------------", NamedTextColor.DARK_AQUA));
    }

     */

}

package com.ebicep.warlords.sr.hypixel;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface BalanceMethod {

    BalanceMethod V1 = new V1();
    BalanceMethod V2 = new V2();
    BalanceMethod V2_1 = new V2_1();
    BalanceMethod[] VALUES = {V1, V2, V2_1};

    void balance(Set<HypixelBalancer.Player> players, List<HypixelBalancer.Filter> filters, Map<Team, HypixelBalancer.TeamBalanceInfo> teams);

    class V1 implements BalanceMethod {
        @Override
        public void balance(Set<HypixelBalancer.Player> players, List<HypixelBalancer.Filter> filters, Map<Team, HypixelBalancer.TeamBalanceInfo> teams) {
            int amountOfPlayers = players.size();
            int maxPerTeam = amountOfPlayers / 2 + (amountOfPlayers % 2);

            for (HypixelBalancer.Filter filter : filters) {
                List<HypixelBalancer.Player> playerList = players.stream()
                                                                 .filter(filter::test)
                                                                 .sorted(Comparator.comparingDouble(player -> -player.weight()))
                                                                 .toList();
                for (HypixelBalancer.Player player : playerList) {
                    players.remove(player);
                    Team team;
                    if (player.preassignedTeam() != null) {
                        team = player.preassignedTeam();
                    } else if (teams.values().stream().anyMatch(teamBalanceInfo -> teamBalanceInfo.getPlayers().size() == maxPerTeam)) {
                        team = teams.entrySet()
                                    .stream()
                                    .filter(entry -> entry.getValue().getPlayers().size() < maxPerTeam)
                                    .map(Map.Entry::getKey)
                                    .findAny()
                                    .get();
                    } else { //put on team with lowest weight
                        team = teams.entrySet().stream()
                                    .min(Comparator.comparingDouble(entry -> entry.getValue().getTotalWeight()))
                                    .map(Map.Entry::getKey)
                                    .orElse(Team.RED);
                    }
                    int index = amountOfPlayers - players.size();
                    teams.get(team).addPlayer(new HypixelBalancer.DebuggedPlayer(player, colors -> colors.aqua() + index));
                }
            }
        }
    }

    class V2 implements BalanceMethod {
        @Override
        public void balance(Set<HypixelBalancer.Player> players, List<HypixelBalancer.Filter> filters, Map<Team, HypixelBalancer.TeamBalanceInfo> teams) {
            int amountOfPlayers = players.size();
            for (HypixelBalancer.Filter filter : filters) {
                List<HypixelBalancer.Player> playerList = players.stream()
                                                                 .filter(filter::test)
                                                                 .sorted(Comparator.comparingDouble(player -> -player.weight()))
                                                                 .toList();
                for (HypixelBalancer.Player player : playerList) {
                    players.remove(player);
                    Team team;
                    if (player.preassignedTeam() != null) {
                        team = player.preassignedTeam();
                    } else {
                        team = teams.entrySet()
                                    .stream()
                                    .min(Comparator.comparingInt(entry -> entry.getValue().getPlayers().size()))
                                    .map(Map.Entry::getKey)
                                    .orElse(Team.RED);
                    }
                    int index = amountOfPlayers - players.size();
                    teams.get(team).addPlayer(new HypixelBalancer.DebuggedPlayer(player, colors -> colors.aqua() + index));
                }
            }
        }
    }

    class V2_1 implements BalanceMethod {
        @Override
        public void balance(Set<HypixelBalancer.Player> players, List<HypixelBalancer.Filter> filters, Map<Team, HypixelBalancer.TeamBalanceInfo> teams) {
            int amountOfPlayers = players.size();
//            Team lastTeam = Team.RED;
            for (HypixelBalancer.Filter filter : filters) {
                List<HypixelBalancer.Player> playerList = players.stream()
                                                                 .filter(filter::test)
                                                                 .sorted(Comparator.comparingDouble(player -> -player.weight()))
                                                                 .toList();
                for (int i = 0; i < playerList.size(); i++) {
                    HypixelBalancer.Player player = playerList.get(i);
                    players.remove(player);
                    boolean firstOfCategory = i == 0;// && filter instanceof Balancer.Filter.SpecTypeFilter;
                    Team team;
                    if (player.preassignedTeam() != null) {
                        team = player.preassignedTeam();
                    } else if (firstOfCategory) {
                        team = teams.entrySet()
                                    .stream()
                                    .min(Comparator.comparingDouble(entry -> entry.getValue().getTotalWeight()))
                                    .map(Map.Entry::getKey)
                                    .orElse(Team.BLUE);
                    } else {
                        team = teams.entrySet()
                                    .stream()
                                    .min(Comparator.comparingDouble(entry -> entry.getValue().getPlayers().size()))
                                    .map(Map.Entry::getKey)
                                    .orElse(Team.BLUE);
                    }
//                    lastTeam = team;
                    int index = amountOfPlayers - players.size();
                    HypixelBalancer.DebuggedPlayer debuggedPlayer = new HypixelBalancer.DebuggedPlayer(player, colors -> colors.aqua() + index);
                    if (player.preassignedTeam() == null && firstOfCategory) {
                        Map<Team, Double> teamsWeights = teams.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getTotalWeight()));
                        double newTeamWeight = teamsWeights.get(team) + player.weight();
                        debuggedPlayer.debuggedMessages().add(colors -> colors.darkPurple() +
                                teamsWeights.entrySet()
                                            .stream()
                                            .map(entry -> {
                                                Team key = entry.getKey();
                                                return key + ": " + HypixelBalancer.format(entry.getValue()) +
                                                        (key == team ? "->" + HypixelBalancer.format(newTeamWeight) : "");
                                            })
                                            .collect(Collectors.joining(" | "))
                        );
                    }
                    teams.get(team).addPlayer(debuggedPlayer);
                }
            }

        }
    }

}

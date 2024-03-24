package com.ebicep.warlords.sr.hypixel;

import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.ebicep.warlords.sr.hypixel.HypixelBalancer.format;

public interface ExtraBalanceFeature {

    ExtraBalanceFeature SWAP_UNEVEN_TEAMS = new SwapUnevenTeams();
    ExtraBalanceFeature SWAP_SPEC_TYPES = new SwapSpecTypes();
    ExtraBalanceFeature SWAP_TEAM_SPEC_TYPES = new SwapTeamSpecTypes();
    ExtraBalanceFeature COMPENSATE = new Compensate();
    ExtraBalanceFeature HARD_SWAP = new HardSwap();

    ExtraBalanceFeature[] VALUES = {SWAP_UNEVEN_TEAMS, SWAP_SPEC_TYPES, SWAP_TEAM_SPEC_TYPES, COMPENSATE, HARD_SWAP};

    @Nullable
    private static ExtraBalanceFeature.SpecTypeWeightWrapper getSpecTypeHighestWeightDiff(
            HypixelBalancer.Printer printer,
            Color colors,
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo1,
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo2
    ) {
        if (teamBalanceInfo1 == null || teamBalanceInfo2 == null) {
            printer.sendMessage(colors.darkRed() + "One of the teams is null");
            return null;
        }
        Map<SpecType, Double> specTypeWeightDiff = new HashMap<>();
        for (SpecType value : SpecType.VALUES) {
            double weight1 = teamBalanceInfo1.getSpecTypeWeight(value);
            double weight2 = teamBalanceInfo2.getSpecTypeWeight(value);
            specTypeWeightDiff.put(value, Math.abs(weight1 - weight2));
        }
        printer.sendMessage(colors.gray() + "-----------");
        specTypeWeightDiff.forEach((specType, diff) -> printer.sendMessage(colors.yellow() + specType + " Weight Diff: " + colors.lightPurple() + format(diff)));
        printer.sendMessage(colors.gray() + "-----------");
        // get the spec type with the most difference in weight
        SpecType specType = null;
        double highestWeightDiff = 0;
        for (Map.Entry<SpecType, Double> entry : specTypeWeightDiff.entrySet()) {
            if (entry.getValue() > highestWeightDiff) {
                highestWeightDiff = entry.getValue();
                specType = entry.getKey();
            }
        }
        return new SpecTypeWeightWrapper(specType, highestWeightDiff);
    }

    /**
     * @param printer          the printer to send messages to
     * @param teamBalanceInfos the team balance infos
     * @return true if the feature was applied
     */
    boolean apply(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos);

    /**
     * <p>Applies the feature to the two teams for a given amount of iterations until #applyTwoTeams returns false.</p>
     */
    interface ExtraBalanceFeatureTwoTeams extends ExtraBalanceFeature {

        @Override
        default boolean apply(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos) {
            Set<Team> teams = teamBalanceInfos.keySet();
            if (teams.size() < 2) {
                printer.sendMessage(printer.colors().darkRed() + "Not 2 teams");
                return false;
            }
            Team[] teamArray = teams.toArray(new Team[0]);
            Team team1 = teamArray[0];
            Team team2 = teamArray[1];
            boolean applied = false;
            for (int i = 0; i < getIterations(); i++) {
                if (applyTwoTeams(printer, teamBalanceInfos, team1, team2, i)) {
                    HypixelBalancer.printBalanceInfo(printer, teamBalanceInfos);
                    applied = true;
                } else {
                    break;
                }
            }
            return applied;
        }

        int getIterations();

        boolean applyTwoTeams(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos, Team team1, Team team2, int index);
    }

    /**
     * <p>Swaps players between the two teams to even out the player count.</p>
     * <p>- Only tries to swap players that have a spec type whose count isnt even between the two teams</p>
     * <p>- Only tries to swap player which would even out the weights the most</p>
     * <p>- Players swapped indicated by "MOVED" </p>
     */
    class SwapUnevenTeams implements ExtraBalanceFeature {
        @Override
        public boolean apply(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos) {
            // check if there are any teams with at least 2 more players than the other team
            int minPlayers = 0;
            HypixelBalancer.TeamBalanceInfo minPlayersTeamInfo = null;
            int maxPlayers = 0;
            HypixelBalancer.TeamBalanceInfo maxPlayersTeamInfo = null;
            for (Map.Entry<Team, HypixelBalancer.TeamBalanceInfo> entry : teamBalanceInfos.entrySet()) {
                int size = entry.getValue().getPlayers().size();
                if (minPlayers == 0 || size < minPlayers) {
                    minPlayers = size;
                    minPlayersTeamInfo = entry.getValue();
                }
                if (size > maxPlayers) {
                    maxPlayers = size;
                    maxPlayersTeamInfo = entry.getValue();
                }
            }
            Color colors = printer.colors();
            int playerDifference = maxPlayers - minPlayers;
            if (playerDifference <= 1) {
                printer.sendMessage(colors.yellow() + "Teams are even");
                return false;
            }
            boolean applied = false;
            for (int i = 0; i < playerDifference / 2; i++) {
                if (trySwapping(minPlayersTeamInfo, maxPlayersTeamInfo, printer, colors)) {
                    applied = true;
                }
            }
            return applied;
        }

        private boolean trySwapping(
                HypixelBalancer.TeamBalanceInfo minPlayersTeamInfo,
                HypixelBalancer.TeamBalanceInfo maxPlayersTeamInfo,
                HypixelBalancer.Printer printer,
                Color colors
        ) {
            // check which spec type to swap
            EnumSet<SpecType> specTypes = EnumSet.allOf(SpecType.class);
            EnumSet<SpecType> backupSpecTypes = EnumSet.noneOf(SpecType.class);
            Map<SpecType, List<HypixelBalancer.DebuggedPlayer>> playersMatchingSpecType = new HashMap<>();
            for (SpecType specType : SpecType.VALUES) {
                List<HypixelBalancer.DebuggedPlayer> playersMatching = maxPlayersTeamInfo.getPlayersMatching(specType);
                playersMatchingSpecType.put(specType, playersMatching);
                if (minPlayersTeamInfo.getSpecTypeCount(specType) == maxPlayersTeamInfo.getSpecTypeCount(specType)) {
                    specTypes.remove(specType);
                    if (!playersMatching.isEmpty()) {
                        backupSpecTypes.add(specType);
                    }
                }
            }
            if (specTypes.stream().allMatch(specType -> playersMatchingSpecType.get(specType).isEmpty())) {
                specTypes.addAll(backupSpecTypes);
            }
            printer.sendMessage(colors.yellow() + "Swappable spec types: " + colors.darkAqua() + specTypes);
            // find any player on the team with the most players that has the spec type and would even out the weights the most
            double weightDiff = maxPlayersTeamInfo.getTotalWeight() - minPlayersTeamInfo.getTotalWeight();
            HypixelBalancer.DebuggedPlayer playerToMove = null;
            double lowestWeightDiff = Double.MAX_VALUE;
            for (SpecType specType : specTypes) {
                for (HypixelBalancer.DebuggedPlayer player : playersMatchingSpecType.get(specType)) {
                    HypixelBalancer.Player p = player.player();
                    double newLowestWeightDiff = Math.abs(weightDiff - p.weight() * 2);
                    if (newLowestWeightDiff < lowestWeightDiff) {
                        lowestWeightDiff = newLowestWeightDiff;
                        playerToMove = player;
                    }
                }
            }
            if (playerToMove == null) {
                printer.sendMessage(colors.darkRed() + "No player to move");
                return false;
            }
            printer.sendMessage(colors.yellow() + "Moving " + playerToMove.player().getInfo(colors));
            playerToMove.debuggedMessages().add(c -> colors.yellow() + "MOVED");
            maxPlayersTeamInfo.removePlayer(playerToMove);
            minPlayersTeamInfo.addPlayer(playerToMove);
            return true;
        }
    }

    /**
     * <p>Swaps players between the two teams to even out the spec type weights.</p>
     * <p>- First gets the spec type with the most difference in weight.</p>
     * <p>- Then gets the players with the spec type on the two teams.</p>
     * <p>- Then finds a swap that would even out the spec type weights the most.</p>
     * <p>- Players swapped indicated by "SWAPPED".</p>
     * <p>- This repeats 5 times or until no more swaps can be made.</p>
     */
    class SwapSpecTypes implements ExtraBalanceFeatureTwoTeams {

        @Override
        public boolean applyTwoTeams(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos, Team team1, Team team2, int index) {
            Color colors = printer.colors();
            // find teams with equal amount of a spec type with the most difference in weight
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo1 = teamBalanceInfos.get(team1);
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo2 = teamBalanceInfos.get(team2);
            SpecTypeWeightWrapper infoSpecTypeWrapper = getSpecTypeHighestWeightDiff(printer, colors, teamBalanceInfo1, teamBalanceInfo2);
            if (infoSpecTypeWrapper == null) {
                return false;
            }
            SpecType specType = infoSpecTypeWrapper.specType;
            List<HypixelBalancer.DebuggedPlayer> team1Matching = teamBalanceInfo1.getPlayersMatching(specType);
            List<HypixelBalancer.DebuggedPlayer> team2Matching = teamBalanceInfo2.getPlayersMatching(specType);
            // find the players that would even out the spec type weights the most
            HypixelBalancer.DebuggedPlayer team1Swap = null;
            HypixelBalancer.DebuggedPlayer team2Swap = null;
            double lowestWeightDiff = infoSpecTypeWrapper.weightDiff;
            for (HypixelBalancer.DebuggedPlayer player1 : team1Matching) {
                for (HypixelBalancer.DebuggedPlayer player2 : team2Matching) {
                    double newLowestWeightDiff = Math.abs(
                            (teamBalanceInfo1.getSpecTypeWeight(specType) - player1.player().weight() + player2.player().weight()) -
                                    (teamBalanceInfo2.getSpecTypeWeight(specType) - player2.player().weight() + player1.player().weight())
                    );
                    if (newLowestWeightDiff < lowestWeightDiff) {
                        lowestWeightDiff = newLowestWeightDiff;
                        team1Swap = player1;
                        team2Swap = player2;
                    }
                }
            }
            if (team1Swap == null) {
                printer.sendMessage(colors.darkRed() + "No players to swap");
                return false;
            }
            printer.sendMessage(colors.yellow() + "Swapping " +
                    colors.blue() + "BLUE" +
                    colors.gray() + "(" + team1Swap.player().getInfo(colors) +
                    colors.gray() + ") " +
                    colors.red() + "RED" +
                    colors.gray() + "(" + team2Swap.player().getInfo(colors) +
                    colors.gray() + ") = (" +
                    colors.lightPurple() + format(Math.abs(team1Swap.player().weight() - team2Swap.player().weight())) +
                    colors.gray() + ")");
            team1Swap.debuggedMessages().add(c -> colors.yellow() + "SWAPPED #" + (index + 1));
            team2Swap.debuggedMessages().add(c -> colors.yellow() + "SWAPPED #" + (index + 1));
            teamBalanceInfo1.removePlayer(team1Swap);
            teamBalanceInfo2.removePlayer(team2Swap);
            teamBalanceInfo1.addPlayer(team2Swap);
            teamBalanceInfo2.addPlayer(team1Swap);
            return true;
        }

        @Override
        public int getIterations() {
            return 5;
        }
    }

    /**
     * Swaps groups of players with matching spec types between the two teams to even out the total weight between the teams.
     * <p>- First gets the spec type with the most difference in weight.</p>
     * <p>- Then ensures all players of the other spec types which combine to have the lowest spec type weight goes on that team.</p>
     * <p>Example: </p>
     * <p>BLUE = (0, 100, 50), RED = (50, 0, 0) | 100 is the highest diff</p>
     * <p>BLUE = (0, 100, 0), RED = (50, 0, 50)</p>
     */
    class SwapTeamSpecTypes implements ExtraBalanceFeatureTwoTeams {

        @Override
        public boolean applyTwoTeams(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos, Team team1, Team team2, int index) {
            Color colors = printer.colors();
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo1 = teamBalanceInfos.get(team1);
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo2 = teamBalanceInfos.get(team2);
            SpecTypeWeightWrapper infoSpecTypeWrapper = ExtraBalanceFeature.getSpecTypeHighestWeightDiff(printer, printer.colors(), teamBalanceInfo1, teamBalanceInfo2);
            if (infoSpecTypeWrapper == null) {
                return false;
            }
            EnumSet<SpecType> otherSpecTypes = EnumSet.allOf(SpecType.class);
            SpecType specType = infoSpecTypeWrapper.specType;
            otherSpecTypes.remove(specType);
            // get team with the highest weight of spec type
            List<HypixelBalancer.DebuggedPlayer> specTypePlayers1 = teamBalanceInfo1.getPlayersMatching(specType);
            List<HypixelBalancer.DebuggedPlayer> specTypePlayers2 = teamBalanceInfo2.getPlayersMatching(specType);
            double specTypeWeight1 = teamBalanceInfo1.getSpecTypeWeight(specType);
            double specTypeWeight2 = teamBalanceInfo2.getSpecTypeWeight(specType);
            HypixelBalancer.TeamBalanceInfo highestSpecTypeWeightTeam = specTypeWeight1 > specTypeWeight2 ? teamBalanceInfo1 : teamBalanceInfo2;
            printer.sendMessage(printer.colors().yellow() + "Highest spec type weight team: " +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? team1 : team2) + " " +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? format(specTypeWeight1) : format(specTypeWeight2)) + ">" +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? format(specTypeWeight2) : format(specTypeWeight1))
            );
            // make team with lowest weight of other spec types on the team with highest weight of spec type
            boolean applied = false;
            for (SpecType otherSpecType : otherSpecTypes) {
                List<HypixelBalancer.DebuggedPlayer> otherSpecTypePlayers1 = new ArrayList<>(teamBalanceInfo1.getPlayersMatching(otherSpecType));
                List<HypixelBalancer.DebuggedPlayer> otherSpecTypePlayers2 = new ArrayList<>(teamBalanceInfo2.getPlayersMatching(otherSpecType));
                if (specTypePlayers1.size() != specTypePlayers2.size() || otherSpecTypePlayers1.size() != otherSpecTypePlayers2.size()) {
                    printer.sendMessage(colors.darkRed() + "Teams don't have equal amount of " + otherSpecType);
                    return false;
                }
                double otherSpecTypeWeight1 = teamBalanceInfo1.getSpecTypeWeight(otherSpecType);
                double otherSpecTypeWeight2 = teamBalanceInfo2.getSpecTypeWeight(otherSpecType);
                printer.sendMessage(printer.colors().yellow() + otherSpecType + " Weight team 1: " + printer.colors().lightPurple() + format(
                        otherSpecTypeWeight1));
                printer.sendMessage(printer.colors().yellow() + otherSpecType + " Weight team 2: " + printer.colors().lightPurple() + format(
                        otherSpecTypeWeight2));
                boolean swapGroupToTeam2 = highestSpecTypeWeightTeam == teamBalanceInfo1 && otherSpecTypeWeight1 > otherSpecTypeWeight2;
                boolean swapGroupToTeam1 = highestSpecTypeWeightTeam == teamBalanceInfo2 && otherSpecTypeWeight2 > otherSpecTypeWeight1;
                boolean shouldSwap = swapGroupToTeam2 || swapGroupToTeam1;
                if (!shouldSwap) {
                    printer.sendMessage(printer.colors().yellow() + "Not swapping " + otherSpecType + " group");
                    continue;
                }
                printer.sendMessage(printer.colors().yellow() + "Swapping " + otherSpecType + " group");
                for (HypixelBalancer.DebuggedPlayer debuggedPlayer : otherSpecTypePlayers1) {
                    teamBalanceInfo1.removePlayer(debuggedPlayer);
                    teamBalanceInfo2.addPlayer(debuggedPlayer);
                }
                for (HypixelBalancer.DebuggedPlayer debuggedPlayer : otherSpecTypePlayers2) {
                    teamBalanceInfo2.removePlayer(debuggedPlayer);
                    teamBalanceInfo1.addPlayer(debuggedPlayer);
                }
                applied = true;
            }
            return applied;
        }

        @Override
        public int getIterations() {
            return 1;
        }
    }

    /**
     * <p>Assuming spec types are as even as they can be.</p>
     * <p>- Only works if weight diff is greater than 1.</p>
     * <p>- First takes the highest spec type weight difference = x.</p>
     * <p>- Then finds a player on higher different spec type weighted team to swap with another player on the lower weighted team who is lower weight and would add up to x/2.</p>
     */
    class Compensate implements ExtraBalanceFeatureTwoTeams {

        private final double bonus;

        public Compensate() {
            this(1);
        }

        public Compensate(int bonus) {
            this.bonus = bonus;
        }

        @Override
        public boolean apply(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos) {
            if (HypixelBalancer.getMaxWeightDiff(teamBalanceInfos) <= 1) {
                return false;
            }
            return ExtraBalanceFeatureTwoTeams.super.apply(printer, teamBalanceInfos);
        }

        @Override
        public boolean applyTwoTeams(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos, Team team1, Team team2, int index) {
            Color colors = printer.colors();
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo1 = teamBalanceInfos.get(team1);
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo2 = teamBalanceInfos.get(team2);
            SpecTypeWeightWrapper infoSpecTypeWrapper = ExtraBalanceFeature.getSpecTypeHighestWeightDiff(printer, colors, teamBalanceInfo1, teamBalanceInfo2);
            if (infoSpecTypeWrapper == null) {
                return false;
            }
            double weightDiff = infoSpecTypeWrapper.weightDiff; // x
            if (weightDiff <= .5) {
                printer.sendMessage(colors.darkRed() + "Weight diff is less than threshold");
                return false;
            }
            // get team with the highest weight diff of spec type
            SpecType specType = infoSpecTypeWrapper.specType;
            double team1Weight = teamBalanceInfo1.getTotalWeight();
            double team2Weight = teamBalanceInfo2.getTotalWeight();
            HypixelBalancer.TeamBalanceInfo highestSpecTypeWeightTeam;
            HypixelBalancer.TeamBalanceInfo lowestSpecTypeWeightTeam;
            if (team1Weight > team2Weight) {
                highestSpecTypeWeightTeam = teamBalanceInfo1;
                lowestSpecTypeWeightTeam = teamBalanceInfo2;
            } else {
                highestSpecTypeWeightTeam = teamBalanceInfo2;
                lowestSpecTypeWeightTeam = teamBalanceInfo1;
            }
            printer.sendMessage(colors.yellow() + "Highest spec type weight team: " +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? team1 : team2) + " " +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? format(team1Weight) : format(team2Weight)) + ">" +
                    (highestSpecTypeWeightTeam == teamBalanceInfo1 ? format(team2Weight) : format(team1Weight))
            );
            // find a player on higher spec type weighted team to swap with another player on the lower weighted team who is lower weight and would add up to x
            EnumSet<SpecType> otherSpecTypes = EnumSet.allOf(SpecType.class);
            otherSpecTypes.remove(specType);
            double maxWeightDiff = Math.abs(team1Weight - team2Weight) / 2 + bonus; // x/2 + BONUS adjustable to allow for closer swaps
            printer.sendMessage(colors.yellow() + "Max compensate weight diff: " + colors.lightPurple() + format(maxWeightDiff));
            double highestWeightDiff = Double.MIN_VALUE;
            HypixelBalancer.DebuggedPlayer highestPlayerToSwap = null;
            HypixelBalancer.DebuggedPlayer lowestPlayerToSwap = null;
            for (SpecType otherSpecType : otherSpecTypes) {
                for (HypixelBalancer.DebuggedPlayer p1 : highestSpecTypeWeightTeam.getPlayersMatching(otherSpecType)) {
                    if (p1.debuggedMessages().stream().anyMatch(debuggedMessage -> debuggedMessage.getMessage(colors).contains("COMPENSATE"))) {
                        continue;
                    }
                    double p1Weight = p1.player().weight();
                    for (HypixelBalancer.DebuggedPlayer p2 : lowestSpecTypeWeightTeam.getPlayersMatching(otherSpecType)) {
                        if (p2.debuggedMessages().stream().anyMatch(debuggedMessage -> debuggedMessage.getMessage(colors).contains("COMPENSATE"))) {
                            continue;
                        }
                        double p2Weight = p2.player().weight();
                        if (p1Weight < p2Weight) {
                            continue;
                        }
                        double diff = p1Weight - p2Weight;
                        if (highestWeightDiff < diff && diff < maxWeightDiff) {
                            highestWeightDiff = diff;
                            highestPlayerToSwap = p1;
                            lowestPlayerToSwap = p2;
                        }
                    }
                }
            }
            if (highestPlayerToSwap == null) {
                printer.sendMessage(colors.darkRed() + "No players to swap");
                return false;
            }
            printer.sendMessage(colors.yellow() + "Swapping " +
                    colors.blue() + "BLUE" +
                    colors.gray() + "(" + highestPlayerToSwap.player().getInfo(colors) +
                    colors.gray() + ") " +
                    colors.red() + "RED" +
                    colors.gray() + "(" + lowestPlayerToSwap.player().getInfo(colors) +
                    colors.gray() + ") = (" +
                    colors.lightPurple() + format(highestWeightDiff) +
                    colors.gray() + ")");
            String compensateInfo = format(maxWeightDiff) + "|" + format(highestWeightDiff);
            highestPlayerToSwap.debuggedMessages().add(c -> colors.yellow() + "COMPENSATE SWAP #" + (index + 1) + " " + compensateInfo + "");
            lowestPlayerToSwap.debuggedMessages().add(c -> colors.yellow() + "COMPENSATE SWAP #" + (index + 1) + " " + compensateInfo + "");
            highestSpecTypeWeightTeam.removePlayer(highestPlayerToSwap);
            lowestSpecTypeWeightTeam.removePlayer(lowestPlayerToSwap);
            highestSpecTypeWeightTeam.addPlayer(lowestPlayerToSwap);
            lowestSpecTypeWeightTeam.addPlayer(highestPlayerToSwap);
            return true;
        }

        @Override
        public int getIterations() {
            return 8;
        }
    }

    /**
     * <p>Finds best swap between to players with matching spec types that would even out the teams weights</p>
     */
    class HardSwap implements ExtraBalanceFeatureTwoTeams {

        @Override
        public boolean apply(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos) {
            if (HypixelBalancer.getMaxWeightDiff(teamBalanceInfos) <= 1) {
                return false;
            }
            return ExtraBalanceFeatureTwoTeams.super.apply(printer, teamBalanceInfos);
        }

        @Override
        public boolean applyTwoTeams(HypixelBalancer.Printer printer, Map<Team, HypixelBalancer.TeamBalanceInfo> teamBalanceInfos, Team team1, Team team2, int index) {
            Color colors = printer.colors();
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo1 = teamBalanceInfos.get(team1);
            HypixelBalancer.TeamBalanceInfo teamBalanceInfo2 = teamBalanceInfos.get(team2);

            double team1Weight = teamBalanceInfo1.getTotalWeight();
            double team2Weight = teamBalanceInfo2.getTotalWeight();

            double maxWeightDiff = Math.abs(team1Weight - team2Weight) / 2 + .5; // x/2, +1 adjustable to allow for closer swaps
            printer.sendMessage(colors.yellow() + "Max compensate weight diff: " + colors.lightPurple() + format(maxWeightDiff));
            double highestWeightDiff = Double.MIN_VALUE;
            HypixelBalancer.TeamBalanceInfo highestWeightTeam;
            HypixelBalancer.TeamBalanceInfo lowestWeightTeam;
            if (team1Weight > team2Weight) {
                highestWeightTeam = teamBalanceInfo1;
                lowestWeightTeam = teamBalanceInfo2;
            } else {
                highestWeightTeam = teamBalanceInfo2;
                lowestWeightTeam = teamBalanceInfo1;
            }
            HypixelBalancer.DebuggedPlayer highestPlayerToSwap = null;
            HypixelBalancer.DebuggedPlayer lowestPlayerToSwap = null;
            for (SpecType specType : SpecType.VALUES) {
                for (HypixelBalancer.DebuggedPlayer p1 : highestWeightTeam.getPlayersMatching(specType)) {
//                    if (p1.debuggedMessages().stream().anyMatch(debuggedMessage -> debuggedMessage.getMessage(colors).contains("HARD"))) {
//                        continue;
//                    }
                    double p1Weight = p1.player().weight();
                    for (HypixelBalancer.DebuggedPlayer p2 : lowestWeightTeam.getPlayersMatching(specType)) {
//                        if (p2.debuggedMessages().stream().anyMatch(debuggedMessage -> debuggedMessage.getMessage(colors).contains("HARD"))) {
//                            continue;
//                        }
                        double p2Weight = p2.player().weight();
                        if (p1Weight < p2Weight) {
                            continue;
                        }
                        double diff = p1Weight - p2Weight;
                        if (highestWeightDiff < diff && diff < maxWeightDiff) {
                            highestWeightDiff = diff;
                            highestPlayerToSwap = p1;
                            lowestPlayerToSwap = p2;
                        }
                    }
                }
            }
            if (highestPlayerToSwap == null) {
                printer.sendMessage(colors.darkRed() + "No players to swap");
                return false;
            }
            printer.sendMessage(colors.yellow() + "Swapping " +
                    colors.blue() + "BLUE" +
                    colors.gray() + "(" + highestPlayerToSwap.player().getInfo(colors) +
                    colors.gray() + ") " +
                    colors.red() + "RED" +
                    colors.gray() + "(" + lowestPlayerToSwap.player().getInfo(colors) +
                    colors.gray() + ") = (" +
                    colors.lightPurple() + format(highestWeightDiff) +
                    colors.gray() + ")");
            try {
                highestWeightTeam.removePlayer(highestPlayerToSwap);
            } catch (Exception e) {
                e.printStackTrace();
                HypixelBalancer.printBalanceInfo(printer, teamBalanceInfos);
                throw new RuntimeException("Failed to remove player " + highestPlayerToSwap.player()
                                                                                           .getInfo(colors) + " from " + (highestWeightTeam == teamBalanceInfo1 ? team1 : team2));
            }
            lowestWeightTeam.removePlayer(lowestPlayerToSwap);
            highestWeightTeam.addPlayer(lowestPlayerToSwap);
            lowestWeightTeam.addPlayer(highestPlayerToSwap);
            double newTeam1Weight = teamBalanceInfo1.getTotalWeight();
            double newTeam2Weight = teamBalanceInfo2.getTotalWeight();
            String compensateInfo = (highestWeightTeam == teamBalanceInfo1 ? team1 : team2) + " >> " +
                    format(team1Weight) + "|" + format(team2Weight) + " >> " +
                    format(newTeam1Weight) + "|" + format(newTeam2Weight) + " >> " +
                    format(maxWeightDiff) + "|" + format(highestWeightDiff);
            highestPlayerToSwap.debuggedMessages().add(c -> colors.yellow() + "HARD SWAP #" + (index + 1) + " >> " + compensateInfo + "");
            lowestPlayerToSwap.debuggedMessages().add(c -> colors.yellow() + "HARD SWAP #" + (index + 1) + " >> " + compensateInfo + "");
            return true;
        }

        @Override
        public int getIterations() {
            return 5;
        }
    }

    /**
     * Wrapper class for a spec type and its weight difference between two teams
     */
    class SpecTypeWeightWrapper {
        protected final SpecType specType;
        private final double weightDiff;

        SpecTypeWeightWrapper(SpecType specType, double weightDiff) {
            this.specType = specType;
            this.weightDiff = weightDiff;
        }
    }

}

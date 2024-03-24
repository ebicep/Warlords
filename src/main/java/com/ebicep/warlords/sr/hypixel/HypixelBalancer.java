package com.ebicep.warlords.sr.hypixel;


import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HypixelBalancer {

    public static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("#.##");
    private static final List<Filter> FILTERS = List.of(
            (Filter.SpecificationFilter) () -> Specialization.DEFENDER,
            (Filter.SpecificationFilter) () -> Specialization.CRYOMANCER,
            (Filter.SpecTypeFilter) () -> SpecType.TANK,
            (Filter.SpecTypeFilter) () -> SpecType.DAMAGE,
            (Filter.SpecTypeFilter) () -> SpecType.HEALER
    );

    public static String format(double weight) {
        return WEIGHT_FORMAT.format(weight);
    }

    public static void balance(
            BalanceMethod balanceMethod,
            WeightGenerationMethod weightGenerationMethod,
            ExtraBalanceFeature... extraBalanceFeatures
    ) {
        List<ExtraBalanceFeature> features = List.of(extraBalanceFeatures);
        balance(new Printer(System.out::println, new Color() {}),
                50_000,
                24,
                balanceMethod,
                weightGenerationMethod,
                features.isEmpty() ? Collections.emptyList() : new ArrayList<>(features)
        );
    }

    public static void balance(
            Printer printer,
            int iterations,
            int playerCount,
            BalanceMethod balanceMethod,
            WeightGenerationMethod weightGenerationMethod,
            List<ExtraBalanceFeature> extraBalanceFeatures
    ) {
        Color colors = printer.colors;
        printer.sendMessage(colors.white() + "-------------------------------------------------");
        printer.sendMessage(colors.white() + "-------------------------------------------------");
        printer.sendMessage(colors.gray() + "Iterations: " + colors.green() + iterations);
        printer.sendMessage(colors.gray() + "Player Count: " + colors.green() + playerCount);
        printer.sendMessage(colors.gray() + "Balance Method: " + colors.green() + balanceMethod.getClass().getSimpleName());
        printer.sendMessage(colors.gray() + "Random Weight Method: " + colors.green() + weightGenerationMethod.getClass().getSimpleName());
        printer.sendMessage(colors.gray() + "Extra Balance Features: " + colors.green() + extraBalanceFeatures.stream()
                                                                                                              .map(extraBalanceFeature -> extraBalanceFeature.getClass()
                                                                                                                                                             .getSimpleName())
                                                                                                              .collect(Collectors.joining(", ")));
        double totalWeightDiff = 0;
        double maxWeightDiff = 0;
        Map<Integer, Integer> weightDiffCount = new HashMap<>();
        Map<Team, TeamBalanceInfo> mostUnbalancedTeam = new HashMap<>();
        for (int i = 0; i < iterations; i++) {
            Set<Player> players = new HashSet<>();
            for (int j = 0; j < playerCount; j++) {
                players.add(new Player(Specialization.getRandomSpec(), weightGenerationMethod.generateRandomWeight()));//, j < 10 ? Team.BLUE : null));
            }
            if (iterations == 1) {
                // printing list of players to be balanced in order
                players.stream()
                       .sorted(Comparator.<Player>comparingInt(player -> {
                           int index = 0;
                           for (Filter filter : FILTERS) {
                               if (filter.test(player)) {
                                   return index;
                               }
                               index++;
                           }
                           return index;
                       }).thenComparingDouble(player -> -player.weight))
                       .forEachOrdered(player -> printer.sendMessage("  " + player.getInfo(colors)));
                printer.sendMessage(colors.white() + "-------------------------------------------------");
            }
            Map<Team, TeamBalanceInfo> teams = getBalancedTeams(players, balanceMethod);
            if (iterations == 1) {
                printBalanceInfo(printer, teams);
            }
            printer.setEnabled(iterations == 1);
            printer.sendMessage(colors.white() + "-------------------------------------------------");
            double weightDiff = getMaxWeightDiff(teams);
            printer.sendMessage(colors.gray() + "Weight Diff: " + colors.darkPurple() + WEIGHT_FORMAT.format(weightDiff));
            printer.sendMessage(colors.white() + "-------------------------------------------------");
            for (ExtraBalanceFeature extraBalanceFeature : extraBalanceFeatures) {
                printer.sendMessage(colors.yellow() + "Extra Balance Feature: " + extraBalanceFeature.getClass().getSimpleName());
                boolean applied = extraBalanceFeature.apply(printer, teams);
                if (!applied) {
                    weightDiff = getMaxWeightDiff(teams);
                    printer.sendMessage(colors.yellow() + "No changes applied");
                    printer.sendMessage(colors.gray() + "--------------------------------");
                    continue;
                }
                weightDiff = getMaxWeightDiff(teams);
                printer.sendMessage(colors.gray() + "Max Weight Diff: " + colors.darkPurple() + WEIGHT_FORMAT.format(weightDiff));
                printBalanceInfo(printer, teams);
                printer.sendMessage(colors.gray() + "--------------------------------");
            }
            totalWeightDiff += weightDiff;
//            if (teams.get(Team.BLUE).players.size() != teams.get(Team.RED).players.size()) {
//                printer.setEnabled(true);
//                printer.sendMessage(colors.red() + "!!!!!!!! Teams are not even !!!!!!!!");
//                maxWeightDiff = weightDiff;
//                mostUnbalancedTeam = teams;
//                break;
//            }
            weightDiffCount.merge((int) weightDiff, 1, Integer::sum);
            if (weightDiff > maxWeightDiff) {
                maxWeightDiff = weightDiff;
                mostUnbalancedTeam = teams;
            }
        }
        printer.setEnabled(true);
        printer.sendMessage(colors.gray() + "Average Weight Diff: " + colors.darkPurple() + WEIGHT_FORMAT.format(totalWeightDiff / iterations));
        printer.sendMessage(colors.gray() + "Max Weight Diff: " + colors.darkPurple() + WEIGHT_FORMAT.format(maxWeightDiff));
        printBalanceInfo(printer, mostUnbalancedTeam);

        printer.sendMessage(colors.white() + "-------------------------------------------------");
        printer.sendMessage(colors.white() + "-------------------------------------------------");

        mostUnbalancedTeam.forEach((key, value) -> {
            for (DebuggedPlayer player : value.players) {
                printer.sendMessage("new Player(Specialization." + player.player.spec + ", " + WEIGHT_FORMAT.format(player.player.weight) + ")");
            }
        });

        weightDiffCount.forEach((weightDiff, count) -> {
            printer.sendMessage(weightDiff + " - " + count + " (" + WEIGHT_FORMAT.format(count / (double) iterations * 100) + "%)");
        });
    }

    private static Map<Team, TeamBalanceInfo> getBalancedTeams(Set<Player> players, BalanceMethod balanceMethod) {
        Map<Team, TeamBalanceInfo> teams = new LinkedHashMap<>();
        for (Team team : Team.VALUES) {
            teams.put(team, new TeamBalanceInfo());
        }

        balanceMethod.balance(players, FILTERS, teams);

        return teams;
    }

    public static void printBalanceInfo(Printer printer, Map<Team, TeamBalanceInfo> teams) {
        Color colors = printer.colors;
        teams.forEach((team, teamBalanceInfo) -> {
            printer.sendMessage(colors.gray() + "-----------------------");
            printer.sendMessage(team.getColor.apply(colors) + team + colors.gray() + " - " + colors.darkPurple() + WEIGHT_FORMAT.format(teamBalanceInfo.totalWeight));
            teamBalanceInfo.cachedSpecTypeData
                    .entrySet()
                    .stream()
                    .sorted(Comparator.comparingInt(o -> o.getKey().ordinal()))
                    .map(entry -> {
                        SpecType specType = entry.getKey();
                        double totalSpecTypeWeight = teamBalanceInfo.getSpecTypeWeight(specType);
                        return specType.getColor.apply(colors) + specType +
                                colors.gray() + ": " +
                                colors.green() + entry.getValue().getCount() +
                                colors.gray() + " (" + colors.lightPurple() + WEIGHT_FORMAT.format(totalSpecTypeWeight) + colors.gray() + ")" +
                                colors.gray() + " (" + colors.darkPurple() + WEIGHT_FORMAT.format(totalSpecTypeWeight / teamBalanceInfo.totalWeight * 100) + "%" + colors.gray() + ")";
                    })
                    .forEachOrdered(s -> printer.sendMessage("  " + s));
            printer.sendMessage(colors.gray() + "  -----------------------");
            List<DebuggedPlayer> players = teamBalanceInfo.players;
            for (DebuggedPlayer debuggedPlayer : players) {
                printer.sendMessage("  " + debuggedPlayer.getInfo(colors));
            }
        });
    }

    public static double getMaxWeightDiff(Map<Team, TeamBalanceInfo> teams) {
        double maxWeight = 0;
        double minWeight = Double.MAX_VALUE;
        for (TeamBalanceInfo teamBalanceInfo : teams.values()) {
            maxWeight = Math.max(maxWeight, teamBalanceInfo.totalWeight);
            minWeight = Math.min(minWeight, teamBalanceInfo.totalWeight);
        }
        return maxWeight - minWeight;
    }


    /**
     * Debug message supplier
     */
    interface DebuggedMessage {
        String getMessage(Color colors);
    }

    /**
     * Balance filters
     */
    interface Filter {
        boolean test(Player player);

        interface SpecTypeFilter extends Filter {
            @Override
            default boolean test(Player player) {
                return player.spec().specType == specType();
            }

            SpecType specType();
        }

        interface SpecificationFilter extends Filter {
            @Override
            default boolean test(Player player) {
                return player.spec() == spec();
            }

            Specialization spec();
        }

    }

    /**
     * Balance information for an entire team
     */
    static class TeamBalanceInfo {
        private final List<DebuggedPlayer> players = new ArrayList<>();
        private final Map<SpecType, SpecTypeData> cachedSpecTypeData = new HashMap<>();
        private double totalWeight = 0;

        public void addPlayer(DebuggedPlayer debuggedPlayer) {
            players.add(debuggedPlayer);
            cachedSpecTypeData.computeIfAbsent(debuggedPlayer.player.spec.specType, k -> new SpecTypeData()).addPlayer(debuggedPlayer);
            totalWeight += debuggedPlayer.player.weight;
        }

        public void removePlayer(DebuggedPlayer debuggedPlayer) {
            boolean removed = players.remove(debuggedPlayer);
            if (!removed) {
                throw new IllegalStateException("Tried to remove a player that wasn't in the list");
            }
            cachedSpecTypeData.computeIfAbsent(debuggedPlayer.player.spec.specType, k -> new SpecTypeData()).removePlayer(debuggedPlayer);
            totalWeight -= debuggedPlayer.player.weight;
        }

        public double getSpecTypeWeight(SpecType specType) {
            return cachedSpecTypeData.computeIfAbsent(specType, k -> new SpecTypeData()).getWeight();
        }

        public int getSpecTypeCount(SpecType specType) {
            return cachedSpecTypeData.computeIfAbsent(specType, k -> new SpecTypeData()).getCount();
        }

        public List<DebuggedPlayer> getPlayersMatching(SpecType specType) {
            return cachedSpecTypeData.computeIfAbsent(specType, k -> new SpecTypeData()).getNonPreAssignedPlayers();
        }

        public List<DebuggedPlayer> getPlayersMatching(Predicate<Player> filter) {
            return players.stream()
                          .filter(debuggedPlayer -> debuggedPlayer.player.preassignedTeam == null)
                          .filter(debuggedPlayer -> filter.test(debuggedPlayer.player))
                          .toList();
        }

        public List<DebuggedPlayer> getPlayers() {
            return players;
        }

        public double getTotalWeight() {
            return totalWeight;
        }

        static class SpecTypeData {
            private final List<DebuggedPlayer> allPlayers = new ArrayList<>();
            private final List<DebuggedPlayer> nonPreAssignedPlayers = new ArrayList<>();
            private double weight = 0;

            public int getCount() {
                return allPlayers.size();
            }

            public void addPlayer(DebuggedPlayer debuggedPlayer) {
                allPlayers.add(debuggedPlayer);
                if (debuggedPlayer.player.preassignedTeam == null) {
                    nonPreAssignedPlayers.add(debuggedPlayer);
                }
                weight += debuggedPlayer.player.weight;
            }

            public void removePlayer(DebuggedPlayer debuggedPlayer) {
                boolean removed = allPlayers.remove(debuggedPlayer);
                if (!removed) {
                    throw new IllegalStateException("Tried to remove a player that wasn't in the list");
                }
                if (debuggedPlayer.player.preassignedTeam == null) {
                    nonPreAssignedPlayers.remove(debuggedPlayer);
                }
                weight -= debuggedPlayer.player.weight;
            }

            public List<DebuggedPlayer> getAllPlayers() {
                return allPlayers;
            }

            public List<DebuggedPlayer> getNonPreAssignedPlayers() {
                return nonPreAssignedPlayers;
            }

            public double getWeight() {
                return weight;
            }
        }
    }

    /**
     * Player wrapper class with debug messages
     *
     * @param player           the player
     * @param debuggedMessages debug messages
     */
    record DebuggedPlayer(Player player, List<DebuggedMessage> debuggedMessages) {
        public DebuggedPlayer(Player player, DebuggedMessage... messages) {
            this(player, new ArrayList<>(List.of(messages)));
            if (player.preassignedTeam() != null) {
                debuggedMessages.add(colors -> colors.gold() + "PREASSIGNED");
            }
        }

        public String getInfo(Color colors) {
            StringBuilder info = new StringBuilder(player.getInfo(colors));
            for (DebuggedMessage debuggedMessage : debuggedMessages) {
                info.append(colors.gray()).append(" (");
                info.append(debuggedMessage.getMessage(colors));
                info.append(colors.gray()).append(")");
            }
            return info.toString();
        }
    }

    /**
     * @param uuid   the uuid of the player - needed for equals and hashcode
     * @param spec   the specialization of the player
     * @param weight the weight of the player
     */
    record Player(UUID uuid, Specialization spec, double weight, Team preassignedTeam) {

        public Player(Specialization spec, double weight, Team preassignedTeam) {
            this(UUID.randomUUID(), spec, weight, preassignedTeam);
        }

        public Player(Specialization spec, double weight) {
            this(UUID.randomUUID(), spec, weight, null);
        }

        public String getInfo(Color colors) {
            return spec.specType.getColor.apply(colors) + spec + colors.gray() + " - " + colors.lightPurple() + WEIGHT_FORMAT.format(weight);
        }

        @Override
        public String toString() {
            return "Player{" +
                    "" + spec +
                    ", " + WEIGHT_FORMAT.format(weight) +
                    '}';
        }
    }

    /**
     * Class used as an interface to print debug messages
     */
    static final class Printer {
        private final Consumer<String> sendMessage;
        private final Color colors;
        private boolean enabled = true;

        /**
         * @param sendMessage the consumer to send messages to
         * @param colors      color interface to use for coloring messages
         */
        Printer(Consumer<String> sendMessage, Color colors) {
            this.sendMessage = sendMessage;
            this.colors = colors;
        }

        public void sendMessage(String message) {
            if (enabled) {
                sendMessage.accept(message);
            }
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Color colors() {
            return colors;
        }

    }

}
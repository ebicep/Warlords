package com.ebicep.warlords.pve.commands;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboard;
import com.ebicep.warlords.database.leaderboards.stats.StatsLeaderboardManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.database.repositories.player.pojos.pve.PvEDatabaseStatInformation;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.function.Function;

@CommandAlias("myposition|mp")
@Conditions("database:player")
public class MyPositionCommand extends BaseCommand {

    private static final StatLeaderboardTarget[] STAT_LEADERBOARD_TARGETS = new StatLeaderboardTarget[]{
            new StatLeaderboardTarget("Level", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getLevel())),
            new StatLeaderboardTarget("Kills", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getKills())),
            new StatLeaderboardTarget("Assists", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getAssists())),
            new StatLeaderboardTarget("Deaths", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getDeaths())),
            //new StatLeaderboardTarget("Waves Cleared", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getTotalWavesCleared())),
            new StatLeaderboardTarget("Games Cleared", pvEDatabaseStatInformation -> String.valueOf(pvEDatabaseStatInformation.getWins())),
            new StatLeaderboardTarget("Clear Rate", pvEDatabaseStatInformation -> NumberFormat.addCommaAndRound(pvEDatabaseStatInformation.getWinRate() * 100) + "%"),
    };

    private static void appendStats(
            StatLeaderboardTarget statLeaderboardTarget,
            DatabasePlayer databasePlayer,
            PvEDatabaseStatInformation statInformation,
            String prefix,
            StringBuilder stats
    ) {
        for (StatsLeaderboard statsLeaderboard : StatsLeaderboardManager.STATS_LEADERBOARDS.get(StatsLeaderboardManager.GameType.PVE)
                .getCategories()
                .get(0)
                .getStatsLeaderboards()
        ) {
            if ((prefix + statLeaderboardTarget.getLeaderboardName()).equals(statsLeaderboard.getTitle())) {
                stats.append("\n").append(ChatColor.GREEN).append(statLeaderboardTarget.getDisplayName())
                        .append(": ").append(statLeaderboardTarget.getStatFunction().apply(statInformation));
                int index = statsLeaderboard.getSortedPlayers(PlayersCollections.LIFETIME).indexOf(databasePlayer);
                if (index != -1) {
                    stats.append(ChatColor.YELLOW).append(" (#").append(index + 1).append(")");
                }
                break;
            }
        }
    }

    private static void appendStat(String title, DatabasePlayer databasePlayer, StringBuilder stats) {
        for (StatsLeaderboard statsLeaderboard : StatsLeaderboardManager.STATS_LEADERBOARDS.get(StatsLeaderboardManager.GameType.PVE)
                .getCategories()
                .get(0)
                .getStatsLeaderboards()
        ) {
            if (statsLeaderboard.getTitle().equals(title)) {
                stats.append("\n").append(ChatColor.GREEN).append(title)
                        .append(": ").append(statsLeaderboard.getStringFunction().apply(databasePlayer));
                int index = statsLeaderboard.getSortedPlayers(PlayersCollections.LIFETIME).indexOf(databasePlayer);
                if (index != -1) {
                    stats.append(ChatColor.YELLOW).append(" (#").append(index + 1).append(")");
                }
                break;
            }
        }
    }

    @Default
    @Description("Displays your general stats position in the leaderboards")
    public void myStats(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            DatabasePlayerPvE databasePlayerPvE = databasePlayer.getPveStats();

            StringBuilder stats = new StringBuilder(ChatColor.GOLD + "Your Stats");
            for (StatsLeaderboard statsLeaderboard : StatsLeaderboardManager.STATS_LEADERBOARDS.get(StatsLeaderboardManager.GameType.ALL)
                    .getCategories()
                    .get(0)
                    .getStatsLeaderboards()
            ) {
                if (statsLeaderboard.getTitle().equals("Experience")) {
                    stats.append("\n").append(ChatColor.GREEN).append("Level")
                            .append(": ").append(NumberFormat.addCommaAndRound(databasePlayer.getLevel()));
                    int index = statsLeaderboard.getSortedPlayers(PlayersCollections.LIFETIME).indexOf(databasePlayer);
                    if (index != -1) {
                        stats.append(ChatColor.YELLOW).append(" (#").append(index + 1).append(")");
                    }
                    break;
                }
            }
            for (int i = 0, size = STAT_LEADERBOARD_TARGETS.length; i < size; i++) {
                if (i == 0) {
                    continue;
                }
                StatLeaderboardTarget statLeaderboardTarget = STAT_LEADERBOARD_TARGETS[i];
                appendStats(statLeaderboardTarget, databasePlayer, databasePlayerPvE, "", stats);
            }

            ChatUtils.sendMessageToPlayer(player, stats.toString(), ChatColor.GREEN, true);
        });
    }

    @Subcommand("class")
    @CommandCompletion("@classesalias")
    @Description("Displays your class stats position in the leaderboards")
    public void myStatsClass(Player player, String className) {
        Classes classes = Classes.getClassFromNameNullable(className);
        if (classes == null) {
            throw new InvalidCommandArgument("Invalid Class");
        }
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            PvEDatabaseStatInformation databasePlayerPvE = databasePlayer.getPveStats().getClass(classes);

            StringBuilder stats = new StringBuilder(ChatColor.GOLD + "Your Stats (" + classes.name + ")");
            for (StatLeaderboardTarget statLeaderboardTarget : STAT_LEADERBOARD_TARGETS) {
                appendStats(statLeaderboardTarget, databasePlayer, databasePlayerPvE, "", stats);
            }
            ChatUtils.sendMessageToPlayer(player, stats.toString(), ChatColor.GREEN, true);
        });
    }

    @Subcommand("spec")
    @CommandCompletion("@specsalias")
    @Description("Displays your specialization stats position in the leaderboards")
    public void myStatsSpec(Player player, String specName) {
        Specializations specializations = Specializations.getSpecFromNameNullable(specName);
        if (specializations == null) {
            throw new InvalidCommandArgument("Invalid Specialization");
        }
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            PvEDatabaseStatInformation databasePlayerPvE = databasePlayer.getPveStats().getSpec(specializations);

            StringBuilder stats = new StringBuilder(ChatColor.GOLD + "Your Stats (" + specializations.name + ")");
            for (StatLeaderboardTarget statLeaderboardTarget : STAT_LEADERBOARD_TARGETS) {
                appendStats(statLeaderboardTarget, databasePlayer, databasePlayerPvE, "", stats);
            }
            ChatUtils.sendMessageToPlayer(player, stats.toString(), ChatColor.GREEN, true);
        });
    }

    @Subcommand("masterworksfair|mwf")
    public void myStatsMasterworksFair(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            StringBuilder stats = new StringBuilder(ChatColor.GOLD + "Your Stats (Masterworks Fair)");

            appendStat("Masterworks Fair Wins", databasePlayer, stats);
            for (WeaponsPvE value : WeaponsPvE.VALUES) {
                if (value.getPlayerEntries == null) {
                    continue;
                }
                appendStat("Masterworks Fair " + value.name + " Wins", databasePlayer, stats);
            }
            stats.append("\n");
            appendStat("Average Masterworks Fair Placement", databasePlayer, stats);
            for (WeaponsPvE value : WeaponsPvE.VALUES) {
                if (value.getPlayerEntries == null) {
                    continue;
                }
                appendStat("Average Masterworks Fair " + value.name + " Placement", databasePlayer, stats);
            }

            ChatUtils.sendMessageToPlayer(player, stats.toString().replaceAll("Masterworks Fair ", ""), ChatColor.GREEN, true);
        });
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

    static class StatLeaderboardTarget {
        private final String leaderboardName;
        private final String displayName;
        private final Function<PvEDatabaseStatInformation, String> statFunction;

        public StatLeaderboardTarget(String leaderboardName, Function<PvEDatabaseStatInformation, String> statFunction) {
            this.leaderboardName = leaderboardName;
            this.displayName = leaderboardName;
            this.statFunction = statFunction;
        }

        StatLeaderboardTarget(String leaderboardName, String displayName, Function<PvEDatabaseStatInformation, String> statFunction) {
            this.leaderboardName = leaderboardName;
            this.displayName = displayName;
            this.statFunction = statFunction;
        }

        public String getLeaderboardName() {
            return leaderboardName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Function<PvEDatabaseStatInformation, String> getStatFunction() {
            return statFunction;
        }
    }


}

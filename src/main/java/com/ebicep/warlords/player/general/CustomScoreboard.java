package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.leaderboards.Leaderboard;
import com.ebicep.warlords.database.leaderboards.LeaderboardManager;
import com.ebicep.warlords.database.leaderboards.sections.LeaderboardCategory;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.AbstractDatabaseStatInformation;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.database.leaderboards.LeaderboardManager.*;

public class CustomScoreboard {

    private static final String[] teamEntries = new String[]{"🎂", "🎉", "🎁", "👹", "🏀", "⚽", "🍭", "🌠", "👾", "🐍", "🔮", "👽", "💣", "🍫", "🔫", "🧭", "🧱", "💈", "🦽", "🦼"};
    private final Player player;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;

    public CustomScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();

        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");

        this.player = player;
        this.player.setScoreboard(scoreboard);
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Objective getHealth() {
        return health;
    }

    public void setHealth(Objective health) {
        this.health = health;
    }

    public void setSideBarTeamPrefixAndSuffix(int team, String prefix, String suffix) {
        if (prefix.length() > 16) {
            prefix = "Error";
        }
        if (suffix.length() > 16) {
            suffix = "Error";
        }
        scoreboard.getTeam("team_" + team).setPrefix(prefix);
        scoreboard.getTeam("team_" + team).setSuffix(suffix);
    }

    public void setSideBarTeam(int team, String entry) {
        if (entry.length() > 16) {
            if (entry.charAt(15) == '§') {
                scoreboard.getTeam("team_" + team).setPrefix(entry.substring(0, 15));
                if (entry.length() > 31) {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(15, 31));
                } else {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(15));
                }
            } else {
                scoreboard.getTeam("team_" + team).setPrefix(entry.substring(0, 16));
                if (entry.length() > 32) {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(16, 32));
                } else {
                    scoreboard.getTeam("team_" + team).setSuffix(entry.substring(16));
                }
            }
        } else {
            scoreboard.getTeam("team_" + team).setPrefix(entry);
            scoreboard.getTeam("team_" + team).setSuffix("");
        }
    }

    public void giveNewSideBar(boolean forceClear, List<String> entries) {
        // 0 is faster here than .size(), see https://stackoverflow.com/a/29444594/1542723
        giveNewSideBar(forceClear, entries.toArray(new String[0]));
    }
    public void giveNewSideBar(boolean forceClear, String... entries) {
        //clearing all teams if size doesnt match
        int sideBarTeams = (int) scoreboard.getTeams().stream().filter(team -> team.getName().contains("team")).count();
        if (forceClear || entries.length != sideBarTeams) {
            scoreboard.getTeams().forEach(Team::unregister);
            clearSideBar();

            //making new sidebar
            for (int i = 0; i < entries.length; i++) {
                Team tempTeam = scoreboard.registerNewTeam("team_" + (i + 1));
                tempTeam.addEntry(teamEntries[i]);
                sideBar.getScore(teamEntries[i]).setScore(i + 1);
            }
        }

        //giving prefix/suffix from pairs
        for (int i = entries.length; i > 0; i--) {
            String entry = entries[entries.length - i];
            setSideBarTeam(i, entry == null ? "" : entry);
        }
    }

    private void clearSideBar() {
        sideBar.unregister();
        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");
    }


    public void giveMainLobbyScoreboard() {
        if (scoreboard.getObjective("health") != null) {
            scoreboard.getObjective("health").unregister();
            health = null;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            for (Team team : scoreboard.getTeams()) {
                if (team.getName().equals(onlinePlayer.getName())) {
                    team.unregister();
                    break;
                }
            }
        }

        if (loaded) {
            LeaderboardCategory<?> leaderboardCategory = getLeaderboardCategoryFromPlayer(player);
            if (leaderboardCategory == null) return;

            Leaderboard leaderboard = leaderboardCategory.leaderboards.get(0);
            List<DatabasePlayer> databasePlayerList;
            switch (playerLeaderboardTime.getOrDefault(player.getUniqueId(), PlayersCollections.LIFETIME)) {
                case LIFETIME:
                    databasePlayerList = leaderboard.getSortedAllTime();
                    break;
                case SEASON_6:
                    databasePlayerList = leaderboard.getSortedSeason6();
                    break;
                case SEASON_5:
                    databasePlayerList = leaderboard.getSortedSeason5();
                    break;
                case SEASON_4:
                    databasePlayerList = leaderboard.getSortedSeason4();
                    break;
                case WEEKLY:
                    databasePlayerList = leaderboard.getSortedWeekly();
                    break;
                case DAILY:
                    databasePlayerList = leaderboard.getSortedDaily();
                    break;
                default:
                    databasePlayerList = leaderboard.getSortedAllTime();
                    break;
            }
            LeaderboardManager.GameType selectedType = playerLeaderboardGameType.get(player.getUniqueId());
            LeaderboardManager.Category selectedCategory = playerLeaderboardCategory.get(player.getUniqueId());
            PlayersCollections selectedCollection = playerLeaderboardTime.get(player.getUniqueId());
            if (selectedType == null) selectedType = GameType.ALL;
            if (selectedCollection == null) selectedCategory = Category.ALL;
            if (selectedCollection == null) selectedCollection = PlayersCollections.LIFETIME;

            String scoreboardSelection = "";
            if (!selectedType.shortName.isEmpty()) {
                scoreboardSelection += selectedType.shortName + "/";
            }
            if (!selectedCategory.shortName.isEmpty()) {
                scoreboardSelection += selectedCategory.shortName + "/";
            }
            scoreboardSelection += selectedCollection.name;

            Optional<DatabasePlayer> optionalDatabasePlayer = databasePlayerList.stream()
                    .filter(databasePlayer -> databasePlayer.getUuid().equalsIgnoreCase(player.getUniqueId().toString()))
                    .findAny();
            if (optionalDatabasePlayer.isPresent()) {
                DatabasePlayer databasePlayer = optionalDatabasePlayer.get();
                AbstractDatabaseStatInformation playerInformation = leaderboardCategory.statFunction.apply(databasePlayer);
                giveNewSideBar(true,
                        ChatColor.GRAY + scoreboardSelection,
                        "",
                        "Kills: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(playerInformation.getKills()),
                        "Assists: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(playerInformation.getAssists()),
                        "Deaths: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(playerInformation.getDeaths()),
                        " " + "",
                        "Wins: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(playerInformation.getWins()),
                        "Losses: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(playerInformation.getLosses()),
                        "  " + "",
                        "Damage: " + ChatColor.RED + NumberFormat.addCommaAndRound(playerInformation.getDamage()),
                        "Healing: " + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(playerInformation.getHealing()),
                        "Absorbed: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(playerInformation.getAbsorbed()),
                        "    ",
                        "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                        "  " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + Warlords.VERSION
                );
            } else {
                giveNASidebar(scoreboardSelection);
            }
            return;
        }
        if (DatabaseManager.playerService == null) {
            giveNASidebar("Lifetime");
            return;
        }
        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
        giveNewSideBar(true,
                ChatColor.GRAY + "Lifetime",
                " ",
                "Kills: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(databasePlayer.getKills()),
                "Assists: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(databasePlayer.getAssists()),
                "Deaths: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(databasePlayer.getDeaths()),
                " " + "",
                "Wins: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(databasePlayer.getWins()),
                "Losses: " + ChatColor.GREEN + NumberFormat.addCommaAndRound(databasePlayer.getLosses()),
                "  " + "",
                "Damage: " + ChatColor.RED + NumberFormat.addCommaAndRound(databasePlayer.getDamage()),
                "Healing: " + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(databasePlayer.getHealing()),
                "Absorbed: " + ChatColor.GOLD + NumberFormat.addCommaAndRound(databasePlayer.getAbsorbed()),
                "    ",
                "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                "  " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + Warlords.VERSION
        );
    }

    private void giveNASidebar(String title) {
        giveNewSideBar(true,
                ChatColor.GRAY + title,
                " ",
                "Kills: " + ChatColor.GREEN + "N/A",
                "Assists: " + ChatColor.GREEN + "N/A",
                "Deaths: " + ChatColor.GREEN + "N/A",
                " " + "",
                "Wins: " + ChatColor.GREEN + "N/A",
                "Losses: " + ChatColor.GREEN + "N/A",
                "  " + "",
                "Damage: " + ChatColor.RED + "N/A",
                "Healing: " + ChatColor.DARK_GREEN + "N/A",
                "Absorbed: " + ChatColor.GOLD + "N/A",
                "    ",
                "            " + ChatColor.WHITE + ChatColor.BOLD + "Update",
                "  " + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + Warlords.VERSION
        );
    }
}
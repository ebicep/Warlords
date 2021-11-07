package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class CustomScoreboard {

    private final Player player;
    private final Scoreboard scoreboard;
    private Objective sideBar;
    private Objective health;
    private static final String[] teamEntries = new String[]{"🎂", "🎉", "🎁", "👹", "🏀", "⚽", "🍭", "🌠", "👾", "🐍", "🔮", "👽", "💣", "🍫", "🔫"};

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
        if(prefix.length() > 16) {
            prefix = "Error";
        }
        if(suffix.length() > 16) {
            suffix = "Error";
        }
        scoreboard.getTeam("team_" + team).setPrefix(prefix);
        scoreboard.getTeam("team_" + team).setSuffix(suffix);
    }

    public void giveNewSideBar(boolean forceClear, CustomScoreboardPair... pairs) {
        //clearing all teams if size doesnt match
        int sideBarTeams = (int) scoreboard.getTeams().stream().filter(team -> team.getName().contains("team")).count();
        if(forceClear || pairs.length != sideBarTeams) {
            scoreboard.getTeams().forEach(Team::unregister);
            clearSideBar();

            //making new sidebar
            for (int i = 0; i < pairs.length; i++) {
                Team tempTeam = scoreboard.registerNewTeam("team_" + (i + 1));
                tempTeam.addEntry(teamEntries[i]);
                sideBar.getScore(teamEntries[i]).setScore(i + 1);
            }
        }

        //giving prefix/suffix from pairs
        for (int i = pairs.length; i > 0; i--) {
            CustomScoreboardPair pair = pairs[pairs.length - i];
            setSideBarTeamPrefixAndSuffix(i, pair.getPrefix(), pair.getSuffix());
        }
    }

    private void clearSideBar() {
        sideBar.unregister();
        sideBar = scoreboard.registerNewObjective("WARLORDS", "dummy");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS 2.0");
    }


    public void giveMainLobbyScoreboard() {
        if (!DatabaseManager.connected) return;
        if(scoreboard.getObjective("health") != null) {
            scoreboard.getObjective("health").unregister();
            health = null;
        }
        Warlords.newChain()
                .async(() -> DatabaseManager.addPlayer(player.getUniqueId(), false))
                .sync(() -> {
                    giveNewSideBar(true,
                            new CustomScoreboardPair("", ""),
                            new CustomScoreboardPair("Kills: ", ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "kills")))),
                            new CustomScoreboardPair("Assists: ", ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "assists")))),
                            new CustomScoreboardPair("Deaths: ", ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "deaths")))),
                            new CustomScoreboardPair(" ", ""),
                            new CustomScoreboardPair("Wins: ", ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "wins")))),
                            new CustomScoreboardPair("Losses: ", ChatColor.GREEN + Utils.addCommaAndRound(((Integer) DatabaseManager.getPlayerInfoWithDotNotation(player, "losses")))),
                            new CustomScoreboardPair("  ", ""),
                            new CustomScoreboardPair("Damage: ", ChatColor.RED + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "damage")).doubleValue())),
                            new CustomScoreboardPair("Healing: ", ChatColor.DARK_GREEN + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "healing")).doubleValue())),
                            new CustomScoreboardPair("Absorbed: ", ChatColor.GOLD + Utils.addCommaAndRound(((Number) DatabaseManager.getPlayerInfoWithDotNotation(player, "absorbed")).doubleValue())),
                            new CustomScoreboardPair("    ", ""),
                            new CustomScoreboardPair("    ", ""),
                            new CustomScoreboardPair("          ", "§e§lUpdate"),
                            new CustomScoreboardPair("    ", ChatColor.GREEN.toString() + ChatColor.BOLD + Warlords.VERSION)
                    );
                }).execute();
    }
}
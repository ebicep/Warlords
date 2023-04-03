package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Document(collection = "Games_Information_PvE")
public class DatabaseGamePvE extends DatabaseGameBase implements WavesCleared, TimeElapsed, Difficulty {

    private DifficultyIndex difficulty;
    @Field("waves_cleared")
    private int wavesCleared;
    @Field("time_elapsed")
    private int timeElapsed;
    @Field("total_mobs_killed")
    private int totalMobsKilled;
    private List<DatabaseGamePlayerPvE> players = new ArrayList<>();

    public DatabaseGamePvE() {

    }

    public DatabaseGamePvE(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        //this.difficulty =
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption) {
                PveOption pveOption = (PveOption) option;
                this.difficulty = pveOption.getDifficulty();
                if (option instanceof WaveDefenseOption) {
                    WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                    this.wavesCleared = waveDefenseOption.getWavesCleared();
                }
                game.warlordsPlayers().forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvE(warlordsPlayer, pveOption)));
            }
        }
        this.timeElapsed = RecordTimeElapsedOption.getTicksElapsed(game);
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvE -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvE,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvE.getName());
        });
    }

    @Override
    public Set<DatabaseGamePlayerBase> getBasePlayers() {
        return new HashSet<>(players);
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return wavesCleared >= difficulty.getMaxWaves() ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        HologramLines hologramLines = hologram.getLines();
        hologramLines.appendText(ChatColor.GRAY + date);
        hologramLines.appendText(ChatColor.GREEN + map.getMapName() + " - " + Utils.formatTimeLeft(timeElapsed / 20));
        hologramLines.appendText(ChatColor.YELLOW + difficulty.getName() + " Waves Cleared: " + wavesCleared +
                (difficulty.getMaxWaves() != Integer.MAX_VALUE ? ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves() : ""));

    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DHP_PER_MINUTE_LOCATION);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        Hologram mobKills = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DAMAGE_ON_CARRIER_LOCATION);
        holograms.add(mobKills);
        mobKills.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Mob Kills");

        Hologram mobDeaths = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_HEALING_ON_CARRIER_LOCATION);
        holograms.add(mobDeaths);
        mobDeaths.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Mob Deaths");

        int minutes = (timeElapsed / 1200) == 0 ? 1 : (timeElapsed / 1200);

        List<DatabaseGamePlayerPvE> allPlayers = players;
        List<String> topDHPPerGamePlayers = new ArrayList<>();


        allPlayers.stream().sorted((o1, o2) -> {
            Long p1DHPPerGame = o1.getTotalDHP() / minutes;
            Long p2DHPPerGame = o2.getTotalDHP() / minutes;
            return p2DHPPerGame.compareTo(p1DHPPerGame);
        }).forEach(databaseGamePlayer -> {
            topDHPPerGamePlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
        });

        topDHPPerGamePlayers.forEach(s -> topDHPPerMinute.getLines().appendText(s));

        LinkedHashMap<String, Long> mobKillsMap = new LinkedHashMap<>();
        LinkedHashMap<String, Long> mobDeathsMap = new LinkedHashMap<>();
        for (DatabaseGamePlayerPvE playerPvE : allPlayers) {
            playerPvE.getMobKills().forEach((s, aLong) -> mobKillsMap.merge(s, aLong, Long::sum));
            playerPvE.getMobDeaths().forEach((s, aLong) -> mobDeathsMap.merge(s, aLong, Long::sum));
        }

        mobKillsMap.forEach((mob, aLong) -> mobKills.getLines()
                                                    .appendText(ChatColor.RED + mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));
        mobDeathsMap.forEach((mob, aLong) -> mobDeaths.getLines()
                                                      .appendText(ChatColor.RED + mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));
    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.YELLOW + "Waves Cleared: " + wavesCleared + ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves() + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();

    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return Team.BLUE;
    }

    @Override
    public List<String> getExtraLore() {
        return Arrays.asList(
                ChatColor.GRAY + "Time Elapsed: " + ChatColor.YELLOW + Utils.formatTimeLeft(timeElapsed),
                ChatColor.GRAY + "Waves Cleared: " + ChatColor.YELLOW + wavesCleared,
                ChatColor.GRAY + "Total Mobs Killed: " + ChatColor.YELLOW + totalMobsKilled,
                ChatColor.GRAY + "Players: " + ChatColor.YELLOW + players.size()
        );
    }

    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    public int getWavesCleared() {
        return wavesCleared;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    public int getTotalMobsKilled() {
        return totalMobsKilled;
    }

    public List<DatabaseGamePlayerPvE> getPlayers() {
        return players;
    }
}

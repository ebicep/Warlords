package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.Utils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.VisibilitySettings;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Document(collection = "Games_Information_PvE")
public class DatabaseGamePvE extends DatabaseGameBase {

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
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                this.difficulty = waveDefenseOption.getDifficulty();
                this.wavesCleared = waveDefenseOption.getWavesCleared();
                game.warlordsPlayers().forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvE(warlordsPlayer, waveDefenseOption)));
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
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return wavesCleared >= difficulty.getMaxWaves() ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
    }

    @Override
    public void createHolograms() {
        List<Hologram> holograms = new ArrayList<>();

        //readding game holograms
        Hologram lastGameStats = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.LAST_GAME_STATS_LOCATION);
        holograms.add(lastGameStats);
        lastGameStats.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Last " + (isPrivate() ? "Comp" : "Pub") + " Game Stats");

        Hologram topDamage = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DAMAGE_LOCATION);
        holograms.add(topDamage);
        topDamage.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Damage");

        Hologram topHealing = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_HEALING_LOCATION);
        holograms.add(topHealing);
        topHealing.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Healing");

        Hologram topAbsorbed = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_ABSORBED_LOCATION);
        holograms.add(topAbsorbed);
        topAbsorbed.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top Absorbed");

        Hologram topDHPPerMinute = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DHP_PER_MINUTE_LOCATION);
        holograms.add(topDHPPerMinute);
        topDHPPerMinute.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Top DHP per Minute");

        Hologram mobKills = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_DAMAGE_ON_CARRIER_LOCATION);
        holograms.add(mobKills);
        mobKills.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Mob Kills");

        Hologram mobDeaths = HolographicDisplaysAPI.get(Warlords.getInstance()).createHologram(DatabaseGameBase.TOP_HEALING_ON_CARRIER_LOCATION);
        holograms.add(mobDeaths);
        mobDeaths.getLines().appendText(ChatColor.AQUA + ChatColor.BOLD.toString() + "Mob Deaths");

        //last game stats
        int minutes = (timeElapsed / 1200) == 0 ? 1 : (timeElapsed / 1200);
        lastGameStats.getLines().appendText(ChatColor.GRAY + date);
        lastGameStats.getLines()
                .appendText(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + Utils.formatTimeLeft(timeElapsed / 20));
        lastGameStats.getLines()
                .appendText(ChatColor.YELLOW + "Waves Cleared: " + wavesCleared + ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves());


        List<DatabaseGamePlayerPvE> allPlayers = players;
        List<String> players = new ArrayList<>();

        for (String s : Utils.specsOrdered) {
            StringBuilder playerSpecs = new StringBuilder(ChatColor.AQUA + s).append(": ");
            final boolean[] add = {false};
            allPlayers.stream().filter(o -> o.getSpec().name.equalsIgnoreCase(s)).forEach(p -> {
                playerSpecs.append(ChatColor.BLUE).append(p.getName()).append(p.getKDAString()).append(ChatColor.GRAY).append(", ");
                add[0] = true;
            });
            if (add[0]) {
                playerSpecs.setLength(playerSpecs.length() - 2);
                players.add(playerSpecs.toString());
            }
        }
        players.forEach(s -> lastGameStats.getLines().appendText(s));

        //top dmg/healing/absorbed + dhp per game
        List<String> topDamagePlayers = new ArrayList<>();
        List<String> topHealingPlayers = new ArrayList<>();
        List<String> topAbsorbedPlayers = new ArrayList<>();
        List<String> topDHPPerGamePlayers = new ArrayList<>();

        Map<ChatColor, Long> totalDamage = new HashMap<>();
        Map<ChatColor, Long> totalHealing = new HashMap<>();
        Map<ChatColor, Long> totalAbsorbed = new HashMap<>();

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayerPvE::getTotalDamage).reversed()).forEach(databaseGamePlayer -> {
            totalDamage.put(ChatColor.BLUE, totalDamage.getOrDefault(ChatColor.BLUE, 0L) + databaseGamePlayer.getTotalDamage());
            topDamagePlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDamage()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayerPvE::getTotalHealing).reversed()).forEach(databaseGamePlayer -> {
            totalHealing.put(ChatColor.BLUE, totalHealing.getOrDefault(ChatColor.BLUE, 0L) + databaseGamePlayer.getTotalHealing());
            topHealingPlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalHealing()));
        });

        allPlayers.stream().sorted(Comparator.comparingLong(DatabaseGamePlayerPvE::getTotalAbsorbed).reversed()).forEach(databaseGamePlayer -> {
            totalAbsorbed.put(ChatColor.BLUE, totalAbsorbed.getOrDefault(ChatColor.BLUE, 0L) + databaseGamePlayer.getTotalAbsorbed());
            topAbsorbedPlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalAbsorbed()));
        });

        allPlayers.stream().sorted((o1, o2) -> {
            Long p1DHPPerGame = o1.getTotalDHP() / minutes;
            Long p2DHPPerGame = o2.getTotalDHP() / minutes;
            return p2DHPPerGame.compareTo(p1DHPPerGame);
        }).forEach(databaseGamePlayer -> {
            topDHPPerGamePlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
        });


        appendTeamDHP(topDamage, totalDamage);
        appendTeamDHP(topHealing, totalHealing);
        appendTeamDHP(topAbsorbed, totalAbsorbed);

        topDamagePlayers.forEach(s -> topDamage.getLines().appendText(s));
        topHealingPlayers.forEach(s -> topHealing.getLines().appendText(s));
        topAbsorbedPlayers.forEach(s -> topAbsorbed.getLines().appendText(s));
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

        //setting visibility to none
        holograms.forEach(hologram -> {
            hologram.getVisibilitySettings().setGlobalVisibility(VisibilitySettings.Visibility.HIDDEN);
        });

        this.holograms = holograms;
    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.YELLOW + "Waves Cleared: " + wavesCleared + ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves() + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();

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

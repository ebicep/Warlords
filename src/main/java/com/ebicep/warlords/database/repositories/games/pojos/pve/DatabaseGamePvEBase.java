package com.ebicep.warlords.database.repositories.games.pojos.pve;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.DifficultyIndex;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Display;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class DatabaseGamePvEBase<T extends DatabaseGamePlayerPvEBase> extends DatabaseGameBase<T> implements TimeElapsed, Difficulty {

    protected DifficultyIndex difficulty;
    @Field("time_elapsed")
    protected int timeElapsed;
    @Field("total_mobs_killed")
    protected int totalMobsKilled;

    public DatabaseGamePvEBase() {

    }

    public DatabaseGamePvEBase(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, counted);
        //this.difficulty =
        for (Option option : game.getOptions()) {
            if (option instanceof PveOption pveOption) {
                this.difficulty = pveOption.getDifficulty();
            }
        }
        this.timeElapsed = RecordTimeElapsedOption.getTicksElapsed(game);
    }

    @Override
    public void appendLastGameStats(ComponentBuilder componentBuilder) {
        componentBuilder.newLine(ChatColor.GRAY + date);
        componentBuilder.newLine(ChatColor.GREEN + map.getMapName() + " - " + StringUtils.formatTimeLeft(timeElapsed / 20));
    }

    @Override
    public abstract Set<T> getBasePlayers();

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return Team.BLUE;
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        ComponentBuilder topDHPPerMinuteComponent = ComponentBuilder.create("Top DHP per Minute", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder mobKillsComponent = ComponentBuilder.create("Mob Kills", NamedTextColor.AQUA, TextDecoration.BOLD);
        ComponentBuilder mobDeathsComponent = ComponentBuilder.create("Mob Deaths", NamedTextColor.AQUA, TextDecoration.BOLD);

        int minutes = (timeElapsed / 1200) == 0 ? 1 : (timeElapsed / 1200);

        Set<T> allPlayers = getBasePlayers();
        List<String> topDHPPerGamePlayers = new ArrayList<>();


        allPlayers.stream().sorted((o1, o2) -> {
            Long p1DHPPerGame = o1.getTotalDHP() / minutes;
            Long p2DHPPerGame = o2.getTotalDHP() / minutes;
            return p2DHPPerGame.compareTo(p1DHPPerGame);
        }).forEach(databaseGamePlayer -> {
            topDHPPerGamePlayers.add(ChatColor.BLUE + databaseGamePlayer.getName() + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(databaseGamePlayer.getTotalDHP() / minutes));
        });

        topDHPPerGamePlayers.forEach(topDHPPerMinuteComponent::newLine);

        LinkedHashMap<String, Long> mobKillsMap = new LinkedHashMap<>();
        LinkedHashMap<String, Long> mobDeathsMap = new LinkedHashMap<>();
        for (DatabaseGamePlayerPvEBase playerPvE : allPlayers) {
            playerPvE.getMobKills().forEach((s, aLong) -> mobKillsMap.merge(s, aLong, Long::sum));
            playerPvE.getMobDeaths().forEach((s, aLong) -> mobDeathsMap.merge(s, aLong, Long::sum));
        }

        mobKillsMap.forEach((mob, aLong) -> mobKillsComponent.newLine(ChatColor.RED + mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));
        mobDeathsMap.forEach((mob, aLong) -> mobDeathsComponent.newLine(ChatColor.RED + mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));

        HologramDataText topDHPPerMinuteData = new HologramDataText.Builder<>(topDHPPerMinuteComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram topDHPPerMinute = new Hologram.Builder("topDHPPerMinute" + exactDate,
                TOP_DHP_PER_MINUTE_LOCATION,
                p -> topDHPPerMinuteData
        ).build();
        HologramManager.addHologram(topDHPPerMinute);

        HologramDataText mobKillsData = new HologramDataText.Builder<>(mobKillsComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram mobKills = new Hologram.Builder("mobKills" + exactDate,
                TOP_DAMAGE_ON_CARRIER_LOCATION,
                p -> mobKillsData
        ).build();
        HologramManager.addHologram(mobKills);

        HologramDataText mobDeathsData = new HologramDataText.Builder<>(mobDeathsComponent.build())
                .setBillboard(Display.Billboard.FIXED)
                .build();
        Hologram mobDeaths = new Hologram.Builder("mobDeaths" + exactDate,
                TOP_HEALING_ON_CARRIER_LOCATION,
                p -> mobDeathsData
        ).build();
        HologramManager.addHologram(mobDeaths);
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase<T> databaseGame, int multiplier) {
        getBasePlayers().forEach(databaseGamePlayerPvE -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvE,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvE.getName());
        });
    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY;
    }

    @Override
    public List<Component> getExtraLore() {
        return Arrays.asList(
                Component.text("Time Elapsed: ", NamedTextColor.GRAY)
                         .append(Component.text(StringUtils.formatTimeLeft(timeElapsed), NamedTextColor.GREEN)),
                Component.text("Total Mobs Killed: ", NamedTextColor.GRAY)
                         .append(Component.text(totalMobsKilled, NamedTextColor.YELLOW)),
                Component.text("Players: ", NamedTextColor.GRAY)
                         .append(Component.text(getBasePlayers().size(), NamedTextColor.YELLOW))
        );
    }

    @Override
    public DifficultyIndex getDifficulty() {
        return difficulty;
    }

    @Override
    public int getTimeElapsed() {
        return timeElapsed;
    }

    public int getTotalMobsKilled() {
        return totalMobsKilled;
    }

}

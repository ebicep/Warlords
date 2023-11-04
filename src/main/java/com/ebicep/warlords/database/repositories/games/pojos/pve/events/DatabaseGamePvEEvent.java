package com.ebicep.warlords.database.repositories.games.pojos.pve.events;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.TimeElapsed;
import com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense.DatabaseGamePlayerPvEWaveDefense;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.RecordTimeElapsedOption;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.java.StringUtils;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.HologramLines;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Document(collection = "Games_Information_Event_PvE")
public abstract class DatabaseGamePvEEvent extends DatabaseGameBase implements TimeElapsed {

    @Field("time_elapsed")
    private int timeElapsed;

    public DatabaseGamePvEEvent() {
    }

    public DatabaseGamePvEEvent(@Nonnull Game game, boolean counted) {
        super(game, counted);
        this.timeElapsed = RecordTimeElapsedOption.getTicksElapsed(game);
    }

    public abstract GameEvents getEvent();

    public int getPointLimit() {
        return 100_000;
    }

    public int getTimeElapsed() {
        return timeElapsed;
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        HologramLines hologramLines = hologram.getLines();
        hologramLines.appendText(ChatColor.GRAY + date);
        hologramLines.appendText(ChatColor.GREEN + map.getMapName() + ChatColor.GRAY + "  -  " + ChatColor.GREEN + StringUtils.formatTimeLeft(timeElapsed / 20));
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

        List<DatabaseGamePlayerPvEEvent> allPlayers = getPlayers();
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
        for (DatabaseGamePlayerPvEWaveDefense playerPvE : allPlayers) {
            playerPvE.getMobKills().forEach((s, aLong) -> mobKillsMap.merge(s, aLong, Long::sum));
            playerPvE.getMobDeaths().forEach((s, aLong) -> mobDeathsMap.merge(s, aLong, Long::sum));
        }

        mobKillsMap.forEach((mob, aLong) -> mobKills.getLines()
                                                    .appendText(mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));
        mobDeathsMap.forEach((mob, aLong) -> mobDeaths.getLines()
                                                      .appendText(mob + ": " + ChatColor.YELLOW + NumberFormat.addCommaAndRound(aLong)));
    }

    @Override
    public Team getTeam(DatabaseGamePlayerBase player) {
        return Team.BLUE;
    }

    @Override
    public String getGameLabel() {
        return ChatColor.GRAY + date + ChatColor.DARK_GRAY + " - " +
                ChatColor.GREEN + map + ChatColor.DARK_GRAY + " - " +
                ChatColor.YELLOW + "Time Elapsed: " + timeElapsed + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();
    }

    @Override
    public List<Component> getExtraLore() {
        return Arrays.asList(
                Component.text("Time Elapsed: ", NamedTextColor.GRAY)
                         .append(Component.text(StringUtils.formatTimeLeft(timeElapsed), NamedTextColor.GREEN)),
                Component.text("Players: ", NamedTextColor.GRAY)
                         .append(Component.text(getPlayers().size(), NamedTextColor.YELLOW))
        );
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return DatabaseGamePlayerResult.NONE;
    }

    public abstract List<DatabaseGamePlayerPvEEvent> getPlayers();

}

package com.ebicep.warlords.database.repositories.games.pojos.pve.anomaly;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.anomaly.AbstractAnomalyOption;
import com.ebicep.warlords.game.option.pve.anomaly.Anomalies;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "Games_Information_Anomaly")
public class DatabaseGamePvEAnomaly extends DatabaseGamePvEBase<DatabaseGamePlayerPvEAnomaly> {

    @Field("anomaly_name")
    private String anomalyName;
    @Field("objectives_completed")
    private int objectivesCompleted;
    @Field("anomaly_completed")
    private boolean anomalyCompleted;
    protected List<DatabaseGamePlayerPvEAnomaly> players = new ArrayList<>();

    public DatabaseGamePvEAnomaly() {
    }

    private DatabaseGamePvEAnomaly(
            @Nonnull Game game,
            @Nullable WarlordsGameTriggerWinEvent gameWinEvent,
            boolean counted,
            @Nonnull AbstractAnomalyOption anomalyOption
    ) {
        super(game, gameWinEvent, counted);
        this.anomalyName = anomalyOption.getCurrentAnomaly().name();
        this.objectivesCompleted = anomalyOption.getObjectivesCompleted();
        this.anomalyCompleted = anomalyOption.isCompleted();
        game.warlordsPlayers().forEach(warlordsPlayer ->
                players.add(new DatabaseGamePlayerPvEAnomaly(warlordsPlayer, gameWinEvent, anomalyOption, counted))
        );
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Nullable
    public static DatabaseGamePvEAnomaly createValidated(
            @Nonnull Game game,
            @Nullable WarlordsGameTriggerWinEvent gameWinEvent,
            boolean counted
    ) {
        List<AbstractAnomalyOption> anomalyOptions = game.getOptions()
                .stream()
                .filter(AbstractAnomalyOption.class::isInstance)
                .map(AbstractAnomalyOption.class::cast)
                .toList();
        if (anomalyOptions.size() != 1 || game.warlordsPlayers().findAny().isEmpty()) {
            return null;
        }
        AbstractAnomalyOption anomalyOption = anomalyOptions.getFirst();
        if (anomalyOption.getCurrentAnomaly() == null
                || anomalyOption.getObjectivesCompleted() < 0
                || anomalyOption.getObjectivesCompleted() > 3) {
            return null;
        }
        DatabaseGamePvEAnomaly databaseGame = new DatabaseGamePvEAnomaly(game, gameWinEvent, counted, anomalyOption);
        long uniquePlayers = databaseGame.players.stream()
                .map(DatabaseGamePlayerPvEAnomaly::getUuid)
                .distinct()
                .count();
        return uniquePlayers == databaseGame.players.size() ? databaseGame : null;
    }

    @Override
    public Set<DatabaseGamePlayerPvEAnomaly> getBasePlayers() {
        return new HashSet<>(players);
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return anomalyCompleted ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
    }

    @Override
    public void appendLastGameStats(ComponentBuilder componentBuilder) {
        super.appendLastGameStats(componentBuilder);
        componentBuilder.newLine(ChatColor.YELLOW + String.valueOf(objectivesCompleted)
                + ChatColor.GRAY + "/3 objectives");
    }

    @Override
    public List<Component> getExtraLore() {
        List<Component> lore = new ArrayList<>(super.getExtraLore());
        lore.add(Component.text("Anomaly: ", NamedTextColor.GRAY)
                .append(Component.text(getDisplayAnomalyName(), NamedTextColor.AQUA)));
        lore.add(Component.text("Objectives completed: ", NamedTextColor.GRAY)
                .append(Component.text(objectivesCompleted + "/3", NamedTextColor.YELLOW)));
        return lore;
    }

    private String getDisplayAnomalyName() {
        try {
            return Anomalies.valueOf(anomalyName).getName();
        } catch (IllegalArgumentException ignored) {
            return anomalyName;
        }
    }

    public String getAnomalyName() {
        return anomalyName;
    }

    public int getObjectivesCompleted() {
        return objectivesCompleted;
    }

    public boolean isAnomalyCompleted() {
        return anomalyCompleted;
    }
}

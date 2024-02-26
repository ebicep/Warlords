package com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.boltarobonanza;

import com.ebicep.warlords.commands.debugcommands.misc.GamesCommand;
import com.ebicep.warlords.database.repositories.events.pojos.GameEvents;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGameBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.events.boltaro.DatabaseGamePvEEventBoltaro;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.EventPointsOption;
import com.ebicep.warlords.game.option.pve.wavedefense.events.modes.BoltaroBonanzaOption;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DatabaseGamePvEEventBoltaroBonanza extends DatabaseGamePvEEventBoltaro<DatabaseGamePlayerPvEEventBoltaroBonanza> {

    @Field("highest_split")
    private int highestSplit;
    @Field("total_mobs_killed")
    private int totalMobsKilled;
    private List<DatabaseGamePlayerPvEEventBoltaroBonanza> players = new ArrayList<>();

    public DatabaseGamePvEEventBoltaroBonanza() {
    }

    public DatabaseGamePvEEventBoltaroBonanza(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
        AtomicReference<WaveDefenseOption> waveDefenseOption = new AtomicReference<>();
        AtomicReference<EventPointsOption> eventPointsOption = new AtomicReference<>();
        AtomicReference<BoltaroBonanzaOption> boltaroBonanzaOption = new AtomicReference<>();
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                waveDefenseOption.set((WaveDefenseOption) option);
            } else if (option instanceof EventPointsOption) {
                eventPointsOption.set((EventPointsOption) option);
            } else if (option instanceof BoltaroBonanzaOption) {
                boltaroBonanzaOption.set((BoltaroBonanzaOption) option);
            }
        }
        if (waveDefenseOption.get() == null || eventPointsOption.get() == null || boltaroBonanzaOption.get() == null) {
            throw new IllegalStateException("Missing option");
        }
        game.warlordsPlayers()
            .forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEEventBoltaroBonanza(warlordsPlayer,
                    gameWinEvent, waveDefenseOption.get(),
                    eventPointsOption.get(),
                    counted
            )));
        this.highestSplit = boltaroBonanzaOption.get().getHighestSplitValue();
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Override
    public GameEvents getEvent() {
        return GameEvents.BOLTARO;
    }

    @Override
    public int getPointLimit() {
        return 15_000;
    }

    @Override
    public void updatePlayerStatsFromGame(DatabaseGameBase<DatabaseGamePlayerPvEEventBoltaroBonanza> databaseGame, int multiplier) {
        players.forEach(databaseGamePlayerPvEEventBoltaroBonanza -> {
            DatabaseGameBase.updatePlayerStatsFromTeam(databaseGame,
                    databaseGamePlayerPvEEventBoltaroBonanza,
                    multiplier
            );
            GamesCommand.PLAYER_NAMES.add(databaseGamePlayerPvEEventBoltaroBonanza.getName());
        });
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        super.appendLastGameStats(hologram);
        hologram.getLines().appendText(ChatColor.YELLOW + "Highest Split: " + highestSplit);
    }

    @Override
    public void addCustomHolograms(List<Hologram> holograms) {
        super.addCustomHolograms(holograms);

    }

    @Override
    public String getGameLabel() {
        return super.getGameLabel() + " - " + ChatColor.YELLOW + highestSplit;
    }

    @Override
    public List<Component> getExtraLore() {
        List<Component> extraLore = super.getExtraLore();
        extraLore.add(Component.text("Highest Split: ", NamedTextColor.GRAY)
                               .append(Component.text(highestSplit, NamedTextColor.YELLOW))
        );
        return extraLore;
    }

    @Override
    public List<DatabaseGamePlayerPvEEventBoltaroBonanza> getPlayers() {
        return new ArrayList<>(players);
    }

    @Override
    public Set<DatabaseGamePlayerPvEEventBoltaroBonanza> getBasePlayers() {
        return new HashSet<>(players);
    }

    public int getHighestSplit() {
        return highestSplit;
    }

}

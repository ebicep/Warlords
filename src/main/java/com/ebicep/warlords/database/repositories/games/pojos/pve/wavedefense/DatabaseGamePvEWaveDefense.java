package com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerBase;
import com.ebicep.warlords.database.repositories.games.pojos.DatabaseGamePlayerResult;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.WavesCleared;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "Games_Information_Wave_Defense")
public class DatabaseGamePvEWaveDefense extends DatabaseGamePvEBase implements WavesCleared {

    @Field("waves_cleared")
    private int wavesCleared;
    protected List<DatabaseGamePlayerPvEWaveDefense> players = new ArrayList<>();
    public DatabaseGamePvEWaveDefense() {

    }

    public DatabaseGamePvEWaveDefense(@Nonnull Game game, @Nullable WarlordsGameTriggerWinEvent gameWinEvent, boolean counted) {
        super(game, gameWinEvent, counted);
        //this.difficulty =
        for (Option option : game.getOptions()) {
            if (option instanceof WaveDefenseOption) {
                WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                this.wavesCleared = waveDefenseOption.getWavesCleared();
                game.warlordsPlayers().forEach(warlordsPlayer -> players.add(new DatabaseGamePlayerPvEWaveDefense(warlordsPlayer, waveDefenseOption)));
            }
        }
        this.totalMobsKilled = players.stream().mapToInt(DatabaseGamePlayerBase::getTotalKills).sum();
    }

    @Override
    public Set<DatabaseGamePlayerPvEBase> getBasePlayers() {
        return new HashSet<>(players);
    }

    @Override
    public DatabaseGamePlayerResult getPlayerGameResult(DatabaseGamePlayerBase player) {
        return wavesCleared >= difficulty.getMaxWaves() ? DatabaseGamePlayerResult.WON : DatabaseGamePlayerResult.LOST;
    }

    @Override
    public void appendLastGameStats(Hologram hologram) {
        super.appendLastGameStats(hologram);
        hologram.getLines().appendText(ChatColor.YELLOW + difficulty.getName() + " Waves Cleared: " + wavesCleared +
                (difficulty.getMaxWaves() != Integer.MAX_VALUE ? ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves() : ""));

    }

    @Override
    public String getGameLabel() {
        return super.getGameLabel() + " - " +
                ChatColor.YELLOW + "Waves Cleared: " + wavesCleared + ChatColor.GRAY + "/" + ChatColor.YELLOW + difficulty.getMaxWaves() + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + isCounted();

    }

    @Override
    public List<String> getExtraLore() {
        List<String> lore = new ArrayList<>(super.getExtraLore());
        lore.add(ChatColor.GRAY + "Waves Cleared: " + ChatColor.YELLOW + wavesCleared);
        return lore;
    }

    public int getWavesCleared() {
        return wavesCleared;
    }
}

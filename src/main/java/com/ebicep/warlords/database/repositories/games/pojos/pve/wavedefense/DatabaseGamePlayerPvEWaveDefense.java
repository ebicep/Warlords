package com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.MostDamageInWave;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Spendable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

public class DatabaseGamePlayerPvEWaveDefense extends DatabaseGamePlayerPvEBase implements MostDamageInWave {

    @Field("most_damage_in_wave")
    private long mostDamageInWave;
    @Field("ascendant_pouch")
    private Map<Spendable, Long> ascendantPouch = new HashMap<>();

    public DatabaseGamePlayerPvEWaveDefense() {
    }

    public DatabaseGamePlayerPvEWaveDefense(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, PveOption pveOption, boolean counted) {
        super(warlordsPlayer, gameWinEvent, pveOption, counted);
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName());
        UUID uuid = warlordsPlayer.getUuid();
        PlayerPveRewards playerPveRewards = pveOption.getRewards()
                                                     .getPlayerRewards(uuid);
        Collection<Long> values = playerPveRewards.getWaveDamage().values();
        if (!values.isEmpty()) {
            this.mostDamageInWave = Collections.max(values);
        }
        this.ascendantPouch = playerPveRewards.getAscendantPouch();
    }

    @Override
    public long getMostDamageInWave() {
        return mostDamageInWave;
    }

    public Map<Spendable, Long> getAscendantPouch() {
        return ascendantPouch;
    }
}

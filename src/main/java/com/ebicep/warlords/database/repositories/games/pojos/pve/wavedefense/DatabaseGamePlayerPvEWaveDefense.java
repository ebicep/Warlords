package com.ebicep.warlords.database.repositories.games.pojos.pve.wavedefense;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.database.repositories.games.pojos.pve.MostDamageInWave;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class DatabaseGamePlayerPvEWaveDefense extends DatabaseGamePlayerPvEBase implements MostDamageInWave {

    @Field("most_damage_in_wave")
    private long mostDamageInWave;

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
    }

    @Override
    public long getMostDamageInWave() {
        return mostDamageInWave;
    }
}

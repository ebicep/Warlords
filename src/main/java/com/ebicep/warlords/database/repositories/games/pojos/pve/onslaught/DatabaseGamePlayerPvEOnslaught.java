package com.ebicep.warlords.database.repositories.games.pojos.pve.onslaught;

import com.ebicep.warlords.database.repositories.games.pojos.pve.DatabaseGamePlayerPvEBase;
import com.ebicep.warlords.events.game.WarlordsGameTriggerWinEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.rewards.PlayerPveRewards;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.Spendable;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DatabaseGamePlayerPvEOnslaught extends DatabaseGamePlayerPvEBase {

    @Field("synthetic_pouch")
    private Map<Spendable, Long> syntheticPouch = new HashMap<>();
    @Field("aspirant_pouch")
    private Map<Spendable, Long> aspirantPouch = new HashMap<>();

    public DatabaseGamePlayerPvEOnslaught() {
    }

    public DatabaseGamePlayerPvEOnslaught(WarlordsPlayer warlordsPlayer, WarlordsGameTriggerWinEvent gameWinEvent, PveOption pveOption, boolean counted) {
        super(warlordsPlayer, gameWinEvent, pveOption, counted);
        //ChatUtils.MessageTypes.GAME_DEBUG.sendMessage("DatabaseGamePlayerPvE - " + warlordsPlayer.getName());
        UUID uuid = warlordsPlayer.getUuid();
        PlayerPveRewards playerPveRewards = pveOption.getRewards()
                                                     .getPlayerRewards(uuid);
        this.syntheticPouch = playerPveRewards.getSyntheticPouch();
        this.aspirantPouch = playerPveRewards.getAspirantPouch();
    }

    public Map<Spendable, Long> getSyntheticPouch() {
        return syntheticPouch;
    }

    public Map<Spendable, Long> getAspirantPouch() {
        return aspirantPouch;
    }
}

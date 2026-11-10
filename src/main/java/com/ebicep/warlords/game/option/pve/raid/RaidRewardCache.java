package com.ebicep.warlords.game.option.pve.raid;

import com.ebicep.warlords.database.repositories.events.pojos.GameEventReward;
import com.ebicep.warlords.pve.Spendable;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.time.Instant;
import java.util.LinkedHashMap;

public class RaidRewardCache extends GameEventReward {

    public RaidRewardCache() {
    }

    public RaidRewardCache(LinkedHashMap<Spendable, Long> rewards, Raid raid) {
        super(rewards, raid.getName(), Instant.now().getEpochSecond());
    }

    @Override
    public TextColor getNameColor() {
        return NamedTextColor.LIGHT_PURPLE;
    }

}

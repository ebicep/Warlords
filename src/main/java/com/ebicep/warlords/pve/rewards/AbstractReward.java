package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReward {

    protected Map<Currencies, Long> rewards = new LinkedHashMap<>();
    protected String from;
    @Field("time_claimed")
    protected Instant timeClaimed;

    public AbstractReward() {
    }

    public AbstractReward(LinkedHashMap<Currencies, Long> rewards, String from) {
        this.rewards = rewards;
        this.from = from;
    }

    public abstract void giveToPlayer(DatabasePlayer databasePlayer);

    public List<String> getLore() {
        return new ArrayList<>();
    }

    public ItemStack getItem() {
        return null;
    }

    public Map<Currencies, Long> getRewards() {
        return rewards;
    }

    public String getFrom() {
        return from;
    }

    public Instant getTimeClaimed() {
        return timeClaimed;
    }

    public void setTimeClaimed() {
        this.timeClaimed = Instant.now();
    }

}

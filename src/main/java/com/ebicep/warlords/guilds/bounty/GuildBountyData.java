package com.ebicep.warlords.guilds.bounty;

import com.ebicep.warlords.guilds.upgrades.AbstractGuildUpgrade;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GuildBountyData extends AbstractGuildUpgrade<GuildBountyStorageUpgrade> {

    @Field("week_start_epoch_day")
    private long weekStartEpochDay = Long.MIN_VALUE;
    @Field("active_bounties")
    private List<GuildBountyProgress> activeBounties = new ArrayList<>();

    public GuildBountyData() {
        this.upgrade = GuildBountyStorageUpgrade.DATA;
        this.activationDate = Instant.now();
    }

    public GuildBountyData(int unlockedSlots) {
        this();
        this.tier = unlockedSlots;
    }

    @Override
    public void addItemClickLore(ItemBuilder itemBuilder) {
    }

    public int getUnlockedSlots() {
        return tier;
    }

    public void setUnlockedSlots(int unlockedSlots) {
        this.tier = Math.max(0, Math.min(unlockedSlots, 2));
    }

    public long getWeekStartEpochDay() {
        return weekStartEpochDay;
    }

    public void setWeekStartEpochDay(long weekStartEpochDay) {
        this.weekStartEpochDay = weekStartEpochDay;
    }

    public List<GuildBountyProgress> getActiveBounties() {
        if (activeBounties == null) {
            activeBounties = new ArrayList<>();
        }
        return activeBounties;
    }
}

package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Reward {

    private RewardTypes reward;
    private float amount;
    private String from;
    @Field("time_given")
    private Instant timeGiven;
    @Field("time_claimed")
    private Instant timeClaimed;

    public Reward(RewardTypes reward, float amount, String from) {
        this.reward = reward;
        this.amount = amount;
        this.from = from;
        this.timeGiven = Instant.now();
    }

    private List<String> getLore() {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "From: " + ChatColor.GREEN + from);
        if (amount > 0) {
            lore.add(ChatColor.GRAY + "Amount: " + ChatColor.GREEN + NumberFormat.formatOptionalHundredths(amount));
        }
        return lore;
    }

    public ItemStack getItem() {
        List<String> lore = getLore();
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to claim!");
        return new ItemBuilder(reward.item)
                .name(ChatColor.GREEN + reward.name)
                .lore(lore)
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .get();
    }

    public ItemStack getItemWithoutClaim() {
        return new ItemBuilder(reward.item)
                .name(ChatColor.GREEN + reward.name)
                .lore(getLore())
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .get();
    }

    public RewardTypes getReward() {
        return reward;
    }

    public float getAmount() {
        return amount;
    }

    public String getFrom() {
        return from;
    }

    public Instant getTimeGiven() {
        return timeGiven;
    }

    public Instant getTimeClaimed() {
        return timeClaimed;
    }

    public void setTimeClaimed() {
        this.timeClaimed = Instant.now();
    }
}

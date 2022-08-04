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

public class MasterworksFairReward extends AbstractReward {

    @Field("time_given")
    private Instant timeGiven;

    public MasterworksFairReward(RewardTypes reward, float amount, Instant timeGiven) {
        super(reward, amount, "Masterworks Fair");
        this.timeGiven = timeGiven;
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
    public Instant getTimeGiven() {
        return timeGiven;
    }

}

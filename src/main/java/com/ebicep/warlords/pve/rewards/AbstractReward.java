package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReward {

    protected Map<Spendable, Long> rewards = new LinkedHashMap<>();
    protected String from;
    @Field("time_claimed")
    protected Instant timeClaimed;

    public AbstractReward() {
    }

    public AbstractReward(LinkedHashMap<Spendable, Long> rewards, String from) {
        this.rewards = rewards;
        this.from = from;
    }

    public void giveToPlayer(DatabasePlayer databasePlayer) {
        rewards.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
        setTimeClaimed();
    }

    public List<String> getLore() {
        return rewards.entrySet()
                      .stream()
                      .map(currencyValue -> currencyValue.getKey().getCostColoredName(currencyValue.getValue()))
                      .toList();
    }

    public ItemStack getItem() {
        List<String> lore = getLore();
        lore.add(0, "");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to claim!");
        return new ItemBuilder(Material.CHEST)
                .name(getNameColor() + from + " Reward")
                .lore(lore)
                .flags(ItemFlag.HIDE_ITEM_SPECIFICS)
                .get();
    }

    public ItemStack getItemWithoutClaim() {
        return new ItemBuilder(Material.CHEST)
                .name(getNameColor() + from + " Reward")
                .lore(getLore())
                .flags(ItemFlag.HIDE_ITEM_SPECIFICS)
                .get();
    }

    public ChatColor getNameColor() {
        return ChatColor.GREEN;
    }

    public Map<Spendable, Long> getRewards() {
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

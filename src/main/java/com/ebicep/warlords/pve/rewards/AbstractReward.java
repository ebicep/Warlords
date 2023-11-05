package com.ebicep.warlords.pve.rewards;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<Component> getLore() {
        return rewards.entrySet()
                      .stream()
                      .map(currencyValue -> currencyValue.getKey().getCostColoredName(currencyValue.getValue()))
                      .collect(Collectors.toList());
    }

    public ItemStack getItem() {
        List<Component> lore = getLore();
        lore.add(0, Component.empty());
        lore.add(Component.empty());
        lore.add(Component.text("Click to claim!", NamedTextColor.YELLOW));
        return new ItemBuilder(Material.CHEST)
                .name(Component.text(from + " Reward", getNameColor()))
                .lore(lore)
                .get();
    }

    public ItemStack getItemWithoutClaim() {
        return new ItemBuilder(Material.CHEST)
                .name(Component.text(from + " Reward", getNameColor()))
                .lore(getLore())
                .get();
    }

    public TextColor getNameColor() {
        return NamedTextColor.GREEN;
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

    @Override
    public String toString() {
        return "AbstractReward{" +
                "rewards=" + rewards +
                ", from='" + from + '\'' +
                ", timeClaimed=" + timeClaimed +
                '}';
    }
}

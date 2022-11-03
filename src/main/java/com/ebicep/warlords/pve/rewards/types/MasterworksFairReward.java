package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MasterworksFairReward extends AbstractReward {

    @Field("time_given")
    private Instant timeGiven;

    public MasterworksFairReward() {
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        rewards.forEach(pveStats::addCurrency);
    }

    public MasterworksFairReward(LinkedHashMap<Currencies, Long> rewards, Instant timeGiven, WeaponsPvE rarity) {
        super(rewards, "Masterworks Fair " + rarity.name);
        this.timeGiven = timeGiven;
    }

    @Override
    public List<String> getLore() {
        return rewards.entrySet()
                .stream()
                .map(currenciesLongEntry -> {
                    Currencies currency = currenciesLongEntry.getKey();
                    Long value = currenciesLongEntry.getValue();
                    return currency.chatColor.toString() + value + " " + currency.name + (value != 1 ? "s" : "");
                }).collect(Collectors.toList());
    }

    @Override
    public ItemStack getItem() {
        List<String> lore = getLore();
        lore.add(0, "");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Click to claim!");
        return new ItemBuilder(Material.CHEST)
                .name(ChatColor.GREEN + from + " Reward")
                .lore(lore)
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .get();
    }

    public ItemStack getItemWithoutClaim() {
        return new ItemBuilder(Material.CHEST)
                .name(ChatColor.GREEN + from + " Reward")
                .lore(getLore())
                .flags(ItemFlag.HIDE_POTION_EFFECTS)
                .get();
    }

    public Instant getTimeGiven() {
        return timeGiven;
    }

}

package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.bountysystem.rewards.RewardSpendable;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBounty implements RewardSpendable {

    public static final Map<Currencies, Long> COST = new HashMap<>() {{
        put(Currencies.COIN, 5000L);
    }};
    public static Map<PlayersCollections, Integer> MAX_BOUNTIES = new HashMap<>() {{
        put(PlayersCollections.DAILY, 2);
        put(PlayersCollections.WEEKLY, 2);
        put(PlayersCollections.LIFETIME, Integer.MAX_VALUE);
    }};
    private boolean started = false;
    private Instant claimed = null;

    public ItemBuilder getItem() { //TODO maybe center everything
        ItemBuilder itemBuilder = new ItemBuilder(started ? Material.PAPER : Material.MAP)
                .name(Component.text(getName(), NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.GRAY), 160));
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Rewards:", NamedTextColor.GRAY)
        );
        getCurrencyReward().forEach((currencies, aLong) -> itemBuilder.addLore(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));

        Component progress = getProgress();
        itemBuilder.addLore(
                Component.empty(),
                started ? progress == null ?
                          Component.text("Click to Claim!", NamedTextColor.GREEN) :
                          Component.text("Progress: ", NamedTextColor.GRAY).append(progress) :
                Component.text("Click to Start!", NamedTextColor.GREEN)
        );

        if (started && claimed != null) {
            itemBuilder.enchant(Enchantment.OXYGEN, 1);
        }
        return itemBuilder;
    }

    /**
     * @return Progress display - if null, bounty is completed
     */
    @Nullable
    public abstract Component getProgress();

    public String getName() {
        return "TODO"; //TODO abstract
    }

    public abstract String getDescription();

    protected Component getProgress(int progress, int target) {
        return getProgress(progress, String.valueOf(target));
    }

    protected Component getProgress(int progress, String target) {
        return Component.textOfChildren(
                Component.text(progress, NamedTextColor.GOLD),
                Component.text("/", NamedTextColor.AQUA),
                Component.text(target, NamedTextColor.GOLD)
        );
    }

    public Map<Currencies, Long> getCost() {
        return COST;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean notClaimed() {
        return claimed == null;
    }

    public void claim(DatabasePlayer databasePlayer) {
        this.claimed = Instant.now();
        getCurrencyReward().forEach((spendable, aLong) -> spendable.addToPlayer(databasePlayer, aLong));
    }
}

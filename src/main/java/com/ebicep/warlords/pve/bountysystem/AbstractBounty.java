package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.bountysystem.rewards.RewardSpendable;
import com.ebicep.warlords.pve.rewards.types.BountyReward;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nullable;
import java.util.Map;

import static com.ebicep.warlords.pve.bountysystem.BountyUtils.BOUNTY_COLLECTION_INFO;

public abstract class AbstractBounty implements RewardSpendable {

    protected int value;
    private boolean started = false;

    public ItemBuilder getItemWithProgress() { //TODO maybe center everything
        ItemBuilder itemBuilder = getItem();

        Component progress = getProgress();
        itemBuilder.addLore(Component.empty());
        if (started) {
            if (progress == null) {
                itemBuilder.addLore(Component.text("Click to Claim!", NamedTextColor.GREEN));
            } else {
                itemBuilder.addLore(Component.text("Progress: ", NamedTextColor.GRAY).append(progress));
            }
        } else {
            itemBuilder.addLore(PvEUtils.getCostLore(BountyUtils.COST, false));
            itemBuilder.addLore(
                    Component.empty(),
                    Component.text("Click to Start!", NamedTextColor.GREEN)
            );
        }

        if (started && progress == null) {
            itemBuilder.enchant(Enchantment.OXYGEN, 1);
        }
        return itemBuilder;
    }

    public ItemBuilder getItem() {
        ItemBuilder itemBuilder = new ItemBuilder(started ? Material.PAPER : Material.MAP)
                .name(Component.text(getName(), NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text(getDescription(), NamedTextColor.GRAY), 160));
        itemBuilder.addLore(
                Component.empty(),
                Component.text("Rewards:", NamedTextColor.GRAY)
        );
        getCurrencyReward().forEach((currencies, aLong) -> itemBuilder.addLore(Component.text(" +", NamedTextColor.DARK_GRAY).append(currencies.getCostColoredName(aLong))));
        return itemBuilder;
    }

    /**
     * @return Progress display - if null, bounty is completed
     */
    @Nullable
    public Component getProgress() {
        if (value >= getTarget()) {
            return null;
        }
        return getProgress(value, getTarget());
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getTarget();

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

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Map<Currencies, Long> getCost() {
        return BountyUtils.COST;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public void claim(DatabasePlayer databasePlayer, PlayersCollections collection) {
        DatabasePlayerPvE pveStats = databasePlayer.getPveStats();
        pveStats.addBountiesCompleted();
        int replaceIndex = pveStats.getActiveBounties().indexOf(this);
        int maxBounties = BOUNTY_COLLECTION_INFO.get(collection).maxBounties();
        AbstractBounty replacementBounty = null;
        if (pveStats.getBountiesCompleted() < maxBounties) {
            Bounty randomBounty = BountyUtils.getRandomBounty(collection, pveStats.getCompletedBounties().keySet().stream().toList());
            if (randomBounty != null) {
                replacementBounty = randomBounty.create.get();
            }
        }
        pveStats.getActiveBounties().set(replaceIndex, replacementBounty);

        if (collection != PlayersCollections.LIFETIME) {
            pveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
        }
        DatabaseManager.getPlayer(databasePlayer.getUuid(), lifetimeDatabasePlayer -> {
            DatabasePlayerPvE lifetimePveStats = lifetimeDatabasePlayer.getPveStats();
            lifetimePveStats.getBountyRewards().add(new BountyReward(getCurrencyReward(), getBounty()));
            lifetimePveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
        });
    }

    public abstract Bounty getBounty();

}

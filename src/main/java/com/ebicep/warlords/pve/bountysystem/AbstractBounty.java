package com.ebicep.warlords.pve.bountysystem;

import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.PlayersCollections;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.pve.DatabasePlayerPvE;
import com.ebicep.warlords.pve.PvEUtils;
import com.ebicep.warlords.pve.bountysystem.costs.BountyCost;
import com.ebicep.warlords.pve.bountysystem.rewards.RewardSpendable;
import com.ebicep.warlords.pve.rewards.types.BountyReward;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static com.ebicep.warlords.pve.bountysystem.BountyUtils.BOUNTY_COLLECTION_INFO;

public abstract class AbstractBounty implements RewardSpendable, BountyCost {

    protected long value;
    private boolean started = false;

    public ItemBuilder getItemWithProgress() { //TODO maybe center everything
        ItemBuilder itemBuilder = getItem();

        List<Component> progress = getProgress();
        itemBuilder.addLore(Component.empty());
        if (started) {
            if (progress == null) {
                itemBuilder.addLore(Component.text("Click to Claim!", NamedTextColor.GREEN));
            } else {
                itemBuilder.addLore(progress);
            }
        } else {
            itemBuilder.addLore(PvEUtils.getCostLore(getCost(), false));
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
    public List<Component> getProgress() {
        if (value >= getTarget()) {
            return null;
        }
        if (getTarget() == 1) { // for boolean bounties - if target is actually 1 then just override
            return getNoProgress();
        }
        return Collections.singletonList(Component.textOfChildren(
                Component.text("Progress: ", NamedTextColor.GRAY),
                Component.text(value, NamedTextColor.GOLD),
                Component.text("/", NamedTextColor.AQUA),
                Component.text(NumberFormat.addCommaAndRound(getTarget()), NamedTextColor.GOLD)
        ));
    }

    public abstract String getName();

    public abstract String getDescription();

    public abstract int getTarget();

    protected List<Component> getNoProgress() {
        return Collections.singletonList(Component.textOfChildren(
                Component.text("Progress: ", NamedTextColor.GRAY),
                Component.text("✖", NamedTextColor.RED)
        ));
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
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
        if (pveStats.getBountiesCompleted() <= maxBounties) {
            Bounty randomBounty = BountyUtils.getRandomBounty(collection, pveStats.getCompletedBounties().keySet().stream().toList());
            if (randomBounty != null) {
                replacementBounty = randomBounty.create.get();
            }
        }
        pveStats.getActiveBounties().set(replaceIndex, replacementBounty);

        if (collection != PlayersCollections.LIFETIME) {
            pveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
            DatabaseManager.queueUpdatePlayerAsync(databasePlayer, collection);
        }
        DatabaseManager.updatePlayer(databasePlayer.getUuid(), lifetimeDatabasePlayer -> {
            DatabasePlayerPvE lifetimePveStats = lifetimeDatabasePlayer.getPveStats();
            lifetimePveStats.getBountyRewards().add(new BountyReward(getCurrencyReward(), getBounty()));
            lifetimePveStats.getCompletedBounties().merge(getBounty(), 1L, Long::sum);
        });
    }

    public abstract Bounty getBounty();

}

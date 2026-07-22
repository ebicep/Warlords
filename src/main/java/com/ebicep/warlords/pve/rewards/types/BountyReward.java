package com.ebicep.warlords.pve.rewards.types;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.bountysystem.Bounty;
import com.ebicep.warlords.pve.bountysystem.BountyUtils;
import com.ebicep.warlords.pve.items.types.SpendableRandomItem;
import com.ebicep.warlords.pve.newitems.SpendableRandomNewItem;
import com.ebicep.warlords.pve.rewards.AbstractReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BountyReward extends AbstractReward {

    private Bounty bounty;

    public BountyReward() {
    }

    public BountyReward(LinkedHashMap<Spendable, Long> rewards, Bounty bounty) {
        super(rewards, bounty.create.get().getName() + " Bounty");
        this.bounty = bounty;
    }

    private void normalizeLegacyLifetimeItemReward() {
        if (bounty == null || rewards == null || !rewards.containsKey(SpendableRandomItem.DELTA)) {
            return;
        }
        boolean lifetimeBounty = Arrays.stream(Bounty.BountyGroup.LIFETIME_ALL.bounties)
                                       .anyMatch(value -> value == bounty);
        if (!lifetimeBounty) {
            return;
        }

        Map<Spendable, Long> normalizedRewards = new LinkedHashMap<>();
        rewards.forEach((spendable, amount) -> {
            if (spendable == SpendableRandomItem.DELTA) {
                normalizedRewards.merge(SpendableRandomNewItem.SOVEREIGN, amount, Long::sum);
            } else {
                normalizedRewards.merge(spendable, amount, Long::sum);
            }
        });
        rewards = normalizedRewards;
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {
        normalizeLegacyLifetimeItemReward();
        super.giveToPlayer(databasePlayer);
    }

    @Override
    public List<Component> getLore() {
        normalizeLegacyLifetimeItemReward();
        return super.getLore();
    }

    @Override
    public Map<Spendable, Long> getRewards() {
        normalizeLegacyLifetimeItemReward();
        return super.getRewards();
    }

    @Override
    public TextColor getNameColor() {
        return BountyUtils.COLOR;
    }

}

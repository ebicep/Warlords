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

    public BountyReward(LinkedHashMap<Spendable, Long> rewards, String bountyName) {
        super(rewards, bountyName + " Guild Bounty");
    }

    private void normalizeLegacyItemRewards() {
        if (rewards == null || rewards.keySet().stream().noneMatch(SpendableRandomItem.class::isInstance)) {
            return;
        }

        Map<Spendable, Long> normalizedRewards = new LinkedHashMap<>();
        rewards.forEach((spendable, amount) -> normalizedRewards.merge(toNewItemReward(spendable), amount, Long::sum));
        rewards = normalizedRewards;
    }

    private Spendable toNewItemReward(Spendable spendable) {
        if (!(spendable instanceof SpendableRandomItem legacyItem)) {
            return spendable;
        }
        return switch (legacyItem) {
            case ALPHA -> SpendableRandomNewItem.COMMON;
            case BETA -> SpendableRandomNewItem.RARE;
            case GAMMA -> SpendableRandomNewItem.EPIC;
            case DELTA -> SpendableRandomNewItem.SOVEREIGN;
        };
    }

    @Override
    public void giveToPlayer(DatabasePlayer databasePlayer) {
        normalizeLegacyItemRewards();
        super.giveToPlayer(databasePlayer);
    }

    @Override
    public List<Component> getLore() {
        normalizeLegacyItemRewards();
        return super.getLore();
    }

    @Override
    public Map<Spendable, Long> getRewards() {
        normalizeLegacyItemRewards();
        return super.getRewards();
    }

    @Override
    public TextColor getNameColor() {
        return BountyUtils.COLOR;
    }

}

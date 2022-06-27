package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilties.ArcaneShield;
import org.bukkit.inventory.ItemStack;

public class ArcaneShieldUpgradeBranch extends UpgradeBranch<ArcaneShield> {

    public ArcaneShieldUpgradeBranch(AbilityTree abilityTree, ArcaneShield ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "+10% Damage absorbed"));
        upgrades.add(new Upgrade("Tier 2", "+5% Damage resistance while shield is active."));
        upgrades.add(new Upgrade("Tier 3", "+10% Damage absorbed"));
        upgrades.add(new Upgrade("Tier 4", "+5% Damage resistance while shield is active."));
        upgrades.add(new Upgrade("Tier 5", "-50% Cooldown, +30% Damage absorbed and +50% Duration."));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setShieldPercentage(60);
    }

    @Override
    public void tierTwoUpgrade() {
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setShieldPercentage(70);
    }

    @Override
    public void tierFourUpgrade() {
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setDuration((int) (ability.getDuration() * 1.5f));
        ability.setShieldPercentage(100);
        ability.setCooldown(ability.getCooldown() * .5f);
    }

}

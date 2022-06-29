package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeBranch;
import org.bukkit.inventory.ItemStack;

public class ArcaneShieldBranch extends UpgradeBranch<ArcaneShield> {

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability, ItemStack itemStack) {
        super(abilityTree, ability, itemStack);
        upgrades.add(new Upgrade("Tier 1", "+10% Damage absorbed"));
        upgrades.add(new Upgrade("Tier 2", "+5% Damage resistance while shield is active"));
        upgrades.add(new Upgrade("Tier 3", "+10% Damage absorbed"));
        upgrades.add(new Upgrade("Tier 4", "+5% Damage resistance while shield is active"));
        upgrades.add(new Upgrade("Tier 5", "-50% Cooldown\n+30% Damage absorbed\n+50% Duration\n+15% Damage resistance while shield is active"));
    }

    @Override
    public void tierOneUpgrade() {
        ability.setShieldPercentage(60);
    }

    @Override
    public void tierTwoUpgrade() {
        ability.setPveUpgrade(true);
        ability.setPveDamageReduction(5);
    }

    @Override
    public void tierThreeUpgrade() {
        ability.setShieldPercentage(70);
    }

    @Override
    public void tierFourUpgrade() {
        ability.setPveDamageReduction(10);
    }

    @Override
    public void tierFiveUpgrade() {
        ability.setDuration((int) (ability.getDuration() * 1.5f));
        ability.setShieldPercentage(100);
        ability.setCooldown(ability.getCooldown() * .5f);
        ability.setPveDamageReduction(25);
    }
}

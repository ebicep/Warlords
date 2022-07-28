package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.RemedicChains;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class RemedicChainsBranch extends AbstractUpgradeBranch<RemedicChains> {

    public RemedicChainsBranch(AbilityTree abilityTree, RemedicChains ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+10 Blocks cast and break range.\n\nIncrease the damage boost provided by\nRemedic Chains by 30%",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minHealing * 1.1f);
        ability.setMaxDamageHeal(maxHealing * 1.1f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minHealing * 1.2f);
        ability.setMaxDamageHeal(maxHealing * 1.2f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minHealing * 1.4f);
        ability.setMaxDamageHeal(maxHealing * 1.4f);
    }

    @Override
    public void b1() {

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    float cooldown = ability.getCooldown();

    @Override
    public void c1() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void c2() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void c3() {
        ability.setCooldown(cooldown * 0.6f);
    }

    @Override
    public void master() {
        ability.setLinkBreakRadius(ability.getLinkBreakRadius() + 10);
        ability.setCastRange(ability.getCastRange() + 10);
        ability.setAllyDamageIncrease(30);
    }
}

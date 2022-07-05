package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.ChainLightning;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ChainLightningBranch extends AbstractUpgradeBranch<ChainLightning> {
    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage Reduction - Tier I", "+5% Damage reduction", 5000));
        treeA.add(new Upgrade("Damage Reduction - Tier II", "+10% Damage reduction", 10000));
        treeA.add(new Upgrade("Damage Reduction - Tier III", "+20% Damage reduction", 20000));

        treeB.add(new Upgrade("Utility - Tier I", "+1 Chain bounces", 5000));
        treeB.add(new Upgrade("Utility - Tier II", "+2 Chain bounces", 10000));
        treeB.add(new Upgrade("Utility - Tier III", "+3 Chain bounces", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n+10 Blocks cast and bounce range\n\nChain Lightning now deals 5% more damage per bounce instead of less.",
                50000
        );
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setMaxDamageReduction(ability.getMaxDamageReduction() + 5);
    }

    @Override
    public void a2() {
        ability.setMaxDamageReduction(ability.getMaxDamageReduction() + 5);
    }

    @Override
    public void a3() {
        ability.setMaxDamageReduction(ability.getMaxDamageReduction() + 10);
    }

    @Override
    public void b1() {
        ability.setMaxBounces(4);
    }

    @Override
    public void b2() {
        ability.setMaxBounces(5);
    }

    @Override
    public void b3() {
        ability.setMaxBounces(6);
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.8f);
        ability.setMaxDamageHeal(maxDamage * 1.8f);
    }

    @Override
    public void master() {
        ability.setRadius(30);
        ability.setBounceRange(20);
        ability.setPveUpgrade(true);
    }
}

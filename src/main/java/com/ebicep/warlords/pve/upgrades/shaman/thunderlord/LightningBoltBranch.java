package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.LightningBolt;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningBoltBranch extends AbstractUpgradeBranch<LightningBolt> {

    public LightningBoltBranch(AbilityTree abilityTree, LightningBolt ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Crit Multiplier - Tier I", "+10% Crit multiplier", 5000));
        treeC.add(new Upgrade("Crit Multiplier - Tier II", "+20% Crit multiplier", 10000));
        treeC.add(new Upgrade("Crit Multiplier - Tier III", "+40% Crit multiplier", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Lightning Bolt shoots two additional projectiles.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minDamage * 1.1f);
        ability.setMaxDamageHeal(maxDamage * 1.1f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    float energyCost = ability.getEnergyCost();

    @Override
    public void b1() {
        ability.setEnergyCost(energyCost - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(energyCost - 15);
    }

    @Override
    public void c1() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 10);
    }

    @Override
    public void c2() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 10);
    }

    @Override
    public void c3() {
        ability.setCritMultiplier(ability.getCritMultiplier() + 20);
    }

    @Override
    public void master() {
        ability.setShotsFiredAtATime(3);
    }
}

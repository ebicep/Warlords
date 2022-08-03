package com.ebicep.warlords.pve.upgrades.mage.pyromancer;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FlameburstBranch extends AbstractUpgradeBranch<FlameBurst> {

    public FlameburstBranch(AbilityTree abilityTree, FlameBurst ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeB.add(new Upgrade("Range - Tier I", "+1 Block splash radius", 5000));
        treeB.add(new Upgrade("Range - Tier II", "+2 Blocks splash radius", 10000));
        treeB.add(new Upgrade("Range - Tier III", "+3 Blocks splash radius", 20000));

        treeC.add(new Upgrade("Damage - Tier I", "+15% Damage", 5000));
        treeC.add(new Upgrade("Damage - Tier II", "+30% Damage", 10000));
        treeC.add(new Upgrade("Damage - Tier III", "+60% Damage", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nFlame Burst gains an additional 0.5% Crit Chance and\n1% Crit Multiplier for each block it travels.",
                50000
        );
    }

    float cooldown = ability.getCooldown();

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minDamage * 1.15f);
        ability.setMaxDamageHeal(maxDamage * 1.15f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minDamage * 1.3f);
        ability.setMaxDamageHeal(maxDamage * 1.3f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minDamage * 1.6f);
        ability.setMaxDamageHeal(maxDamage * 1.6f);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setEnergyCost(0);
        ability.setPveUpgrade(true);
    }
}

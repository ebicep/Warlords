package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.BloodLust;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BloodlustBranch extends AbstractUpgradeBranch<BloodLust> {

    public BloodlustBranch(AbilityTree abilityTree, BloodLust ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing Conversion - Tier I", "+5% Healing conversion", 5000));
        treeA.add(new Upgrade("Healing Conversion - Tier II", "+10% Healing conversion", 10000));
        treeA.add(new Upgrade("Healing Conversion - Tier III", "+20% Healing conversion", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-5% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-10% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-20% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "While Blood Lust is active, increase all damage against bleeding or wounded targets by 20%",
                50000
        );
    }

    int conversion = ability.getDamageConvertPercent();

    float cooldown = ability.getCooldown();

    @Override
    public void c1() {
        ability.setCooldown(cooldown * 0.95f);
    }

    @Override
    public void c2() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void c3() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}

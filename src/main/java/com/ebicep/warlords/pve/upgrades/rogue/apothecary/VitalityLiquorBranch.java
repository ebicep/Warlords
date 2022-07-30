package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.VitalityLiquor;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class VitalityLiquorBranch extends AbstractUpgradeBranch<VitalityLiquor> {

    public VitalityLiquorBranch(AbilityTree abilityTree, VitalityLiquor ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+2 Blocks radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+4 Blocks radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+6 Blocks radius", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER: +100% Healing",
                50000
        );
    }

    int vitalityRange = ability.getVitalityRange();

    @Override
    public void a1() {
        ability.setVitalityRange(vitalityRange + 2);
    }

    @Override
    public void a2() {
        ability.setVitalityRange(vitalityRange + 4);
    }

    @Override
    public void a3() {
        ability.setVitalityRange(vitalityRange + 6);
    }

    @Override
    public void a4() {

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

    @Override
    public void b4() {

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
    public void c4() {

    }

    @Override
    public void master() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 2);
        ability.setMinDamageHeal(ability.getMaxDamageHeal() * 2);
        ability.setEnergyPerSecond(ability.getEnergyPerSecond() * 2);
    }
}

package com.ebicep.warlords.pve.upgrades.protector;

import com.ebicep.warlords.abilties.ProtectorsStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ProtectorStrikeBranch extends AbstractUpgradeBranch<ProtectorsStrike> {

    public ProtectorStrikeBranch(AbilityTree abilityTree, ProtectorsStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+20% Healing conversion", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+40% Healing conversion", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+80% Healing conversion", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Protector's Strike hits 2 additional targets and\n heals 2 additional allies. Protector's Strike deals\n20% of it's damage as true damage.",
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

    @Override
    public void b1() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(ability.getEnergyCost() - 5);
    }

    @Override
    public void c1() {
        ability.setMinConvert(120);
        ability.setMaxConvert(95);
    }

    @Override
    public void c2() {
        ability.setMinConvert(140);
        ability.setMaxConvert(115);
    }

    @Override
    public void c3() {
        ability.setMinConvert(180);
        ability.setMaxConvert(155);
    }

    @Override
    public void master() {
        ability.setMaxAllies(ability.getMaxAllies() + 2);
        ability.setPveUpgrade(true);
    }
}

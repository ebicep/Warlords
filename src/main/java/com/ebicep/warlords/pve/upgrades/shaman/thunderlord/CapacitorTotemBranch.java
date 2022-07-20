package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.CapacitorTotem;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class CapacitorTotemBranch extends AbstractUpgradeBranch<CapacitorTotem> {

    public CapacitorTotemBranch(AbilityTree abilityTree, CapacitorTotem ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeC.add(new Upgrade("Range - Tier I", "+1 Block radius", 5000));
        treeC.add(new Upgrade("Range - Tier II", "+3 Block radius", 10000));
        treeC.add(new Upgrade("Range - Tier III", "+3 Block radius", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Duration\n\nChain Lightning now deals 5% more damage per bounce instead of less.",
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

    }

    @Override
    public void b2() {

    }

    @Override
    public void b3() {

    }

    @Override
    public void c1() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void c2() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void c3() {
        ability.setRadius(ability.getRadius() + 1);
    }

    @Override
    public void util1() {

    }

    @Override
    public void util2() {

    }

    @Override
    public void master() {
        ability.setDuration((int) (ability.getDuration() * 1.5f));
        ability.setPveUpgrade(true);
    }
}

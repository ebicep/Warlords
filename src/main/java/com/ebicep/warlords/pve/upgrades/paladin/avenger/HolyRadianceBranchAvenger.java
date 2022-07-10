package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.HolyRadianceAvenger;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HolyRadianceBranchAvenger extends AbstractUpgradeBranch<HolyRadianceAvenger> {

    public HolyRadianceBranchAvenger(AbilityTree abilityTree, HolyRadianceAvenger ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+20% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+40% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+80% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nIncrease Avenger's Mark cast range by 4 blocks and energy drain by 100%",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minDamage * 1.2f);
        ability.setMaxDamageHeal(maxDamage * 1.2f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minDamage * 1.4f);
        ability.setMaxDamageHeal(maxDamage * 1.4f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minDamage * 1.8f);
        ability.setMaxDamageHeal(maxDamage * 1.8f);
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
        ability.setEnergyCost(0);
        ability.setMarkRadius(20);
        ability.setEnergyPerSecond(ability.getEnergyPerSecond() * 2);
    }
}

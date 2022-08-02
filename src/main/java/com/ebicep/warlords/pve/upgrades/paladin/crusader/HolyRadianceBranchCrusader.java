package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilties.HolyRadianceCrusader;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HolyRadianceBranchCrusader extends AbstractUpgradeBranch<HolyRadianceCrusader> {

    public HolyRadianceBranchCrusader(AbilityTree abilityTree, HolyRadianceCrusader ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+20% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+40% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+80% Healing", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nIncrease Crusader's Mark's duration by 4 seconds\nand energy regeneration by 100%",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

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
        ability.setEnergyCost(0);
        ability.setEnergyPerSecond(ability.getEnergyPerSecond() * 2);
        ability.setMarkDuration(ability.getMarkDuration() + 4);
    }
}

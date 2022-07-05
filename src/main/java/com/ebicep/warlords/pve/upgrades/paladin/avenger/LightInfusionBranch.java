package com.ebicep.warlords.pve.upgrades.paladin.avenger;

import com.ebicep.warlords.abilties.LightInfusion;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightInfusionBranch extends AbstractUpgradeBranch<LightInfusion> {

    public LightInfusionBranch(AbilityTree abilityTree, LightInfusion ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Speed - Tier I", "+10% Speed", 5000));
        treeA.add(new Upgrade("Speed - Tier II", "+20% Speed", 10000));
        treeA.add(new Upgrade("Speed - Tier III", "+30% Speed", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Energy given\n+100% Duration\n\nReduce all knockback by 20% while Light Infusion is active.",
                50000
        );
    }

    @Override
    public void a1() {
        ability.setSpeedBuff(50);
    }

    @Override
    public void a2() {
        ability.setSpeedBuff(60);
    }

    @Override
    public void a3() {
        ability.setSpeedBuff(70);
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
        ability.setEnergyGiven((int) (ability.getEnergyGiven() * 1.5f));
        ability.setDuration(ability.getDuration() * 2);
    }
}

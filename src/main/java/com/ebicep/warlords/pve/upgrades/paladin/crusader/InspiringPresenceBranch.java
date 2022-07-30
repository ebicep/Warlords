package com.ebicep.warlords.pve.upgrades.paladin.crusader;

import com.ebicep.warlords.abilties.InspiringPresence;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class InspiringPresenceBranch extends AbstractUpgradeBranch<InspiringPresence> {

    public InspiringPresenceBranch(AbilityTree abilityTree, InspiringPresence ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+1s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+2s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+4s Duration", 20000));

        treeC.add(new Upgrade("Energy - Tier I", "+5 Energy per second", 5000));
        treeC.add(new Upgrade("Energy - Tier II", "+10 Energy per second", 10000));
        treeC.add(new Upgrade("Energy - Tier III", "+15 Energy per second", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "-30% Cooldown reduction\n\nReset the cooldown on all caster's abilities (other than Inspiring Presence.)",
                50000
        );
    }

    int duration = ability.getDuration();

    @Override
    public void a1() {
        ability.setDuration(duration + 1);
    }

    @Override
    public void a2() {
        ability.setDuration(duration + 2);
    }

    @Override
    public void a3() {
        ability.setDuration(duration + 4);
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

    int energyPerSecond = ability.getEnergyPerSecond();

    @Override
    public void c1() {
        ability.setEnergyPerSecond(energyPerSecond + 5);
    }

    @Override
    public void c2() {
        ability.setEnergyPerSecond(energyPerSecond + 10);
    }

    @Override
    public void c3() {
        ability.setEnergyPerSecond(energyPerSecond + 15);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setCooldown(ability.getCooldown() * 0.7f);
        ability.setPveUpgrade(true);
    }
}

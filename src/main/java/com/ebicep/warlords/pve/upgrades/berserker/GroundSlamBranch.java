package com.ebicep.warlords.pve.upgrades.berserker;

import com.ebicep.warlords.abilties.GroundSlam;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class GroundSlamBranch extends AbstractUpgradeBranch<GroundSlam> {

    public GroundSlamBranch(AbilityTree abilityTree, GroundSlam ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+1 Block radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+2 Blocks radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+3 Blocks radius", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "A second Ground Slam will follow after the\ninitial cast of Ground Slam after 0.8s",
                50000
        );
    }

    int slamSize = ability.getSlamSize();

    @Override
    public void a1() {
        ability.setSlamSize(slamSize + 1);
    }

    @Override
    public void a2() {
        ability.setSlamSize(slamSize + 2);
    }

    @Override
    public void a3() {
        ability.setSlamSize(slamSize + 3);
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
    public void util1() {

    }

    @Override
    public void util2() {

    }

    @Override
    public void master() {
        ability.setPveUpgrade(true);
    }
}

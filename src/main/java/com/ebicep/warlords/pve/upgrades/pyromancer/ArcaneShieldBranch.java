package com.ebicep.warlords.pve.upgrades.pyromancer;

import com.ebicep.warlords.abilties.ArcaneShield;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ArcaneShieldBranch extends AbstractUpgradeBranch<ArcaneShield> {

    public ArcaneShieldBranch(AbilityTree abilityTree, ArcaneShield ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeC.add(new Upgrade("Absorption - Tier I", "+15% Max shield health", 5000));
        treeC.add(new Upgrade("Absorption - Tier II", "+30% Max shield health", 10000));
        treeC.add(new Upgrade("Absorption - Tier III", "+60% Max shield health", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Increase duration by 100%",
                50000
        );
    }

    float cooldown = ability.getCooldown();

    @Override
    public void a1() {
        ability.setCooldown(cooldown * 0.9f);
    }

    @Override
    public void a2() {
        ability.setCooldown(cooldown * 0.8f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.6f);
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
        ability.setShieldPercentage(65);
        ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
        ability.updateDescription(null);
    }

    @Override
    public void c2() {
        ability.setShieldPercentage(80);
        ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
        ability.updateDescription(null);
    }

    @Override
    public void c3() {
        ability.setShieldPercentage(110);
        ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
        ability.updateDescription(null);
    }

    @Override
    public void master() {
        ability.setDuration(ability.getDuration() * 2);
        abilityTree.getPlayer().setMaxHealth((int) (abilityTree.getPlayer().getMaxHealth() * 1.5f));
        ability.updateShieldHealth(abilityTree.getPlayer().getSpec());
    }
}

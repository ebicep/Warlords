package com.ebicep.warlords.pve.upgrades.mage.cryomancer;

import com.ebicep.warlords.abilties.FreezingBreath;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class FreezingBreathBranch extends AbstractUpgradeBranch<FreezingBreath> {

    public FreezingBreathBranch(AbilityTree abilityTree, FreezingBreath ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        treeB.add(new Upgrade("Range - Tier I", "+10% Cone range", 5000));
        treeB.add(new Upgrade("Range - Tier II", "+20% Cone range", 10000));
        treeB.add(new Upgrade("Range - Tier III", "+30% Cone range", 20000));

        treeC.add(new Upgrade("Slowness - Tier I", "+10% Slowness", 5000));
        treeC.add(new Upgrade("Slowness - Tier II", "+20% Slowness", 10000));
        treeC.add(new Upgrade("Slowness - Tier III", "+40% Slowness", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+30% Damage\n\nEnemies hit have a chance to be frozen for 2 seconds.",
                500000
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
        ability.setHitbox(11);
        ability.setMaxAnimationTime(16);
    }

    @Override
    public void b2() {
        ability.setHitbox(12);
        ability.setMaxAnimationTime(20);
    }

    @Override
    public void b3() {
        ability.setHitbox(14);
        ability.setMaxAnimationTime(24);
    }

    @Override
    public void c1() {
        ability.setSlowness(45);
    }

    @Override
    public void c2() {
        ability.setSlowness(55);
    }

    @Override
    public void c3() {
        ability.setSlowness(75);
    }

    @Override
    public void master() {
        ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.3f);
        ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.3f);
        ability.setHitbox(18);
        ability.setMaxAnimationTime(32);
        ability.setPveUpgrade(true);
    }
}

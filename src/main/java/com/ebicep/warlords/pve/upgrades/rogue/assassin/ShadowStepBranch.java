package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilties.ShadowStep;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ShadowStepBranch extends AbstractUpgradeBranch<ShadowStep> {

    public ShadowStepBranch(AbilityTree abilityTree, ShadowStep ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+20% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+40% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+80% Damage", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-15% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-30% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-60% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Enemies hit upon landing are immobilized for 1.5\nseconds. Gain 20% speed and 20% knockback resistance\nfor 3 seconds.",
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
        ability.setCooldown(cooldown * 0.85f);
    }

    @Override
    public void c2() {
        ability.setCooldown(cooldown * 0.7f);
    }

    @Override
    public void c3() {
        ability.setCooldown(cooldown * 0.4f);
    }

    @Override
    public void master() {

    }
}

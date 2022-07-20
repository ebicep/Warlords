package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.Boulder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BoulderBranch extends AbstractUpgradeBranch<Boulder> {

    public BoulderBranch(AbilityTree abilityTree, Boulder ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Knockback - Tier I", "+10% Knockback", 5000));
        treeB.add(new Upgrade("Knockback - Tier II", "+20% Knockback", 10000));
        treeB.add(new Upgrade("Knockback - Tier III", "+30% Knockback", 20000));

        treeC.add(new Upgrade("Range - Tier I", "+1 Block hit radius", 5000));
        treeC.add(new Upgrade("Range - Tier II", "+2 Blocks hit radius", 10000));
        treeC.add(new Upgrade("Range - Tier III", "+3 Blocks hit radius", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nIncrease the projectile speed of Boulder by 100%\nand increase the damage by an additional 50%",
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

    double velocity = ability.getVelocity();

    @Override
    public void b1() {
        ability.setVelocity(velocity + .1);
    }

    @Override
    public void b2() {
        ability.setVelocity(velocity + .2);
    }

    @Override
    public void b3() {
        ability.setVelocity(velocity + .3);
    }

    double hitbox = ability.getHitbox();

    @Override
    public void c1() {
        ability.setHitbox(hitbox + 1);
    }

    @Override
    public void c2() {
        ability.setHitbox(hitbox + 2);
    }

    @Override
    public void c3() {
        ability.setHitbox(hitbox + 3);
    }

    @Override
    public void util1() {

    }

    @Override
    public void util2() {

    }

    @Override
    public void master() {
        ability.setEnergyCost(0);
        ability.setBoulderSpeed(ability.getBoulderSpeed() * 2);
        ability.setMinDamageHeal(minDamage * 1.9f);
        ability.setMaxDamageHeal(maxDamage * 1.9f);
    }
}

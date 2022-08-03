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

    double velocity = ability.getVelocity();

    double hitbox = ability.getHitbox();

}

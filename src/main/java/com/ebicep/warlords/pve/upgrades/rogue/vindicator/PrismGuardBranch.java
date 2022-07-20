package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.PrismGuard;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class PrismGuardBranch extends AbstractUpgradeBranch<PrismGuard> {

    public PrismGuardBranch(AbilityTree abilityTree, PrismGuard ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage Reduction - Tier I", "+5% Damage reduction", 5000));
        treeA.add(new Upgrade("Damage Reduction - Tier II", "+10% Damage reduction", 10000));
        treeA.add(new Upgrade("Damage Reduction - Tier III", "+20% Damage reduction", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+25% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+50% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Double the range of Prism Guard, nullify any damage\nthat incoming projectiles would've dealt.",
                50000
        );
    }

    int damageReduction = ability.getDamageReduction();
    int projectileDamageReduction = ability.getProjectileDamageReduction();

    @Override
    public void a1() {
        ability.setDamageReduction(damageReduction + 5);
        ability.setProjectileDamageReduction(projectileDamageReduction + 5);
    }

    @Override
    public void a2() {
        ability.setDamageReduction(damageReduction + 10);
        ability.setProjectileDamageReduction(projectileDamageReduction + 10);
    }

    @Override
    public void a3() {
        ability.setDamageReduction(damageReduction + 20);
        ability.setProjectileDamageReduction(projectileDamageReduction + 20);
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

    int bubbleHealing = ability.getBubbleHealing();
    float bubbleMissingHealing = ability.getBubbleMissingHealing();

    @Override
    public void c1() {
        ability.setBubbleHealing((int) (bubbleHealing * 1.1f));
        ability.setBubbleMissingHealing(bubbleMissingHealing * 1.1f);
    }

    @Override
    public void c2() {
        ability.setBubbleHealing((int) (bubbleHealing * 1.25f));
        ability.setBubbleMissingHealing(bubbleMissingHealing * 1.25f);
    }

    @Override
    public void c3() {
        ability.setBubbleHealing((int) (bubbleHealing * 1.5f));
        ability.setBubbleMissingHealing(bubbleMissingHealing * 1.5f);
    }

    @Override
    public void util1() {

    }

    @Override
    public void util2() {

    }

    @Override
    public void master() {
        ability.setProjectileDamageReduction(100);
        ability.setBubbleRadius(ability.getBubbleRadius() * 2);
    }
}

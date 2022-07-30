package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthenSpikeBranch extends AbstractUpgradeBranch<EarthenSpike> {
    public EarthenSpikeBranch(AbilityTree abilityTree, EarthenSpike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+10% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+20% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+40% Damage", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-10 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-20 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-30 Energy cost", 20000));

        treeC.add(new Upgrade("Speed - Tier I", "+20% Spike Speed", 5000));
        treeC.add(new Upgrade("Speed - Tier II", "+40% Spike Speed", 10000));
        treeC.add(new Upgrade("Speed - Tier III", "+80% Spike Speed", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Splash hit radius\n\nEarthen Spike deals 50% more vertical\nknockback and applies the FLOAT status.\n\nFLOAT: All knockback taken is increased by\n50% for 10 seconds.",
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

    @Override
    public void a4() {

    }

    int energyCost = ability.getEnergyCost();

    @Override
    public void b1() {
        ability.setEnergyCost(energyCost - 10);
    }

    @Override
    public void b2() {
        ability.setEnergyCost(energyCost - 20);
    }

    @Override
    public void b3() {
        ability.setEnergyCost(energyCost - 30);
    }

    @Override
    public void b4() {

    }

    float speed = ability.getSpeed();

    @Override
    public void c1() {
        ability.setSpeed(speed * 1.2f);
    }

    @Override
    public void c2() {
        ability.setSpeed(speed * 1.4f);
    }

    @Override
    public void c3() {
        ability.setSpeed(speed * 1.8f);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setSpikeHitbox(ability.getSpikeHitbox() * 1.5);
        ability.setVerticalVelocity(ability.getVerticalVelocity() * 1.5);
        ability.setPveUpgrade(true);
    }
}

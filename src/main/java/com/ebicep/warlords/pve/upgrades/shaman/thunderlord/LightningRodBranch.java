package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilties.LightningRod;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class LightningRodBranch extends AbstractUpgradeBranch<LightningRod> {

    public LightningRodBranch(AbilityTree abilityTree, LightningRod ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown Reduction", 5000));
        treeA.add(new Upgrade("Cooldown - Tier II", "-15% Cooldown Reduction", 10000));
        treeA.add(new Upgrade("Cooldown - Tier III", "-20% Cooldown Reduction", 20000));

        treeC.add(new Upgrade("Healing - Tier I", "+10% Healing", 5000));
        treeC.add(new Upgrade("Healing - Tier II", "+20% Healing", 10000));
        treeC.add(new Upgrade("Healing - Tier III", "+40% Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "+50% Energy given\n\nLightning Rod increases damage dealt by\n30% for 10 seconds after initial cast.",
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
        ability.setCooldown(cooldown * 0.85f);
    }

    @Override
    public void a3() {
        ability.setCooldown(cooldown * 0.8f);
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

    int healthRestore = ability.getHealthRestore();

    @Override
    public void c1() {
        ability.setHealthRestore(healthRestore + 10);
    }

    @Override
    public void c2() {
        ability.setHealthRestore(healthRestore + 20);
    }

    @Override
    public void c3() {
        ability.setHealthRestore(healthRestore + 40);
    }

    @Override
    public void c4() {

    }

    @Override
    public void master() {
        ability.setEnergyRestore((int) (ability.getEnergyRestore() * 1.5f));
        ability.setPveUpgrade(true);
    }
}

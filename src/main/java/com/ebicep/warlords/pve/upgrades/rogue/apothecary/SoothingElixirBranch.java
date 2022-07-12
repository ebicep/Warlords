package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilties.SoothingElixir;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoothingElixirBranch extends AbstractUpgradeBranch<SoothingElixir> {

    public SoothingElixirBranch(AbilityTree abilityTree, SoothingElixir ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Healing - Tier I", "+15% Healing", 5000));
        treeA.add(new Upgrade("Healing - Tier II", "+30% Healing", 10000));
        treeA.add(new Upgrade("Healing - Tier III", "+60% Healing", 20000));

        treeB.add(new Upgrade("Energy - Tier I", "-5 Energy cost", 5000));
        treeB.add(new Upgrade("Energy - Tier II", "-10 Energy cost", 10000));
        treeB.add(new Upgrade("Energy - Tier III", "-15 Energy cost", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-5% Cooldown Reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-10% Cooldown Reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-20% Cooldown Reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "PLACEHOLDER: Double the duration of Soothing Elixir",
                50000
        );
    }

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();

    @Override
    public void a1() {
        ability.setMinDamageHeal(minHealing * 1.15f);
        ability.setMaxDamageHeal(maxHealing * 1.15f);
    }

    @Override
    public void a2() {
        ability.setMinDamageHeal(minHealing * 1.3f);
        ability.setMaxDamageHeal(maxHealing * 1.3f);
    }

    @Override
    public void a3() {
        ability.setMinDamageHeal(minHealing * 1.6f);
        ability.setMaxDamageHeal(maxHealing * 1.6f);
    }

    float puddleRadius = ability.getPuddleRadius();

    @Override
    public void b1() {
        ability.setPuddleRadius(puddleRadius + 1);
    }

    @Override
    public void b2() {
        ability.setPuddleRadius(puddleRadius + 2);
    }

    @Override
    public void b3() {
        ability.setPuddleRadius(puddleRadius + 3);
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
    public void master() {
        ability.setPuddleDuration(ability.getPuddleDuration() * 2);
    }
}

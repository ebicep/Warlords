package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.HammerOfLight;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HammerOfLightBranch extends AbstractUpgradeBranch<HammerOfLight> {

    public HammerOfLightBranch(AbilityTree abilityTree, HammerOfLight ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Duration - Tier I", "+1s Duration", 5000));
        treeA.add(new Upgrade("Duration - Tier II", "+2s Duration", 10000));
        treeA.add(new Upgrade("Duration - Tier III", "+4s Duration", 20000));

        treeC.add(new Upgrade("Damage/Healing - Tier I", "+10% Damage and Healing", 5000));
        treeC.add(new Upgrade("Damage/Healing - Tier II", "+20% Damage and Healing", 10000));
        treeC.add(new Upgrade("Damage/Healing - Tier III", "+40% Damage and Healing", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "For each ally within the Hammer of Light (including\nyourself), increase damage dealt by Protector's\nStrike by 10%.\n\nFor each ally within the Crown of Light radius,\nincrease the healing of the caster's abilities\nby 20% (maximum 4 allies.)",
                50000
        );
    }

    @Override
    public void a1() {
        ability.setDuration(ability.getDuration() + 1);
    }

    @Override
    public void a2() {
        ability.setDuration(ability.getDuration() + 1);
    }

    @Override
    public void a3() {
        ability.setDuration(ability.getDuration() + 2);
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

    float minHealing = ability.getMinDamageHeal();
    float maxHealing = ability.getMaxDamageHeal();
    float minDamage = ability.getMinDamage();
    float maxDamage = ability.getMaxDamage();

    @Override
    public void c1() {
        ability.setMinDamageHeal(minHealing * 1.1f);
        ability.setMaxDamageHeal(maxHealing * 1.1f);
        ability.setMinDamage(minDamage * 1.1f);
        ability.setMaxDamage(maxDamage * 1.1f);
    }

    @Override
    public void c2() {
        ability.setMinDamageHeal(minHealing * 1.2f);
        ability.setMaxDamageHeal(maxHealing * 1.2f);
        ability.setMinDamage(minDamage * 1.2f);
        ability.setMaxDamage(maxDamage * 1.2f);
    }

    @Override
    public void c3() {
        ability.setMinDamageHeal(minHealing * 1.4f);
        ability.setMaxDamageHeal(maxHealing * 1.4f);
        ability.setMinDamage(minDamage * 1.4f);
        ability.setMaxDamage(maxDamage * 1.4f);
    }

    @Override
    public void master() {

    }
}

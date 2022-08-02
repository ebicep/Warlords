package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.SoulShackle;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SoulShackleBranch extends AbstractUpgradeBranch<SoulShackle> {

    public SoulShackleBranch(AbilityTree abilityTree, SoulShackle ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Damage - Tier I", "+20% Damage", 5000));
        treeA.add(new Upgrade("Damage - Tier II", "+40% Damage", 10000));
        treeA.add(new Upgrade("Damage - Tier III", "+80% Damage", 20000));

        treeB.add(new Upgrade("Silence Duration - Tier I", "+1s Silence duration", 5000));
        treeB.add(new Upgrade("Silence Duration - Tier II", "+2s Silence duration", 10000));
        treeB.add(new Upgrade("Silence Duration - Tier III", "+4s Silence duration", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nSoul Shackle now hits up to 5 enemies in a cone.",
                50000
        );
    }

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    int minTicks = ability.getMinSilenceDurationInTicks();
    int maxTicks = ability.getMaxSilenceDurationInTicks();

    @Override
    public void b1() {
        ability.setMinSilenceDurationInTicks(minTicks + 20);
        ability.setMaxSilenceDurationInTicks(maxTicks + 20);
    }

    @Override
    public void b2() {
        ability.setMinSilenceDurationInTicks(minTicks + 40);
        ability.setMaxSilenceDurationInTicks(maxTicks + 40);
    }

    @Override
    public void b3() {
        ability.setMinSilenceDurationInTicks(minTicks + 80);
        ability.setMaxSilenceDurationInTicks(maxTicks + 80);
    }

    @Override
    public void b4() {

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
    public void c4() {

    }

    @Override
    public void master() {
        ability.setEnergyCost(0);
        ability.setPveUpgrade(true);
    }
}

package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilties.HeartToHeart;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class HeartToHeartBranch extends AbstractUpgradeBranch<HeartToHeart> {

    public HeartToHeartBranch(AbilityTree abilityTree, HeartToHeart ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade("Range - Tier I", "+4 Blocks cast radius", 5000));
        treeA.add(new Upgrade("Range - Tier II", "+8 Blocks cast radius", 10000));
        treeA.add(new Upgrade("Range - Tier III", "+16 Blocks cast radius", 20000));

        treeC.add(new Upgrade("Cooldown - Tier I", "-10% Cooldown reduction", 5000));
        treeC.add(new Upgrade("Cooldown - Tier II", "-20% Cooldown reduction", 10000));
        treeC.add(new Upgrade("Cooldown - Tier III", "-40% Cooldown reduction", 20000));

        masterUpgrade = new Upgrade(
                "Master Upgrade",
                "Remove energy cost\n\nIncrease Avenger's Mark cast range by 4 blocks and energy drain by 100%",
                50000
        );
    }

    int radius = ability.getRadius();
    int verticalRadius = ability.getVerticalRadius();

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

    }
}

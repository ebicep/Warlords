package com.ebicep.warlords.pve.upgrades.arcanist.luminary;

import com.ebicep.warlords.abilities.BeaconOfShadow;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class BeaconOfShadowBranch extends AbstractUpgradeBranch<BeaconOfShadow> {

    float cooldown = ability.getCooldown();
    float radius = ability.getRadius();

    public BeaconOfShadowBranch(AbilityTree abilityTree, BeaconOfShadow ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Zeal - Tier I",
                "-5% Cooldown reduction",
                5000,
                () -> {
                    ability.setCooldown(cooldown * 0.95f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier II",
                "-10% Cooldown reduction",
                10000,
                () -> {
                    ability.setCooldown(cooldown * 0.9f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier III",
                "-15% Cooldown reduction",
                15000,
                () -> {
                    ability.setCooldown(cooldown * 0.85f);
                }
        ));
        treeA.add(new Upgrade(
                "Zeal - Tier IV",
                "-20% Cooldown reduction",
                20000,
                () -> {
                    ability.setCooldown(cooldown * 0.8f);
                }
        ));

        treeB.add(new Upgrade(
                "Scope - Tier I",
                "+0.5 Block radius",
                5000,
                () -> {
                    ability.setRadius(radius + 0.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+1 Block radius",
                10000,
                () -> {
                    ability.setRadius(radius + 1);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+1.5 Block radius",
                15000,
                () -> {
                    ability.setRadius(radius + 1.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+2 Block radius",
                20000,
                () -> {
                    ability.setRadius(radius + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Beacon of Gloom",
                "Beacon of Shadow - Master Upgrade",
                """
                        Increase the damage de-buff on enemies by 20%. Enemies within the radius have their movement speed reduced by 30%. Additionally, the beacon will grant Merciful Hex stacks twice as fast.
                        """,
                50000,
                () -> {
                    ability.setHexIntervalTicks((int) (ability.getHexIntervalTicks() * 0.5f));
                    ability.setDamageReductionPve(ability.getDamageReductionPve() + 20);
                }
        );
    }

}

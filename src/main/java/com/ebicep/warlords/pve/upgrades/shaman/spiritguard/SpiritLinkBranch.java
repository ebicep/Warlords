package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilties.SpiritLink;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SpiritLinkBranch extends AbstractUpgradeBranch<SpiritLink> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    int bounceRange = ability.getBounceRange();

    public SpiritLinkBranch(AbilityTree abilityTree, SpiritLink ability) {
        super(abilityTree, ability);

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+7.5% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.075f);
                    ability.setMaxDamageHeal(maxDamage * 1.075f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+15% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+22.5% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.225f);
                    ability.setMaxDamageHeal(maxDamage * 1.225f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+30% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "+4 Blocks hit radius",
                5000,
                () -> {
                    ability.setBounceRange(bounceRange + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "+6 Blocks hit radius",
                10000,
                () -> {
                    ability.setBounceRange(bounceRange + 6);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "+8 Blocks hit radius",
                15000,
                () -> {
                    ability.setBounceRange(bounceRange + 8);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "+10 Blocks hit radius",
                20000,
                () -> {
                    ability.setBounceRange(bounceRange + 10);
                }
        ));

        masterUpgrade = new Upgrade(
                "Phantasmic Bond",
                "Spirit Link - Master Upgrade",
                "Damage reduction and speed duration per link\nis increased by 50%",
                50000,
                () -> {
                    ability.setDamageReductionDuration(ability.getDamageReductionDuration() * 1.5);
                    ability.setSpeedDuration(ability.getSpeedDuration() * 1.5);
                }
        );
    }
}

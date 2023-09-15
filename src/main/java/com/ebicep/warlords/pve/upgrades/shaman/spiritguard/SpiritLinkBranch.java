package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.SpiritLink;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class SpiritLinkBranch extends AbstractUpgradeBranch<SpiritLink> {

    float minDamage;
    float maxDamage;

    public SpiritLinkBranch(AbilityTree abilityTree, SpiritLink ability) {
        super(abilityTree, ability);
        if (abilityTree.getWarlordsPlayer().isInPve()) {
            ability.setMinDamageHeal(ability.getMinDamageHeal() * 1.2f);
            ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 1.2f);
        }
        minDamage = ability.getMinDamageHeal();
        maxDamage = ability.getMaxDamageHeal();

        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+10% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+20% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.2f);
                    ability.setMaxDamageHeal(maxDamage * 1.2f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+30% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.3f);
                    ability.setMaxDamageHeal(maxDamage * 1.3f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+40% Damage",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Scope - Tier I",
                "+4 Blocks hit radius",
                5000,
                () -> {
                    ability.setBounceRange(ability.getBounceRange() + 4);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier II",
                "+6 Blocks hit radius",
                10000,
                () -> {
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier III",
                "+8 Blocks hit radius",
                15000,
                () -> {
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));
        treeB.add(new Upgrade(
                "Scope - Tier IV",
                "+10 Blocks hit radius",
                20000,
                () -> {
                    ability.setBounceRange(ability.getBounceRange() + 2);
                }
        ));

        masterUpgrade = new Upgrade(
                "Phantasmic Bond",
                "Spirit Link - Master Upgrade",
                "Damage reduction and speed duration have been doubled. Additionally, Spirit Link will bounce 3 times instead of 2.",
                50000,
                () -> {
                    ability.setAdditionalBounces(ability.getAdditionalBounces() + 1);
                    ability.setDamageReductionDuration(ability.getDamageReductionDuration() * 2);
                    ability.setSpeedDuration(ability.getSpeedDuration() * 2);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Puppet Strings",
                "Spirit Link - Master Upgrade",
                """
                        Spirit Link now pulls the aggro of targets hit. Additionally, Spirit Link will bounce 4 more times instead of 2, and Soulbound targets no longer count as a bounce.
                        """,
                50000,
                () -> {
                    ability.setAdditionalBounces(ability.getAdditionalBounces() + 2);
                }
        );
    }
}

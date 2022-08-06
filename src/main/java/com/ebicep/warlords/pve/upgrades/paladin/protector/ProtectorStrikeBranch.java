package com.ebicep.warlords.pve.upgrades.paladin.protector;

import com.ebicep.warlords.abilties.ProtectorsStrike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class ProtectorStrikeBranch extends AbstractUpgradeBranch<ProtectorsStrike> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();

    public ProtectorStrikeBranch(AbilityTree abilityTree, ProtectorsStrike ability) {
        super(abilityTree, ability);
        treeA.add(new Upgrade(
                "Impair - Tier I",
                "+3.75% Damage",
                5000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.0375f);
                    ability.setMaxDamageHeal(maxDamage * 1.0375f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier II",
                "+8% Damage",
                10000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.08f);
                    ability.setMaxDamageHeal(maxDamage * 1.08f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier III",
                "+11.25% Damage",
                15000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.1125f);
                    ability.setMaxDamageHeal(maxDamage * 1.1125f);
                }
        ));
        treeA.add(new Upgrade(
                "Impair - Tier IV",
                "+15% Damage\n+1 Block strike hit radius",
                20000,
                () -> {
                    ability.setMinDamageHeal(minDamage * 1.15f);
                    ability.setMaxDamageHeal(maxDamage * 1.15f);
                    ability.setHitbox(ability.getHitbox() + 1);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-2.5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 2.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-5 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-7.5 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 7.5f);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-10 Energy cost\n+1 Healing target",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                    ability.setMaxAllies(ability.getMaxAllies() + 1);
                }
        ));

        masterUpgrade = new Upgrade(
                "Alleviating Strike",
                "Protector's Strike - Master Upgrade",
                "Increase the healing of Protector's Strike\non the lowest health ally and you by 70%",
                50000,
                () -> {
                    ability.setPveUpgrade(true);
                }
        );
    }
}

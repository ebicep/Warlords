package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilties.EarthenSpike;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;

public class EarthenSpikeBranch extends AbstractUpgradeBranch<EarthenSpike> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float energyCost = ability.getEnergyCost();
    float speed = ability.getSpeed();

    public EarthenSpikeBranch(AbilityTree abilityTree, EarthenSpike ability) {
        super(abilityTree, ability);
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
                    ability.setMinDamageHeal(minDamage * 1.1f);
                    ability.setMaxDamageHeal(maxDamage * 1.1f);
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
                "+40% Damage\n+5 Blocks cast radius",
                20000,
                () -> {
                    ability.setRadius(ability.getRadius() + 5);
                    ability.setMinDamageHeal(minDamage * 1.4f);
                    ability.setMaxDamageHeal(maxDamage * 1.4f);
                }
        ));

        treeB.add(new Upgrade(
                "Spark - Tier I",
                "-5 Energy cost",
                5000,
                () -> {
                    ability.setEnergyCost(energyCost - 5);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier II",
                "-10 Energy cost",
                10000,
                () -> {
                    ability.setEnergyCost(energyCost - 10);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier III",
                "-15 Energy cost",
                15000,
                () -> {
                    ability.setEnergyCost(energyCost - 15);
                }
        ));
        treeB.add(new Upgrade(
                "Spark - Tier IV",
                "-20 Energy cost\n+50% Spike speed",
                20000,
                () -> {
                    ability.setEnergyCost(energyCost - 20);
                    ability.setSpeed(speed * 1.5f);
                }
        ));

        masterUpgrade = new Upgrade(
                "Earthen Rupture",
                "Earthen Spike - Master Upgrade",
                "+1.5 Blocks hit radius\n\nEarthen Spike will emit an additional Earth Rupture on impact that deals 838-1063 damage to all nearby enemies and slow them by 50% for 1 second.",
                50000,
                () -> {
                    ability.setSpikeHitbox(ability.getSpikeHitbox() + 1.5);
                    ability.setPveUpgrade(true);
                }
        );
    }
}

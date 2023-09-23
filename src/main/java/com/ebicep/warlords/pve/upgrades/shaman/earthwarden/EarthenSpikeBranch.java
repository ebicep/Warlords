package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.pve.upgrades.*;

public class EarthenSpikeBranch extends AbstractUpgradeBranch<EarthenSpike> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    float speed = ability.getSpeed();

    public EarthenSpikeBranch(AbilityTree abilityTree, EarthenSpike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DamageUpgradeType() {
                    @Override
                    public void run(float value) {
                        float v = 1 + value / 100;
                        ability.setMinDamageHeal(minDamage * v);
                        ability.setMaxDamageHeal(maxDamage * v);
                    }
                }, 10f)
                .addUpgradeHitBox(ability, 5f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeEnergy(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Spike Speed";
                    }

                    @Override
                    public void run(float value) {
                        value = 1 + value / 100;
                        ability.setSpeed(speed * value);
                    }
                }, 50f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Earthen Rupture",
                "Earthen Spike - Master Upgrade",
                "+1.5 Blocks hit radius\n\nEarthen Spike will emit an additional Earth Rupture on impact that deals 548-695 damage to all nearby enemies and slow them by 35% for 1 second.",
                50000,
                () -> {
                    ability.setSpikeHitbox(ability.getSpikeHitbox() + 1.5);

                }
        );
        masterUpgrade2 = new Upgrade(
                "Earthen Verdancy",
                "Earthen Spike - Master Upgrade",
                """
                        +30 Energy cost
                                                
                        Earthen Spike will now release 3 spikes, enemies hit will be ROOTED.
                                                
                        ROOTED: Enemies effected by this are connected to each other. Damage dealt to a rooted enemy, will damage all other rooted enemies for 30% of the damage taken.
                        """,
                50000,
                () -> {
                }
        );
    }
}

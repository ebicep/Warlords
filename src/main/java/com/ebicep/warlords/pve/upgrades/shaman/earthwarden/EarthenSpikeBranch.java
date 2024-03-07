package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.pve.upgrades.*;

public class EarthenSpikeBranch extends AbstractUpgradeBranch<EarthenSpike> {

    float speed = ability.getSpeed();

    public EarthenSpikeBranch(AbilityTree abilityTree, EarthenSpike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 10f)
                .addUpgradeHitBox(ability, 5f, 4)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                        A single Earthen Spike will be sent out hitting 1 enemy but dealing 4x the damage.
                        Enemies hit are CRIPPLED for 5s, and killed enemies heal the user for 35% of the damage dealt.
                        Critical spikes will refund the caster with 10 energy.
                        """,
                50000,
                () -> {
                    ability.getMinDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 3f);
                    ability.getMaxDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 3f);
                }
        );
    }
}

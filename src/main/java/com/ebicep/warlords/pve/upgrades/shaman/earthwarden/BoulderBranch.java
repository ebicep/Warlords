package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.Boulder;
import com.ebicep.warlords.pve.upgrades.*;

public class BoulderBranch extends AbstractUpgradeBranch<Boulder> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();
    double hitbox = ability.getHitbox();

    public BoulderBranch(AbilityTree abilityTree, Boulder ability) {
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
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeEnergy(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Terrestrial Meteor",
                "Boulder - Master Upgrade",
                "Boulder throws upwards, deals 4x times the damage and increased hit range at the cost of higher energy cost, cooldown, and reduced knockback.",
                50000,
                () -> {
                    ability.setBoulderSpeed(ability.getBoulderSpeed() * 0.25f);
                    ability.getCooldown().addMultiplicativeModifierMult("Terrestrial Meteor", 2);
                    ability.getEnergyCost().addMultiplicativeModifierMult("Master Upgrade Branch", 1.5f);
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 4);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 4);
                    ability.setHitbox(hitbox + 3);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Tectonic Rival",
                "Boulder - Master Upgrade",
                """
                        Boulder will now deal 2x the damage and will explode on impact causing a small earthquake within a 5 block radius. The earthquake deals 450-630 damage.
                        """,
                50000,
                () -> {
                    ability.setMinDamageHeal(ability.getMinDamageHeal() * 2);
                    ability.setMaxDamageHeal(ability.getMaxDamageHeal() * 2);
                }
        );
    }
}

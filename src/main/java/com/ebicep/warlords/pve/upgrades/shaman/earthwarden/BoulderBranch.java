package com.ebicep.warlords.pve.upgrades.shaman.earthwarden;

import com.ebicep.warlords.abilities.Boulder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.Upgrade;
import com.ebicep.warlords.pve.upgrades.UpgradeTreeBuilder;

public class BoulderBranch extends AbstractUpgradeBranch<Boulder> {


    double hitbox = ability.getHitbox();

    public BoulderBranch(AbilityTree abilityTree, Boulder ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                    ability.getMinDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 3);
                    ability.getMaxDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 3);
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
                    ability.getMinDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 1);
                    ability.getMaxDamageHeal().addMultiplicativeModifierAdd("Master Upgrade Branch", 1);
                }
        );
    }
}

package com.ebicep.warlords.pve.upgrades.shaman.thunderlord;

import com.ebicep.warlords.abilities.ChainLightning;
import com.ebicep.warlords.pve.upgrades.*;

public class ChainLightningBranch extends AbstractUpgradeBranch<ChainLightning> {


    int radius = ability.getRadius();
    int bounceRange = ability.getBounceRange();

    public ChainLightningBranch(AbilityTree abilityTree, ChainLightning ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 7.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Blocks Cast and Bounce Range";
                    }

                    @Override
                    public void run(float value) {
                        ability.setRadius((int) (radius + value));
                        ability.setBounceRange((int) (bounceRange + value));
                    }
                }, 2f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Chain Bounce";
                    }

                    @Override
                    public void run(float value) {
                        ability.setAdditionalBounces((int) (ability.getAdditionalBounces() + value));
                    }
                }, 1f, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Electrifying Chains",
                "Chain Lightning - Master Upgrade",
                """
                        2x Chain Bounces
                                                
                        Chain Lightning now deals 10% more damage per bounce instead of less.""",
                50000,
                () -> {
                    ability.setAdditionalBounces(ability.getAdditionalBounces() * 2);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Aftershock",
                "Chain Lightning - Master Upgrade",
                """
                        Chain Lightning will now give enemies hit the SHOCKED status for 3s.
                                                
                        SHOCKED: Enemies that are shocked take 30% more damage from the caster and reduces the caster's ultimate ability cooldown by .5s if killed by the caster.
                        """,
                50000,
                () -> {
                }
        );
    }

}

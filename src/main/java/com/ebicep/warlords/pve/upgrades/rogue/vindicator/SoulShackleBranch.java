package com.ebicep.warlords.pve.upgrades.rogue.vindicator;

import com.ebicep.warlords.abilities.SoulShackle;
import com.ebicep.warlords.pve.upgrades.*;

public class SoulShackleBranch extends AbstractUpgradeBranch<SoulShackle> {

    float minDamage = ability.getMinDamageHeal();
    float maxDamage = ability.getMaxDamageHeal();

    public SoulShackleBranch(AbilityTree abilityTree, SoulShackle ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
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
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);


        masterUpgrade = new Upgrade(
                "Conscience Crush",
                "Soul Shackle - Master Upgrade",
                "Soul Shackle now hits up to 8 enemies in a cone and increase silence duration by 4s.",
                50000,
                () -> {

                    ability.setMinSilenceDurationInTicks(ability.getMinSilenceDurationInTicks() + 80);
                    ability.setMaxSilenceDurationInTicks(ability.getMaxSilenceDurationInTicks() + 80);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Oppressive Chains",
                "Soul Shackle - Master Upgrade",
                """
                        Soul Shackle now hits up to 8 enemies, all enemies hit take 25% more damage from all sources for 3s.
                        """,
                50000,
                () -> {
                }
        );
    }
}

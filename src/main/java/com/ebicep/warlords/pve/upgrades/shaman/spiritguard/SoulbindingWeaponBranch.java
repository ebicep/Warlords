package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.pve.upgrades.*;

public class SoulbindingWeaponBranch extends AbstractUpgradeBranch<Soulbinding> {


    public SoulbindingWeaponBranch(AbilityTree abilityTree, Soulbinding ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgrade(new UpgradeTypes.DurationUpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "s Bind Duration";
                    }

                    @Override
                    public void run(float value) {
                        ability.setBindDuration(ability.getBindDuration() + (int) value);
                    }
                }, 10f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Curse Binding",
                "Soulbinding Weapon - Master Upgrade",
                "Gain 1 energy for each soulbound target hit by Fallen Souls and Spirit Link, increase your own and the allied ability\ncooldown reduction by 0.25s",
                50000,
                () -> {
                    ability.setSelfCooldownReduction(ability.getSelfCooldownReduction() + 0.25f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Free Spirit",
                "Soulbinding Weapon - Master Upgrade",
                """
                        The range of Soulbinding buffs is doubled, and increases the amount of allies affected by 2.
                        """,
                50000,
                () -> {
                    ability.setRadius(ability.getRadius() * 2);
                    ability.setMaxAlliesHit(ability.getMaxAlliesHit() + 2);
                }
        );
    }
}

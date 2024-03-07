package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.SpiritLink;
import com.ebicep.warlords.pve.upgrades.*;
import org.jetbrains.annotations.Nullable;

public class SpiritLinkBranch extends AbstractUpgradeBranch<SpiritLink> {

    int bounceRange = ability.getBounceRange();

    @Override
    public void runOnce() {
        ability.getMinDamageHeal().addMultiplicativeModifierAdd("PvE", .2f);
        ability.getMaxDamageHeal().addMultiplicativeModifierAdd("PvE", .2f);
    }

    public SpiritLinkBranch(AbilityTree abilityTree, SpiritLink ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability, 10f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.NamedUpgradeType() {

                    @Override
                    public String getName() {
                        return "Scope";
                    }

                    @Nullable
                    @Override
                    public String getDescription(double value) {
                        return UpgradeTypes.NamedUpgradeType.super.getDescription(value + 2);
                    }

                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + " Bounce Block Radius";
                    }

                    @Override
                    public void run(float value) {
                        ability.setBounceRange((int) (bounceRange + value + 2));
                    }
                }, 2f)
                .addTo(treeB);

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

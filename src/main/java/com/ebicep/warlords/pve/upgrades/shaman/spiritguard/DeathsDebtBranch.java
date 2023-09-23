package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.DeathsDebt;
import com.ebicep.warlords.pve.upgrades.*;
import org.jetbrains.annotations.Nullable;

public class DeathsDebtBranch extends AbstractUpgradeBranch<DeathsDebt> {

    int radius = ability.getDebtRadius();
    int spiteRadius = ability.getRespiteRadius();

    public DeathsDebtBranch(AbilityTree abilityTree, DeathsDebt ability) {
        super(abilityTree, ability);
        ability.setSelfDamageInPercentPerSecond(ability.getSelfDamageInPercentPerSecond() * 0.5f);

        UpgradeTreeBuilder
                .create()
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
                        return "+" + value + " Block Radius";
                    }

                    @Override
                    public void run(float value) {
                        int rad = (int) value + 2;
                        ability.setDebtRadius(radius + rad);
                        ability.setRespiteRadius(spiteRadius + rad);
                    }
                }, 2f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create()
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Ghoul’s Gamble",
                "Death's Debt - Master Upgrade",
                "Double the damage dealt based on damage taken after Death's Debt ends. Additionally, " +
                        "gain 80% knockback resistance while Spirit's Respite is active and reduce damage taken by an additional 40%",
                50000,
                () -> {

                    ability.setDamagePercent(ability.getDamagePercent() * 2);
                    ability.setSelfDamageInPercentPerSecond(ability.getSelfDamageInPercentPerSecond() * 0.2f);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Death Parade",
                "Death's Debt - Master Upgrade",
                """
                        All enemies struck by Death's Debt are afflicted with Soulbinding, max 6. For every enemy Soulbound by Death's Debt, gain 2.5% damage reduction for 5 seconds.
                        """,
                50000,
                () -> {
                }
        );
    }
}

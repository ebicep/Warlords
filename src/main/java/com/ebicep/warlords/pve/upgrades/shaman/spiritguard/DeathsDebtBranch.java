package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.DeathsDebt;
import com.ebicep.warlords.pve.upgrades.*;
import org.jetbrains.annotations.Nullable;

public class DeathsDebtBranch extends AbstractUpgradeBranch<DeathsDebt> {

    int radius = ability.getDebtRadius();
    int spiteRadius = ability.getRespiteRadius();

    @Override
    public void runOnce() {
        ability.setDelayedDamageTaken(ability.getDelayedDamageTaken() * .5f);
    }

    public DeathsDebtBranch(AbilityTree abilityTree, DeathsDebt ability) {
        super(abilityTree, ability);

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
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Rite of the Unpaid",
                "Death's Debt - Master Upgrade",
                """
                        Death's Debt summons an infernal ritual. Spirit's Respite lasts twice as long but you take nor deal no damage when Death's Debt ends.
        
                        For every 10,000 damage you take while Spirit's Respite is active, the totem releases a ritual wave, reducing nearby allies' cooldowns by 2 seconds and increasing their melee attack speed by 200% for 5s.
                        """,
                50000,
                () -> {
                    ability.setTickDuration(ability.getTickDuration() * 2);
                    ability.setDamagePercent(0);
                    ability.setDelayedDamageTaken(0);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Death Parade",
                "Death's Debt - Master Upgrade",
                """
                        Reduce damage taken by 40% and increase damage dealt based on damage taken by 50%.
                        
                        All enemies struck by Death's Debt are afflicted with Soulbinding, max 10. For every enemy Soulbound by Death's Debt, gain 2.5% damage reduction for 5 seconds.
                        """,
                50000,
                () -> {
                    ability.setDelayedDamageTaken(ability.getDelayedDamageTaken() - 40);
                    ability.setDamagePercent(ability.getDamagePercent() * 1.5f);
                }
        );
    }
}

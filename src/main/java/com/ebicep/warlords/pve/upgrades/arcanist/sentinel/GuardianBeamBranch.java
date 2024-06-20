package com.ebicep.warlords.pve.upgrades.arcanist.sentinel;

import com.ebicep.warlords.abilities.GuardianBeam;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;

public class GuardianBeamBranch extends AbstractUpgradeBranch<GuardianBeam> {

    double maxDistance = ability.getMaxDistance();

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getBeamDamage();
        damage.min().addMultiplicativeModifierAdd("PvE", .3f);
        damage.max().addMultiplicativeModifierAdd("PvE", .3f);
    }

    public GuardianBeamBranch(AbilityTree abilityTree, GuardianBeam ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getBeamDamage(), 5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeCooldown(ability)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+15 Block Range";
                    }

                    @Override
                    public void run(float value) {
                        ability.setMaxDistance(maxDistance + 15);
                    }
                }, 4)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Sentry Beam",
                "Guardian Beam - Master Upgrade",
                """
                        Enemy cooldowns are increased by an additional 3.5s. Additionally, shield health is increased by 25%.
                        """,
                50000,
                () -> {
                    ability.setRuneTimerIncrease(ability.getRuneTimerIncrease() + 3.5f);
                    ability.setShieldPercentSelf(ability.getShieldPercentSelf() + 25);
                    ability.setShieldPercentAlly(ability.getShieldPercentAlly() + 25);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Conservator Beam",
                "Guardian Beam - Master Upgrade",
                """
                        When Guardian Beam hits an enemy, reduce their speed by 25% for 5s. Additionally, when Guardian Beam hits an ally, increase their speed by 25% for 7s.
                        """,
                50000,
                () -> {
                }
        );
    }

}

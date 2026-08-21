package com.ebicep.warlords.pve.upgrades.shaman.spiritguard;

import com.ebicep.warlords.abilities.SpiritLink;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.jetbrains.annotations.Nullable;

public class SpiritLinkBranch extends AbstractUpgradeBranch<SpiritLink> {

    int bounceRange = ability.getBounceRange();

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getLinkDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .2f);
        damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .2f);
    }

    public SpiritLinkBranch(AbilityTree abilityTree, SpiritLink ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability)
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
                "Court of Spirits",
                "Spirit Link - Master Upgrade",
                """
                        Spirit Link Judges enemies hit for 3 seconds.
        
                        Judged enemies are punished based on their actions: damaging you causes 1000 additional damage, damaging allies applies BOUND and damaging nobody slows them by 50% for 3 seconds.
        
                        Judging 4 or more enemies grants Tribunal Guard for 5 seconds. Granting you 20% damage reduction and 40% knockback resistance.
                        """,
                50000,
                () -> {
                    ability.setAdditionalBounces(ability.getAdditionalBounces() + 1);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Puppet Strings",
                "Spirit Link - Master Upgrade",
                """
                        Duration of the damage reduction is doubled.
                        
                        Spirit Link now pulls the aggro of targets hit. Additionally, Spirit Link will bounce 4 more times instead of 2, and Soulbound targets no longer count as a bounce.
                        """,
                50000,
                () -> {
                    ability.setAdditionalBounces(ability.getAdditionalBounces() + 2);
                    ability.setDamageReductionDuration(ability.getDamageReductionDuration() * 2);
                }
        );
    }
}

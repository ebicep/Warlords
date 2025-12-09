package com.ebicep.warlords.pve.upgrades.rogue.apothecary;

import com.ebicep.warlords.abilities.ImpalingStrike;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

public class ImpalingStrikeBranch extends AbstractUpgradeBranch<ImpalingStrike> {

    public ImpalingStrikeBranch(AbilityTree abilityTree, ImpalingStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 7.5f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeEnergy(ability, 2.5f)
                .addUpgrade(new UpgradeTypes.UpgradeType() {
                    @Override
                    public String getDescription0(String value) {
                        return "+" + value + "% Leech Heal";
                    }

                    @Override
                    public void run(float value) {
                        ability.setLeechAmount(leechAmount + value);
                    }
                }, 1.5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Impaling Slash",
                "Impaling Strike - Master Upgrade",
                """
                        -10 Additional energy cost
                        
                        Your Impaling strikes now hit 2 additional targets and triple the damage to enemies afflicted by LEECH""",
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                }
        );
        masterUpgrade2 = new Upgrade(
                "Impaling Assault",
                "Impaling Strike - Master Upgrade",
                """
                        -20 Energy cost
                        
                        Impaling Strike now hits 5 additional targets.
                        """,
                50000,
                () -> {
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -20);
                }
        );
    }

    float leechAmount = ability.getLeechAmount();

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "PvE", .3f);
        ability.setLeechAmount(14);
    }
}

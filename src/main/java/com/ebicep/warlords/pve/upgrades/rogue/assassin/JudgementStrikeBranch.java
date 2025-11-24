package com.ebicep.warlords.pve.upgrades.rogue.assassin;

import com.ebicep.warlords.abilities.JudgementStrike;
import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.*;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import com.ebicep.warlords.util.warlords.modifiablevalues.MultiFloatModifiable;

public class JudgementStrikeBranch extends AbstractUpgradeBranch<JudgementStrike> {


    public JudgementStrikeBranch(AbilityTree abilityTree, JudgementStrike ability) {
        super(abilityTree, ability);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgradeDamage(ability.getDamageValues().getStrikeDamage(), 15f)
                .addTo(treeA);

        UpgradeTreeBuilder
                .create(abilityTree, this)
                .addUpgrade(new UpgradeTypes.HealingUpgradeType() {

                                @Override
                                public String getDescription0(String value) {
                                    return "+" + value + " Healing on Strike Kill";
                                }

                                @Override
                                public void modifyFloatModifiable(FloatModifiable.FloatModifier modifier, float value) {
                                    modifier.setModifier(value);
                                }
                            }, ability.getHealValues().getStrikeHealing().value().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Upgrade Branch", 0), 100f
                )
                .addUpgradeEnergy(ability, 5f)
                .addTo(treeB);

        masterUpgrade = new Upgrade(
                "Death Strike",
                "Judgement Strike - Master Upgrade",
                """
                        +15% Damage
                        +100 Healing on Strike Kill
                        -10 Energy cost
                        
                        Each strike deals 1% of the target's max health as bonus damage. Additionally, strikes guaranteed crit occurs every 2 hits instead of 4.
                        """,
                50000,
                () -> {
                    ability.getDamageValues().getStrikeDamage().min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE,
                            "Master Upgrade Branch", .15f
                    );
                    ability.getDamageValues().getStrikeDamage().max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE,
                            "Master Upgrade Branch", .15f
                    );
                    ability.getHealValues().getStrikeHealing().value().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 100);
                    ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", -10);
                    ability.setStrikeCritInterval(2);

                    abilityTree.getWarlordsPlayer().getCooldownManager().addCooldown(new PermanentCooldown<>(
                            "MAX HP DAMAGE (Death Strike)",
                            null,
                            JudgementStrikeBranch.class,
                            null,
                            abilityTree.getWarlordsPlayer(),
                            CooldownTypes.MASTERY,
                            cm -> {},
                            false
                    ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                                if (event.getCause().equals("Judgement Strike")) {
                                    currentDamageValue.addModifier(50, MultiFloatModifiable.ApplyFloatModifiableType.ADDITIVE, FloatModifiable.ModifierType.ADDITIVE,
                                            "MAX HP DAMAGE (Death Strike)", DamageCheck.clamp(event.getWarlordsEntity().getMaxHealth() * 0.01f)
                                    );
                                }
                            }
                    ));
                }
        );
        masterUpgrade2 = new Upgrade(
                "Judgemental Fury",
                "Judgement Strike - Master Upgrade",
                """
                        +30% Crit multiplier
                        
                        Judgement Strike will now hit twice in one use, the second strike is counted as an additional strike for a guaranteed crit.
                        """,
                50000,
                () -> {
                    ability.getDamageValues().getStrikeDamage().critMultiplier().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Master Upgrade Branch", 30);
                }
        );
    }

    @Override
    public void runOnce() {
        Value.RangedValueCritable damage = ability.getDamageValues().getStrikeDamage();
        damage.min().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        damage.max().addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE, "PvE", .3f);
        ability.setDamageIncreaseHealthThreshold(ability.getDamageIncreaseHealthThreshold() + 30);
    }

}

package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class ForsakenFlux extends BaseSet {

    private int nonUltimateCooldownReductionPercent;
    private int primaryDamagePenaltyPercent;
    private int primaryHealingPenaltyPercent;

    @Override
    public void init() {
        super.init();
        this.nonUltimateCooldownReductionPercent = getValue("nonUltimateCooldownReductionPercent", int.class);
        this.primaryDamagePenaltyPercent = getValue("primaryDamagePenaltyPercent", int.class);
        this.primaryHealingPenaltyPercent = getValue("primaryHealingPenaltyPercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "forsakenFlux";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(nonUltimateCooldownReductionPercent, primaryDamagePenaltyPercent, primaryHealingPenaltyPercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            for (AbstractAbility ability : warlordsPlayer.getAbilities()) {
                if (!(ability instanceof OrangeAbilityIcon)) {
                    ability.getCooldown().addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 - nonUltimateCooldownReductionPercent / 100f
                    );
                }
            }
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    ForsakenFlux.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_HEALING, (event, currentHealValue) -> {
                if (event.getAbility() instanceof WeaponAbilityIcon) {
                    currentHealValue.addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 - primaryHealingPenaltyPercent / 100f
                    );
                }
            }).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                if (event.getAbility() instanceof WeaponAbilityIcon) {
                    currentDamageValue.addModifier(
                            FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER,
                            getName(),
                            1 - primaryDamagePenaltyPercent / 100f
                    );
                }
            }));
        }

    }

}

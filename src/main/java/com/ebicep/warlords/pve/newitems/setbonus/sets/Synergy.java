package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class Synergy extends BaseSet {

    private int dotEffectivenessIncreasePercent;

    @Override
    public void init() {
        super.init();
        this.dotEffectivenessIncreasePercent = getValue("dotEffectivenessIncreasePercent", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "synergy";
    }

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of(dotEffectivenessIncreasePercent);
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            float multiplier = dotEffectivenessIncreasePercent / 100f;
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getName(),
                    null,
                    Synergy.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.ITEM,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_HEALING,
                    (event, currentHealValue) -> {
                        if (event.getFlags().contains(InstanceFlags.DOT)) {
                            currentHealValue.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, getName(), multiplier);
                        }
                    }
            ).addModifier(
                    Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                    (event, currentDamageValue) -> {
                        if (event.getFlags().contains(InstanceFlags.DOT)) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, getName(), multiplier);
                        }
                    }
            ));
        }

    }

}

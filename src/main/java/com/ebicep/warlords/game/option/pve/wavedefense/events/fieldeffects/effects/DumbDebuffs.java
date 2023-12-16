package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffectOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;

public class DumbDebuffs implements FieldEffect {
    @Override
    public String getName() {
        return "Dumb Debuffs";
    }

    @Override
    public String getDescription() {
        return "Each debuff on a mobs will increase the damage they take by 15%. (Max 120%)";
    }

    @Override
    public void onWarlordsEntityCreated(WarlordsEntity player) {
        if (player instanceof WarlordsNPC) {
            player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    "Dumb Debuffs",
                    null,
                    FieldEffectOption.class,
                    null,
                    player,
                    CooldownTypes.FIELD_EFFECT,
                    cooldownManager -> {
                    },
                    false
            ) {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    int debuffDamageBoost = Math.min(event.getWarlordsEntity().getCooldownManager().getDebuffCooldowns(true).size(), 12);
                    return currentDamageValue * (1 + (debuffDamageBoost * .15f));
                }
            });
        }
    }

}

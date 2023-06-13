package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractEnergySeer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;

public class EnergySeerConjurer extends AbstractEnergySeer<EnergySeerConjurer> {

    private int damageIncrease = 10;

    @Override
    public Component getBonus() {
        return Component.text("increase your damage by ")
                        .append(Component.text(damageIncrease + "%", NamedTextColor.RED));
    }

    @Override
    public Class<EnergySeerConjurer> getEnergySeerClass() {
        return EnergySeerConjurer.class;
    }

    @Override
    public EnergySeerConjurer getObject() {
        return new EnergySeerConjurer();
    }

    @Override
    public RegularCooldown<EnergySeerConjurer> getBonusCooldown(@Nonnull WarlordsEntity wp) {
        return new RegularCooldown<>(
                name,
                "SEER",
                getEnergySeerClass(),
                getObject(),
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {

                },
                bonusDuration
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + damageIncrease / 100f);
            }
        };
    }
}

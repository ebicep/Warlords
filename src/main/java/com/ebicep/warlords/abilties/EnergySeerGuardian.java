package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractEnergySeer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;

public class EnergySeerGuardian extends AbstractEnergySeer<EnergySeerGuardian> {

    private int damageResistance = 10;

    @Override
    public Component getBonus() {
        return Component.text("gain ")
                        .append(Component.text(damageResistance + "%", NamedTextColor.YELLOW))
                        .append(Component.text(" damage reduction"));
    }

    @Override
    public Class<EnergySeerGuardian> getEnergySeerClass() {
        return EnergySeerGuardian.class;
    }

    @Override
    public EnergySeerGuardian getObject() {
        return new EnergySeerGuardian();
    }

    @Override
    public RegularCooldown<EnergySeerGuardian> getBonusCooldown(@Nonnull WarlordsEntity wp) {
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
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - damageResistance / 100f);
            }
        };
    }

    public int getDamageResistance() {
        return damageResistance;
    }

    public void setDamageResistance(int damageResistance) {
        this.damageResistance = damageResistance;
    }
}

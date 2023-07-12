package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.AbstractEnergySeer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;

public class EnergySeerLuminary extends AbstractEnergySeer<EnergySeerLuminary> {

    private int critChanceIncrease = 20;

    @Override
    public Component getBonus() {
        return Component.text("increase your Crit Chance by ")
                        .append(Component.text(critChanceIncrease + "%", NamedTextColor.RED));
    }

    @Override
    public Class<EnergySeerLuminary> getEnergySeerClass() {
        return EnergySeerLuminary.class;
    }

    @Override
    public EnergySeerLuminary getObject() {
        return new EnergySeerLuminary();
    }

    @Override
    public RegularCooldown<EnergySeerLuminary> getBonusCooldown(@Nonnull WarlordsEntity wp) {
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
            public float addCritChanceFromAttacker(WarlordsDamageHealingEvent event, float currentCritChance) {
                return currentCritChance + critChanceIncrease;
            }
        };
    }

    public int getCritChanceIncrease() {
        return critChanceIncrease;
    }

    public void setCritChanceIncrease(int critChanceIncrease) {
        this.critChanceIncrease = critChanceIncrease;
    }
}

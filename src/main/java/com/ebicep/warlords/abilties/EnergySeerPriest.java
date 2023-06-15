package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractEnergySeer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import javax.annotation.Nonnull;

public class EnergySeerPriest extends AbstractEnergySeer<EnergySeerPriest> {

    private int critChanceIncrease = 40;

    @Override
    public Component getBonus() {
        return Component.text("increase your critical chance by ")
                        .append(Component.text(critChanceIncrease + "%", NamedTextColor.RED));
    }

    @Override
    public Class<EnergySeerPriest> getEnergySeerClass() {
        return EnergySeerPriest.class;
    }

    @Override
    public EnergySeerPriest getObject() {
        return new EnergySeerPriest();
    }

    @Override
    public RegularCooldown<EnergySeerPriest> getBonusCooldown(@Nonnull WarlordsEntity wp) {
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

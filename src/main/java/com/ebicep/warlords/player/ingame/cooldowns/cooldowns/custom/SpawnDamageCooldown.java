package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;

public class SpawnDamageCooldown extends RegularCooldown<SpawnDamageCooldown> {

    private final float damageBoost;

    public SpawnDamageCooldown(WarlordsEntity from, int tickDuration, float damageBoost) {
        super(
                "Spawn Damage",
                "DMG",
                SpawnDamageCooldown.class,
                null,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {},
                tickDuration
        );
        this.damageBoost = damageBoost;
    }

    @Override
    public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
        return currentDamageValue * damageBoost;
    }

}

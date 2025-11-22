package com.ebicep.warlords.player.ingame.cooldowns.cooldowns.custom;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;

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
        this.addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    currentDamageValue.addAdditiveModifier(name, damageBoost);
                }
        );
    }

}

package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;

public interface Overheal {
    Overheal OVERHEAL_MARKER = new Overheal() {};
    int OVERHEAL_DURATION = 15;

    static void giveOverHeal(WarlordsEntity from, WarlordsEntity to) {
        to.getCooldownManager().removeCooldownByObject(Overheal.OVERHEAL_MARKER);
        to.getCooldownManager().addRegularCooldown(
                "Overheal",
                "OVERHEAL",
                Overheal.class,
                Overheal.OVERHEAL_MARKER,
                from,
                CooldownTypes.BUFF,
                cooldownManager -> {
                },
                Overheal.OVERHEAL_DURATION * 15
        );
    }
}

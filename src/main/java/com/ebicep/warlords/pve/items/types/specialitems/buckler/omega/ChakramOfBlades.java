package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class ChakramOfBlades extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public ChakramOfBlades() {
    }

    public ChakramOfBlades(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Which napkin will you take? The left, or the right?...";
    }

    @Override
    public String getBonus() {
        return "Every 7 enemies killed gain 30 energy.";
    }

    @Override
    public String getName() {
        return "Dirty Chakram";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ChakramOfBlades.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            int kills = 0;

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (event.getAttacker().equals(warlordsPlayer) && isKiller) {
                    kills++;
                    if (kills % 7 == 0) {
                        warlordsPlayer.addEnergy(warlordsPlayer, getName(), 30);
                    }
                }
            }
        });
    }

}

package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class CrescentBulwark extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public CrescentBulwark() {
    }

    public CrescentBulwark(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "It's covered in olive oil. No, it doesn't come off.";
    }

    @Override
    public String getBonus() {
        return "For every mob on the field, increase your damage by 0.25%.";
    }

    @Override
    public String getName() {
        return "Waxing Bulwark";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                CrescentBulwark.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * getDamageBoost();
            }

            private float getDamageBoost() {
                int mobCount = pveOption.mobCount();
                return 1 + (mobCount * .005f);
            }
        });
    }

}

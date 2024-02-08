package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.HandsOfTheHolyCorpse;

import java.util.Set;

public class SamsonsFists extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {
    public SamsonsFists() {

    }

    public SamsonsFists(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                SamsonsFists.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getAbility().isEmpty()) {
                    return currentDamageValue * 1.4f;
                }
                return currentDamageValue;
            }
        });
    }

    @Override
    public String getName() {
        return "Samson's Fists";
    }

    @Override
    public String getBonus() {
        return "Increases your melee damage by 40%.";
    }

    @Override
    public String getDescription() {
        return "Don't cut your hair!";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new HandsOfTheHolyCorpse(statPool);
    }
}

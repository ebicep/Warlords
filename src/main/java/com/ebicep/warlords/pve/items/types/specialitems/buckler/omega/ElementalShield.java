package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;

import java.util.Set;

public class ElementalShield extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public ElementalShield() {
    }

    public ElementalShield(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Not too sure how to hold this, good luck!";
    }

    @Override
    public String getBonus() {
        return "Damage done to a target will heal nearby allies around that target for 5% of the damage inflicted.";
    }

    @Override
    public String getName() {
        return "Elemental Shield";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ElementalShield.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false
        ) {

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                float healAmount = currentDamageValue * .05f;
                PlayerFilter.entitiesAround(event.getWarlordsEntity(), 3, 3, 3)
                            .aliveTeammatesOfExcludingSelf(warlordsPlayer)
                            .forEach(warlordsEntity -> {
                                warlordsEntity.addHealingInstance(
                                        warlordsEntity,
                                        ElementalShield.this.getName(),
                                        healAmount,
                                        healAmount,
                                        isCrit ? 100 : 0,
                                        100
                                );
                            });
            }
        });
    }

}

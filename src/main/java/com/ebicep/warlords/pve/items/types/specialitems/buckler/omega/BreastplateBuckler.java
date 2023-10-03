package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BreastplateBuckler extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public BreastplateBuckler() {
    }

    public BreastplateBuckler(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Pairs nicely with a crown of thorns.";
    }

    @Override
    public String getBonus() {
        return "Repeated attacks from same target deal 2% less damage, up to 20%.";
    }

    @Override
    public String getName() {
        return "Breastplate Buckler";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        Map<AbstractMob<?>, Integer> repeatedAttacks = new ConcurrentHashMap<>();
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                BreastplateBuckler.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {
                },
                false,
                (cooldown, ticksElapsed) -> {
                    if (ticksElapsed % 40 == 0) {
                        repeatedAttacks.entrySet().removeIf(warlordsEntityIntegerEntry -> !pveOption.getMobs().contains(warlordsEntityIntegerEntry.getKey()));
                    }
                }
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
                    AbstractMob<?> mob = warlordsNPC.getMob();
                    float damageReduction = Math.max(1 - (repeatedAttacks.getOrDefault(mob, 0) * 0.02f), 0.8f);
                    repeatedAttacks.merge(mob, 1, Integer::sum);
                    return currentDamageValue * damageReduction;
                }
                return currentDamageValue;
            }
        });
    }

}

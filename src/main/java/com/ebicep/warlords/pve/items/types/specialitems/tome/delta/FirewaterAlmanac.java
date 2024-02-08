package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.FlemingAlmanac;
import org.bukkit.entity.Entity;

import java.util.Set;

public class FirewaterAlmanac extends SpecialDeltaTome implements CraftsInto {

    public FirewaterAlmanac() {

    }

    public FirewaterAlmanac(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Now Including Ice Spells!";
    }

    @Override
    public String getBonus() {
        return "For every enemy targeted on you gain 1% damage reduction (Max 10%)";
    }

    @Override
    public String getName() {
        return "Firewater Grimiore";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                FirewaterAlmanac.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                int targeted = pveOption.getMobs()
                                        .stream()
                                        .mapToInt(mob -> {
                                            Entity target = mob.getTarget();
                                            return target != null && target.getUniqueId().equals(warlordsPlayer.getUuid()) ? 1 : 0;
                                        })
                                        .sum();
                targeted = Math.min(10, targeted);
                return currentDamageValue * (1 - (targeted * 0.01f));
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new FlemingAlmanac(statPool);
    }
}

package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.CrescentBulwark;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class AerialAegis extends SpecialDeltaBuckler implements CraftsInto {

    public AerialAegis() {
    }

    public AerialAegis(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        new GameRunnable(warlordsPlayer.getGame()) {

            @Override
            public void run() {
                warlordsPlayer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 120, 1, true, false));
            }
        }.runTaskTimer(0, 100);
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                AerialAegis.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!warlordsPlayer.getEntity().isOnGround()) {
                    return currentDamageValue * 0.8f;
                }
                return currentDamageValue;
            }
        });
    }

    @Override
    public String getName() {
        return "Aerial Aegis";
    }

    @Override
    public String getBonus() {
        return "+1 Block Jump Height. Take 20% less damage while in the air.";
    }

    @Override
    public String getDescription() {
        return "It's floating?! How?!";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new CrescentBulwark(statPool);
    }
}

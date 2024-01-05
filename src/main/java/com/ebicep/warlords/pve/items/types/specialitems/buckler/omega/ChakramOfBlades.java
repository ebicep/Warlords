package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.instances.InstanceFlags;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        return "For every target you kill, you have a 2% chance to fully restore yourself.";
    }

    @Override
    public String getName() {
        return "Dirty Chakram";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onFinalDamageHeal(WarlordsDamageHealingFinalEvent event) {
                if (!event.getAttacker().equals(warlordsPlayer) || !event.isDead()) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > .02) {
                    return;
                }
                float healthMissing = warlordsPlayer.getMaxHealth() - warlordsPlayer.getCurrentHealth();
                warlordsPlayer.addHealingInstance(
                        warlordsPlayer,
                        getName(),
                        healthMissing,
                        healthMissing,
                        0,
                        100,
                        EnumSet.of(InstanceFlags.PIERCE)
                );
            }
        });
    }

}

package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ElementalShield extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {
    public ElementalShield(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public ElementalShield() {

    }

    @Override
    public String getName() {
        return "Elemental Shield";
    }

    @Override
    public String getBonus() {
        return "Negates damage done by Primary Attack projectiles.";
    }

    @Override
    public String getDescription() {
        return "No way? No. Yahweh.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (!Utils.isPrimaryProjectile(event.getAbility())) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() < 0.05) {
                    event.setCancelled(true);
                }
            }
        });
    }

}

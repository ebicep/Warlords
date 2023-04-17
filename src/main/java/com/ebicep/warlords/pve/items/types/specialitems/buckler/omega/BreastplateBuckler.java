package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class BreastplateBuckler extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {
    public BreastplateBuckler(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public BreastplateBuckler() {

    }

    @Override
    public String getName() {
        return "Breastplate Buckler";
    }

    @Override
    public String getBonus() {
        return "Increases Damage Reduction by 1% for every 5% of health you lose.";
    }

    @Override
    public String getDescription() {
        return "Pairs nicely with a crown of thorns.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                float damageReduction = Math.max(.8f, getDamageReduction(warlordsPlayer));
                event.setMin(event.getMin() * damageReduction);
                event.setMax(event.getMax() * damageReduction);
            }
        });
    }

    private static float getDamageReduction(WarlordsPlayer warlordsPlayer) {
        float healthLost = warlordsPlayer.getHealth() / warlordsPlayer.getMaxBaseHealth();
        //get health lost in multiples of 5 rounded up to nearest multiple of 5
        float healthLostRounded = (int) Math.ceil(healthLost * 20) * .05f;
        return 1 - healthLostRounded;
    }

}

package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class LovelyOmamori extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public LovelyOmamori() {
    }

    public LovelyOmamori(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "How many items? 10? Ugh.";
    }

    @Override
    public String getBonus() {
        return "50% chance to prevent getting a debuff.";
    }

    @Override
    public String getName() {
        return "Lovely Omamori";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onCooldownAdd(WarlordsAddCooldownEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    return;
                }
                if (event.getAbstractCooldown().getCooldownType() != CooldownTypes.DEBUFF) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > 0.5) {
                    return;
                }
                event.setCancelled(true);
            }

        });
    }

}

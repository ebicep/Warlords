package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveMobDropEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.ChakramOfBlades;
import com.ebicep.warlords.pve.mobs.MobDrop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ShieldOfSnatching extends SpecialDeltaBuckler implements CraftsInto {

    public ShieldOfSnatching() {
    }

    public ShieldOfSnatching(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Kinda looks like a hand... Is that my wallet?";
    }

    @Override
    public String getBonus() {
        return "25% chance of stealing someone else's Zenith Star drop.";
    }

    @Override
    public String getName() {
        return "Shield of Snatching";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {
            int timesStolen = 0;

            @EventHandler
            public void onMobDrop(WarlordsGiveMobDropEvent event) {
                if (Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.getMobDrop() != MobDrop.ZENITH_STAR) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > 0.25) {
                    return;
                }
                if (timesStolen > 0) {
                    return;
                }
                if (warlordsPlayer.getEntity() instanceof Player) {
                    timesStolen++;
                    event.getStolenBy().add(warlordsPlayer);
                }
            }

        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new ChakramOfBlades(statPool);
    }
}

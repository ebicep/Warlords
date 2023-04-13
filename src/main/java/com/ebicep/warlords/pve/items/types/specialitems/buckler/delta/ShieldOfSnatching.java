package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsGiveMobDropEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.mobs.MobDrops;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ShieldOfSnatching extends SpecialDeltaBuckler {

    public ShieldOfSnatching(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public ShieldOfSnatching() {

    }

    @Override
    public String getName() {
        return "Shield of Snatching";
    }

    @Override
    public String getBonus() {
        return "25% chance of stealing someone else's Zenith Star drop.";
    }

    @Override
    public String getDescription() {
        return "Kinda looks like a hand... Is that my wallet?";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onMobDrop(WarlordsGiveMobDropEvent event) {
                if (Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    return;
                }
                if (event.getMobDrop() != MobDrops.ZENITH_STAR) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() > 0.25) {
                    return;
                }
                if (warlordsPlayer.getEntity() instanceof Player) {
                    event.getStolenBy().add(warlordsPlayer);
                }
            }

        });
    }

}

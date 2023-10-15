package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.FlemingAlmanac;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
        return "+10% chance to not deal damage, but also +10% chance to not take damage.";
    }

    @Override
    public String getName() {
        return "Firewater Grimiore";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {


            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (event.isHealingInstance()) {
                    return;
                }
                if (Objects.equals(event.getAttacker(), warlordsPlayer) || Objects.equals(event.getWarlordsEntity(), warlordsPlayer)) {
                    if (ThreadLocalRandom.current().nextDouble() < 0.1) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new FlemingAlmanac(statPool);
    }
}

package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.AthenianAegis;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class PridwensBulwark extends SpecialDeltaBuckler implements CraftsInto {

    public PridwensBulwark() {
        super();
    }

    public PridwensBulwark(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Pridwen's Bulwark";
    }

    @Override
    public String getBonus() {
        return "+25% chance for your red ability to IMMOBILIZE enemies for 1.5 seconds.";
    }

    @Override
    public String getDescription() {
        return "Say hello to Mary!";
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        List<String> redAbility = warlordsPlayer.getAbilities()
                                                .stream().filter(ability -> ability instanceof RedAbilityIcon)
                                                .map(AbstractAbility::getName)
                                                .toList();
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getSource(), warlordsPlayer)) {
                    return;
                }
                if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
                    if (ThreadLocalRandom.current().nextDouble() > 0.25) {
                        return;
                    }
                    if (redAbility.contains(event.getCause())) {
                        //delayed to account for wave kb
                        new GameRunnable(warlordsNPC.getGame()) {

                            @Override
                            public void run() {
                                warlordsNPC.setStunTicks(10);
                            }
                        }.runTaskLater(event.getCause().equals("Seismic Wave") ? 3 : 0);
                    } else if (Objects.equals(event.getCause(), "Reckless Charge")) {
                        event.setMin(event.getMin() * 1.25f);
                        event.setMax(event.getMax() * 1.25f);
                    }
                }
            }

        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new AthenianAegis(statPool);
    }
}

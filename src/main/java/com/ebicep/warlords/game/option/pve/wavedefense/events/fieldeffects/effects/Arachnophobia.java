package com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.effects;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.option.pve.wavedefense.events.fieldeffects.FieldEffect;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventEggSac;
import com.ebicep.warlords.pve.mobs.events.spidersburrow.EventPoisonousSpider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Arachnophobia implements FieldEffect {
    @Override
    public String getName() {
        return "Arachnophobia";
    }

    @Override
    public String getDescription() {
        return "All strikes deal 200% more damage to Egg Sacs and Poisonous Spiders. All healing abilities are increased by 15%.";
    }

    @Override
    public void onStart(Game game) {
        game.registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!(event.getSource() instanceof WarlordsPlayer)) {
                    return;
                }
                if (event.isDamageInstance()) {
                    if (!(event.getWarlordsEntity() instanceof WarlordsNPC)) {
                        return;
                    }
                    AbstractMob mob = ((WarlordsNPC) event.getWarlordsEntity()).getMob();
                    if (!(mob instanceof EventPoisonousSpider) && !(mob instanceof EventEggSac)) {
                        return;
                    }
                    if (event.getCause().contains("Strike")) {
                        event.setMin(event.getMin() * 3);
                        event.setMax(event.getMax() * 3);
                    }
                } else if (event.isHealingInstance()) {
                    event.setMin(event.getMin() * 1.15f);
                    event.setMax(event.getMax() * 1.15f);
                }
            }

//                    @EventHandler
//                    public void onWaveClear(WarlordsGameWaveClearEvent event) {
//                        int waveCleared = event.getWaveCleared();
//                        if (waveCleared != 0 && waveCleared % 5 == 0 && waveCleared <= 25) {
//                            game.warlordsPlayers().forEach(warlordsPlayer -> {
//                                for (AbstractAbility ability : warlordsPlayer.getSpec().getAbilities()) {
//                                    ability.setCooldown(ability.getCooldown() - .5f);
//                                }
//                            });
//                        }
//                    }

        });
    }

}

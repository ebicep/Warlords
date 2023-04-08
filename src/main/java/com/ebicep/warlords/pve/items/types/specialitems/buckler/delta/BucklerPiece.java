package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class BucklerPiece extends SpecialDeltaBuckler {

    @Override
    public String getName() {
        return "Buckler Piece";
    }

    @Override
    public String getBonus() {
        return "Time Warp damages nearby enemies for 50% of the healing it gave.";
    }

    @Override
    public String getDescription() {
        return "Punk Hazard on a plate.";
    }

    @Override
    public Classes getClasses() {
        return Classes.MAGE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHealFinal(WarlordsDamageHealingFinalEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.isDamageInstance()) {
                    return;
                }
                if (Objects.equals(event.getAbility(), "Time Warp")) {
                    float damageValue = event.getValue() * .5f;
                    PlayerFilter.entitiesAround(warlordsPlayer.getLocation(), 5, 5, 5)
                                .aliveEnemiesOf(warlordsPlayer)
                                .forEach(hit -> {
                                    hit.addDamageInstance(
                                            warlordsPlayer,
                                            "Time Warp",
                                            damageValue,
                                            damageValue,
                                            0,
                                            100,
                                            false
                                    );
                                });
                }
            }

        });
    }


}

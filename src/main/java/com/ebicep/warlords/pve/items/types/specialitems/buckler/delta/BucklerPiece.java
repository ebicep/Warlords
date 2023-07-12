package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.ElementalShield;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class BucklerPiece extends SpecialDeltaBuckler implements CraftsInto {

    public BucklerPiece(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public BucklerPiece() {

    }

    @Override
    public String getName() {
        return "Buckler Piece";
    }

    @Override
    public String getBonus() {
        return "50% of the healing received from Time Warp is dealt as damage to nearby enemies.";
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
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
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
                    PlayerFilter.entitiesAround(warlordsPlayer.getLocation(), 2, 2, 2)
                                .aliveEnemiesOf(warlordsPlayer)
                                .forEach(hit -> {
                                    hit.addDamageInstance(
                                            warlordsPlayer,
                                            "Time Warp",
                                            damageValue,
                                            damageValue,
                                            0,
                                            100
                                    );
                                });
                }
            }

        });
    }


    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new ElementalShield(statPool);
    }
}

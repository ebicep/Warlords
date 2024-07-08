package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.GlassKnuckles;
import org.bukkit.util.Vector;

import java.util.Set;

public class PendragonGauntlets extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public PendragonGauntlets() {

    }

    public PendragonGauntlets(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                PendragonGauntlets.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            int hits = 0;

            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (event.getCause().isEmpty()) {
                    hits++;
                    if (hits == 5) {
                        warlordsPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                getName() + " KB",
                                "KB RES",
                                PendragonGauntlets.class,
                                null,
                                warlordsPlayer,
                                CooldownTypes.ITEM,
                                cooldownManager -> {
                                },
                                2 * 20
                        ) {
                            @Override
                            public void multiplyKB(Vector currentVector) {
                                currentVector.multiply(0.35);
                            }
                        });
                        hits = 0;
                    }
                }
            }
        });
    }

    @Override
    public String getDescription() {
        return "For the worthy.";
    }

    @Override
    public String getBonus() {
        return "Hitting any enemy with melee 5 times gives 50% kb res for 2 seconds.";
    }

    @Override
    public String getName() {
        return "Pendragon Gauntlets";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new GlassKnuckles(statPool);
    }
}

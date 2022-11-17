package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsEnergyUsedEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryVigorous extends AbstractLegendaryWeapon {

    private static final int PASSIVE_EFFECT_DURATION = 10;
    private static final int PASSIVE_EFFECT_COOLDOWN = 20;

    public LegendaryVigorous() {
    }

    public LegendaryVigorous(UUID uuid) {
        super(uuid);
    }

    public LegendaryVigorous(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    public String getPassiveEffect() {
        return "+10 Energy per Second for 10 seconds after using 400 energy. Can be triggered every " + (PASSIVE_EFFECT_COOLDOWN + PASSIVE_EFFECT_DURATION) + " seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    @Override
    protected float getHealthBonusValue() {
        return 600;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 4;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.VIGOROUS;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        final AtomicInteger cooldown = new AtomicInteger(0);
        final AtomicDouble energyUsed = new AtomicDouble(0);

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsEnergyUsedEvent event) {
                if (event.getPlayer() != player) {
                    return;
                }
                if (cooldown.get() > 0) {
                    return;
                }
                energyUsed.getAndAdd(event.getEnergyUsed());
                if (energyUsed.get() >= 400) {
                    cooldown.set(PASSIVE_EFFECT_COOLDOWN);
                    energyUsed.set(0);
                    player.getCooldownManager().addCooldown(new RegularCooldown<>(
                            "LegendaryVigorous",
                            "VIGOR",
                            LegendaryVigorous.class,
                            null,
                            player,
                            CooldownTypes.ABILITY,
                            cooldownManager -> {
                            },
                            PASSIVE_EFFECT_DURATION * 20
                    ) {
                        @Override
                        public float addEnergyGainPerTick(float energyGainPerTick) {
                            return energyGainPerTick + 0.5f;
                        }
                    });
                }

            }
        });
        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (cooldown.get() > 0) {
                    cooldown.getAndDecrement();
                }
            }
        }.runTaskTimer(0, 20);
    }
}


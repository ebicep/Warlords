package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsPlayerEnergyUsed;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryVigorous extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 140;
    public static final int MELEE_DAMAGE_MAX = 170;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 180;
    public static final int HEALTH_BONUS = 600;
    public static final int SPEED_BONUS = 10;
    public static final int ENERGY_PER_SECOND_BONUS = 4;

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
    public String getTitle() {
        return "Vigorous";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        final AtomicInteger cooldown = new AtomicInteger(0);
        final AtomicDouble energyUsed = new AtomicDouble(0);

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsPlayerEnergyUsed event) {
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

    @Override
    public String getPassiveEffect() {
        return "+10 Energy per Second for 10 seconds after using 400 energy. Can be triggered every " + (PASSIVE_EFFECT_COOLDOWN + PASSIVE_EFFECT_DURATION) + " seconds.";
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
        this.energyPerSecondBonus = ENERGY_PER_SECOND_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

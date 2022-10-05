package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsPlayerEnergyUsed;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class LegendaryVigorous extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 140;
    public static final int MELEE_DAMAGE_MAX = 170;
    public static final int CRIT_CHANCE = 20;
    public static final int CRIT_MULTIPLIER = 180;
    public static final int HEALTH_BONUS = 600;
    public static final int SPEED_BONUS = 10;
    public static final int ENERGY_PER_SECOND_BONUS = 2;

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
    public String getPassiveEffect() {
        return "+2 Energy per Second for 10 seconds after using 500 energy. Can be triggered once per 30 seconds.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        final int[] cooldown = new int[1];
        final float[] energyUsed = new float[1];

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsPlayerEnergyUsed event) {
                if (event.getPlayer() != player) {
                    return;
                }
                if (cooldown[0] > 0) {
                    return;
                }
                energyUsed[0] += event.getEnergyUsed();
                if (energyUsed[0] >= 500) {
                    cooldown[0] = PASSIVE_EFFECT_COOLDOWN;
                    energyUsed[0] = 0;
                    player.getCooldownManager().addCooldown(new RegularCooldown<LegendaryVigorous>(
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
                            return energyGainPerTick + 0.2f;
                        }
                    });
                }

            }
        });
        new GameRunnable(player.getGame()) {

            @Override
            public void run() {
                if (cooldown[0] > 0) {
                    cooldown[0]--;
                }
            }
        }.runTaskTimer(0, 20);
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

package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.events.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.DamageHealCompleteCooldown;
import com.ebicep.warlords.pve.weapons.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.warlords.GameRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LegendaryVorpal extends AbstractLegendaryWeapon {
    public static final int MELEE_DAMAGE_MIN = 200;
    public static final int MELEE_DAMAGE_MAX = 220;
    public static final int CRIT_CHANCE = 35;
    public static final int CRIT_MULTIPLIER = 245;
    public static final int HEALTH_BONUS = 300;
    public static final int SPEED_BONUS = 14;

    public LegendaryVorpal() {
    }

    public LegendaryVorpal(UUID uuid) {
        super(uuid);
    }

    @Override
    public String getPassiveEffect() {
        return "Every 5th melee hit deals 7x damage, bypassing damage reduction.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);
        new GameRunnable(player.getGame()) {
            int meleeCounter = 0;
            final Set<WarlordsDamageHealingFinalEvent> recordedEvents = new HashSet<>();

            @Override
            public void run() {
                for (WarlordsDamageHealingFinalEvent warlordsDamageHealingFinalEvent : player.getSecondStats().getAllEventsAsAttacker()) {
                    if (recordedEvents.contains(warlordsDamageHealingFinalEvent)) {
                        continue;
                    }
                    recordedEvents.add(warlordsDamageHealingFinalEvent);
                    if (warlordsDamageHealingFinalEvent.getAbility().isEmpty()) {
                        meleeCounter++;
                        if (meleeCounter % 5 == 0) {
                            player.getCooldownManager().addCooldown(new DamageHealCompleteCooldown<LegendaryVorpal>(
                                    "LegendaryVorpal",
                                    null,
                                    LegendaryVorpal.class,
                                    null,
                                    player,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    }
                            ) {
                                @Override
                                public void doBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
                                    event.setMin(event.getMin() * 7);
                                    event.setMax(event.getMax() * 7);
                                    event.setIgnoreReduction(true);
                                }
                            });
                        }
                    }
                }

            }
        }.runTaskTimer(10, 0);
    }

    @Override
    public void generateStats() {
        this.meleeDamage = MELEE_DAMAGE_MIN;
        this.critChance = CRIT_CHANCE;
        this.critMultiplier = CRIT_MULTIPLIER;
        this.healthBonus = HEALTH_BONUS;
        this.speedBonus = SPEED_BONUS;
    }

    @Override
    public int getMeleeDamageRange() {
        return MELEE_DAMAGE_MAX - MELEE_DAMAGE_MIN;
    }
}

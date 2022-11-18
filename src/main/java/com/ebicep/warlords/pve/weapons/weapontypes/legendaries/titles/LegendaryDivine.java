package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryDivine extends AbstractLegendaryWeapon {

    public static final int DAMAGE_BOOST = 20;
    public static final int TARGETS_TO_HIT = 40;
    public static final int DURATION = 30;

    public LegendaryDivine() {
    }

    public LegendaryDivine(UUID uuid) {
        super(uuid);
    }

    public LegendaryDivine(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a " + DAMAGE_BOOST + "% Damage Boost for " + DURATION + " seconds when you deal damage " + TARGETS_TO_HIT + " times. Maximum 3 stacks.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 120;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {

            final AtomicInteger targetsHit = new AtomicInteger(0);
            final AtomicInteger damageBoost = new AtomicInteger(0);
            RegularCooldown<LegendaryDivine> cooldown = null;

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingFinalEvent event) {
                if (!event.getAttacker().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (targetsHit.incrementAndGet() >= TARGETS_TO_HIT) {
                    targetsHit.set(0);
                    damageBoost.set(Math.min(3, damageBoost.get() + 1));
                    if (cooldown == null) {
                        player.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                                "Divine",
                                "DIV 1",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.BUFF,
                                cooldownManager -> {
                                    cooldown = null;
                                    damageBoost.set(0);
                                },
                                DURATION * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * (1 + damageBoost.get() * DAMAGE_BOOST / 100f);
                            }
                        });
                    } else {
                        cooldown.setTicksLeft(DURATION * 20);
                        cooldown.setName("Divine " + damageBoost.get());
                        cooldown.setNameAbbreviation("DIV " + damageBoost.get());
                    }
                }
            }

        });
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.DIVINE;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 100;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 175;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return -7;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }
}

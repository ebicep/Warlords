package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.NumberFormat;
import com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class LegendaryFervent extends AbstractLegendaryWeapon {

    public static final int DAMAGE_BOOST = 20;
    public static final int DAMAGE_TO_TAKE = 5000;
    public static final int DURATION = 30;

    public LegendaryFervent() {
    }

    public LegendaryFervent(UUID uuid) {
        super(uuid);
    }

    public LegendaryFervent(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public String getPassiveEffect() {
        return "Gain a " + DAMAGE_BOOST + "% damage boost for " + DURATION + " seconds when you lose " + NumberFormat.addCommas(DAMAGE_TO_TAKE) +
                " health (Post damage reduction). Maximum 3 stacks.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 190;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        player.getGame().registerEvents(new Listener() {

            final AtomicDouble damageTaken = new AtomicDouble(0);
            final AtomicInteger damageBoost = new AtomicInteger(0);
            RegularCooldown<LegendaryFervent> cooldown = null;

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingFinalEvent event) {
                if (!event.getPlayer().equals(player)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                if (damageTaken.addAndGet(event.getValue()) >= DAMAGE_TO_TAKE) {
                    damageTaken.set(0);
                    damageBoost.set(Math.min(3, damageBoost.get() + 1));

                    if (cooldown == null || !player.getCooldownManager().hasCooldown(cooldown)) {
                        player.getCooldownManager().addCooldown(cooldown = new RegularCooldown<>(
                                "Fervent 1",
                                "FER 1",
                                LegendaryFervent.class,
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
                        cooldown.setName("Fervent " + damageBoost.get());
                        cooldown.setNameAbbreviation("FER " + damageBoost.get());
                    }
                }
            }

        });
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FERVENT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 15;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 200;
    }

    @Override
    protected float getHealthBonusValue() {
        return 800;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 10;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 10;
    }
}

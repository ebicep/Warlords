package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryDivine extends AbstractLegendaryWeapon {

    public static final int DAMAGE_BOOST = 5;
    public static final int TARGETS_TO_HIT = 40;
    public static final int DURATION = 30;

    public static final int ABILITY_DAMAGE_BOOST = 20;
    public static final int ABILITY_DAMAGE_BOOST_PER_UPGRADE = 5;
    public static final int ABILITY_EPS = 20;
    public static final int ABILITY_EPS_PER_UPGRADE = 5;

    public static final int MAX_STACKS = 3;

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
        return "Gain a " + DAMAGE_BOOST + "% damage boost for " + DURATION + " seconds when you deal damage " + TARGETS_TO_HIT + " times." +
                " Maximum 3 stacks.\nWhen at max stacks, shift for 1 second to consume all 3 stacks and gain 25% energy cost reduction for all abilities, " +
                formatTitleUpgrade(ABILITY_DAMAGE_BOOST + ABILITY_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%") + " increased damage, and " +
                formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevel()) + " EPS for 5 seconds.";
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 120;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player) {
        super.applyToWarlordsPlayer(player);

        final AtomicInteger targetsHit = new AtomicInteger(0);
        final AtomicInteger damageBoost = new AtomicInteger(0);
        final AtomicReference<RegularCooldown<LegendaryDivine>> cooldown = new AtomicReference<>(null);

        player.getGame().registerEvents(new Listener() {

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
                    damageBoost.set(Math.min(MAX_STACKS, damageBoost.get() + 1));
                    if (cooldown.get() == null || !player.getCooldownManager().hasCooldown(cooldown.get())) {
                        RegularCooldown<LegendaryDivine> regularCooldown = new RegularCooldown<>(
                                "Divine",
                                "DIV 1",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                    cooldown.set(null);
                                    damageBoost.set(0);
                                },
                                DURATION * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * (1 + damageBoost.get() * DAMAGE_BOOST / 100f);
                            }
                        };
                        cooldown.set(regularCooldown);
                        player.getCooldownManager().addCooldown(regularCooldown);
                    } else {
                        cooldown.get().setTicksLeft(DURATION * 20);
                        cooldown.get().setName("Divine " + damageBoost.get());
                        cooldown.get().setNameAbbreviation("DIV " + damageBoost.get());
                    }
                }
            }

        });

        new GameRunnable(player.getGame()) {

            int shiftTickTime = 0;
            int abilityCooldown = 0;
            HashMap<AbstractAbility, Float> abilityEnergyCostReduction = new HashMap<>();

            @Override
            public void run() {
                if (abilityCooldown > 0) {
                    abilityCooldown--;
                    return;
                }
                if (cooldown.get() == null || !player.getCooldownManager().hasCooldown(cooldown.get())) {
                    return;
                }
                if (player.isSneaking()) {
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.getCooldownManager().removeCooldown(cooldown.get());
                        for (AbstractAbility ability : player.getSpec().getAbilities()) {
                            if (ability.getEnergyCost() > 0) {
                                abilityEnergyCostReduction.put(ability, ability.getEnergyCost() * 0.25f);
                            }
                        }
                        abilityEnergyCost(-1);
                        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Divine Ability",
                                "POOP",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                    abilityEnergyCost(1);
                                },
                                5 * 20
                        ) {
                            @Override
                            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * (1 + (ABILITY_DAMAGE_BOOST + ABILITY_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel()) / 100f);
                            }

                            @Override
                            public float addEnergyGainPerTick(float energyGainPerTick) {
                                return energyGainPerTick + 2.5f;
                            }
                        });
                        abilityCooldown = 40 * 20;
                    }
                } else {
                    shiftTickTime = 0;
                }
            }

            public void abilityEnergyCost(int multiplier) {
                abilityEnergyCostReduction.forEach((abstractAbility, aFloat) -> abstractAbility.setEnergyCost(abstractAbility.getEnergyCost() + aFloat * multiplier));
                player.updateItems();
            }
        }.runTaskTimer(0, 0);
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

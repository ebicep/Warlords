package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryDivine extends AbstractLegendaryWeapon implements PassiveCooldown {

    public static final int DAMAGE_BOOST = 5;
    public static final int TARGETS_TO_HIT = 40;
    public static final int DURATION = 30;

    public static final int ABILITY_DAMAGE_BOOST = 20;
    public static final int ABILITY_DAMAGE_BOOST_PER_UPGRADE = 5;
    public static final int ABILITY_EPS = 20;
    public static final int ABILITY_EPS_PER_UPGRADE = 5;

    public static final int MAX_STACKS = 3;

    @Transient
    private int passiveCooldown = 0;

    public LegendaryDivine() {
    }

    public LegendaryDivine(UUID uuid) {
        super(uuid);
    }

    public LegendaryDivine(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

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
                if (player.getCooldownManager().hasCooldownFromName("Divine Ability")) {
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

            final HashMap<AbstractAbility, Float> abilityEnergyCostReduction = new HashMap<>();
            int shiftTickTime = 0;

            @Override
            public void run() {
                if (passiveCooldown > 0) {
                    passiveCooldown--;
                    if (passiveCooldown <= 0) {
                        shiftTickTime = 0;
                    }
                    return;
                }
                if (cooldown.get() == null || !player.getCooldownManager().hasCooldown(cooldown.get()) || !cooldown.get().getName().equals("Divine 3")) {
                    return;
                }
                if (player.isSneaking()) {
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, .5f + .05f * shiftTickTime);
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                        player.getCooldownManager().removeCooldown(cooldown.get());
                        for (AbstractAbility ability : player.getSpec().getAbilities()) {
                            if (ability.getEnergyCost() > 0) {
                                abilityEnergyCostReduction.put(ability, ability.getEnergyCost() * 0.4f);
                            }
                        }
                        abilityEnergyCost(-1);
                        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Divine Ability",
                                "DIVINE",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                    abilityEnergyCost(1);
                                },
                                6 * 20
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
                        passiveCooldown = 40 * GameRunnable.SECOND;
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
    public String getPassiveEffect() {
        return "Gain a " + DAMAGE_BOOST + "% damage boost for " + DURATION + " seconds when you deal damage " + TARGETS_TO_HIT + " times." +
                " Maximum 3 stacks.\n\nWhen at max stacks, shift for 1 second to consume all 3 stacks and gain 40% energy cost reduction for all abilities, " +
                formatTitleUpgrade(ABILITY_DAMAGE_BOOST + ABILITY_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%") + " increased damage, and " +
                formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevel()) + " EPS for 6 seconds. Can be triggered every 40 seconds.";
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

    @Override
    protected float getMeleeDamageMaxValue() {
        return 120;
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
    public List<Pair<String, String>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(ABILITY_DAMAGE_BOOST + ABILITY_DAMAGE_BOOST_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(ABILITY_DAMAGE_BOOST + ABILITY_DAMAGE_BOOST_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevelUpgraded())
                )
        );
    }

    @Override
    public int getTickCooldown() {
        return passiveCooldown;
    }
}

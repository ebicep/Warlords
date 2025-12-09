package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryDivine extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int DAMAGE_BOOST = 5;
    public static final int TARGETS_TO_HIT = 60;
    public static final int DURATION = 45;

    public static final int ABILITY_DAMAGE_BOOST = 20;
    public static final int ABILITY_ENERGY_COST_REDUCTION = 25;
    public static final int ABILITY_ENERGY_COST_REDUCTION_PER_UPGRADE = 5;
    public static final int ABILITY_EPS = 15;
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
    public TextComponent getPassiveEffect() {
        return Component.text("Gain a " + DAMAGE_BOOST + "% damage boost for " + DURATION + " seconds when you deal damage " + TARGETS_TO_HIT + " times. Maximum 3 stacks.",
                                NamedTextColor.GRAY
                        )
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("When at max stacks, shift for 1 second to consume all 3 stacks and gain "))
                        .append(formatTitleUpgrade(ABILITY_ENERGY_COST_REDUCTION + ABILITY_ENERGY_COST_REDUCTION_PER_UPGRADE * getTitleLevel(), "%"))
                        .append(Component.text(" energy cost reduction for all abilities, " + ABILITY_DAMAGE_BOOST + "% increased damage, and "))
                        .append(formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevel()))
                        .append(Component.text(" EPS for 10 seconds. Can be triggered every 40 seconds."));
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
        return 7;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 3;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return -5;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        this.passiveCooldown = 0;
        final AtomicInteger targetsHit = new AtomicInteger(0);
        final AtomicInteger damageBoost = new AtomicInteger(0);
        final AtomicReference<RegularCooldown<LegendaryDivine>> cooldown = new AtomicReference<>(null);

        float energyCostReduction = -(ABILITY_ENERGY_COST_REDUCTION + ABILITY_ENERGY_COST_REDUCTION_PER_UPGRADE * getTitleLevel()) / 100f;

        player.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHealing(WarlordsDamageHealingFinalEvent event) {
                if (!event.getSource().equals(player)) {
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
                        );
                        regularCooldown.addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), 1 + damageBoost.get() * DAMAGE_BOOST / 100f);
                                }
                        );
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
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, .5f + .05f * shiftTickTime);
                    shiftTickTime++;
                    if (shiftTickTime == 20) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        player.getCooldownManager().removeCooldown(cooldown.get());
                        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
                        for (AbstractAbility ability : player.getSpec().getAbilities()) {
                            if (ability.getEnergyCostValue() > 0) {
                                modifiers.add(ability.getEnergyCost().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER,
                                        "Divine", energyCostReduction
                                ));
                            }
                        }
                        RegularCooldown<LegendaryDivine> divineCooldown = new RegularCooldown<>(
                                "Divine Ability",
                                "DIVINE",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                cooldownManager -> {
                                    modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                                    player.updateItems();
                                },
                                10 * 20
                        );
                        divineCooldown.addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), 1 + ABILITY_DAMAGE_BOOST / 100f);
                                }
                        );
                        divineCooldown.addModifier(Modifier.ENERGY_GAIN_PER_TICK, energyGainPerTick -> energyGainPerTick.addModifier(FloatModifiable.ModifierType.ADDITIVE,
                                        "Divine Ability", 2.5f
                                )
                        );
                        player.getCooldownManager().addCooldown(divineCooldown);
                        passiveCooldown = 40 * GameRunnable.SECOND;
                    }
                } else {
                    shiftTickTime = 0;
                }
            }
        }.runTaskTimer(0, 0);
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
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(ABILITY_ENERGY_COST_REDUCTION + ABILITY_ENERGY_COST_REDUCTION_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(ABILITY_ENERGY_COST_REDUCTION + ABILITY_ENERGY_COST_REDUCTION_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevel()),
                        formatTitleUpgrade(ABILITY_EPS + ABILITY_EPS_PER_UPGRADE * getTitleLevelUpgraded())
                )
        );
    }

    @Override
    public int getCounter() {
        return passiveCooldown / 20;
    }

}

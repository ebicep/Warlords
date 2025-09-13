package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LegendaryFlux extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int FLUX_THRESHOLD = 10;
    public static final int ENERGY_PER_FLUX_BASE = 100;
    public static final int ENERGY_PER_FLUX_DEC_PER_LEVEL = 5;

    public static final float REGEN_BONUS_PERCENT_BASE = 50f;
    public static final float REGEN_BONUS_INC_PER_LEVEL = 5f;

    public static final float CDR_PERCENT_BASE = 30f;
    public static final float CDR_PERCENT_INC_PER_LEVEL = 2.5f;

    public static final int BUFF_DURATION_SECONDS = 6;
    public static final String CDR_MOD_KEY = "Flux Master";

    @Transient
    private double flux = 0.0;
    @Transient
    private boolean buffActive = false;
    @Transient
    private Instant buffEndsAt = Instant.EPOCH;
    @Transient
    private boolean cdrApplied = false;
    @Transient
    private long tickCounter = 0L;
    @Transient
    private final Set<UUID> seenCastIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    @Transient
    private final Map<String, Long> castGateUntilTick = new ConcurrentHashMap<>();

    public LegendaryFlux() {

    }
    public LegendaryFlux(UUID uuid) { super(uuid); }

    public LegendaryFlux(AbstractLegendaryWeapon copy) { super(copy); }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Spending energy grants Flux (", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getEnergyPerFlux()))
                .append(Component.text(" energy per 1 Flux). At " + FLUX_THRESHOLD + " Flux, consume all stacks to gain ", NamedTextColor.GRAY))
                .append(formatTitleUpgrade(getRegenBonusPercent(), "%"))
                .append(Component.text(" more energy per second and ", NamedTextColor.GRAY))
                .append(formatTitleUpgrade(getCdrPercent(), "%"))
                .append(Component.text(" ability cooldown reduction for " + BUFF_DURATION_SECONDS + " seconds.", NamedTextColor.GRAY));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getEnergyPerFluxAtLevel(getTitleLevel())),
                        formatTitleUpgrade(getEnergyPerFluxAtLevel(getTitleLevelUpgraded()))
                ),
                new Pair<>(
                        Component.text("+" + trim(getRegenBonusPercentAtLevel(getTitleLevel())) + "% regen / -" + trim(getCdrPercentAtLevel(getTitleLevel())) + "% CDR", NamedTextColor.GREEN),
                        Component.text("+" + trim(getRegenBonusPercentAtLevel(getTitleLevelUpgraded())) + "% regen / -" + trim(getCdrPercentAtLevel(getTitleLevelUpgraded())) + "% CDR", NamedTextColor.GREEN)
                )
        );
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Flux",
                null,
                LegendaryFlux.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                var ability = event.getAbility();
                if (ability == null) return;

                int cost = (int) Math.max(0, ability.getEnergyCost().getCalculatedValue());

                if (cost <= 0) return;

                UUID castId = event.getUUID();
                if (castId != null) {
                    if (seenCastIds.add(castId)) {
                        flux += (double) cost / getEnergyPerFlux();
                        maybeStartFluxBuff(player);
                    }
                } else {
                    String key = ability.getClass().getName();
                    long gate = castGateUntilTick.getOrDefault(key, 0L);
                    if (tickCounter >= gate) {
                        flux += (double) cost / getEnergyPerFlux();
                        castGateUntilTick.put(key, tickCounter + 2);
                        maybeStartFluxBuff(player);
                    }
                }
            }

            @Override
            public float multiplyEnergyGainPerTick(float energyGainPerTick) {
                if (buffActive) {
                    return energyGainPerTick * (1f + getRegenBonusPercent() / 100f);
                }
                return energyGainPerTick;
            }
        });

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                tickCounter++;

                if (!player.isOnline() || player.isDead()) {
                    if (cdrApplied) {
                        player.getAbilities().forEach(ab ->
                                ab.getCooldown().addMultiplicativeModifierMult(CDR_MOD_KEY, 1f));
                        cdrApplied = false;
                    }
                    flux = 0.0;
                    buffActive = false;
                    seenCastIds.clear();
                    castGateUntilTick.clear();
                    return;
                }

                if (buffActive && Instant.now().isAfter(buffEndsAt)) {
                    buffActive = false;
                    if (cdrApplied) {
                        player.getAbilities().forEach(ab ->
                                ab.getCooldown().addMultiplicativeModifierMult(CDR_MOD_KEY, 1f));
                        cdrApplied = false;
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void maybeStartFluxBuff(WarlordsPlayer player) {
        if (flux < FLUX_THRESHOLD || buffActive) return;
        flux = 0.0;
        buffActive = true;
        buffEndsAt = Instant.now().plus(BUFF_DURATION_SECONDS, ChronoUnit.SECONDS);
        if (!cdrApplied) {
            float mult = 1f - (getCdrPercent() / 100f);
            player.getAbilities().forEach(ab ->
                    ab.getCooldown().addMultiplicativeModifierMult(CDR_MOD_KEY, mult));
            cdrApplied = true;
        }
    }

    private int getEnergyPerFluxAtLevel(int level) {
        return Math.max(5, ENERGY_PER_FLUX_BASE - ENERGY_PER_FLUX_DEC_PER_LEVEL * level);
    }

    private int getEnergyPerFlux() {
        return getEnergyPerFluxAtLevel(getTitleLevel());
    }

    private float getRegenBonusPercentAtLevel(int level) {
        return REGEN_BONUS_PERCENT_BASE + REGEN_BONUS_INC_PER_LEVEL * level;
    }

    private float getRegenBonusPercent() {
        return getRegenBonusPercentAtLevel(getTitleLevel());
    }

    private float getCdrPercentAtLevel(int level) {
        return CDR_PERCENT_BASE + CDR_PERCENT_INC_PER_LEVEL * level;
    }

    private float getCdrPercent() {
        return getCdrPercentAtLevel(getTitleLevel());
    }

    private String trim(double v) {
        String s = String.format(java.util.Locale.US, "%.2f", v);
        if (s.endsWith("00")) return s.substring(0, s.length() - 3);
        if (s.endsWith("0")) return s.substring(0, s.length() - 1);
        return s;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FLUX;
    }

    @Override protected float getMeleeDamageMinValue() { return 140; }
    @Override protected float getMeleeDamageMaxValue() { return 160; }
    @Override protected float getCritChanceValue()     { return 20; }
    @Override protected float getCritMultiplierValue() { return 160; }
    @Override protected float getHealthBonusValue()    { return 1000; }
    @Override protected float getSpeedBonusValue()     { return 7; }

    @Override
    public int getCounter() {
        if (buffActive) {
            return (int) Math.max(0, ChronoUnit.SECONDS.between(Instant.now(), buffEndsAt));
        }
        return (int) Math.floor(Math.min(FLUX_THRESHOLD, flux));
    }
}
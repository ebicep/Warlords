package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LegendaryFlux extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int FLUX_THRESHOLD = 10;
    public static final int ENERGY_PER_FLUX_BASE = 100;
    public static final int ENERGY_PER_FLUX_DEC_PER_LEVEL = 2;

    public static final float REGEN_BONUS_PERCENT_BASE = 50f;
    public static final float REGEN_BONUS_INC_PER_LEVEL = 5f;

    public static final float CDR_PERCENT_BASE = 30f;
    public static final float CDR_PERCENT_INC_PER_LEVEL = 2.5f;

    public static final int BUFF_DURATION_SECONDS = 6;
    public static final String CDR_MOD_KEY = "Flux Master";
    private static final String FLUX_BUFF_NAME = "Flux (Buff)";
    private static final int COOLDOWN = 20;

    @Transient
    private double flux = 0.0;
    @Transient
    private long tickCounter = 0L;
    @Transient
    private final Set<UUID> seenCastIds = Collections.newSetFromMap(new ConcurrentHashMap<>());
    @Transient
    private final Map<String, Long> castGateUntilTick = new ConcurrentHashMap<>();
    private int cooldown = COOLDOWN;

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
                .append(Component.text(" ability cooldown reduction for " + BUFF_DURATION_SECONDS + " seconds. Has a cooldown of " + COOLDOWN + " seconds.", NamedTextColor.GRAY));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getEnergyPerFluxAtLevel(getTitleLevel())),
                        formatTitleUpgrade(getEnergyPerFluxAtLevel(getTitleLevelUpgraded()))
                ),
                new Pair<>(
                        formatTitleUpgrade(REGEN_BONUS_PERCENT_BASE + REGEN_BONUS_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(REGEN_BONUS_PERCENT_BASE + REGEN_BONUS_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(CDR_PERCENT_BASE + CDR_PERCENT_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(CDR_PERCENT_BASE + CDR_PERCENT_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
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
                if (cooldown > 0) {
                    return;
                }

                AbstractAbility ability = event.getAbility();
                if (ability == null) return;

                int cost = (int) Math.max(0, ability.getEnergyCost().getCalculatedValue());
                if (cost <= 0) return;

                UUID castId = event.getUUID();
                if (castId != null) {
                    if (seenCastIds.add(castId)) {
                        addFluxAndMaybeStartBuff(player, cost);
                    }
                } else {
                    // fallback by ability class, gated by ticks
                    String key = ability.getClass().getName();
                    long gate = castGateUntilTick.getOrDefault(key, 0L);
                    if (tickCounter >= gate) {
                        addFluxAndMaybeStartBuff(player, cost);
                        castGateUntilTick.put(key, tickCounter + 2);
                    }
                }
            }

            @Override
            public float multiplyEnergyGainPerTick(float energyGainPerTick) {
                if (hasFluxBuff(player)) {
                    return energyGainPerTick * (1f + getRegenBonusPercent() / 100f);
                }
                return energyGainPerTick;
            }
        });

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                tickCounter++;

                if (tickCounter % 20 == 0 && cooldown > 0) {
                    cooldown--;
                }

                if (!player.isOnline() || player.isDead()) {
                    flux = 0.0;
                    seenCastIds.clear();
                    castGateUntilTick.clear();
                }
            }
        }.runTaskTimer(0, 1);
    }

    private void addFluxAndMaybeStartBuff(WarlordsPlayer player, int energyCost) {
        if (hasFluxBuff(player)) return;
        flux += (double) energyCost / getEnergyPerFlux();
        if (flux >= FLUX_THRESHOLD) {
            flux = 0.0;
            startFluxBuff(player);
        }
    }

    private void startFluxBuff(WarlordsPlayer player) {
        float mult = 1f - (getCdrPercent() / 100f);
        player.getAbilities().forEach(ab ->
                ab.getCooldown().addMultiplicativeModifierMult(CDR_MOD_KEY, mult)
        );

        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                FLUX_BUFF_NAME,
                "FLUX",
                LegendaryFlux.class,
                null,
                player,
                CooldownTypes.BUFF,
                cm -> { // clean up cdr
                    player.getAbilities().forEach(ab ->
                            ab.getCooldown().addMultiplicativeModifierMult(CDR_MOD_KEY, 1f)
                    );
                },
                BUFF_DURATION_SECONDS * 20
        ));
        cooldown = 20;
    }

    private boolean hasFluxBuff(WarlordsPlayer player) {
        return player.getCooldownManager().hasCooldown(FLUX_BUFF_NAME);
    }

    private int getEnergyPerFluxAtLevel(int level) {
        return Math.max(ENERGY_PER_FLUX_DEC_PER_LEVEL, ENERGY_PER_FLUX_BASE - ENERGY_PER_FLUX_DEC_PER_LEVEL * level);
    }

    private int getEnergyPerFlux() {
        return getEnergyPerFluxAtLevel(getTitleLevel());
    }

    private float getRegenBonusPercentAtLevel(int level) {
        return Math.max(REGEN_BONUS_INC_PER_LEVEL, REGEN_BONUS_PERCENT_BASE + REGEN_BONUS_INC_PER_LEVEL * level);
    }

    private float getRegenBonusPercent() {
        return getRegenBonusPercentAtLevel(getTitleLevel());
    }

    private float getCdrPercentAtLevel(int level) {
        return Math.max(CDR_PERCENT_INC_PER_LEVEL, CDR_PERCENT_BASE + CDR_PERCENT_INC_PER_LEVEL * level);
    }

    private float getCdrPercent() {
        return getCdrPercentAtLevel(getTitleLevel());
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FLUX;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 150;
    }
    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }
    @Override
    protected float getCritChanceValue()     {
        return 20;
    }
    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }
    @Override
    protected float getHealthBonusValue() {
        return 600;
    }
    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }
    @Override
    public float getSkillCritMultiplierBonusValue() {
        return 20;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return -4;
    }

    @Override
    public int getCounter() {
        if (cooldown > 0) {
            return cooldown;
        }
        return (int) Math.floor(Math.min(FLUX_THRESHOLD, flux));
    }
}
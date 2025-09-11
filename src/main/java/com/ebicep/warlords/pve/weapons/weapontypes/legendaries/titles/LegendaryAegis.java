package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryAegis extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final float OVERHEAL_CONVERT_PERCENT = 50;
    public static final float OVERHEAL_CONVERT_INC_PER_LEVEL = 5;

    public static final float BARRIER_CAP_PERCENT = 25;
    public static final float BARRIER_CAP_INC_PER_LEVEL = 3;

    public static final int DMG_BONUS_WHILE_BARRIER_PERCENT = 20;

    public static final int PULSE_HEAL_ALLIES_PERCENT = 5;
    public static final int PULSE_RADIUS = 8;
    public static final int PULSE_SLOW_DURATION_TICKS = 40; // 2s
    public static final int PULSE_SLOW_PERCENT = 30;

    public static final int BARRIER_TIMEOUT_SECONDS = 10;
    public static final int PULSE_INTERNAL_COOLDOWN_SECONDS = 8;

    @Transient
    private float barrierPool = 0f;
    @Transient
    private final AtomicReference<Instant> barrierExpireAt = new AtomicReference<>(Instant.EPOCH);
    @Transient
    private final AtomicReference<Instant> pulseReadyAt = new AtomicReference<>(Instant.EPOCH);

    public LegendaryAegis() {

    }

    public LegendaryAegis(UUID uuid) { super(uuid); }

    public LegendaryAegis(AbstractLegendaryWeapon copy) { super(copy); }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Convert ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getOverhealConvertPercent(), "%"))
                .append(Component.text(" of overhealing into a Barrier (absorbs damage) up to ", NamedTextColor.GRAY))
                .append(formatTitleUpgrade(getBarrierCapPercent(), "%"))
                .append(Component.text(" of your max health. While the Barrier persists, deal " + DMG_BONUS_WHILE_BARRIER_PERCENT + "%", NamedTextColor.GRAY))
                .append(Component.text(" more damage. When the Barrier breaks or expires, heal nearby allies for ", NamedTextColor.GRAY))
                .append(Component.text(PULSE_HEAL_ALLIES_PERCENT + "%")
                .append(Component.text(" of your max health and slow enemies by " + PULSE_SLOW_PERCENT + "%", NamedTextColor.GRAY))
                .append(Component.text(" for 2s. Has a cooldown of " + PULSE_INTERNAL_COOLDOWN_SECONDS + " seconds.", NamedTextColor.GRAY)));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(OVERHEAL_CONVERT_PERCENT + OVERHEAL_CONVERT_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(OVERHEAL_CONVERT_PERCENT + OVERHEAL_CONVERT_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(BARRIER_CAP_PERCENT + BARRIER_CAP_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(BARRIER_CAP_PERCENT + BARRIER_CAP_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Aegis",
                null,
                LegendaryAegis.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ) {
            @Override
            public float modifyHealingFromSelf(WarlordsDamageHealingEvent event, float currentHealValue) {
                // replace with actual Shield class
                float max = player.getMaxHealth();
                float cur = player.getCurrentHealth();
                float potential = cur + currentHealValue;
                if (potential > max) {
                    float overheal = potential - max;
                    float gained = overheal * (getOverhealConvertPercent() / 100f);
                    float cap = max * (getBarrierCapPercent() / 100f);
                    barrierPool = Math.min(cap, barrierPool + gained);
                    barrierExpireAt.set(Instant.now().plus(BARRIER_TIMEOUT_SECONDS, ChronoUnit.SECONDS));
                    player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 2, 1.4f);
                }
                return currentHealValue;
            }

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (barrierActive()) {
                    currentDamageValue *= (1f + DMG_BONUS_WHILE_BARRIER_PERCENT / 100f);
                }
                return currentDamageValue;
            }

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                if (!barrierActive()) {
                    return currentDamageValue;
                }
                // replace with actual Shield class
                float absorb = Math.min(barrierPool, currentDamageValue);
                barrierPool -= absorb;
                float leftover = currentDamageValue - absorb;
                if (barrierPool <= 0f) {
                    tryTriggerPulse(player);
                }
                return leftover;
            }
        });

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    barrierPool = 0f;
                    return;
                }
                if (barrierPool > 0f && Instant.now().isAfter(barrierExpireAt.get())) {
                    barrierPool = 0f;
                    tryTriggerPulse(player);
                }
            }
        }.runTaskTimer(0, 10);
    }

    private boolean barrierActive() {
        return barrierPool > 0f && Instant.now().isBefore(barrierExpireAt.get());
    }

    private void tryTriggerPulse(WarlordsPlayer player) {
        Instant now = Instant.now();
        if (now.isBefore(pulseReadyAt.get())) {
            return;
        }
        pulseReadyAt.set(now.plus(PULSE_INTERNAL_COOLDOWN_SECONDS, ChronoUnit.SECONDS));

        float selfMax = player.getMaxHealth();
        float healValue = selfMax * (PULSE_HEAL_ALLIES_PERCENT / 100f);

        for (WarlordsEntity ally : PlayerFilter
                .entitiesAround(player, PULSE_RADIUS, PULSE_RADIUS, PULSE_RADIUS)
                .aliveTeammatesOfExcludingSelf(player)
        ) {
            ally.addInstance(InstanceBuilder
                    .healing()
                    .cause("Aegis Pulse")
                    .source(player)
                    .value(healValue)
            );
        }

        for (WarlordsEntity enemy : PlayerFilter
                .entitiesAround(player, PULSE_RADIUS, PULSE_RADIUS, PULSE_RADIUS)
                .aliveEnemiesOf(player)
        ) {
            enemy.addSpeedModifier(player, "Aegis Weapon", PULSE_SLOW_PERCENT, PULSE_SLOW_DURATION_TICKS);
        }

        player.playSound(player.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 2, 1.6f);
    }

    private float getOverhealConvertPercent() {
        return OVERHEAL_CONVERT_PERCENT + OVERHEAL_CONVERT_INC_PER_LEVEL * getTitleLevel();
    }

    private float getBarrierCapPercent() {
        return BARRIER_CAP_PERCENT + BARRIER_CAP_INC_PER_LEVEL * getTitleLevel();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.AEGIS;
    }

    @Override
    protected float getMeleeDamageMinValue() { return 135; }

    @Override
    protected float getMeleeDamageMaxValue() { return 150; }

    @Override
    protected float getCritChanceValue() { return 35; }

    @Override
    protected float getCritMultiplierValue() { return 150; }

    @Override
    protected float getHealthBonusValue() { return 1100; }

    @Override
    protected float getSpeedBonusValue() { return 5; }

    @Override
    public int getCounter() {
        if (barrierActive()) {
            float cap = getBarrierCapPercent() / 100f * warlordsPlayer.getMaxHealth();
            return cap <= 0 ? 0 : (int) Math.ceil((barrierPool / cap) * 100f);
        }
        Instant now = Instant.now();
        return now.isBefore(pulseReadyAt.get()) ? (int) ChronoUnit.SECONDS.between(now, pulseReadyAt.get()) : 0;
    }
}
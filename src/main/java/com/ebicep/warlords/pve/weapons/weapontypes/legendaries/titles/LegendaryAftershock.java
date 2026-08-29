package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryAftershock extends AbstractLegendaryWeapon implements PassiveCounter {

    private static final class AftershockSession {
        Instant nextReadyAt = Instant.EPOCH;
    }

    public static final int RADIUS_BLOCKS = 5;
    public static final int DURATION_SECONDS = 3;
    public static final int TICK_INTERVAL_TICKS = 5;
    public static final int COOLDOWN_SECONDS = 3;

    public static final float SLOW_PERCENT = 25f;

    public static final float THRESHOLD_PERCENT_BASE = 15f;
    public static final float THRESHOLD_PERCENT_DEC_PER_LEVEL = 1;

    public static final float ZONE_DAMAGE_PERCENT_BASE = 30f;
    public static final float ZONE_DAMAGE_INC_PER_LEVEL = 1.5f;

    @Transient
    private AftershockSession session;

    public LegendaryAftershock() {

    }

    public LegendaryAftershock(UUID uuid) {
        super(uuid);
    }

    public LegendaryAftershock(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Dealing burst damage of at least ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(getThresholdPercent(), "%"))
                .append(Component.text(" of the target’s max health creates an Aftershock zone (5 blocks) at the target for 3 seconds. The zone deals ", NamedTextColor.GRAY))
                .append(formatTitleUpgrade(getZoneDamagePercent(), "%"))
                .append(Component.text(" of the triggering hit over its duration and slows enemies by 25%. Only the first hit can trigger Aftershock. Has a " + COOLDOWN_SECONDS + " second cooldown.", NamedTextColor.GRAY));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getThresholdPercentAtLevel(getTitleLevel()), "%"),
                        formatTitleUpgrade(getThresholdPercentAtLevel(getTitleLevelUpgraded()), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(getZoneDamagePercentAtLevel(getTitleLevel()), "%"),
                        formatTitleUpgrade(getZoneDamagePercentAtLevel(getTitleLevelUpgraded()), "%")
                )
        );
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        this.session = new AftershockSession();
        final AftershockSession session = this.session;

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        "Aftershock",
                        null,
                        LegendaryAftershock.class,
                        null,
                        player,
                        CooldownTypes.WEAPON,
                        cm -> {
                        },
                        false
                ).addModifier(
                        Modifier.ON_OUTGOING_DAMAGE,
                        (event, currentDamageValue, isCrit) -> {
                            Instant now = Instant.now();
                            if (now.isBefore(session.nextReadyAt)) {
                                return;
                            }

                            float targetMax = event.getWarlordsEntity().getMaxHealth();
                            if (targetMax <= 0) {
                                return;
                            }
                            float threshold = targetMax * (getThresholdPercent() / 100f);
                            if (currentDamageValue < threshold) {
                                return;
                            }

                            Location center = event.getWarlordsEntity().getLocation();
                            if (center.getWorld() == null) {
                                return;
                            }

                            float totalZoneDamage = currentDamageValue * (getZoneDamagePercent() / 100f);
                            spawnAftershockZone(player, center.clone(), totalZoneDamage);
                            session.nextReadyAt = now.plus(COOLDOWN_SECONDS, ChronoUnit.SECONDS);
                        })
        );
    }

    private void spawnAftershockZone(WarlordsPlayer owner, Location center, float totalDamage) {
        int totalTicks = Math.max(1, (DURATION_SECONDS * 20) / TICK_INTERVAL_TICKS);
        float damagePerTick = totalDamage / totalTicks;

        new GameRunnable(owner.getGame()) {
            int ticks = 0;

            @Override
            public void run() {
                if (owner.isDead() || center.getWorld() == null) {
                    this.cancel();
                    return;
                }

                PlayerFilter.entitiesAround(center, RADIUS_BLOCKS, RADIUS_BLOCKS, RADIUS_BLOCKS)
                        .aliveEnemiesOf(owner)
                        .forEach(enemy -> {
                            enemy.addInstance(InstanceBuilder
                                    .damage()
                                    .cause("Aftershock")
                                    .source(owner)
                                    .min(damagePerTick)
                                    .max(damagePerTick)
                                    .flags(
                                            InstanceFlags.NO_HEALING_ORBS,
                                            InstanceFlags.NO_HEALING_LEECH,
                                            InstanceFlags.NO_LUST_HEALING,
                                            InstanceFlags.DOT
                                    )
                            );
                            enemy.addSpeedModifier(owner, "Aftershock", -SLOW_PERCENT, DURATION_SECONDS);
                        });
                EffectUtils.drawRing(center, RADIUS_BLOCKS, 2, Particle.FLAME);

                ticks++;
                if (ticks >= totalTicks) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, TICK_INTERVAL_TICKS);
    }

    private float getThresholdPercentAtLevel(int level) {
        return Math.max(1f, THRESHOLD_PERCENT_BASE - THRESHOLD_PERCENT_DEC_PER_LEVEL * level);
    }

    private float getThresholdPercent() {
        return getThresholdPercentAtLevel(getTitleLevel());
    }

    private float getZoneDamagePercentAtLevel(int level) {
        return ZONE_DAMAGE_PERCENT_BASE + ZONE_DAMAGE_INC_PER_LEVEL * level;
    }

    private float getZoneDamagePercent() {
        return getZoneDamagePercentAtLevel(getTitleLevel());
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.AFTERSHOCK;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 160;
    }

    @Override
    protected float getCritChanceValue() {
        return 20;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 8;
    }

    @Override
    public void cleanup() {
        this.session = null;
        super.cleanup();
    }

    @Override
    public int getCounter() {
        if (session == null) {
            return 0;
        }
        Instant now = Instant.now();
        return now.isBefore(session.nextReadyAt) ? (int) Math.ceil(ChronoUnit.MILLIS.between(now, session.nextReadyAt) / 1000d) : 0;
    }
}
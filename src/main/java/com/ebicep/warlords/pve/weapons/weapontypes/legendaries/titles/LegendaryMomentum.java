package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
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
import org.bukkit.Location;
import org.springframework.data.annotation.Transient;

import java.util.List;
import java.util.UUID;

public class LegendaryMomentum extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final float DMG_PER_STACK_PERCENT = 1.25f;
    public static final float DR_PER_STACK_PERCENT = .75f;

    public static final int BASE_MAX_STACKS = 20;
    public static final int MAX_STACKS_INCREASE_PER_LEVEL = 2;

    public static final int BASE_GAIN_INTERVAL_TICKS = 10;

    public static final int DECAY_PER_SECOND = 5;
    public static final int DECAY_INTERVAL_TICKS = 20 / DECAY_PER_SECOND;

    public static final double MOVE_EPSILON_SQ = 0.01;

    @Transient
    private int stacks = 0;
    @Transient
    private int gainCountdown = BASE_GAIN_INTERVAL_TICKS;
    @Transient
    private int decayCountdown = DECAY_INTERVAL_TICKS;
    @Transient
    private Location lastLoc = null;

    public LegendaryMomentum() {

    }

    public LegendaryMomentum(UUID uuid) {
        super(uuid);
    }

    public LegendaryMomentum(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("While moving, gain 1 Momentum every " + BASE_GAIN_INTERVAL_TICKS / 20f + "s", NamedTextColor.GRAY)
                        .append(Component.text(", up to ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getMaxStacks()))
                        .append(Component.text(" stacks. Each stack grants " + DMG_PER_STACK_PERCENT + "%", NamedTextColor.GRAY))
                        .append(Component.text(" damage and " + DR_PER_STACK_PERCENT + "%", NamedTextColor.GRAY))
                        .append(Component.text(" damage reduction. Lose 5 stacks per second when not moving.", NamedTextColor.GRAY));
    }

    private int getMaxStacks() {
        return getMaxStacksAtLevel(getTitleLevel());
    }

    private int getMaxStacksAtLevel(int level) {
        return BASE_MAX_STACKS + MAX_STACKS_INCREASE_PER_LEVEL * level;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.MOMENTUM;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 120;
    }

    @Override
    protected float getHealthBonusValue() {
        return 600;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 20;
    }

    @Override
    protected float getEnergyPerHitBonusValue() {
        return 5;
    }

    @Override
    public float getSkillCritChanceBonusValue() {
        return 5;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        lastLoc = null;

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Momentum",
                null,
                LegendaryMomentum.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ).addModifier(
                Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE,
                (event, currentDamageValue) -> {
                    float mul = 1f + (stacks * DMG_PER_STACK_PERCENT) / 100f;
                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), mul);
                }
        ).addModifier(
                Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE,
                (event, currentDamageValue) -> {
                    float dr = (stacks * DR_PER_STACK_PERCENT) / 100f;
                    dr = Math.min(dr, 0.6f);
                    currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), (1f - dr));
                }
        ));

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    stacks = 0;
                    gainCountdown = getGainIntervalTicks();
                    decayCountdown = DECAY_INTERVAL_TICKS;
                    lastLoc = player.getLocation();
                    return;
                }

                Location now = player.getLocation();
                if (lastLoc == null || now.getWorld() != lastLoc.getWorld()) {
                    lastLoc = now.clone();
                    return;
                }

                boolean moving = now.distanceSquared(lastLoc) > MOVE_EPSILON_SQ;
                lastLoc = now.clone();

                if (moving) {
                    if (--gainCountdown <= 0) {
                        if (stacks < getMaxStacks()) {
                            stacks++;
                        }
                        gainCountdown = getGainIntervalTicks();
                    }
                    decayCountdown = DECAY_INTERVAL_TICKS;
                } else {
                    gainCountdown = getGainIntervalTicks();
                    if (--decayCountdown <= 0) {
                        if (stacks > 0) {
                            stacks = Math.max(0, stacks - 1);
                        }
                        decayCountdown = DECAY_INTERVAL_TICKS;
                    }
                }
            }
        }.runTaskTimer(0, 1);
    }

    private int getGainIntervalTicks() {
        return getGainIntervalTicksAtLevel(getTitleLevel());
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return List.of(
                new Pair<>(
                        formatTitleUpgrade(getMaxStacksAtLevel(getTitleLevel())),
                        formatTitleUpgrade(getMaxStacksAtLevel(getTitleLevelUpgraded()))
                )
        );
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 180;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 180;
    }

    private int getGainIntervalTicksAtLevel(int level) {
        return BASE_GAIN_INTERVAL_TICKS;
    }

    @Override
    public int getCounter() {
        return stacks;
    }

    private double getGainIntervalSeconds() {
        return getGainIntervalSecondsAtLevel(getTitleLevel());
    }

    private double getGainIntervalSecondsAtLevel(int level) {
        return getGainIntervalTicksAtLevel(level) / 20.0;
    }

}
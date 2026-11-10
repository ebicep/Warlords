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

public class LegendaryAnchor extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int STATIONARY_START_TICKS = 20;
    public static final int FORTIFY_GAIN_INTERVAL_TICKS = 10;

    public static final float DR_PER_STACK_PERCENT = 3f;
    public static final float DR_PER_STACK_INC_PER_LEVEL = 0.5f;

    public static final int KB_RESIST_PER_STACK_PERCENT = 10;

    public static final int BASE_MAX_STACKS = 10;

    public static final int HEAL_PER_STACK_PERCENT = 3;

    public static final double MOVE_EPSILON_SQ = 0.01;

    @Transient
    private int stacks = 0;
    @Transient
    private int stationaryTicks = 0;
    @Transient
    private int gainCountdown = FORTIFY_GAIN_INTERVAL_TICKS;
    @Transient
    private Location lastLoc = null;
    @Transient
    private boolean wasStationary = false;

    public LegendaryAnchor() {

    }

    public LegendaryAnchor(UUID uuid) {
        super(uuid);
    }

    public LegendaryAnchor(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("After standing still for 1 second, gain Fortify every 0.5", NamedTextColor.GRAY)
                        .append(Component.text("seconds. Fortify stacks grant you ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getDrPerStack(), "%"))
                        .append(Component.text(" damage reduction and " + KB_RESIST_PER_STACK_PERCENT + "%", NamedTextColor.GRAY))
                        .append(Component.text(" knockback resistance per stack, up to " + BASE_MAX_STACKS, NamedTextColor.GRAY))
                        .append(Component.text(" stacks. Moving consumes all stacks to heal " + HEAL_PER_STACK_PERCENT + "%", NamedTextColor.GRAY))
                        .append(Component.text(" of your max health per stack.", NamedTextColor.GRAY));
    }

    private float getDrPerStack() {
        return DR_PER_STACK_PERCENT + DR_PER_STACK_INC_PER_LEVEL * getTitleLevel();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.ANCHOR;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 90;
    }

    @Override
    protected float getHealthBonusValue() {
        return 1200;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        lastLoc = null;

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Anchor",
                null,
                LegendaryAnchor.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    float dr = (stacks * getDrPerStack()) / 100f;
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getTitleName(), (1f - dr));
                }
        ));

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    stacks = 0;
                    stationaryTicks = 0;
                    gainCountdown = FORTIFY_GAIN_INTERVAL_TICKS;
                    lastLoc = player.getLocation();
                    wasStationary = false;
                    return;
                }

                Location now = player.getLocation();
                if (lastLoc == null || now.getWorld() != lastLoc.getWorld()) {
                    lastLoc = now.clone();
                    return;
                }

                boolean moving = now.distanceSquared(lastLoc) > MOVE_EPSILON_SQ;
                lastLoc = now.clone();

                if (!moving) {
                    stationaryTicks = Math.min(stationaryTicks + 1, Integer.MAX_VALUE);
                    if (stationaryTicks >= STATIONARY_START_TICKS) {
                        if (--gainCountdown <= 0) {
                            if (stacks < getMaxStacks()) {
                                stacks++;
                                player.addKnockbackModifier(player, "Legendary Anchor", -getCurrentKnockbackResistancePercent(), 100000);
                            }
                            gainCountdown = FORTIFY_GAIN_INTERVAL_TICKS;
                        }
                    } else {
                        gainCountdown = FORTIFY_GAIN_INTERVAL_TICKS;
                    }
                } else {
                    if (wasStationary && stacks > 0) {
                        float heal = player.getMaxHealth() * (HEAL_PER_STACK_PERCENT / 100f) * stacks;
                        player.addInstance(com.ebicep.warlords.player.ingame.instances.InstanceBuilder
                                .healing()
                                .cause("Anchor")
                                .source(player)
                                .value(heal)
                        );
                    }
                    player.getKnockback().removeNegativeModifiers();
                    stacks = 0;
                    stationaryTicks = 0;
                    gainCountdown = FORTIFY_GAIN_INTERVAL_TICKS;
                }

                wasStationary = !moving;
            }
        }.runTaskTimer(0, 1);
    }

    private int getMaxStacks() {
        return getMaxStacksAtLevel(getTitleLevel());
    }

    public float getCurrentKnockbackResistancePercent() {
        return Math.min(100f, stacks * KB_RESIST_PER_STACK_PERCENT);
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 120;
    }

    @Override
    protected float getCritChanceValue() {
        return 15;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 130;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return List.of(
                new Pair<>(
                        formatTitleUpgrade(DR_PER_STACK_PERCENT + DR_PER_STACK_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(DR_PER_STACK_PERCENT + DR_PER_STACK_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                )
        );
    }

    private int getMaxStacksAtLevel(int level) {
        return BASE_MAX_STACKS;
    }

    @Override
    public int getCounter() {
        return stacks;
    }

}
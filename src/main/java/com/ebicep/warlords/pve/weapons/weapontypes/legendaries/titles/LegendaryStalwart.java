package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

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

public class LegendaryStalwart extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int UNDER_HP_CHECK = 80;
    public static final int UNDER_HP_CHECK_INCREASE_PER_UPGRADE = 5;
    public static final int EVERY_HP_PERCENT = 15;
    public static final float EVERY_HP_PERCENT_DECREASE_PER_UPGRADE = .5f;

    public static final int REDUCTION_DURATION = 5;
    public static final int COOLDOWN = 30;

    @Transient
    private final AtomicReference<Instant> lastActivated = new AtomicReference<>(Instant.now().minus(COOLDOWN, ChronoUnit.SECONDS));

    public LegendaryStalwart() {
    }

    public LegendaryStalwart(UUID uuid) {
        super(uuid);
    }

    public LegendaryStalwart(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("For every ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getEveryHpPercent(), "%"))
                        .append(Component.text(" of HP under "))
                        .append(formatTitleUpgrade(getUnderHpCheck(), "%"))
                        .append(Component.text(", gain an additional 7.5% damage reduction. Maximum 80% damage reduction."))
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("If your health is currently higher than 80% and you will die from the next source of damage, your " +
                                "health will be set to 5% of your max health and gain 99% damage reduction for 5 seconds. " +
                                "Can be triggered every 30 seconds."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(EVERY_HP_PERCENT - EVERY_HP_PERCENT_DECREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(EVERY_HP_PERCENT - EVERY_HP_PERCENT_DECREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(UNDER_HP_CHECK + UNDER_HP_CHECK_INCREASE_PER_UPGRADE * getTitleLevel(), "%"),
                        formatTitleUpgrade(UNDER_HP_CHECK + UNDER_HP_CHECK_INCREASE_PER_UPGRADE * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 160;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        // 80 - 10 = skip +70% hp = .7
        // 85 - 9.5 = skip +75.5% hp = .75.5
        float upperBoundHP = (getUnderHpCheck() - getEveryHpPercent()) / 100;

        player.getCooldownManager().addCooldown(
                new PermanentCooldown<>(
                        "Stalwart",
                        null,
                        LegendaryStalwart.class,
                        null,
                        player,
                        CooldownTypes.WEAPON,
                        cooldownManager -> {

                        },
                        false
                ) {
                    @Override
                    public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                        if (player.getCurrentHealth() >= player.getMaxHealth() * upperBoundHP) {
                            return currentDamageValue;
                        }
                        float currentHpPercent = player.getCurrentHealth() / player.getMaxHealth();
                        int timesToReduce = (int) ((getUnderHpCheck() - currentHpPercent) / getEveryHpPercent());
                        float reduction = Math.min(timesToReduce * .075f, .8f);
                        return currentDamageValue * (1 - reduction);
                    }

                    @Override
                    public float modifyDamageAfterAllFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                        if (player.getCurrentHealth() <= player.getMaxHealth() * .8) {
                            return currentDamageValue;
                        }
                        if (player.getCurrentHealth() - currentDamageValue > 0) {
                            return currentDamageValue;
                        }
                        if (Instant.now().isBefore(lastActivated.get())) {
                            return currentDamageValue;
                        }
                        lastActivated.set(Instant.now().plus(COOLDOWN, ChronoUnit.SECONDS));
                        player.setCurrentHealth(player.getMaxBaseHealth() * .05f);
                        player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Stalwart",
                                "STALWART",
                                LegendaryDivine.class,
                                null,
                                player,
                                CooldownTypes.WEAPON,
                                cooldownManager -> {
                                },
                                REDUCTION_DURATION * 20
                        ) {
                            @Override
                            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                return currentDamageValue * .01f;
                            }
                        });
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        player.sendMessage(Component.text("Triggered Stalwart! +99% damage reduction for 5s.", NamedTextColor.GREEN));
                        return 0;
                    }
                }
        );

    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.STALWART;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
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
        return 1000;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 7;
    }

    private float getEveryHpPercent() {
        return EVERY_HP_PERCENT - EVERY_HP_PERCENT_DECREASE_PER_UPGRADE * getTitleLevel();
    }

    private int getUnderHpCheck() {
        return UNDER_HP_CHECK + UNDER_HP_CHECK_INCREASE_PER_UPGRADE * getTitleLevel();
    }

    @Override
    public int getCounter() {
        if (Instant.now().isBefore(lastActivated.get())) {
            return (int) ChronoUnit.SECONDS.between(Instant.now(), lastActivated.get());
        }
        return 0;
    }
}

package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class LegendaryHuntsman extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int RANGED_MIN_DISTANCE_BLOCKS = 14;

    public static final float RANGED_BONUS_PERCENT = 20f;
    public static final float RANGED_BONUS_INC_PER_LEVEL = 5f;

    public static final float MELEE_GUARD_DR_PERCENT = 25f;
    public static final float MELEE_GUARD_DR_INC_PER_LEVEL = 2.5f;

    public static final int MELEE_GUARD_DURATION_SECONDS = 4;
    public static final int MELEE_GUARD_ICD_SECONDS = 8;

    @Transient
    private final AtomicBoolean guardActive = new AtomicBoolean(false);
    @Transient
    private final AtomicReference<Instant> guardExpireAt = new AtomicReference<>(Instant.EPOCH);
    @Transient
    private final AtomicReference<Instant> guardNextReadyAt = new AtomicReference<>(Instant.EPOCH);

    public LegendaryHuntsman() {

    }

    public LegendaryHuntsman(UUID uuid) {
        super(uuid);
    }

    public LegendaryHuntsman(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Deal ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getRangedBonusPercent(), "%"))
                        .append(Component.text(" increased damage to enemies 14+ blocks away. Landing a melee hit grants ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getMeleeGuardDrPercent(), "%"))
                        .append(Component.text(" damage reduction for " + MELEE_GUARD_DURATION_SECONDS + "s", NamedTextColor.GRAY))
                        .append(Component.text(" (refreshes). ", NamedTextColor.GRAY))
                        .append(Component.text("Has an " + MELEE_GUARD_ICD_SECONDS + "s", NamedTextColor.GRAY))
                        .append(Component.text(" cooldown.", NamedTextColor.GRAY));
    }

    private float getRangedBonusPercent() {
        return RANGED_BONUS_PERCENT + RANGED_BONUS_INC_PER_LEVEL * getTitleLevel();
    }

    private float getMeleeGuardDrPercent() {
        return MELEE_GUARD_DR_PERCENT + MELEE_GUARD_DR_INC_PER_LEVEL * getTitleLevel();
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.HUNTSMAN;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getHealthBonusValue() {
        return 400;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 15;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 10;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Huntsman",
                null,
                LegendaryHuntsman.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                    if (isTargetFar(player, event, RANGED_MIN_DISTANCE_BLOCKS)) {
                        currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getTitleName(), (1f + getRangedBonusPercent() / 100f));
                    }

                    if (isMeleeHit(event)) {
                        Instant now = Instant.now();
                        if (!now.isBefore(guardNextReadyAt.get())) {
                            guardActive.set(true);
                            guardExpireAt.set(now.plus(MELEE_GUARD_DURATION_SECONDS, ChronoUnit.SECONDS));
                            guardNextReadyAt.set(now.plus(MELEE_GUARD_ICD_SECONDS, ChronoUnit.SECONDS));

                            player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Huntsman Guard",
                                    "HUNT",
                                    LegendaryHuntsman.class,
                                    null,
                                    player,
                                    CooldownTypes.WEAPON,
                                    cm -> guardActive.set(false),
                                    MELEE_GUARD_DURATION_SECONDS * 20
                            ));

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1.8f);
                        } else {
                            if (guardActive.get()) {
                                guardExpireAt.set(now.plus(MELEE_GUARD_DURATION_SECONDS, ChronoUnit.SECONDS));
                                player.getCooldownManager().addCooldown(new RegularCooldown<>(
                                        "Huntsman Guard",
                                        "HUNT",
                                        LegendaryHuntsman.class,
                                        null,
                                        player,
                                        CooldownTypes.WEAPON,
                                        cm -> guardActive.set(false),
                                        MELEE_GUARD_DURATION_SECONDS * 20
                                ));
                            }
                        }
                    }
                }
        ).addModifier(Modifier.MODIFY_INCOMING_DAMAGE_AFTER_INTERVENE, (event, currentDamageValue) -> {
                    if (!guardActive.get()) {
                        return;
                    }

                    if (Instant.now().isAfter(guardExpireAt.get())) {
                        guardActive.set(false);
                        return;
                    }

                    float dr = getMeleeGuardDrPercent() / 100f;
                    dr = Math.min(dr, 0.6f);
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getTitleName(), (1f - dr));
                }
        ));
    }

    private boolean isTargetFar(WarlordsPlayer attacker, WarlordsDamageHealingEvent event, int minBlocks) {
        try {
            Location a = attacker.getLocation();
            Location t = event.getWarlordsEntity().getLocation();
            if (!a.getWorld().equals(t.getWorld())) {
                return false;
            }
            return a.distanceSquared(t) >= (minBlocks * minBlocks);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private boolean isMeleeHit(WarlordsDamageHealingEvent event) {
        try {
            if (event.getAbility() == null) {
                return true;
            }
            String ability = String.valueOf(event.getCause());
            return ability.equalsIgnoreCase("melee") || ability.equalsIgnoreCase("Melee");
        } catch (Throwable ignored) {
        }
        return false;
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
        return 200;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(RANGED_BONUS_PERCENT + RANGED_BONUS_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(RANGED_BONUS_PERCENT + RANGED_BONUS_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(MELEE_GUARD_DR_PERCENT + MELEE_GUARD_DR_INC_PER_LEVEL * getTitleLevel(), "%"),
                        formatTitleUpgrade(MELEE_GUARD_DR_PERCENT + MELEE_GUARD_DR_INC_PER_LEVEL * getTitleLevelUpgraded(), "%")
                )
        );
    }

    @Override
    public int getCounter() {
        Instant now = Instant.now();
        int icd = now.isBefore(guardNextReadyAt.get()) ? (int) ChronoUnit.SECONDS.between(now, guardNextReadyAt.get()) : 0;
        int activeLeft = guardActive.get() && now.isBefore(guardExpireAt.get())
                         ? (int) ChronoUnit.SECONDS.between(now, guardExpireAt.get()) : 0;
        return Math.max(icd, activeLeft);
    }

}

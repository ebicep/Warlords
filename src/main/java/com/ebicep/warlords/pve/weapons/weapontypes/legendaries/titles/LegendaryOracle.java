package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class LegendaryOracle extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int BASE_MAX_CLARITY = 10;
    public static final int MAX_CLARITY_INC_PER_LEVEL = 2;
    public static final int HEAL_PER_STACK_PERCENT = 2;
    public static final float CDR_PER_STACK_SECONDS = 0.25f;
    public static final float CDR_PER_STACK_INC_PER_LEVEL = 0.05f;

    @Transient
    private int clarity = 0;
    @Transient
    private Instant lastDamagedAt = Instant.EPOCH;

    public LegendaryOracle() {
    }

    public LegendaryOracle(UUID uuid) {
        super(uuid);
    }

    public LegendaryOracle(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Each second you avoid damage, gain 1 Clarity (max ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getMaxClarity()))
                        .append(Component.text("). On your next hit, consume all Clarity to heal " + HEAL_PER_STACK_PERCENT + "% max health", NamedTextColor.GRAY))
                        .append(Component.text(" per stack and reduce active ability cooldowns by ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getCdrPerStackSeconds(), "s"))
                        .append(Component.text(" per stack.", NamedTextColor.GRAY));
    }

    private int getMaxClarity() {
        return getMaxClarityAtLevel(getTitleLevel());
    }

    private double getCdrPerStackSeconds() {
        return getCdrPerStackSecondsAtLevel(getTitleLevel());
    }

    private int getMaxClarityAtLevel(int level) {
        return BASE_MAX_CLARITY + MAX_CLARITY_INC_PER_LEVEL * level;
    }

    private double getCdrPerStackSecondsAtLevel(int level) {
        return CDR_PER_STACK_SECONDS + CDR_PER_STACK_INC_PER_LEVEL * level;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.ORACLE;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 140;
    }

    @Override
    protected float getHealthBonusValue() {
        return 500;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 9;
    }

    @Override
    protected float getEnergyPerSecondBonusValue() {
        return 7;
    }

    @Override
    protected float getSkillCritChanceBonusValue() {
        return 6;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 8;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Oracle",
                null,
                LegendaryOracle.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ) {
            @Override
            public void onDamageFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit) {
                if (clarity <= 0) {
                    return;
                }
                int stacks = clarity;
                clarity = 0;

                float heal = player.getMaxHealth() * (HEAL_PER_STACK_PERCENT / 100f) * stacks;
                player.addInstance(com.ebicep.warlords.player.ingame.instances.InstanceBuilder
                        .healing()
                        .cause("Oracle")
                        .source(player)
                        .value(heal)
                );

                float cdr = (float) (stacks * getCdrPerStackSeconds());
                player.getAbilitiesMatching(AbstractAbility.class).forEach(a -> a.subtractCurrentCooldown(cdr));
            }
        }.addModifier(Modifier.DAMAGE_AFTER_INTERVENE_SELF, (event, currentDamageValue) -> {
                    if (currentDamageValue.getCalculatedValue() > 0) {
                        lastDamagedAt = Instant.now();
                    }
                }
        ));

        new GameRunnable(player.getGame()) {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    clarity = 0;
                    lastDamagedAt = Instant.now();
                    return;
                }
                if (Instant.now().minusSeconds(1).isAfter(lastDamagedAt)) {
                    if (clarity < getMaxClarity()) {
                        clarity++;
                    }
                    lastDamagedAt = Instant.now();
                }
            }
        }.runTaskTimer(0, 20);
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 170;
    }

    @Override
    protected float getCritChanceValue() {
        return 25;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 160;
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getMaxClarityAtLevel(getTitleLevel())),
                        formatTitleUpgrade(getMaxClarityAtLevel(getTitleLevelUpgraded()))
                ),
                new Pair<>(
                        formatTitleUpgrade(CDR_PER_STACK_SECONDS + CDR_PER_STACK_INC_PER_LEVEL * getTitleLevel(), "s"),
                        formatTitleUpgrade(CDR_PER_STACK_SECONDS + CDR_PER_STACK_INC_PER_LEVEL * getTitleLevelUpgraded(), "s")
                )
        );
    }

    @Override
    public int getCounter() {
        return clarity;
    }

}

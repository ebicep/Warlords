package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.PassiveCounter;
import com.ebicep.warlords.util.java.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.springframework.data.annotation.Transient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class LegendaryConduit extends AbstractLegendaryWeapon implements PassiveCounter {

    public static final int HEAL_THRESHOLD_PERCENT = 10;

    public static final int ENERGY_ON_TRIGGER_BASE = 15;
    public static final int ENERGY_ON_TRIGGER_INC_PER_LEVEL = 3;

    public static final int ENERGY_ON_PAIR_PROC_BASE = 25;
    public static final int ENERGY_ON_PAIR_PROC_INC_PER_LEVEL = 5;

    public static final double CDR_ON_PAIR_PROC_SECONDS_BASE = 2.5;
    public static final double CDR_ON_PAIR_PROC_INC_PER_LEVEL = 0.5;

    public static final int LINK_DURATION_SECONDS = 3;
    public static final int PAIR_ICD_SECONDS = 8;

    @Transient
    private final Map<UUID, Instant> pairIcdUntil = new ConcurrentHashMap<>();

    public LegendaryConduit() {

    }

    public LegendaryConduit(UUID uuid) {
        super(uuid);
    }

    public LegendaryConduit(AbstractLegendaryWeapon copy) {
        super(copy);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Receiving a heal of at least " + HEAL_THRESHOLD_PERCENT + "% of your max health grants ", NamedTextColor.GRAY)
                        .append(formatTitleUpgrade(getEnergyOnTrigger()))
                        .append(Component.text(" energy and links you to the healer for " + LINK_DURATION_SECONDS + "s. If they deal damage during the link, you both gain ",
                                NamedTextColor.GRAY
                        ))
                        .append(formatTitleUpgrade(getEnergyOnPairProc()))
                        .append(Component.text(" energy and ", NamedTextColor.GRAY))
                        .append(formatTitleUpgrade(getCdrOnPairProcSeconds(), "s"))
                        .append(Component.text(" active ability cooldowns. Has a cooldown of " + PAIR_ICD_SECONDS + " seconds.", NamedTextColor.GRAY));
    }

    private int getEnergyOnTrigger() {
        return getEnergyOnTriggerAtLevel(getTitleLevel());
    }

    private int getEnergyOnPairProc() {
        return getEnergyOnPairProcAtLevel(getTitleLevel());
    }

    private double getCdrOnPairProcSeconds() {
        return getCdrOnPairProcSecondsAtLevel(getTitleLevel());
    }

    private int getEnergyOnTriggerAtLevel(int level) {
        return ENERGY_ON_TRIGGER_BASE + ENERGY_ON_TRIGGER_INC_PER_LEVEL * level;
    }

    private int getEnergyOnPairProcAtLevel(int level) {
        return ENERGY_ON_PAIR_PROC_BASE + ENERGY_ON_PAIR_PROC_INC_PER_LEVEL * level;
    }

    private double getCdrOnPairProcSecondsAtLevel(int level) {
        return CDR_ON_PAIR_PROC_SECONDS_BASE + CDR_ON_PAIR_PROC_INC_PER_LEVEL * level;
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.CONDUIT;
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 120;
    }

    @Override
    protected float getHealthBonusValue() {
        return 350;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 6;
    }

    @Override
    public float getSkillCritMultiplierBonusValue() {
        return 10;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                "Conduit",
                null,
                LegendaryConduit.class,
                null,
                player,
                CooldownTypes.WEAPON,
                cm -> {},
                false
        ).addModifier(Modifier.ON_INCOMING_HEALING, (event, currentHealingValue, isCrit) -> {
                    float threshold = player.getMaxHealth() * (HEAL_THRESHOLD_PERCENT / 100f);
                    if (currentHealingValue < threshold) {
                        return;
                    }

                    player.addEnergy(player, "Conduit Title", getEnergyOnTrigger());

                    WarlordsEntity source = event.getSource();
                    if (source instanceof WarlordsPlayer healer && healer != player && healer.isTeammate(player)) {
                        Instant now = Instant.now();
                        Instant until = pairIcdUntil.getOrDefault(healer.getUuid(), Instant.EPOCH);
                        if (!now.isBefore(until)) {
                            pairIcdUntil.put(healer.getUuid(), now.plus(PAIR_ICD_SECONDS, ChronoUnit.SECONDS));
                            AtomicBoolean granted = new AtomicBoolean(false);
                            healer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Conduit Link",
                                    "CONDUIT",
                                    LegendaryConduit.class,
                                    null,
                                    healer,
                                    CooldownTypes.WEAPON,
                                    cm -> {},
                                    LINK_DURATION_SECONDS * 20
                            ).addModifier(Modifier.OUTGOING_DAMAGE_BEFORE_INTERVENE, (e, currentDamageValue) -> {
                                        if (granted.compareAndSet(false, true)) {
                                            int energy = getEnergyOnPairProc();
                                            float cdr = (float) getCdrOnPairProcSeconds();
                                            healer.addEnergy(player, "Conduit Title", energy);
                                            player.addEnergy(player, "Conduit Title", energy);
                                            healer.getAbilitiesMatching(AbstractAbility.class).forEach(a -> a.subtractCurrentCooldown(cdr));
                                            player.getAbilitiesMatching(AbstractAbility.class).forEach(a -> a.subtractCurrentCooldown(cdr));
                                        }
                                    }
                            ));
                        }
                    }
                }
        ));
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 150;
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
                        formatTitleUpgrade(getEnergyOnTriggerAtLevel(getTitleLevel())),
                        formatTitleUpgrade(getEnergyOnTriggerAtLevel(getTitleLevelUpgraded()))
                ),
                new Pair<>(
                        Component.text("+" + getEnergyOnPairProcAtLevel(getTitleLevel()) + " energy / -" + trim(getCdrOnPairProcSecondsAtLevel(getTitleLevel())) + "s CDR",
                                NamedTextColor.GREEN
                        ),
                        Component.text("+" + getEnergyOnPairProcAtLevel(getTitleLevelUpgraded()) + " energy / -" + trim(getCdrOnPairProcSecondsAtLevel(getTitleLevelUpgraded())) + "s CDR",
                                NamedTextColor.GREEN
                        )
                )
        );
    }

    private String trim(double v) {
        String s = String.format(java.util.Locale.US, "%.2f", v);
        if (s.endsWith("00")) {
            return s.substring(0, s.length() - 3);
        }
        if (s.endsWith("0")) {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Override
    public int getCounter() {
        return 0;
    }

}

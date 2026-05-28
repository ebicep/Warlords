package com.ebicep.warlords.pve.weapons.weapontypes.legendaries.titles;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.MobHologram;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.LegendaryTitles;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.springframework.data.annotation.Transient;

import java.util.*;

public class LegendaryFractured extends AbstractLegendaryWeapon implements LibraryArchivesTitle {

    public static final float BASE_CRIT_CHANCE_BONUS_PER_STACK = 2.5f;
    public static final float CRIT_CHANCE_BONUS_PER_STACK_PER_UPGRADE = .5f;
    public static final float BASE_CRIT_MULTIPLIER_BONUS_PER_STACK = 5f;
    public static final float CRIT_MULTIPLIER_BONUS_PER_STACK_PER_UPGRADE = 1f;
    public static final int MAX_STACKS = 10;
    public static final int STACK_DURATION_SECONDS = 3;

    @Transient
    private final Map<UUID, FracturedData> fracturedTargets = new HashMap<>();

    public LegendaryFractured() {
    }

    public LegendaryFractured(UUID uuid) {
        super(uuid);
    }

    public LegendaryFractured(AbstractLegendaryWeapon legendaryWeapon) {
        super(legendaryWeapon);
    }

    @Override
    public TextComponent getPassiveEffect() {
        return Component.text("Critical hits apply Fractured to enemies for " + STACK_DURATION_SECONDS + "s, stacking up to ", NamedTextColor.GRAY)
                .append(formatTitleUpgrade(MAX_STACKS, ""))
                .append(Component.text(" times. Each stack increases your crit chance by "))
                .append(formatTitleUpgrade(getCritChanceBonusPerStack(), "%"))
                .append(Component.text(" and crit multiplier by "))
                .append(formatTitleUpgrade(getCritMultiplierBonusPerStack(), "%"))
                .append(Component.text(" against that target. Applying Fractured refreshes its duration."));
    }

    @Override
    public List<Pair<Component, Component>> getPassiveEffectUpgrade() {
        return Arrays.asList(
                new Pair<>(
                        formatTitleUpgrade(getCritChanceBonusPerStack(), "%"),
                        formatTitleUpgrade(getCritChanceBonusPerStackUpgraded(), "%")
                ),
                new Pair<>(
                        formatTitleUpgrade(getCritMultiplierBonusPerStack(), "%"),
                        formatTitleUpgrade(getCritMultiplierBonusPerStackUpgraded(), "%")
                )
        );
    }

    @Override
    public LegendaryTitles getTitle() {
        return LegendaryTitles.FRACTURED;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer player, PveOption pveOption) {
        super.applyToWarlordsPlayer(player, pveOption);
        fracturedTargets.clear();

        player.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        getTitleName(),
                        null,
                        LegendaryFractured.class,
                        null,
                        player,
                        CooldownTypes.WEAPON,
                        cooldownManager -> fracturedTargets.clear(),
                        false,
                        (cooldown, ticksElapsed) -> tickFracturedTargets()
                ).addModifier(
                        Modifier.MODIFY_OUTGOING_CRIT_CHANCE,
                        (event, currentCritChance) -> applyFracturedCritBonus(player, event, currentCritChance, getCritChanceBonusPerStack())
                ).addModifier(
                        Modifier.MODIFY_OUTGOING_CRIT_MULTIPLIER,
                        (event, currentCritMultiplier) -> applyFracturedCritBonus(player, event, currentCritMultiplier, getCritMultiplierBonusPerStack())
                ).addModifier(
                        Modifier.ON_OUTGOING_DAMAGE,
                        (event, currentDamageValue, isCrit) -> {
                            if (!isValidFracturedEvent(player, event)) {
                                return;
                            }
                            if (!isCrit) {
                                return;
                            }
                            if (currentDamageValue <= 0) {
                                return;
                            }

                            WarlordsEntity target = event.getWarlordsEntity();
                            FracturedData fracturedData = fracturedTargets.computeIfAbsent(target.getUuid(), uuid -> new FracturedData(target));
                            fracturedData.addStack();
                            fracturedData.updateHologram();

                            player.playSound(target.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 1.4f);
                        })
        );
    }

    @Override
    public void cleanup() {
        fracturedTargets.values().forEach(FracturedData::removeHologram);
        fracturedTargets.clear();
        super.cleanup();
    }

    private void applyFracturedCritBonus(WarlordsPlayer player, WarlordsDamageHealingEvent event, FloatModifiable modifiable, float bonusPerStack) {
        if (!isValidFracturedEvent(player, event)) {
            return;
        }

        FracturedData fracturedData = fracturedTargets.get(event.getWarlordsEntity().getUuid());
        if (fracturedData == null || fracturedData.stacks <= 0) {
            return;
        }

        modifiable.addModifier(
                FloatModifiable.ModifierType.ADDITIVE,
                getTitleName(),
                fracturedData.stacks * bonusPerStack
        );
    }

    private boolean isValidFracturedEvent(WarlordsPlayer player, WarlordsDamageHealingEvent event) {
        if (!event.isDamageInstance()) {
            return false;
        }
        if (event.getSource() != player) {
            return false;
        }
        if (event.getWarlordsEntity() == player) {
            return false;
        }
        if (event.getFlags().contains(InstanceFlags.RECURSIVE)) {
            return false;
        }
        return true;
    }

    private void tickFracturedTargets() {
        Iterator<Map.Entry<UUID, FracturedData>> iterator = fracturedTargets.entrySet().iterator();
        while (iterator.hasNext()) {
            FracturedData fracturedData = iterator.next().getValue();
            fracturedData.ticksLeft--;

            if (fracturedData.ticksLeft <= 0) {
                fracturedData.removeHologram();
                iterator.remove();
            }
        }
    }

    private float getCritChanceBonusPerStack() {
        return BASE_CRIT_CHANCE_BONUS_PER_STACK + CRIT_CHANCE_BONUS_PER_STACK_PER_UPGRADE * getTitleLevel();
    }

    private float getCritChanceBonusPerStackUpgraded() {
        return BASE_CRIT_CHANCE_BONUS_PER_STACK + CRIT_CHANCE_BONUS_PER_STACK_PER_UPGRADE * getTitleLevelUpgraded();
    }

    private float getCritMultiplierBonusPerStack() {
        return BASE_CRIT_MULTIPLIER_BONUS_PER_STACK + CRIT_MULTIPLIER_BONUS_PER_STACK_PER_UPGRADE * getTitleLevel();
    }

    private float getCritMultiplierBonusPerStackUpgraded() {
        return BASE_CRIT_MULTIPLIER_BONUS_PER_STACK + CRIT_MULTIPLIER_BONUS_PER_STACK_PER_UPGRADE * getTitleLevelUpgraded();
    }

    @Override
    protected float getMeleeDamageMinValue() {
        return 120;
    }

    @Override
    protected float getMeleeDamageMaxValue() {
        return 150;
    }

    @Override
    protected float getCritChanceValue() {
        return 30;
    }

    @Override
    protected float getCritMultiplierValue() {
        return 300;
    }

    @Override
    protected float getHealthBonusValue() {
        return 150;
    }

    @Override
    protected float getSpeedBonusValue() {
        return 5;
    }

    @Override
    protected float getSkillCritMultiplierBonusValue() {
        return 30;
    }

    private class FracturedData {

        private final WarlordsEntity target;
        private final WarlordsNPC targetMob;
        private MobHologram.CustomHologramLine hologramLine;
        private int stacks;
        private int ticksLeft;

        private FracturedData(WarlordsEntity target) {
            this.target = target;
            this.targetMob = target instanceof WarlordsNPC warlordsNPC ? warlordsNPC : null;
        }

        private void addStack() {
            stacks = Math.min(MAX_STACKS, stacks + 1);
            ticksLeft = STACK_DURATION_SECONDS * 20;
        }

        private void updateHologram() {
            if (targetMob == null) {
                return;
            }

            if (hologramLine == null) {
                hologramLine = new MobHologram.CustomHologramLine(this::getHologramText);
                targetMob.getMobHologram().getCustomHologramLines().add(hologramLine);
            }

            targetMob.getMobHologram().update();
        }

        private Component getHologramText() {
            return Component.text("Fractured: ", NamedTextColor.RED)
                    .append(Component.text(stacks, NamedTextColor.GRAY))
                    .append(Component.text("/", NamedTextColor.DARK_GRAY))
                    .append(Component.text(MAX_STACKS, NamedTextColor.GRAY));
        }

        private void removeHologram() {
            if (hologramLine == null) {
                return;
            }

            hologramLine.setDelete(true);

            if (targetMob != null) {
                targetMob.getMobHologram().update();
            }

            hologramLine = null;
        }

    }

    @Override
    public LinkedHashMap<Currencies, Long> getCost() {
        LinkedHashMap<Currencies, Long> baseCost = super.getCost();
        baseCost.put(Currencies.TITLE_TOKEN_LIBRARY_ARCHIVES, 1L);
        return baseCost;
    }
}
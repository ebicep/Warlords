package com.ebicep.warlords.pve.upgrades;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsUpgradeTreeBuilderAddUpgradeEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.RomanNumber;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class UpgradeTreeBuilder {

    public static UpgradeTreeBuilder create(AbilityTree abilityTree, AbstractUpgradeBranch<?> upgradeBranch) {
        return new UpgradeTreeBuilder(abilityTree.getWarlordsPlayer(), upgradeBranch);
    }

    private final AbstractUpgradeBranch<?> upgradeBranch;
    private final WarlordsEntity warlordsEntity;
    private final LinkedHashMap<Integer, List<UpgradeTypeHolder>> upgradeTypes = new LinkedHashMap<>();

    public UpgradeTreeBuilder(WarlordsEntity warlordsEntity, AbstractUpgradeBranch<?> upgradeBranch) {
        this.upgradeBranch = upgradeBranch;
        this.warlordsEntity = warlordsEntity;
    }

    public UpgradeTreeBuilder addUpgrade(UpgradeTypes.UpgradeType upgradeType, int... levels) {
        return addUpgrade(upgradeType, (FloatModifiable.FloatModifier) null, 0, levels);
    }

    public UpgradeTreeBuilder addUpgrade(UpgradeTypes.UpgradeType upgradeType, FloatModifiable.FloatModifier modifier, float value, int... level) {
        return addUpgrade(upgradeType, modifier == null ? null : List.of(modifier), value, level);
    }

    public UpgradeTreeBuilder addUpgrade(UpgradeTypes.UpgradeType upgradeType, List<FloatModifiable.FloatModifier> modifier, float value, int... level) {
        if (level.length == 0) {
            level = new int[]{1, 2, 3, 4};
        }
        AtomicReference<Float> valueReference = new AtomicReference<>(value);
        if (warlordsEntity.getGame() != null) {
            Bukkit.getPluginManager().callEvent(new WarlordsUpgradeTreeBuilderAddUpgradeEvent(warlordsEntity, this, valueReference));
        }
        for (int i : level) {
            upgradeTypes.computeIfAbsent(i, k -> new ArrayList<>())
                        .add(new UpgradeTypeHolder(upgradeType, modifier, valueReference.get(), level[0])); // assuming level[0] is lowest level
        }
        return this;
    }

    public UpgradeTreeBuilder addUpgradeDamage(Value ability, int... levels) {
        return addUpgradeDamage(ability, .05f, levels);
    }

    public UpgradeTreeBuilder addUpgradeDamage(Value ability, float value, int... levels) {
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        ability.forEachValue(floatModifiable -> modifiers.add(floatModifiable.addMultiplicativeModifierAdd("Upgrade Branch", 0, false)));
        return addUpgrade(
                UpgradeTypes.DAMAGE,
                modifiers,
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeDamage(Value.ValueHolder ability, float value, int... levels) {
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        ability.getValues().forEach(v -> v.forEachValue(floatModifiable -> modifiers.add(floatModifiable.addMultiplicativeModifierAdd("Upgrade Branch", 0, false))));
        return addUpgrade(
                UpgradeTypes.DAMAGE,
                modifiers,
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeHealing(Value value, int... levels) {
        return addUpgradeHealing(value, .05f, levels);
    }

    public UpgradeTreeBuilder addUpgradeHealing(Value ability, float value, int... levels) {
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        ability.forEachValue(floatModifiable -> modifiers.add(floatModifiable.addMultiplicativeModifierAdd("Upgrade Branch", 0, false)));
        return addUpgrade(
                UpgradeTypes.HEALING,
                modifiers,
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeHealing(Value.ValueHolder ability, float value, int... levels) {
        List<FloatModifiable.FloatModifier> modifiers = new ArrayList<>();
        ability.getValues().forEach(v -> v.forEachValue(floatModifiable -> modifiers.add(floatModifiable.addMultiplicativeModifierAdd("Upgrade Branch", 0, false))));
        return addUpgrade(
                UpgradeTypes.HEALING,
                modifiers,
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeCooldown(AbstractAbility ability, int... levels) {
        return addUpgradeCooldown(ability, .05f, levels);
    }

    public UpgradeTreeBuilder addUpgradeCooldown(AbstractAbility ability, float value, int... levels) {
        return addUpgrade(
                UpgradeTypes.COOLDOWN_REDUCTION,
                ability.getCooldown().addMultiplicativeModifierAdd("Upgrade Branch", 0),
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeEnergy(AbstractAbility ability, int... levels) {
        return addUpgradeEnergy(ability, 5, levels);
    }

    public UpgradeTreeBuilder addUpgradeEnergy(AbstractAbility ability, float value, int... levels) {
        return addUpgrade(
                UpgradeTypes.ENERGY_COST,
                ability.getEnergyCost().addAdditiveModifier("Upgrade Branch", 0),
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeDuration(Duration duration, int... levels) {
        return addUpgradeDuration(duration, 20, levels);
    }

    public UpgradeTreeBuilder addUpgradeDuration(Duration duration, float value, int... levels) {
        return addUpgradeDuration(duration::setTickDuration, duration::getTickDuration, value, levels);
    }

    public UpgradeTreeBuilder addUpgradeDuration(Consumer<Integer> setter, Supplier<Integer> getter, float value, int... levels) {
        return addUpgradeDuration(setter, getter, value, true, false, levels);
    }

    public UpgradeTreeBuilder addUpgradeDuration(
            Consumer<Integer> setter,
            Supplier<Integer> getter,
            float value,
            boolean autoScaleDescription,
            boolean noDescription,
            int... levels
    ) {
        return addUpgrade(
                new UpgradeTypes.DurationUpgradeType() {

                    @Override
                    public String getDescription0(String value) {
                        if (noDescription) {
                            return null;
                        }
                        return UpgradeTypes.DurationUpgradeType.super.getDescription0(value);
                    }

                    @Override
                    public boolean autoScaleDescription() {
                        return autoScaleDescription;
                    }

                    @Override
                    public void run(float v) {
                        // note: not auto scaled, i.e. +10 every time not +10 +20 +30...
                        setter.accept(getter.get() + (int) value); //TODO convert to FloatModifiable
                    }
                },
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgrade(UpgradeTypes.UpgradeType upgradeType, float value, int... levels) {
        return addUpgrade(upgradeType, (FloatModifiable.FloatModifier) null, value, levels);
    }

    public UpgradeTreeBuilder addUpgradeDuration(Duration duration, float value, boolean autoScaleDescription, int... levels) {
        return addUpgradeDuration(duration::setTickDuration, duration::getTickDuration, value, autoScaleDescription, false, levels);
    }

    public UpgradeTreeBuilder addUpgradeDuration(Duration duration, float value, boolean autoScaleDescription, boolean noDescription, int... levels) {
        return addUpgradeDuration(duration::setTickDuration, duration::getTickDuration, value, autoScaleDescription, noDescription, levels);
    }

    public UpgradeTreeBuilder addUpgradeHitBox(HitBox hitBox, float value, int... levels) {
        return addUpgrade(
                UpgradeTypes.HITBOX,
                hitBox.getHitBoxRadius().addAdditiveModifier("Upgrade Branch", 0),
                value,
                levels
        );
    }

    public UpgradeTreeBuilder addUpgradeSplash(Splash splash, float value, int... levels) {
        return addUpgrade(
                UpgradeTypes.SPLASH,
                splash.getSplashRadius().addAdditiveModifier("Upgrade Branch", 0),
                value,
                levels
        );
    }

    public void addTo(List<Upgrade> upgrades) {
        upgradeTypes.forEach((integer, upgradeTypeHolders) -> {
            String name = null;
            StringBuilder description = new StringBuilder();
            for (int i = 0; i < upgradeTypeHolders.size(); i++) {
                UpgradeTypeHolder type = upgradeTypeHolders.get(i);
                UpgradeTypes.UpgradeType upgradeType = type.upgradeType;
                if (name == null && upgradeType instanceof UpgradeTypes.NamedUpgradeType namedUpgradeType) {
                    name = namedUpgradeType.getName() + " - Tier " + RomanNumber.toRoman(integer);
                }
                float value = type.value * (upgradeType.autoScaleDescription() ? integer - type.scalingStart + 1 : 1);
                String desc = upgradeType.getDescription(value);
                if (desc != null) {
                    description.append(desc);
                    if (i != upgradeTypeHolders.size() - 1) {
                        description.append("\n");
                    }
                }
            }
            if (name == null) {
                ChatUtils.MessageType.WARLORDS.sendErrorMessage("Upgrade name is null for " + upgradeTypeHolders.get(0).upgradeType.getDescription0("TEST"));
                name = "ERROR";
            }
            upgrades.add(new Upgrade(
                    name,
                    description.toString(),
                    5000 * integer,
                    () -> {
                        upgradeTypeHolders.forEach(type -> {
                            UpgradeTypes.UpgradeType upgradeType = type.upgradeType;
                            List<FloatModifiable.FloatModifier> modifier = type.modifier;
                            float value = type.value * (upgradeType.autoScaleEffect() ? integer - type.scalingStart + 1 : 1);
                            if (modifier != null) {
                                modifier.forEach(floatModifier -> upgradeType.modifyFloatModifiable(floatModifier, value));
                            }
                            upgradeType.run(value);
                        });
                    }
            ));
        });
    }

    public AbstractUpgradeBranch<?> getUpgradeBranch() {
        return upgradeBranch;
    }

    public record UpgradeTypeHolder(UpgradeTypes.UpgradeType upgradeType, @Nullable List<FloatModifiable.FloatModifier> modifier, float value, int scalingStart) {
        public UpgradeTypeHolder(UpgradeTypes.UpgradeType upgradeType, @Nullable FloatModifiable.FloatModifier modifier, float value, int scalingStart) {
            this(upgradeType, modifier == null ? null : List.of(modifier), value, scalingStart);
        }
    }
}

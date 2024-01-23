package com.ebicep.warlords.util.warlords.modifiablevalues;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FloatModifiable {

    private final List<FloatModifier> overridingModifiers = new ArrayList<>(); // these modifiers override the current value
    private final List<FloatModifier> additiveModifiers = new ArrayList<>();
    private final List<FloatModifier> multiplicativeModifiersAdditive = new ArrayList<>(); // these modifiers are added together
    private final List<FloatModifier> multiplicativeModifiersMultiplicative = new ArrayList<>(); // these modifiers are multiplied together
    private final Map<String, FloatModifiableFilter> filters = new HashMap<>() {{
        put("Base", new FloatModifiableFilter.BaseFilter());
    }};
    private float baseValue;

    public FloatModifiable(float baseValue) {
        this.baseValue = baseValue;
        refresh();
    }

    private void refresh() {
        if (!overridingModifiers.isEmpty()) {
            filters.forEach((s, floatModifiableFilter) -> {
                overridingModifiers.stream()
                                   .filter(floatModifiableFilter::overridingFilter)
                                   .findFirst()
                                   .ifPresent(floatModifier -> floatModifiableFilter.setCachedValue(floatModifier.getModifier()));
            });
            return;
        }
        filters.forEach((s, floatModifiableFilter) -> {
            float cachedAdditiveModifier = (float) additiveModifiers
                    .stream()
                    .filter(floatModifiableFilter::additiveFilter)
                    .mapToDouble(FloatModifier::getModifier)
                    .sum();
            float cachedMultiplicativeModifierAdditive = 1 + (float) multiplicativeModifiersAdditive
                    .stream()
                    .filter(floatModifiableFilter::multiplicativeAdditiveFilter)
                    .mapToDouble(FloatModifier::getModifier)
                    .sum();
            float cachedMultiplicativeModifierMultiplicative = (float) multiplicativeModifiersMultiplicative
                    .stream()
                    .filter(floatModifiableFilter::multiplicativeMultiplicativeFilter)
                    .mapToDouble(FloatModifier::getModifier)
                    .reduce(1, (a, b) -> a * b);
            floatModifiableFilter.setCachedValue((baseValue + cachedAdditiveModifier) * cachedMultiplicativeModifierAdditive * cachedMultiplicativeModifierMultiplicative);
            floatModifiableFilter.setCachedAdditiveModifier(cachedAdditiveModifier);
            floatModifiableFilter.setCachedMultiplicativeModifierAdditive(cachedMultiplicativeModifierAdditive);
            floatModifiableFilter.setCachedMultiplicativeModifierMultiplicative(cachedMultiplicativeModifierMultiplicative);
        });
    }

    public void tick() {
        AtomicBoolean dirty = new AtomicBoolean(false);
        tickModifiers(additiveModifiers, dirty);
        tickModifiers(multiplicativeModifiersAdditive, dirty);
        tickModifiers(multiplicativeModifiersMultiplicative, dirty);
        if (dirty.get()) {
            refresh();
        }
    }

    private void tickModifiers(List<FloatModifier> modifiers, AtomicBoolean dirty) {
        modifiers.removeIf(floatModifier -> {
            if (floatModifier.tick()) {
                dirty.set(true);
                return true;
            }
            if (floatModifier.isDirty()) {
                dirty.set(true);
            }
            return false;
        });
    }

    public void removeModifier(String log) {
        overridingModifiers.removeIf(floatModifier -> floatModifier.getLog().equals(log));
        additiveModifiers.removeIf(floatModifier -> floatModifier.getLog().equals(log));
        multiplicativeModifiersAdditive.removeIf(floatModifier -> floatModifier.getLog().equals(log));
        multiplicativeModifiersMultiplicative.removeIf(floatModifier -> floatModifier.getLog().equals(log));
        refresh();
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        refresh();
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier) {
        FloatModifier modifier = new FloatModifier(log, additiveModifier);
        addModifier(this.additiveModifiers, modifier);
        return modifier;
    }

    private void addModifier(List<FloatModifier> list, FloatModifier modifier) {
        list.removeIf(m -> m.getLog().equals(modifier.getLog()));
        list.add(modifier);
        refresh();
    }

    public FloatModifier addOverridingModifier(String log, float overridingModifier) {
        FloatModifier modifier = new FloatModifier(log, overridingModifier);
        addModifier(this.overridingModifiers, modifier);
        return modifier;
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, additiveModifier, ticksLeft);
        addModifier(this.additiveModifiers, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier);
        addModifier(this.multiplicativeModifiersAdditive, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft);
        addModifier(this.multiplicativeModifiersAdditive, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier);
        addModifier(this.multiplicativeModifiersMultiplicative, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier, int ticksLeft) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft);
        addModifier(this.multiplicativeModifiersMultiplicative, modifier);
        return modifier;
    }

    public void addFilter(FloatModifiableFilter floatModifiableFilter) {
        filters.put(floatModifiableFilter.getName(), floatModifiableFilter);
        refresh();
    }

    public List<Component> getDebugInfo() {
        List<Component> components = new ArrayList<>();
        FloatModifiableFilter base = filters.get("Base");
        components.add(getDebugInfo("Calculated", getCalculatedValue()));
        components.add(getDebugInfo("Base", baseValue));
        if (!overridingModifiers.isEmpty()) {
            components.add(getDebugInfo("Overriding", overridingModifiers.get(0).getModifier()));
            components.addAll(getDebugInfo(overridingModifiers));
        }
        components.add(getDebugInfo("Additive", base.getCachedAdditiveModifier()));
        components.addAll(getDebugInfo(additiveModifiers));
        components.add(getDebugInfo("Multiplicative Additive", base.getCachedMultiplicativeModifierAdditive()));
        components.addAll(getDebugInfo(multiplicativeModifiersAdditive));
        components.add(getDebugInfo("Multiplicative Multiplicative", base.getCachedMultiplicativeModifierMultiplicative()));
        components.addAll(getDebugInfo(multiplicativeModifiersMultiplicative));
        return components;
    }

    private Component getDebugInfo(String name, float value) {
        return ComponentBuilder.create()
                               .text(name, NamedTextColor.DARK_GREEN)
                               .text(": ", NamedTextColor.GRAY)
                               .text(value, NamedTextColor.GOLD)
                               .build();
    }

    public float getCalculatedValue() {
        return filters.get("Base").getCachedValue();
    }

    private List<Component> getDebugInfo(List<FloatModifier> modifiers) {
        return modifiers.stream()
                        .map(floatModifier -> ComponentBuilder
                                .create()
                                .text(" - ", NamedTextColor.WHITE)
                                .append(floatModifier.getDebugInfo())
                                .build())
                        .collect(Collectors.toList());
    }

    public List<FloatModifier> getOverridingModifier() {
        return overridingModifiers;
    }

    public List<FloatModifier> getAdditiveModifier() {
        return additiveModifiers;
    }

    public List<FloatModifier> getMultiplicativeModifierAdditive() {
        return multiplicativeModifiersAdditive;
    }

    public List<FloatModifier> getMultiplicativeModifierMultiplicative() {
        return multiplicativeModifiersMultiplicative;
    }

    public static class FloatModifier {

        private final String log;
        private float modifier;
        private int ticksLeft;
        private boolean dirty = false;

        public FloatModifier(String log, float modifier, int ticksLeft) {
            this.log = log;
            this.modifier = modifier;
            this.ticksLeft = ticksLeft;
        }

        public FloatModifier(String log, float modifier) {
            this.log = log;
            this.modifier = modifier;
            this.ticksLeft = -1;
        }

        public Component getDebugInfo() {
            return ComponentBuilder.create()
                                   .text(log, NamedTextColor.GREEN)
                                   .text(": ", NamedTextColor.GRAY)
                                   .text(modifier, NamedTextColor.YELLOW)
                                   .text(" (" + (ticksLeft == -1 ? "INF" : ticksLeft) + ")", NamedTextColor.DARK_GRAY)
                                   .build();
        }

        public boolean tick() {
            if (ticksLeft == -1) {
                return false; // -1 means infinite
            }
            ticksLeft--;
            return ticksLeft <= 0;
        }

        public String getLog() {
            return log;
        }

        public float getModifier() {
            return modifier;
        }

        public void setModifier(float modifier) {
            this.modifier = modifier;
            dirty = true;
        }

        public int getTicksLeft() {
            return ticksLeft;
        }

        public void forceEnd() {
            ticksLeft = 0;
        }

        public boolean isDirty() {
            boolean d = dirty;
            dirty = false;
            return d;
        }
    }

}

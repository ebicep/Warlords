package com.ebicep.warlords.util.warlords.modifiablevalues;

import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.modifiablevalues.filters.BaseFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.*;
import java.util.function.Consumer;

public class FloatModifiable {

    private final List<FloatModifier> overridingModifiers = new ArrayList<>(); // these modifiers override the current value
    private final List<FloatModifier> additiveModifiers = new ArrayList<>();
    private final List<FloatModifier> multiplicativeModifiersAdditive = new ArrayList<>(); // these modifiers are added together
    private final List<FloatModifier> multiplicativeModifiersMultiplicative = new ArrayList<>(); // these modifiers are multiplied together
    private final Map<String, FloatModifiableFilter> filters = new HashMap<>(4);
    private final Map<String, Runnable> onRefresh = new HashMap<>();
    private final Map<ModifierType, Set<Consumer<FloatModifier>>> onAddModifier = new EnumMap<>(ModifierType.class);
    private final Map<String, Map<String, Float>> marginalContributions = new HashMap<>(4);
    private float baseValue;

    public FloatModifiable(float baseValue) {
        this.baseValue = baseValue;
        this.filters.put("Base", new BaseFilter());
        this.marginalContributions.put("Base", new HashMap<>(4));
        refresh();
    }

    public void refresh() {
        if (!overridingModifiers.isEmpty()) {
            processOverridingModifiers();
        } else {
            processNormalModifiers();
        }
        // Call global refresh listeners AFTER processing
        if (!onRefresh.isEmpty()) {
            onRefresh.values().forEach(Runnable::run);
        }
    }

    private void processOverridingModifiers() {
        marginalContributions.values().forEach(Map::clear);

        for (Map.Entry<String, FloatModifiableFilter> entry : filters.entrySet()) {
            String filterName = entry.getKey();
            FloatModifiableFilter filter = entry.getValue();
            Map<String, Float> filterContributions = marginalContributions.computeIfAbsent(filterName, k -> new HashMap<>());
            for (FloatModifier modifier : overridingModifiers) {
                if (filter.overridingFilter(modifier)) {
//                    float overriddenValue = filter.getCachedValue();
                    float newValue = modifier.getModifier();
                    filter.setCachedValue(modifier.getModifier());
                    filterContributions.put(modifier.getLog(), newValue);
                    break;
                }
            }
        }

        if (!onRefresh.isEmpty()) {
            onRefresh.values().forEach(Runnable::run);
        }
    }

    private void processNormalModifiers() {
        for (Map.Entry<String, FloatModifiableFilter> entry : filters.entrySet()) {
            String filterName = entry.getKey();
            FloatModifiableFilter filter = entry.getValue();
            float additiveSum = calculateAdditiveSum(filter);
            float multiplicativeAdditiveProduct = calculateMultiplicativeAdditive(filter);
            float multiplicativeMultiplicativeProduct = calculateMultiplicativeMultiplicative(filter);

            float finalValue = (baseValue + additiveSum) * multiplicativeAdditiveProduct * multiplicativeMultiplicativeProduct;

            filter.setCachedValue(finalValue);
            filter.setCachedAdditiveModifier(additiveSum);
            filter.setCachedMultiplicativeModifierAdditive(multiplicativeAdditiveProduct);
            filter.setCachedMultiplicativeModifierMultiplicative(multiplicativeMultiplicativeProduct);

            // contributions
            Map<String, Float> filterContributions = marginalContributions.computeIfAbsent(filterName, k -> new HashMap<>());
            filterContributions.clear();

            for (FloatModifier modifier : additiveModifiers) {
                if (modifier.isDisabled() || !filter.additiveFilter(modifier)) {
                    filterContributions.put(modifier.getLog(), 0f);
                    continue;
                }
                float additiveSumWithout = additiveSum - modifier.getModifier();
                float valueWithout = (baseValue + additiveSumWithout) * multiplicativeAdditiveProduct * multiplicativeMultiplicativeProduct;
                filterContributions.put(modifier.getLog(), finalValue - valueWithout);
            }

            float multiplicativeAdditiveSum = multiplicativeAdditiveProduct - 1f;
            for (FloatModifier modifier : multiplicativeModifiersAdditive) {
                if (modifier.isDisabled() || !filter.multiplicativeAdditiveFilter(modifier)) {
                    filterContributions.put(modifier.getLog(), 0f);
                    continue;
                }
                float multiplicativeAdditiveSumWithout = multiplicativeAdditiveSum - modifier.getModifier();
                float valueWithout = (baseValue + additiveSum) * (1f + multiplicativeAdditiveSumWithout) * multiplicativeMultiplicativeProduct;
                filterContributions.put(modifier.getLog(), finalValue - valueWithout);
            }

            for (FloatModifier modifier : multiplicativeModifiersMultiplicative) {
                if (modifier.isDisabled() || !filter.multiplicativeMultiplicativeFilter(modifier)) {
                    filterContributions.put(modifier.getLog(), 0f);
                    continue;
                }

                float modifierValue = modifier.getModifier();
                float productWithout;
                if (modifierValue == 0f) {
                    // Need to recalculate product without this zero modifier
                    productWithout = 1f;
                    for (FloatModifier mmMod : multiplicativeModifiersMultiplicative) {
                        if (mmMod == modifier || mmMod.isDisabled() || !filter.multiplicativeMultiplicativeFilter(mmMod)) {
                            continue;
                        }
                        productWithout *= mmMod.getModifier();
                    }
                } else {
                    // Simple division case
                    productWithout = multiplicativeMultiplicativeProduct / modifierValue;
                }
                float valueWithout = (baseValue + additiveSum) * multiplicativeAdditiveProduct * productWithout;

                filterContributions.put(modifier.getLog(), finalValue - valueWithout);
            }
        }
    }

    private float calculateAdditiveSum(FloatModifiableFilter filter) {
        float sum = 0f;
        for (FloatModifier modifier : additiveModifiers) {
            if (modifier.isDisabled()) {
                continue;
            }
            if (filter.additiveFilter(modifier)) {
                sum += modifier.getModifier();
            }
        }
        return sum;
    }

    private float calculateMultiplicativeAdditive(FloatModifiableFilter filter) {
        float sum = 0f;
        for (FloatModifier modifier : multiplicativeModifiersAdditive) {
            if (modifier.isDisabled()) {
                continue;
            }
            if (filter.multiplicativeAdditiveFilter(modifier)) {
                sum += modifier.getModifier();
            }
        }
        return 1f + sum;
    }

    private float calculateMultiplicativeMultiplicative(FloatModifiableFilter filter) {
        float product = 1f;
        for (FloatModifier modifier : multiplicativeModifiersMultiplicative) {
            if (modifier.isDisabled()) {
                continue;
            }
            if (filter.multiplicativeMultiplicativeFilter(modifier)) {
                product *= modifier.getModifier();
            }
        }
        return product;
    }

    public FloatModifiable(FloatModifiable floatModifiable) {
        this.baseValue = floatModifiable.baseValue;
        floatModifiable.overridingModifiers.forEach(floatModifier -> this.overridingModifiers.add(new FloatModifier(floatModifier.getLog(),
                floatModifier.getModifier(),
                floatModifier.getTicksLeft()
        )));
        floatModifiable.additiveModifiers.forEach(floatModifier -> this.additiveModifiers.add(new FloatModifier(floatModifier.getLog(),
                floatModifier.getModifier(),
                floatModifier.getTicksLeft()
        )));
        floatModifiable.multiplicativeModifiersAdditive.forEach(floatModifier -> this.multiplicativeModifiersAdditive.add(new FloatModifier(floatModifier.getLog(),
                floatModifier.getModifier(),
                floatModifier.getTicksLeft()
        )));
        floatModifiable.multiplicativeModifiersMultiplicative.forEach(floatModifier -> this.multiplicativeModifiersMultiplicative.add(new FloatModifier(floatModifier.getLog(),
                floatModifier.getModifier(),
                floatModifier.getTicksLeft()
        )));
        floatModifiable.filters.forEach((s, floatModifiableFilter) -> this.filters.put(s, floatModifiableFilter.clone()));
        refresh();
        this.onRefresh.putAll(floatModifiable.onRefresh);
    }

    public void tick() {
        boolean dirty = false;
        dirty |= tickModifiers(overridingModifiers);
        dirty |= tickModifiers(additiveModifiers);
        dirty |= tickModifiers(multiplicativeModifiersAdditive);
        dirty |= tickModifiers(multiplicativeModifiersMultiplicative);
        if (dirty) {
            refresh();
        }
    }

    private boolean tickModifiers(List<FloatModifier> modifiers) {
        boolean hasChanges = false;

        for (int i = modifiers.size() - 1; i >= 0; i--) {
            FloatModifier modifier = modifiers.get(i);

            if (modifier.tick()) {
                modifiers.remove(i);
                hasChanges = true;
            } else if (modifier.isDirty()) {
                hasChanges = true;
            }
        }

        return hasChanges;
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
        return addAdditiveModifier(log, additiveModifier, -1, null);
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier, int ticksLeft, Consumer<Float> callback) {
        FloatModifier modifier = new FloatModifier(log, additiveModifier, ticksLeft, callback);
        callOnAddModifier(ModifierType.ADDITIVE, modifier);
        addModifier(this.additiveModifiers, modifier);
        return modifier;
    }

    private void callOnAddModifier(ModifierType type, FloatModifier modifier) {
        Set<Consumer<FloatModifier>> consumers = onAddModifier.get(type);
        if (consumers != null) {
            consumers.forEach(consumer -> consumer.accept(modifier));
        }
    }

    private void addModifier(List<FloatModifier> list, FloatModifier modifier) {
        list.removeIf(m -> m.getLog().equals(modifier.getLog()));
        list.add(modifier);
        refresh();
    }

    public FloatModifier addOverridingModifier(String log, float overridingModifier) {
        return addOverridingModifier(log, overridingModifier, -1, null);
    }

    public FloatModifier addOverridingModifier(String log, float overridingModifier, int ticksLeft, Consumer<Float> callback) {
        FloatModifier modifier = new FloatModifier(log, overridingModifier, ticksLeft, callback);
        callOnAddModifier(ModifierType.OVERRIDING, modifier);
        addModifier(this.overridingModifiers, modifier);
        return modifier;
    }

    public FloatModifier addOverridingModifier(String log, float overridingModifier, int ticksLeft) {
        return addOverridingModifier(log, overridingModifier, ticksLeft, null);
    }

    public FloatModifier addOverridingModifier(String log, float overridingModifier, Consumer<Float> callback) {
        return addOverridingModifier(log, overridingModifier, -1, callback);
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier, int ticksLeft) {
        return addAdditiveModifier(log, additiveModifier, ticksLeft, null);
    }

    public FloatModifier addAdditiveModifier(String log, float additiveModifier, Consumer<Float> callback) {
        return addAdditiveModifier(log, additiveModifier, -1, callback);
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier) {
        return addMultiplicativeModifierAdd(log, multiplicativeModifier, true);
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, boolean override) {
        if (!override) {
            for (FloatModifier modifier : multiplicativeModifiersAdditive) {
                if (modifier.getLog().equals(log)) {
                    return modifier;
                }
            }
        }
        return addMultiplicativeModifierAdd(log, multiplicativeModifier, -1, null);
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, int ticksLeft, Consumer<Float> callback) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft, callback);
        callOnAddModifier(ModifierType.MULTIPLICATIVE_ADDITIVE, modifier);
        addModifier(this.multiplicativeModifiersAdditive, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, Consumer<Float> callback) {
        return addMultiplicativeModifierAdd(log, multiplicativeModifier, -1, callback);
    }

    public FloatModifier addMultiplicativeModifierAdd(String log, float multiplicativeModifier, int ticksLeft) {
        return addMultiplicativeModifierAdd(log, multiplicativeModifier, ticksLeft, null);
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier) {
        return addMultiplicativeModifierMult(log, multiplicativeModifier, -1, null);
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier, int ticksLeft, Consumer<Float> callback) {
        FloatModifier modifier = new FloatModifier(log, multiplicativeModifier, ticksLeft, callback);
        callOnAddModifier(ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, modifier);
        addModifier(this.multiplicativeModifiersMultiplicative, modifier);
        return modifier;
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier, Consumer<Float> callback) {
        return addMultiplicativeModifierMult(log, multiplicativeModifier, -1, callback);
    }

    public FloatModifier addMultiplicativeModifierMult(String log, float multiplicativeModifier, int ticksLeft) {
        return addMultiplicativeModifierMult(log, multiplicativeModifier, ticksLeft, null);
    }

    public void addFilter(FloatModifiableFilter floatModifiableFilter) {
        filters.put(floatModifiableFilter.getName(), floatModifiableFilter);
        refresh();
    }

    public List<Component> getDebugInfo() {
        List<Component> components = new ArrayList<>();
        FloatModifiableFilter base = filters.get("Base");
        if (getCalculatedValue() != baseValue) {
            components.add(Component.textOfChildren(
                    getDebugInfo("Base", baseValue),
                    Component.text(" -> ", NamedTextColor.GRAY),
                    getDebugInfo("Calculated", getCalculatedValue())
            ));
        } else {
            components.add(getDebugInfo("Calculated", getCalculatedValue()));
        }
        if (!overridingModifiers.isEmpty()) {
            components.add(getDebugInfo("Overriding", overridingModifiers.get(0).getModifier()));
            components.addAll(getDebugInfo(overridingModifiers));
        }
        if (!additiveModifiers.isEmpty()) {
            components.add(getDebugInfo("Additive", base.getCachedAdditiveModifier()));
            components.addAll(getDebugInfo(additiveModifiers));
        }
        if (!multiplicativeModifiersAdditive.isEmpty()) {
            components.add(getDebugInfo("Multiplicative Additive", base.getCachedMultiplicativeModifierAdditive()));
            components.addAll(getDebugInfo(multiplicativeModifiersAdditive));
        }
        if (!multiplicativeModifiersMultiplicative.isEmpty()) {
            components.add(getDebugInfo("Multiplicative Multiplicative", base.getCachedMultiplicativeModifierMultiplicative()));
            components.addAll(getDebugInfo(multiplicativeModifiersMultiplicative));
        }
        return components;
    }

    public float getCalculatedValue() {
        return filters.get("Base").getCachedValue();
    }

    private Component getDebugInfo(String name, float value) {
        return ComponentBuilder.create()
                               .text(name, NamedTextColor.DARK_GREEN)
                               .text(": ", NamedTextColor.GRAY)
                               .text(NumberFormat.formatOptionalHundredths(value), NamedTextColor.GOLD)
                               .build();
    }

    private List<Component> getDebugInfo(List<FloatModifier> modifiers) {
        Map<String, Float> contributions = marginalContributions.getOrDefault("Base", Collections.emptyMap());
        List<Component> result = new ArrayList<>(modifiers.size());
        for (FloatModifier floatModifier : modifiers) {
            result.add(ComponentBuilder
                    .create()
                    .text(" - ", NamedTextColor.WHITE)
                    .append(floatModifier.getDebugInfo())
                    .text(" (", NamedTextColor.GRAY)
                    .text(NumberFormat.formatOptionalHundredths(contributions.getOrDefault(floatModifier.getLog(), 0f)), NamedTextColor.GOLD)
                    .text(")", NamedTextColor.GRAY)
                    .build()
            );
        }
        return result;
    }

    public void clearModifiers() {
        overridingModifiers.clear();
        additiveModifiers.clear();
        multiplicativeModifiersAdditive.clear();
        multiplicativeModifiersMultiplicative.clear();
        filters.forEach((s, floatModifiableFilter) -> floatModifiableFilter.setCachedValue(baseValue));
        refresh();
    }

    public void addRefreshListener(String source, Runnable runnable) {
        onRefresh.put(source, runnable);
    }

    public void addModifierListener(Consumer<FloatModifier> consumer, ModifierType... modifierTypes) {
        for (ModifierType modifierType : modifierTypes) {
            onAddModifier.computeIfAbsent(modifierType, k -> new HashSet<>(2)).add(consumer);
        }
    }

    public void removeModifierListener(Consumer<FloatModifier> consumer, ModifierType... modifierTypes) {
        for (ModifierType modifierType : modifierTypes) {
            Set<Consumer<FloatModifier>> consumers = onAddModifier.get(modifierType);
            if (consumers != null) {
                consumers.remove(consumer);
            }
        }
    }

    public void callContributionCallbacks() {
        callContributionCallbacks("Base");
    }

    public void callContributionCallbacks(String filterName) {
        Map<String, Float> contributions = marginalContributions.get(filterName);
        if (contributions == null) {
            return;
        }
        callContributionCallback(contributions, overridingModifiers);
        callContributionCallback(contributions, additiveModifiers);
        callContributionCallback(contributions, multiplicativeModifiersAdditive);
        callContributionCallback(contributions, multiplicativeModifiersMultiplicative);
    }

    private void callContributionCallback(Map<String, Float> contributions, List<FloatModifier> modifiers) {
        for (FloatModifier modifier : modifiers) {
            Consumer<Float> callback = modifier.callback;
            if (callback != null) {
                callback.accept(contributions.getOrDefault(modifier.log, 0f));
            }
        }
    }

    public enum ModifierType {
        OVERRIDING,
        ADDITIVE,
        MULTIPLICATIVE_ADDITIVE,
        MULTIPLICATIVE_MULTIPLICATIVE
    }

    public static class FloatModifier {

        private final String log;
        private final Set<String> disabledReasons = new HashSet<>(2);
        private final Consumer<Float> callback;
        private float modifier;
        private int ticksLeft;
        private boolean dirty = false;

        public FloatModifier(String log, float modifier, Consumer<Float> callback) {
            this(log, modifier, -1, callback);
        }

        public FloatModifier(String log, float modifier, int ticksLeft, Consumer<Float> callback) {
            this.log = log;
            this.modifier = modifier;
            this.ticksLeft = ticksLeft;
            this.callback = callback;
        }

        public FloatModifier(String log, float modifier, int ticksLeft) {
            this(log, modifier, ticksLeft, null);
        }

        public FloatModifier(String log, float modifier) {
            this(log, modifier, -1, null);
        }

        public Component getDebugInfo() {
            ComponentBuilder builder = ComponentBuilder
                    .create()
//                    .decorate(TextDecoration.STRIKETHROUGH, isDisabled())
                    .text(log, isDisabled() ? NamedTextColor.RED : NamedTextColor.GREEN)
                    .text(": ", NamedTextColor.GRAY)
                    .text(modifier, NamedTextColor.YELLOW)
                    .text(" (" + (ticksLeft == -1 ? "INF" : ticksLeft) + ")", NamedTextColor.DARK_GRAY);
            if (isDisabled()) {
                builder.text(" [" + String.join(", ", disabledReasons) + "]", NamedTextColor.RED);
            }
            return builder.build();
        }

        public boolean isDisabled() {
            return !disabledReasons.isEmpty();
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

        public void addDisabledReason(String reason) {
            disabledReasons.add(reason);
            dirty = true;
        }

    }

}

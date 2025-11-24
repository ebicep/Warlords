package com.ebicep.warlords.util.warlords.modifiablevalues;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class MultiFloatModifiable implements Modifiable {

    private final Map<ApplyFloatModifiable, FloatModifiable> modifiables = new TreeMap<>(Comparator
            .comparingInt(ApplyFloatModifiable::priority) // highest priority first
            .reversed()
            .thenComparing(ApplyFloatModifiable::applyType) // enum natural order (ordinal)
    );
    private final FloatModifiable baseModifiable;
    private final Map<ApplyFloatModifiable, Map<String, Float>> globalMarginalContributions = new HashMap<>();
    private final Map<Consumer<FloatModifiable.FloatModifier>, FloatModifiable.ModifierType[]> onAddModifiers = new HashMap<>(2);

    public MultiFloatModifiable(FloatModifiable baseModifiable) {
        this.baseModifiable = baseModifiable;
        this.modifiables.put(new ApplyFloatModifiable(100, ApplyFloatModifiableType.ADDITIVE), baseModifiable);
    }

    @Override
    public float getCalculatedValue() {
        float value = 0;
        for (Map.Entry<ApplyFloatModifiable, FloatModifiable> entry : modifiables.entrySet()) {
            ApplyFloatModifiable applyFloatModifiable = entry.getKey();
            FloatModifiable floatModifiable = entry.getValue();
            if (applyFloatModifiable.applyType() == ApplyFloatModifiableType.ADDITIVE) {
                value += floatModifiable.getCalculatedValue();
            } else if (applyFloatModifiable.applyType() == ApplyFloatModifiableType.MULTIPLICATIVE) {
                value *= floatModifiable.getCalculatedValue();
            }
        }
        return value;
    }

    @Override
    public void refresh() {
        for (FloatModifiable value : modifiables.values()) {
            value.refresh();
        }
        calculateGlobalContributions();
    }

    @Override
    public FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, int ticksLeft, Consumer<Float> callback, boolean override) {
        return baseModifiable.addModifier(type, log, value, ticksLeft, callback, override);
    }

    @Override
    public FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, boolean override) {
        return null;
    }

    @Override
    public FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value) {
        return baseModifiable.addModifier(type, log, value);
    }

    @Override
    public FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, int ticksLeft) {
        return baseModifiable.addModifier(type, log, value, ticksLeft);
    }

    @Override
    public FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, Consumer<Float> callback) {
        return baseModifiable.addModifier(type, log, value, callback);
    }

    @Override
    public void addModifierListener(Consumer<FloatModifiable.FloatModifier> consumer, FloatModifiable.ModifierType... modifierTypes) {
        onAddModifiers.put(consumer, modifierTypes);
        for (FloatModifiable value : modifiables.values()) {
            value.addModifierListener(consumer, modifierTypes);
        }
    }

    @Override
    public void removeModifierListener(Consumer<FloatModifiable.FloatModifier> consumer, FloatModifiable.ModifierType... modifierTypes) {
        onAddModifiers.remove(consumer);
        for (FloatModifiable value : modifiables.values()) {
            value.removeModifierListener(consumer, modifierTypes);
        }
    }

    @Override
    public void callContributionCallbacks() {
        for (FloatModifiable value : modifiables.values()) {
            value.callContributionCallbacks();
        }
    }

    /**
     * Use Chain Rule + Total Derivative to calculate global contributions
     */
    private void calculateGlobalContributions() {
        globalMarginalContributions.values().forEach(Map::clear);

        List<Map.Entry<ApplyFloatModifiable, FloatModifiable>> entries = new ArrayList<>(modifiables.entrySet());
        int size = entries.size();

        // pre-calculate future multipliers
        float[] futureMultipliers = new float[size];
        float currentFutureMult = 1.0f;

        for (int i = size - 1; i >= 0; i--) {
            futureMultipliers[i] = currentFutureMult;
            Map.Entry<ApplyFloatModifiable, FloatModifiable> entry = entries.get(i);
            if (entry.getKey().applyType() == ApplyFloatModifiableType.MULTIPLICATIVE) {
                currentFutureMult *= entry.getValue().getCalculatedValue();
            }
        }

        // calculate Global Contributions per layer
        float currentAccumulator = 0f;

        for (int i = 0; i < size; i++) {
            Map.Entry<ApplyFloatModifiable, FloatModifiable> entry = entries.get(i);
            ApplyFloatModifiable key = entry.getKey();
            FloatModifiable fm = entry.getValue();

            float localValue = fm.getCalculatedValue();
            float scaleForEntry;

            if (key.applyType() == ApplyFloatModifiableType.ADDITIVE) {
                scaleForEntry = futureMultipliers[i];
                currentAccumulator += localValue;
            } else {
                scaleForEntry = currentAccumulator * futureMultipliers[i];
                currentAccumulator *= localValue;
            }

            // specific contribution map for this layer
            Map<String, Float> layerContributions = globalMarginalContributions.computeIfAbsent(key, k -> new HashMap<>());
            // scale local contributions
            Map<String, Float> localContributions = fm.getMarginalContributions("Base");
            for (Map.Entry<String, Float> contribution : localContributions.entrySet()) {
                float globalContribution = contribution.getValue() * scaleForEntry;
                layerContributions.put(contribution.getKey(), globalContribution);
            }
        }
    }

    public void callGlobalContributionCallbacks() {
        for (Map.Entry<ApplyFloatModifiable, FloatModifiable> entry : modifiables.entrySet()) {
            ApplyFloatModifiable key = entry.getKey();
            FloatModifiable fm = entry.getValue();

            Map<String, Float> contributions = globalMarginalContributions.get(key);
            if (contributions != null && !contributions.isEmpty()) {
                fm.callContributionCallbacks(contributions);
            }
        }
    }

    public Map<ApplyFloatModifiable, Map<String, Float>> getGlobalMarginalContributions() {
        return globalMarginalContributions;
    }

    public FloatModifiable.FloatModifier addModifier(
            int priority,
            ApplyFloatModifiableType applyType,
            FloatModifiable.ModifierType type,
            String log,
            float value,
            int ticksLeft,
            Consumer<Float> callback,
            boolean override
    ) {
        FloatModifiable floatModifiable = modifiables.computeIfAbsent(
                new ApplyFloatModifiable(priority, applyType),
                k -> createNewFloatModifiable(applyType)
        );
        return floatModifiable.addModifier(type, log, value, ticksLeft, callback, override);
    }

    @Nonnull
    private FloatModifiable createNewFloatModifiable(ApplyFloatModifiableType applyType) {
        FloatModifiable fm = new FloatModifiable(applyType == ApplyFloatModifiableType.MULTIPLICATIVE ? 1.0f : 0.0f);
        onAddModifiers.forEach(fm::addModifierListener);
        return fm;
    }

    public FloatModifiable.FloatModifier addModifier(int priority, ApplyFloatModifiableType applyType, FloatModifiable.ModifierType type, String log, float value) {
        FloatModifiable floatModifiable = modifiables.computeIfAbsent(
                new ApplyFloatModifiable(priority, applyType),
                k -> createNewFloatModifiable(applyType)
        );
        return floatModifiable.addModifier(type, log, value);
    }

    public FloatModifiable.FloatModifier addModifier(int priority, ApplyFloatModifiableType applyType, FloatModifiable.ModifierType type, String log, float value, int ticksLeft) {
        FloatModifiable floatModifiable = modifiables.computeIfAbsent(
                new ApplyFloatModifiable(priority, applyType),
                k -> createNewFloatModifiable(applyType)
        );
        return floatModifiable.addModifier(type, log, value, ticksLeft);
    }

    public FloatModifiable.FloatModifier addModifier(
            int priority,
            ApplyFloatModifiableType applyType,
            FloatModifiable.ModifierType type,
            String log,
            float value,
            Consumer<Float> callback
    ) {
        FloatModifiable floatModifiable = modifiables.computeIfAbsent(
                new ApplyFloatModifiable(priority, applyType),
                k -> createNewFloatModifiable(applyType)
        );
        return floatModifiable.addModifier(type, log, value, callback);
    }

    public Map<ApplyFloatModifiable, FloatModifiable> getModifiables() {
        return modifiables;
    }

    public enum ApplyFloatModifiableType {
        ADDITIVE,
        MULTIPLICATIVE
    }

    public record ApplyFloatModifiable(int priority, ApplyFloatModifiableType applyType) {

    }

}

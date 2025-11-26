package com.ebicep.warlords.util.warlords.modifiablevalues;

import java.util.function.Consumer;

public interface Modifiable {

    float getCalculatedValue();

    void refresh();

    FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, int ticksLeft, Consumer<Float> callback, boolean override);

    FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, boolean override);

    FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value);

    FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, int ticksLeft);

    FloatModifiable.FloatModifier addModifier(FloatModifiable.ModifierType type, String log, float value, Consumer<Float> callback);

    void addModifierListener(Consumer<FloatModifiable.FloatModifier> consumer, FloatModifiable.ModifierType... modifierTypes);

    void removeModifierListener(Consumer<FloatModifiable.FloatModifier> consumer, FloatModifiable.ModifierType... modifierTypes);

    void callContributionCallbacks();

}

package com.ebicep.warlords.player.ingame.motionsystem.motionaddon;

import com.ebicep.warlords.player.ingame.motionsystem.MotionSystem;

import javax.annotation.Nonnull;

public interface NewValueModifier extends MotionAddon, Comparable<NewValueModifier> {

    @Override
    default int compareTo(@Nonnull NewValueModifier newValueModifier) {
        return Integer.compare(getPriority(), newValueModifier.getPriority());
    }

    default int getPriority() {
        return 0;
    }

    default boolean skipOthers() {
        return false;
    }

    default boolean skipIfNotMinMax() {
        return false;
    }

    default boolean forceApply() {
        return false;
    }

    void modifyNewValue(MotionSystem.NewValueData newValueData);

}

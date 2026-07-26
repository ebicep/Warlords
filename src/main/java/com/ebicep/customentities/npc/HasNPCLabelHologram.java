package com.ebicep.customentities.npc;

import javax.annotation.Nullable;

/**
 * Marker for {@link WarlordsTrait}s that own an {@link NPCLabelHologram}.
 * Cleanup is handled by {@link WarlordsTrait#onRemove()}.
 */
public interface HasNPCLabelHologram {

    @Nullable
    NPCLabelHologram getLabelHologram();
}

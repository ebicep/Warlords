package com.ebicep.customentities.npc;

import com.ebicep.holograms.Hologram;
import com.ebicep.holograms.HologramDataText;
import com.ebicep.holograms.HologramManager;
import com.ebicep.holograms.VisibilityType;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Trait-owned NPC label backed by {@link HologramManager} instead of Citizens {@code HologramTrait}.
 */
public final class NPCLabelHologram {

    private static final double CLEARANCE = 0.25;

    private final String id;
    @Nullable
    private Hologram hologram;
    @Nullable
    private HologramDataText hologramDataText;

    public NPCLabelHologram(@Nonnull String id) {
        this.id = id;
    }

    public void update(@Nonnull NPC npc, @Nonnull Component text) {
        Entity entity = npc.getEntity();
        if (entity == null) {
            return;
        }
        if (hologram == null || hologramDataText == null) {
            Location at = entity.getLocation().add(0, entity.getHeight() + CLEARANCE, 0);
            hologramDataText = new HologramDataText.Builder<>(text)
                    .setBillboard(Display.Billboard.CENTER)
                    .build();
            hologram = new Hologram.Builder(
                    id,
                    at,
                    player -> hologramDataText
            ).setVisibility(VisibilityType.ALL).build();
            HologramManager.addHologram(hologram);
            return;
        }
        hologramDataText.setComponent(text);
        HologramManager.updateHologram(hologram);
    }

    public void delete() {
        if (hologram != null) {
            hologram.deleteHologram();
        }
        hologram = null;
        hologramDataText = null;
    }

    public boolean isPresent() {
        return hologram != null;
    }
}

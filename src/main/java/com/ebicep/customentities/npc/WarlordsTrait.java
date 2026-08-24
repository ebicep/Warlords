package com.ebicep.customentities.npc;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.featureflags.FeatureFlags;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import org.bukkit.event.EventHandler;


/**
 * Extend this class instead of Trait for automatic npc detection and checking
 */
public abstract class WarlordsTrait extends Trait {

    public WarlordsTrait(String name) {
        super(name);
    }

    @Override
    public void onRemove() {
        if (this instanceof HasNPCLabelHologram labeled) {
            NPCLabelHologram hologram = labeled.getLabelHologram();
            if (hologram != null) {
                hologram.delete();
            }
        }
    }

    @EventHandler
    @Deprecated // Use #rightClick instead
    public void rightClickEventImplementation(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            if (!FeatureFlags.isNpcEnabled(event.getNPC().getName(), event.getClicker())) {
                FeatureFlags.sendDisabledMessage(event.getClicker());
                return;
            }
            rightClick(event);
        }
    }

    @EventHandler
    @Deprecated // Use #leftClick instead
    public void leftClickEventImplementation(NPCLeftClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            if (!FeatureFlags.isNpcEnabled(event.getNPC().getName(), event.getClicker())) {
                FeatureFlags.sendDisabledMessage(event.getClicker());
                return;
            }
            leftClick(event);
        }
    }

    /**
     * Override this method for automatic self npc checking
     *
     * @param event The event
     */
    public void rightClick(NPCRightClickEvent event) {
    }

    /**
     * Override this method for automatic self npc checking
     *
     * @param event The event
     */
    public void leftClick(NPCLeftClickEvent event) {
    }

}

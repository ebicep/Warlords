package com.ebicep.holograms;

import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.EntityType;

public class HologramDataText extends HologramData {

    private Component component;
    private int lineWidth;
    private int backgroundColor;
    private int textOpacity;
    private int flags;

    public HologramDataText() {
        super(EntityType.TEXT_DISPLAY);
    }
}

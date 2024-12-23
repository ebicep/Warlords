package com.ebicep.holograms;

import net.minecraft.world.entity.EntityType;
import org.bukkit.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public abstract class HologramData {

    private final EntityType<?> entityType;
    private Vector3f translation = new Vector3f(0, 0, 0);
    private Vector3f scale = new Vector3f(1, 1, 1);
    private Quaternionf rightRotation = new Quaternionf();
    private Quaternionf leftRotation = new Quaternionf();
    private Display.Billboard billboard = Display.Billboard.CENTER;
    private Display.Brightness brightness = new Display.Brightness(15, 15);
    private float viewRange = 50;
    private float width = 0;
    private float height = 0;
    private int glowColor = -1;

    protected HologramData(EntityType<?> entityType) {
        this.entityType = entityType;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public Vector3f getScale() {
        return scale;
    }

    public Quaternionf getRightRotation() {
        return rightRotation;
    }

    public Quaternionf getLeftRotation() {
        return leftRotation;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public Display.Brightness getBrightness() {
        return brightness;
    }

    public float getViewRange() {
        return viewRange;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getGlowColor() {
        return glowColor;
    }
}

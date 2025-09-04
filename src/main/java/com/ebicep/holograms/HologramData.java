package com.ebicep.holograms;

import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class HologramData {

    public static final int DEFAULT_VIEW_RANGE = 50;
    protected final EntityType<?> entityType;
    protected Vector3f translation;
    protected Vector3f scale;
    protected Quaternionf rightRotation;
    protected Quaternionf leftRotation;
    protected Display.Billboard billboard;
    protected Display.Brightness brightness;
    protected float viewRange;
    protected float width;
    protected float height;
    protected int glowColor;

    protected HologramData(Builder<?> builder) {
        this.entityType = builder.entityType;
        this.translation = builder.translation;
        this.scale = builder.scale;
        this.rightRotation = builder.rightRotation;
        this.leftRotation = builder.leftRotation;
        this.billboard = builder.billboard;
        this.brightness = builder.brightness;
        this.viewRange = builder.viewRange;
        this.width = builder.width;
        this.height = builder.height;
        this.glowColor = builder.glowColor;
    }

    public abstract InteractData.AutoData getAutoInteractData();

    protected List<SynchedEntityData.DataValue<?>> getData(Player player) {
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        try {
            data.add(HologramManager.createDataValue(Entity.class, "DATA_SHARED_FLAGS_ID", (byte) 0));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_AIR_SUPPLY_ID", 0));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_CUSTOM_NAME_VISIBLE", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_CUSTOM_NAME", Optional.empty()));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_SILENT", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_NO_GRAVITY", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_POSE", Pose.STANDING));

            data.add(HologramManager.createDataValue(Entity.class, "DATA_TICKS_FROZEN", 0));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_TRANSLATION_ID", translation));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_SCALE_ID", scale));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_RIGHT_ROTATION_ID", rightRotation));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_LEFT_ROTATION_ID", leftRotation));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID", (byte) billboard.ordinal()));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class,
                    "DATA_BRIGHTNESS_OVERRIDE_ID",
                    brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20
            ));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_VIEW_RANGE_ID", viewRange));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_WIDTH_ID", width));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_HEIGHT_ID", height));
            data.add(HologramManager.createDataValue(net.minecraft.world.entity.Display.class, "DATA_GLOW_COLOR_OVERRIDE_ID", glowColor));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ChatUtils.MessageType.HOLOGRAMS.sendErrorMessage(e);
        }
        return data;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public Vector3f getScale() {
        return scale;
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Quaternionf getRightRotation() {
        return rightRotation;
    }

    public void setRightRotation(Quaternionf rightRotation) {
        this.rightRotation = rightRotation;
    }

    public Quaternionf getLeftRotation() {
        return leftRotation;
    }

    public void setLeftRotation(Quaternionf leftRotation) {
        this.leftRotation = leftRotation;
    }

    public Display.Billboard getBillboard() {
        return billboard;
    }

    public void setBillboard(Display.Billboard billboard) {
        this.billboard = billboard;
    }

    public Display.Brightness getBrightness() {
        return brightness;
    }

    public void setBrightness(Display.Brightness brightness) {
        this.brightness = brightness;
    }

    public float getViewRange() {
        return viewRange;
    }

    public void setViewRange(float viewRange) {
        this.viewRange = viewRange;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getGlowColor() {
        return glowColor;
    }

    public void setGlowColor(int glowColor) {
        this.glowColor = glowColor;
    }

    public Vector3f getTranslation() {
        return translation;
    }

    public void setTranslation(Vector3f translation) {
        this.translation = translation;
    }

    public static abstract class Builder<T extends Builder<T>> {

        private final EntityType<?> entityType;
        private Vector3f translation = new Vector3f(0, 0, 0);
        private Vector3f scale = new Vector3f(1, 1, 1);
        private Quaternionf rightRotation = new Quaternionf();
        private Quaternionf leftRotation = new Quaternionf();
        private Display.Billboard billboard = Display.Billboard.CENTER;
        private Display.Brightness brightness = EntitiesUtils.MAX_BRIGHTNESS;
        private float viewRange = DEFAULT_VIEW_RANGE;
        private float width = 0;
        private float height = 0;
        private int glowColor = -1;

        public Builder(EntityType<?> entityType) {
            this.entityType = entityType;
        }

        @SuppressWarnings("unchecked")
        T self() {
            return (T) this;
        }

        public T setTranslation(Vector3f translation) {
            this.translation = translation;
            return self();
        }

        public T setScale(Vector3f scale) {
            this.scale = scale;
            return self();
        }

        public T setRightRotation(Quaternionf rightRotation) {
            this.rightRotation = rightRotation;
            return self();
        }

        public T setLeftRotation(Quaternionf leftRotation) {
            this.leftRotation = leftRotation;
            return self();
        }

        public T setBillboard(Display.Billboard billboard) {
            this.billboard = billboard;
            return self();
        }

        public T setBrightness(Display.Brightness brightness) {
            this.brightness = brightness;
            return self();
        }

        public T setViewRange(float viewRange) {
            this.viewRange = viewRange;
            return self();
        }

        public T setWidth(float width) {
            this.width = width;
            return self();
        }

        public T setHeight(float height) {
            this.height = height;
            return self();
        }

        public T setGlowColor(int glowColor) {
            this.glowColor = glowColor;
            return self();
        }

        public abstract HologramData build();

    }
}

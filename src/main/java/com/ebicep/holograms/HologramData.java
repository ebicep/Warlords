package com.ebicep.holograms;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.ReflectionUtils;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Display;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    protected List<SynchedEntityData.DataValue<?>> getData() {
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        try {
            data.add(createDataValue(Entity.class, "DATA_SHARED_FLAGS_ID", (byte) 0));
            data.add(createDataValue(Entity.class, "DATA_AIR_SUPPLY_ID", 0));
            data.add(createDataValue(Entity.class, "DATA_CUSTOM_NAME_VISIBLE", false));
            data.add(createDataValue(Entity.class, "DATA_CUSTOM_NAME", Optional.empty()));
            data.add(createDataValue(Entity.class, "DATA_SILENT", false));
            data.add(createDataValue(Entity.class, "DATA_NO_GRAVITY", false));
            data.add(createDataValue(Entity.class, "DATA_POSE", Pose.STANDING));

            data.add(createDataValue(Entity.class, "DATA_TICKS_FROZEN", 0));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_TRANSLATION_ID", translation));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_SCALE_ID", scale));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_RIGHT_ROTATION_ID", rightRotation));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_LEFT_ROTATION_ID", leftRotation));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_BILLBOARD_RENDER_CONSTRAINTS_ID", (byte) billboard.ordinal()));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_BRIGHTNESS_OVERRIDE_ID", brightness.getBlockLight() << 4 | brightness.getSkyLight() << 20));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_VIEW_RANGE_ID", viewRange));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_WIDTH_ID", width));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_HEIGHT_ID", height));
            data.add(createDataValue(net.minecraft.world.entity.Display.class, "DATA_GLOW_COLOR_OVERRIDE_ID", glowColor));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ChatUtils.MessageType.HOLOGRAMS.sendErrorMessage(e);
        }
        return data;
    }

    protected static <T, R> SynchedEntityData.DataValue<?> createDataValue(Class<T> clazz, String variableName, R value) throws NoSuchFieldException, IllegalAccessException {
        return SynchedEntityData.DataValue.create(ReflectionUtils.getStaticField(clazz, variableName), value);
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
}

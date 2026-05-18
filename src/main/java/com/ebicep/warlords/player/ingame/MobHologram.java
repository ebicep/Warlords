package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Zombie;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BoundingBox;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class MobHologram {

    protected final List<CustomHologramLine> customHologramLines = new ArrayList<>(); // lines to add on top of default health and name
    protected boolean hidden = false;

    @Nullable
    public abstract Entity getEntity();

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (hidden) {
            customHologramLines.forEach(customHologramLine -> {
                if (customHologramLine.getEntity() != null) {
                    customHologramLine.getEntity().remove();
                }
            });
        } else {
            update();
        }
    }

    public void update() {
        if (hidden) {
            return;
        }
        Entity entity = getEntity();
        if (entity == null) {
            return;
        }
        customHologramLines.removeIf(customHologramLine -> {
            if (customHologramLine.isDelete()) {
                Entity hologramLineEntity = customHologramLine.getEntity();
                if (hologramLineEntity == null || !hologramLineEntity.isValid()) {
                    return true;
                }
                hologramLineEntity.remove();
                return true;
            }
            return false;
        });
        update(entity);
    }

    protected void update(@Nonnull Entity entity) {
    }

    public List<CustomHologramLine> getCustomHologramLines() {
        return customHologramLines;
    }

    @Deprecated
    public static abstract class ArmorStandHologram extends MobHologram {

        @Override
        protected void update(@Nonnull Entity entity) {
            Location location = entity.getLocation();
            double y = entity.getHeight();
            for (int i = 0; i < customHologramLines.size(); i++) {
                CustomHologramLine customHologramLine = customHologramLines.get(i);
                if (customHologramLine.getEntity() == null) {
                    customHologramLine.setEntity(Utils.spawnArmorStand(location.add(0, y + (i + 1) * 0.275, 0), armorStand -> {
                        armorStand.setMarker(true);
                        armorStand.customName(customHologramLine.getText());
                        armorStand.setCustomNameVisible(true);
                    }));
                } else {
                    customHologramLine.getEntity().customName(customHologramLine.getText());
                    customHologramLine.getEntity().teleport(entity.getLocation().add(0, y + (i + 1) * 0.275, 0));
                }
            }
        }

    }

    public static abstract class TextDisplayHologram extends MobHologram {

        private static final double BASE_HEALTH_NAME_CLEARANCE = .525;
        private static final double SCALE_HEIGHT_CLEARANCE_MULTIPLIER = 1.5;
        private static final double MIN_HEALTH_NAME_CLEARANCE = .12;
        private static final double MAX_HEALTH_NAME_CLEARANCE = 2.25;
        private static final double BASE_LINE_SPACING = .31;

        protected float viewRange;

        public TextDisplayHologram(float viewRange) {
            this.viewRange = viewRange;
        }

        @Override
        protected void update(@Nonnull Entity entity) {
            double entityScale = getEntityScale(entity);
            double scaledHeight = getBoundingBoxHeight(entity);
            double unscaledHeight = scaledHeight / entityScale;
            float displaySize = getDisplaySize(entity);
            double verticalClearance = getVerticalClearance(scaledHeight, unscaledHeight, displaySize);
            double lineSpacing = BASE_LINE_SPACING * Math.max(.5, displaySize);
            Location bottomLineLocation = getBottomLineLocation(entity, verticalClearance);

            for (int i = 0; i < customHologramLines.size(); i++) {
                CustomHologramLine customHologramLine = customHologramLines.get(i);
                Location lineLocation = bottomLineLocation.clone().add(0, i * lineSpacing, 0);
                Entity lineEntity = customHologramLine.getEntity();

                if (lineEntity == null || !lineEntity.isValid()) {
                    TextDisplay textDisplay = lineLocation.getWorld().spawn(lineLocation, TextDisplay.class, display -> {
                        applyTextDisplaySettings(display, customHologramLine, displaySize);
                    });
                    customHologramLine.setEntity(textDisplay);
                } else if (lineEntity instanceof TextDisplay textDisplay) {
                    applyTextDisplaySettings(textDisplay, customHologramLine, displaySize);
                    textDisplay.teleport(lineLocation);
                }
            }
        }

        private Location getBottomLineLocation(Entity entity, double verticalClearance) {
            BoundingBox boundingBox = entity.getBoundingBox();

            double x = (boundingBox.getMinX() + boundingBox.getMaxX()) / 2;
            double y = boundingBox.getMaxY() + verticalClearance;
            double z = (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2;

            return new Location(entity.getWorld(), x, y, z, entity.getLocation().getYaw(), entity.getLocation().getPitch());
        }

        private double getVerticalClearance(double scaledHeight, double unscaledHeight, float displaySize) {
            double scaledExtraHeight = scaledHeight - unscaledHeight;
            double clearance = BASE_HEALTH_NAME_CLEARANCE * displaySize + scaledExtraHeight * SCALE_HEIGHT_CLEARANCE_MULTIPLIER;
            return clamp(clearance, MIN_HEALTH_NAME_CLEARANCE, MAX_HEALTH_NAME_CLEARANCE);
        }

        private double getBoundingBoxHeight(Entity entity) {
            BoundingBox boundingBox = entity.getBoundingBox();
            return Math.max(.1, boundingBox.getMaxY() - boundingBox.getMinY());
        }

        private double getEntityScale(Entity entity) {
            if (!(entity instanceof LivingEntity livingEntity)) {
                return 1;
            }

            AttributeInstance scaleAttribute = livingEntity.getAttribute(Attribute.SCALE);
            if (scaleAttribute == null) {
                return 1;
            }

            return Math.max(.1, scaleAttribute.getValue());
        }

        protected float getDisplaySize(Entity entity) {
            if (entity instanceof Zombie zombie && zombie.isBaby()) {
                return .5f;
            }
            return 1;
        }

        private double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        private void applyTextDisplaySettings(TextDisplay textDisplay, CustomHologramLine customHologramLine, float displaySize) {
            Component text = customHologramLine.getText();

            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setCustomNameVisible(false);
            textDisplay.setSeeThrough(false);
            textDisplay.setTeleportDuration(3);
            textDisplay.setViewRange(viewRange);

            if (!Objects.equals(textDisplay.text(), text)) {
                textDisplay.text(text);
            }

            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f(displaySize, displaySize, displaySize),
                    new Quaternionf()
            ));
        }

    }

    public static class CustomHologramLine {

        private Component text;
        private Supplier<Component> textSupplier = null;
        private boolean delete;
        private Entity entity;

        public CustomHologramLine(Component text) {
            this.text = text;
        }

        public CustomHologramLine(Supplier<Component> textSupplier) {
            this.textSupplier = textSupplier;
        }

        @Override
        public String toString() {
            return "CustomHologramLine{" +
                    "text=" + text +
                    ", textSupplier=" + textSupplier.get().toString() +
                    ", delete=" + delete +
                    ", entity=" + entity +
                    '}';
        }

        public Component getText() {
            if (textSupplier != null) {
                return textSupplier.get();
            }
            return text;
        }

        public void setText(Component text) {
            this.text = text;
        }

        public void setTextSupplier(Supplier<Component> textSupplier) {
            this.textSupplier = textSupplier;
        }

        public boolean isDelete() {
            return delete;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }
    }
}

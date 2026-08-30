package com.ebicep.warlords.player.ingame;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.entity.Zombie;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class MobHologram {

    public static final int POSITION_UPDATE_INTERVAL = 2;
    public static final int TELEPORT_DURATION = 3;

    protected final List<CustomHologramLine> customHologramLines = new ArrayList<>();
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
        update(true);
    }

    public void updatePosition() {
        update(false);
    }

    public void markTextDirty() {
        customHologramLines.forEach(CustomHologramLine::markTextDirty);
    }

    public void setLineVisible(LineTier tier, boolean visible) {
        boolean changed = false;
        for (CustomHologramLine line : customHologramLines) {
            if (line.getTier() == tier && line.isVisible() != visible) {
                line.setVisible(visible);
                changed = true;
            }
        }
        if (changed) {
            update();
        }
    }

    private void update(boolean refreshText) {
        removeDeletedLines();

        if (hidden) {
            return;
        }

        Entity entity = getEntity();
        if (entity == null || !entity.isValid()) {
            return;
        }

        update(entity, refreshText);
    }

    private void removeDeletedLines() {
        customHologramLines.removeIf(customHologramLine -> {
            if (!customHologramLine.isDelete()) {
                return false;
            }

            Entity hologramLineEntity = customHologramLine.getEntity();
            if (hologramLineEntity != null) {
                hologramLineEntity.remove();
            }

            return true;
        });
    }

    public void removeLine(CustomHologramLine customHologramLine) {
        if (customHologramLine == null) {
            return;
        }

        Entity lineEntity = customHologramLine.getEntity();
        if (lineEntity != null) {
            lineEntity.remove();
        }

        customHologramLines.remove(customHologramLine);
    }

    public void clearLines() {
        new ArrayList<>(customHologramLines).forEach(this::removeLine);
    }

    protected void update(@Nonnull Entity entity, boolean refreshText) {
    }

    public List<CustomHologramLine> getCustomHologramLines() {
        return customHologramLines;
    }

    public enum LineTier {
        HEALTH,
        STANDARD
    }

    public static abstract class TextDisplayHologram extends MobHologram {

        private static final double HEALTH_LINE_CLEARANCE = .25;
        private static final double BASE_HEALTH_NAME_CLEARANCE = .525;
        private static final double BASE_LINE_SPACING = .31;
        private static final double POSITION_EPSILON_SQUARED = .000001;

        protected float viewRange;

        private final Location lineLocation = new Location(null, 0, 0, 0);
        private boolean hasLastPosition;
        private double lastX;
        private double lastY;
        private double lastZ;
        private org.bukkit.World lastWorld;

        public TextDisplayHologram(float viewRange) {
            this.viewRange = viewRange;
        }

        @Override
        protected void update(@Nonnull Entity entity, boolean refreshText) {
            float displaySize = getDisplaySize(entity);
            double healthClearance = HEALTH_LINE_CLEARANCE * displaySize;
            double standardBase = BASE_HEALTH_NAME_CLEARANCE * displaySize;
            double lineSpacing = BASE_LINE_SPACING * Math.max(.5, displaySize);
            BoundingBox boundingBox = entity.getBoundingBox();
            double x = (boundingBox.getMinX() + boundingBox.getMaxX()) / 2;
            double baseY = boundingBox.getMaxY();
            double z = (boundingBox.getMinZ() + boundingBox.getMaxZ()) / 2;
            boolean moved = hasMoved(entity, x, baseY, z);
            boolean shouldTeleport = !refreshText || moved;

            lineLocation.setWorld(entity.getWorld());
            lineLocation.setX(x);
            lineLocation.setZ(z);
            lineLocation.setYaw(0);
            lineLocation.setPitch(0);

            int standardIndex = 0;
            for (CustomHologramLine customHologramLine : customHologramLines) {
                if (!customHologramLine.isVisible()) {
                    removeLineEntity(customHologramLine);
                    continue;
                }

                double lineY;
                if (customHologramLine.getTier() == LineTier.HEALTH) {
                    lineY = baseY + healthClearance;
                } else {
                    lineY = baseY + standardBase + standardIndex * lineSpacing;
                    standardIndex++;
                }
                lineLocation.setY(lineY);

                Entity lineEntity = customHologramLine.getEntity();
                if (lineEntity == null || !lineEntity.isValid()) {
                    TextDisplay textDisplay = lineLocation.getWorld().spawn(lineLocation, TextDisplay.class, display -> configureTextDisplay(
                            display,
                            customHologramLine,
                            displaySize
                    ));
                    customHologramLine.setEntity(textDisplay);
                } else if (lineEntity instanceof TextDisplay textDisplay) {
                    if (customHologramLine.getDisplaySize() != displaySize) {
                        applyDisplaySize(textDisplay, displaySize);
                        customHologramLine.setDisplaySize(displaySize);
                    }
                    if (refreshText || customHologramLine.isTextDirty()) {
                        updateText(textDisplay, customHologramLine);
                    }
                    if (shouldTeleport) {
                        textDisplay.teleport(lineLocation);
                    }
                }
            }

            lastWorld = entity.getWorld();
            lastX = x;
            lastY = baseY;
            lastZ = z;
            hasLastPosition = true;
        }

        private void removeLineEntity(CustomHologramLine customHologramLine) {
            Entity lineEntity = customHologramLine.getEntity();
            if (lineEntity != null) {
                lineEntity.remove();
                customHologramLine.setEntity(null);
            }
        }

        private boolean hasMoved(Entity entity, double x, double y, double z) {
            if (!hasLastPosition || lastWorld != entity.getWorld()) {
                return true;
            }
            double deltaX = x - lastX;
            double deltaY = y - lastY;
            double deltaZ = z - lastZ;
            return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ > POSITION_EPSILON_SQUARED;
        }

        protected float getDisplaySize(Entity entity) {
            if (entity instanceof Zombie zombie && zombie.isBaby()) {
                return .5f;
            }
            return 1;
        }

        private void configureTextDisplay(TextDisplay textDisplay, CustomHologramLine customHologramLine, float displaySize) {
            textDisplay.setBillboard(Display.Billboard.CENTER);
            textDisplay.setCustomNameVisible(false);
            textDisplay.setSeeThrough(false);
            textDisplay.setTeleportDuration(TELEPORT_DURATION);
            textDisplay.setViewRange(viewRange);
            applyDisplaySize(textDisplay, displaySize);
            customHologramLine.setDisplaySize(displaySize);
            updateText(textDisplay, customHologramLine);
        }

        private void applyDisplaySize(TextDisplay textDisplay, float displaySize) {
            textDisplay.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new Quaternionf(),
                    new Vector3f(displaySize, displaySize, displaySize),
                    new Quaternionf()
            ));
        }

        private void updateText(TextDisplay textDisplay, CustomHologramLine customHologramLine) {
            Component text = customHologramLine.getText();
            if (!Objects.equals(textDisplay.text(), text)) {
                textDisplay.text(text);
            }
            customHologramLine.markTextClean();
        }

    }

    public static class CustomHologramLine {

        private Component text;
        private Supplier<Component> textSupplier = null;
        private LineTier tier = LineTier.STANDARD;
        private boolean persistent;
        private boolean visible = true;
        private boolean delete;
        private boolean textDirty = true;
        private Entity entity;
        private float displaySize = Float.NaN;

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
                    ", textSupplier=" + (textSupplier != null ? textSupplier.get() : null) +
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
            markTextDirty();
        }

        public void setTextSupplier(Supplier<Component> textSupplier) {
            this.textSupplier = textSupplier;
            markTextDirty();
        }

        public void markTextDirty() {
            textDirty = true;
        }

        public void markTextClean() {
            textDirty = false;
        }

        public boolean isTextDirty() {
            return textDirty;
        }

        public boolean isDelete() {
            return delete;
        }

        public void setDelete(boolean delete) {
            this.delete = delete;
        }

        public LineTier getTier() {
            return tier;
        }

        public void setTier(LineTier tier) {
            this.tier = tier;
        }

        public boolean isPersistent() {
            return persistent;
        }

        public void setPersistent(boolean persistent) {
            this.persistent = persistent;
        }

        public boolean isVisible() {
            return visible;
        }

        public void setVisible(boolean visible) {
            this.visible = visible;
        }

        public Entity getEntity() {
            return entity;
        }

        public void setEntity(Entity entity) {
            this.entity = entity;
        }

        public float getDisplaySize() {
            return displaySize;
        }

        public void setDisplaySize(float displaySize) {
            this.displaySize = displaySize;
        }
    }
}

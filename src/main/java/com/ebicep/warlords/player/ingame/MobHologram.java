package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
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
                customHologramLine.getEntity().remove();
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

        protected float viewRange;

        public TextDisplayHologram(float viewRange) {
            this.viewRange = viewRange;
        }

        @Override
        protected void update(@Nonnull Entity entity) {
            Location location = entity.getLocation().clone();
            location.add(0, entity.getHeight() + 0.275, 0);
            for (CustomHologramLine customHologramLine : customHologramLines) {
                Entity lineEntity = customHologramLine.getEntity();
                if (lineEntity == null || !lineEntity.isValid()) {
                    TextDisplay textDisplay = location.getWorld().spawn(location, TextDisplay.class, display -> {
                        display.setBillboard(Display.Billboard.CENTER);
                        display.text(customHologramLine.getText());
                        display.setCustomNameVisible(true);
                        display.setSeeThrough(false);
                        display.setTeleportDuration(3); // SMOOTH TELEPORTATION
                        display.setViewRange(viewRange);
                    });
                    customHologramLine.setEntity(textDisplay);
                } else if (customHologramLine.getEntity() instanceof TextDisplay textDisplay) {
                    textDisplay.text(customHologramLine.getText());
                    textDisplay.teleport(location.add(0, .325, 0));
                }
            }
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

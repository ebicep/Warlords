package com.ebicep.warlords.player.ingame;

import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class MobHologram {

    protected final List<CustomHologramLine> customHologramLines = new ArrayList<>(); // lines to add on top of default health and name

    public abstract void update();

    @Nullable
    public abstract Entity getEntity();

    public List<CustomHologramLine> getCustomHologramLines() {
        return customHologramLines;
    }

    public static abstract class ArmorStandHologram extends MobHologram {

        @Override
        public void update() {
            Entity entity = getEntity();
            if (entity == null) {
                return;
            }
            Location location = entity.getLocation();
            double y = entity.getHeight();
            customHologramLines.removeIf(customHologramLine -> {
                if (customHologramLine.isDelete()) {
                    customHologramLine.getEntity().remove();
                    return true;
                }
                return false;
            });
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

        @Override
        public void update() {
            Entity entity = getEntity();
            if (entity == null) {
                return;
            }
            Location location = entity.getLocation();
            double y = entity.getHeight() + 0.275;
            customHologramLines.removeIf(customHologramLine -> {
                if (customHologramLine.isDelete()) {
                    customHologramLine.getEntity().remove();
                    return true;
                }
                return false;
            });
            for (int i = 0; i < customHologramLines.size(); i++) {
                CustomHologramLine customHologramLine = customHologramLines.get(i);
                Entity lineEntity = customHologramLine.getEntity();
                if (lineEntity == null || !lineEntity.isValid()) {
                    float yTranslation = (i + 1.65f) * 0.325f;
                    TextDisplay textDisplay = location.getWorld().spawn(location.add(0, y, 0), TextDisplay.class, display -> {
                        display.setBillboard(Display.Billboard.VERTICAL);  // TODO find way to make billboard center without messing up rotation due to text rotating based on non translated location
                        display.text(customHologramLine.getText());
                        display.setCustomNameVisible(true);
                        entity.addPassenger(display);
                        Transformation transformation = display.getTransformation();
                        transformation.getTranslation().add(0, yTranslation, 0);
                        display.setTransformation(transformation);
                    });
                    customHologramLine.setEntity(textDisplay);
                }

//                else {
//                    customHologramLine.getEntity().customName(customHologramLine.getText());
//                    customHologramLine.getEntity().teleport(entity.getLocation().add(0, y + (i + 1) * 0.275, 0));
//                }
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

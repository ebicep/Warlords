package com.ebicep.holograms.lines;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TextDisplay;

public class TextHologramLine extends AbstractHologramLine {

    private Component text;
    private int lineWidth = 0;
    private byte opacity = 0;
    private boolean isShadowed = false;
    private boolean isSeeThrough = false;
    private boolean defaultBackground = false;
    private TextDisplay.TextAlignment alignment = TextDisplay.TextAlignment.CENTER;

    public TextHologramLine(
            Component text,
            int lineWidth,
            byte opacity,
            boolean isShadowed,
            boolean isSeeThrough,
            boolean defaultBackground,
            TextDisplay.TextAlignment alignment
    ) {
        this.text = text;
        this.lineWidth = lineWidth;
        this.opacity = opacity;
        this.isShadowed = isShadowed;
        this.isSeeThrough = isSeeThrough;
        this.defaultBackground = defaultBackground;
        this.alignment = alignment;
    }

    public Component getText() {
        return text;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public byte getOpacity() {
        return opacity;
    }

    public boolean isShadowed() {
        return isShadowed;
    }

    public boolean isSeeThrough() {
        return isSeeThrough;
    }

    public boolean isDefaultBackground() {
        return defaultBackground;
    }

    public TextDisplay.TextAlignment getAlignment() {
        return alignment;
    }

    @Override
    public Entity create(Location location) {
//        Display.TextDisplay textDisplay = new Display.TextDisplay(EntityType.TEXT_DISPLAY, location
//                .getWorld());
//
//        //new TextDisplay()
//        PacketUtils.spawnEntityForPlayer(null, textDisplay);
        return null;
    }

    static class Builder {

        private Component text;
        private int lineWidth;
        private byte opacity;
        private boolean isShadowed;
        private boolean isSeeThrough;
        private boolean defaultBackground;
        private TextDisplay.TextAlignment alignment;

        public Builder setText(Component text) {
            this.text = text;
            return this;
        }

        public Builder setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
            return this;
        }

        public Builder setOpacity(byte opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setIsShadowed(boolean isShadowed) {
            this.isShadowed = isShadowed;
            return this;
        }

        public Builder setIsSeeThrough(boolean isSeeThrough) {
            this.isSeeThrough = isSeeThrough;
            return this;
        }

        public Builder setDefaultBackground(boolean defaultBackground) {
            this.defaultBackground = defaultBackground;
            return this;
        }

        public Builder setAlignment(TextDisplay.TextAlignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public TextHologramLine createTextHologramLine() {
            return new TextHologramLine(text, lineWidth, opacity, isShadowed, isSeeThrough, defaultBackground, alignment);
        }
    }
}

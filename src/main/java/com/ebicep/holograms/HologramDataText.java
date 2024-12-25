package com.ebicep.holograms;

import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.chat.DefaultFontInfo;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class HologramDataText extends HologramData {

    private Component component;
    private int lineWidth; // wrap width
    private int backgroundColor;
    private byte textOpacity;
    private byte flags;

    private HologramDataText(Builder<?> builder) {
        super(builder);
        this.component = builder.component;
        this.lineWidth = builder.lineWidth;
        this.backgroundColor = builder.backgroundColor;
        this.textOpacity = builder.textOpacity;
        this.flags = builder.flags;
    }

    @Override
    public InteractData.AutoData getAutoInteractData() {
        // TODO test
        String text = LegacyComponentSerializer.legacySection().serialize(component);
        String[] strings = text.split("\n");
        String longest = Arrays.stream(strings)
                               .max(Comparator.comparingInt(String::length))
                               .orElse("");
        int maxStringLength = DefaultFontInfo.getStringLength(longest);
        float width = maxStringLength / 5f / 8;
        float height = (0.5f * (maxStringLength / lineWidth + 1) / 2) * strings.length;
        // rescale based on scale
        width *= scale.x();
        height *= scale.y();
        return new InteractData.AutoData(width, height);
    }

    @Override
    protected List<SynchedEntityData.DataValue<?>> getData() {
        List<SynchedEntityData.DataValue<?>> data = super.getData();
        try {
            data.add(HologramManager.createDataValue(Display.TextDisplay.class, "DATA_TEXT_ID", PaperAdventure.asVanilla(component)));
            data.add(HologramManager.createDataValue(Display.TextDisplay.class, "DATA_LINE_WIDTH_ID", lineWidth));
            data.add(HologramManager.createDataValue(Display.TextDisplay.class, "DATA_BACKGROUND_COLOR_ID", backgroundColor));
            data.add(HologramManager.createDataValue(Display.TextDisplay.class, "DATA_TEXT_OPACITY_ID", textOpacity));
            data.add(HologramManager.createDataValue(Display.TextDisplay.class, "DATA_STYLE_FLAGS_ID", flags));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ChatUtils.MessageType.HOLOGRAMS.sendErrorMessage(e);
        }
        return data;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public byte getTextOpacity() {
        return textOpacity;
    }

    public void setTextOpacity(byte textOpacity) {
        this.textOpacity = textOpacity;
    }

    public byte getFlags() {
        return flags;
    }

    public void setFlags(byte flags) {
        this.flags = flags;
    }

    public static class Builder<T extends Builder<T>> extends HologramData.Builder<T> {

        private final Component component;
        private int lineWidth = 200;
        private int backgroundColor = 1073741824;
        private byte textOpacity = -1;
        private byte flags = 0;

        public Builder(Component component) {
            super(EntityType.TEXT_DISPLAY);
            this.component = component;
        }

        public T setLineWidth(int lineWidth) {
            this.lineWidth = lineWidth;
            return self();
        }

        public T setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return self();
        }

        public T setTextOpacity(byte textOpacity) {
            this.textOpacity = textOpacity;
            return self();
        }

        public T setFlags(byte flags) {
            this.flags = flags;
            return self();
        }

        @Override
        public HologramDataText build() {
            return new HologramDataText(this);
        }

    }
}

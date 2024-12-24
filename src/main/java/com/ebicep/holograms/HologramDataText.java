package com.ebicep.holograms;

import com.ebicep.warlords.util.chat.ChatUtils;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;

import java.util.List;

public class HologramDataText extends HologramData {

    private Component component;
    private int lineWidth = 200;
    private int backgroundColor = 1073741824;
    private byte textOpacity = -1;
    private byte flags = 0;

    public HologramDataText(Component component) {
        super(EntityType.TEXT_DISPLAY);
        this.component = component;
    }

    @Override
    protected List<SynchedEntityData.DataValue<?>> getData() {
        List<SynchedEntityData.DataValue<?>> data = super.getData();
        try {
            data.add(createDataValue(Display.TextDisplay.class, "DATA_TEXT_ID", PaperAdventure.asVanilla(component)));
            data.add(createDataValue(Display.TextDisplay.class, "DATA_LINE_WIDTH_ID", lineWidth));
            data.add(createDataValue(Display.TextDisplay.class, "DATA_BACKGROUND_COLOR_ID", backgroundColor));
            data.add(createDataValue(Display.TextDisplay.class, "DATA_TEXT_OPACITY_ID", textOpacity));
            data.add(createDataValue(Display.TextDisplay.class, "DATA_STYLE_FLAGS_ID", flags));
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

}

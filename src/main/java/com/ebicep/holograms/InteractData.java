package com.ebicep.holograms;

import com.ebicep.warlords.util.chat.ChatUtils;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.Pose;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InteractData {

    private final float width; // -1 = auto fit
    private final float height; // -1 = auto fit
    private final boolean responsive;

    public InteractData(float width, float height, boolean responsive) {
        this.width = width;
        this.height = height;
        this.responsive = responsive;
    }

    public List<SynchedEntityData.DataValue<?>> getData(Hologram hologram, Player player) {
        List<SynchedEntityData.DataValue<?>> data = new ArrayList<>();
        try {
            data.add(HologramManager.createDataValue(Entity.class, "DATA_SHARED_FLAGS_ID", (byte) 0));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_AIR_SUPPLY_ID", 0));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_CUSTOM_NAME_VISIBLE", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_CUSTOM_NAME", Optional.empty()));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_SILENT", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_NO_GRAVITY", false));
            data.add(HologramManager.createDataValue(Entity.class, "DATA_POSE", Pose.STANDING));

            HologramData hologramData = hologram.getDataForPlayer(player);
            AutoData autoInteractData = hologramData.getAutoInteractData();
            data.add(HologramManager.createDataValue(Interaction.class, "DATA_WIDTH_ID", width == -1 ? autoInteractData.width() : width));
            data.add(HologramManager.createDataValue(Interaction.class, "DATA_HEIGHT_ID", height == -1 ? autoInteractData.height() : height));
            data.add(HologramManager.createDataValue(Interaction.class, "DATA_RESPONSE_ID", responsive));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            ChatUtils.MessageType.HOLOGRAMS.sendErrorMessage(e);
        }
        return data;
    }

    public record AutoData(float width, float height) {

    }

    public static class Builder {

        private float width = -1;
        private float height = -1;
        private boolean responsive = true;

        public Builder setWidth(float width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(float height) {
            this.height = height;
            return this;
        }

        public Builder setResponsive(boolean responsive) {
            this.responsive = responsive;
            return this;
        }

        public InteractData build() {
            return new InteractData(width, height, responsive);
        }
    }
}

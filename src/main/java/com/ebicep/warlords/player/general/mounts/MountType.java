package com.ebicep.warlords.player.general.mounts;

import com.ebicep.customentities.nms.CustomGoat;
import com.ebicep.customentities.nms.CustomHoglin;
import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.customentities.nms.CustomLlama;
import com.ebicep.customentities.nms.CustomPig;
import com.ebicep.customentities.nms.CustomRavager;
import com.ebicep.customentities.nms.CustomSkeletonHorse;
import com.ebicep.customentities.nms.CustomSniffer;
import com.ebicep.customentities.nms.CustomZombieHorse;
import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.pve.Currencies;
import net.minecraft.world.entity.Mob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

import javax.annotation.Nullable;

public enum MountType {

    BROWN("Brown Horse", Material.HORSE_SPAWN_EGG, Horse.Color.BROWN, false),
    WHITE("White Horse", Material.WHITE_WOOL, Horse.Color.WHITE, true),
    SKELETON("Skeleton Horse", Material.SKELETON_HORSE_SPAWN_EGG, null, true),
    ZOMBIE("Zombie Horse", Material.ZOMBIE_HORSE_SPAWN_EGG, null, true),
    RAVAGER("Ravager", Material.RAVAGER_SPAWN_EGG, null, true, 0.85),
    PIG("Pig", Material.PIG_SPAWN_EGG, null, true),
    LLAMA("Llama", Material.LLAMA_SPAWN_EGG, null, true),
    HOGLIN("Hoglin", Material.HOGLIN_SPAWN_EGG, null, false),
    SNIFFER("Sniffer", Material.SNIFFER_SPAWN_EGG, null, false),
    GOAT("Goat", Material.GOAT_SPAWN_EGG, null, false),

    ;

    public static final MountType[] VALUES = values();
    public static final HonorificCost COST = HonorificCost.of(Currencies.FAIRY_ESSENCE, 50_000);

    private final String displayName;
    private final Material icon;
    @Nullable
    private final Horse.Color color;
    private final boolean supporterExclusive;
    private final double scale;

    MountType(String displayName, Material icon, @Nullable Horse.Color color, boolean supporterExclusive) {
        this(displayName, icon, color, supporterExclusive, 1.0);
    }

    MountType(String displayName, Material icon, @Nullable Horse.Color color, boolean supporterExclusive, double scale) {
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
        this.supporterExclusive = supporterExclusive;
        this.scale = scale;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    @Nullable
    public Horse.Color getColor() {
        return color;
    }

    public boolean isSupporterExclusive() {
        return supporterExclusive;
    }

    public double getScale() {
        return scale;
    }

    public boolean isHorse() {
        return switch (this) {
            case BROWN, WHITE, SKELETON, ZOMBIE -> true;
            case RAVAGER, PIG, LLAMA, HOGLIN, SNIFFER, GOAT -> false;
        };
    }

    @Nullable
    public HonorificCost getCost() {
        return this == BROWN ? null : COST;
    }

    public Mob create(Location location) {
        return switch (this) {
            case SKELETON -> new CustomSkeletonHorse(location);
            case ZOMBIE -> new CustomZombieHorse(location);
            case RAVAGER -> new CustomRavager(location);
            case PIG -> new CustomPig(location);
            case LLAMA -> new CustomLlama(location);
            case HOGLIN -> new CustomHoglin(location);
            case SNIFFER -> new CustomSniffer(location);
            case GOAT -> new CustomGoat(location);
            case BROWN, WHITE -> new CustomHorse(location);
        };
    }

}

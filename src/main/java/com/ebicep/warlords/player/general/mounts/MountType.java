package com.ebicep.warlords.player.general.mounts;

import com.ebicep.customentities.nms.CustomHorse;
import com.ebicep.customentities.nms.CustomSkeletonHorse;
import com.ebicep.customentities.nms.CustomZombieHorse;
import com.ebicep.warlords.honorifics.HonorificCost;
import com.ebicep.warlords.pve.Currencies;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;

import javax.annotation.Nullable;

public enum MountType {

    BROWN("Brown Horse", Material.HORSE_SPAWN_EGG, Horse.Color.BROWN, false),
    WHITE("White Horse", Material.WHITE_WOOL, Horse.Color.WHITE, true),
    SKELETON("Skeleton Horse", Material.SKELETON_HORSE_SPAWN_EGG, null, true),
    ZOMBIE("Zombie Horse", Material.ZOMBIE_HORSE_SPAWN_EGG, null, true),

    ;

    public static final MountType[] VALUES = values();
    public static final HonorificCost COST = HonorificCost.of(Currencies.FAIRY_ESSENCE, 50_000);

    private final String displayName;
    private final Material icon;
    @Nullable
    private final Horse.Color color;
    private final boolean supporterExclusive;

    MountType(String displayName, Material icon, @Nullable Horse.Color color, boolean supporterExclusive) {
        this.displayName = displayName;
        this.icon = icon;
        this.color = color;
        this.supporterExclusive = supporterExclusive;
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

    @Nullable
    public HonorificCost getCost() {
        return this == BROWN ? null : COST;
    }

    public AbstractHorse create(Location location) {
        return switch (this) {
            case SKELETON -> new CustomSkeletonHorse(location);
            case ZOMBIE -> new CustomZombieHorse(location);
            case BROWN, WHITE -> new CustomHorse(location);
        };
    }

}

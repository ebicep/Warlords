package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.pve.mobs.MobDrop;

public final class HonorificShopCosts {

    private HonorificShopCosts() {
    }

    /*
     * PLACEHOLDER COSTS.
     *
     * Color and font prices intentionally live in this single class. Replace
     * these Spendable definitions with the configured material or mob-drop
     * costs when the final economy values are ready.
     */

    public static HonorificCost getColorCost(HonorificColor color) {
        return switch (color) {
            case AQUA -> null;
            case GOLD -> HonorificCost.of(MobDrop.ZENITH_STAR, 5);
            case RED -> HonorificCost.of(MobDrop.ZENITH_STAR, 8);
            case GREEN -> HonorificCost.of(MobDrop.ZENITH_STAR, 10);
            case BLUE -> HonorificCost.of(MobDrop.ZENITH_STAR, 12);
            case LIGHT_PURPLE -> HonorificCost.of(MobDrop.ZENITH_STAR, 15);
            case WHITE -> HonorificCost.of(MobDrop.ZENITH_STAR, 20);
            case DARK_PURPLE -> HonorificCost.of(MobDrop.ZENITH_STAR, 25);
        };
    }

    public static HonorificCost getFontCost(HonorificFont font) {
        return switch (font) {
            case STANDARD -> null;
            case BOLD -> HonorificCost.of(MobDrop.ZENITH_STAR, 10);
            case ITALIC -> HonorificCost.of(MobDrop.ZENITH_STAR, 15);
            case SMALL_CAPS -> HonorificCost.of(MobDrop.ZENITH_STAR, 25);
        };
    }
}

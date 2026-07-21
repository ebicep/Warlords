package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.pve.Currencies;

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
            case AQUA, GOLD, DARK_RED, BLACK -> null;
            case RED, GREEN, BLUE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 2);
            case LIGHT_PURPLE, WHITE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 4);
            case DARK_PURPLE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 6);
        };
    }

    public static HonorificCost getFontCost(HonorificFont font) {
        return switch (font) {
            case STANDARD, BOLD, SMALL_CAPS -> null;
            case ITALIC -> HonorificCost.of(Currencies.PRESTIGE_ORB, 10);
        };
    }
}

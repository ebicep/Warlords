package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.pve.Currencies;

public final class HonorificShopCosts {

    private HonorificShopCosts() {
    }

    public static HonorificCost getColorCost(HonorificColor color) {
        return switch (color) {
            case AQUA, GOLD, DARK_RED, BLACK, DARK_PURPLE -> null;
            case RED, GREEN, BLUE, YELLOW -> HonorificCost.of(Currencies.PRESTIGE_ORB, 2);
            case LIGHT_PURPLE, WHITE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 4);
        };
    }

    public static HonorificCost getFontCost(HonorificFont font) {
        return switch (font) {
            case STANDARD, BOLD, SMALL_CAPS -> null;
            case ITALIC -> HonorificCost.of(Currencies.PRESTIGE_ORB, 10);
        };
    }
}

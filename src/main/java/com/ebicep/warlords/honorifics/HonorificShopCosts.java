package com.ebicep.warlords.honorifics;

import com.ebicep.warlords.pve.Currencies;
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
            case RED, GREEN, BLUE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 2);
            case LIGHT_PURPLE, WHITE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 4);
            case GOLD, DARK_PURPLE -> HonorificCost.of(Currencies.PRESTIGE_ORB, 6);
            case DARK_RED, BLACK -> HonorificCost.of(Currencies.PRESTIGE_ORB, 8);
        };
    }

    public static HonorificCost getFontCost(HonorificFont font) {
        return switch (font) {
            case STANDARD -> null;
            case BOLD -> HonorificCost.of(Currencies.PRESTIGE_ORB, 5);
            case ITALIC -> HonorificCost.of(Currencies.PRESTIGE_ORB, 10);
            case SMALL_CAPS -> HonorificCost.of(Currencies.PRESTIGE_ORB, 15);
        };
    }
}

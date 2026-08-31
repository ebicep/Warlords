package com.ebicep.warlords.pve.newitems.gems;

import com.ebicep.warlords.util.java.NamedEnum;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

public enum GemTier implements NamedEnum {

    I("I", 1, NamedTextColor.GREEN),
    II("II", 2, NamedTextColor.BLUE),
    III("III", 3, NamedTextColor.DARK_PURPLE),
    IV("IV", 4, NamedTextColor.GOLD),

    ;

    public static final GemTier[] VALUES = values();
    /**
     * How many gems of the same type and tier are consumed to create one gem of the next tier.
     */
    public static final int MERGE_AMOUNT = 3;

    private final String numeral;
    private final int multiplier;
    private final TextColor textColor;

    GemTier(String numeral, int multiplier, TextColor textColor) {
        this.numeral = numeral;
        this.multiplier = multiplier;
        this.textColor = textColor;
    }

    @Nullable
    public GemTier next() {
        return this.ordinal() + 1 >= VALUES.length ? null : VALUES[this.ordinal() + 1];
    }

    @Override
    public String getName() {
        return numeral;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public TextColor getTextColor() {
        return textColor;
    }

}

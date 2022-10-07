package com.ebicep.warlords.pve;

public enum StarPieces {

    COMMON(Currencies.COMMON_STAR_PIECE, 20),
    RARE(Currencies.RARE_STAR_PIECE, 30),
    EPIC(Currencies.EPIC_STAR_PIECE, 40),
    LEGENDARY(Currencies.LEGENDARY_STAR_PIECE, 50);

    public static final StarPieces[] VALUES = values();
    public final Currencies currency;
    public final int starPieceBonusValue;

    StarPieces(Currencies currency, int starPieceBonusValue) {
        this.currency = currency;
        this.starPieceBonusValue = starPieceBonusValue;
    }

    public StarPieces next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }
}

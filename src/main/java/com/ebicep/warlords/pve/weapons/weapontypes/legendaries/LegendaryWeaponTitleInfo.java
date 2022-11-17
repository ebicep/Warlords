package com.ebicep.warlords.pve.weapons.weapontypes.legendaries;

import com.ebicep.warlords.pve.StarPieces;
import com.ebicep.warlords.pve.weapons.WeaponStats;
import org.springframework.data.mongodb.core.mapping.Field;

public class LegendaryWeaponTitleInfo {

    @Field("star_piece")
    private StarPieces starPiece;
    @Field("star_piece_stat")
    private WeaponStats starPieceStat;

    public LegendaryWeaponTitleInfo() {
    }

    public LegendaryWeaponTitleInfo(StarPieces starPiece, WeaponStats starPieceStat) {
        this.starPiece = starPiece;
        this.starPieceStat = starPieceStat;
    }

    public void setStarPieceInfo(StarPieces starPiece, WeaponStats starPieceStat) {
        this.starPiece = starPiece;
        this.starPieceStat = starPieceStat;
    }

    public StarPieces getStarPiece() {
        return starPiece;
    }

    public WeaponStats getStarPieceStat() {
        return starPieceStat;
    }
}

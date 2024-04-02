package com.ebicep.warlords.util.pve;

public enum PlayerSkullID implements Skull {

    APUNCH("YmVlN2M3ZTJhNjNmNzI4YmI3NzhkNWFjNjMzOWZhMTZiMzc2ZjVmNTYyYjRmNGZkYWNiMzI0ODVjNmZlYyJ9fX0="),
    SUMSMASH("YzJkODY1NTNlZmJhMWJjMWFmNjZjNWU3YjcwOWMyODFjMWY0NzZjMzI3MTE0ODM1NmI2NzdlNTc2ZTMyYzRjMyJ9fX0="),
    CHESSKING345("YTE3NzBiZTEyNTlkMTc4MDQwYWE4YzY0N2M2NTAyZjY3MWI0MzFhYzc0ZDBhOGNmMjA2MzI1NDQxMWEzNjcwMSJ9fX0="),
    HEATRAN("OTRmYzljYzBlNTg5ZDQ3MThiM2YxMjFlMjFjOTI1ZDI2MjQ3Y2U1YjRmYTAyZDE2YmExZjE1YTFlNTg3NWRmMyJ9fX0="),

    ;

    public static final PlayerSkullID[] VALUES = values();
    private final String textureID;

    PlayerSkullID(String textureID) {
        this.textureID = textureID;
    }

    @Override
    public String getTextureID() {
        return "e3RleHR1cmVzOntTS0lOOnt1cmw6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + textureID;
    }


}

package com.ebicep.warlords.game.option.pve.treasurehunt;

public enum RoomFace {

    NORTH_ODD_PARITY(0, -1),
    NORTH_EVEN_PARITY(-1, -1),
    WEST_ODD_PARITY(-1, 0),
    WEST_EVEN_PARITY(-1, 1),
    EAST_OOD_PARITY(1, 0),
    EAST_EVEN_PARITY(1, -1),
    SOUTH_ODD_PARITY(0, 1),
    SOUTH_EVEN_PARTY(1, 1)

    ;

    private final int x;
    private final int z;

    RoomFace(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public boolean isOpposite(RoomFace roomFace) {
        return roomFace.getX() == -x && roomFace.getZ() == -z;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }
}

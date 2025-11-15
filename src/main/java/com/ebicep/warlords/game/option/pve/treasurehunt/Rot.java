package com.ebicep.warlords.game.option.pve.treasurehunt;

enum Rot {

    R0,
    R90,
    R180,
    R270;

    int rotX(int x, int z, int w, int d) {
        return switch (this) {
            case R0 -> x;
            case R90 -> d - 1 - z;
            case R180 -> w - 1 - x;
            case R270 -> z;
        };
    }

    int rotZ(int x, int z, int w, int d) {
        return switch (this) {
            case R0 -> z;
            case R90 -> x;
            case R180 -> d - 1 - z;
            case R270 -> w - 1 - x;
        };
    }

    int rotW(int w, int d) { return (this == R0 || this == R180) ? w : d; }

    int rotD(int w, int d) { return (this == R0 || this == R180) ? d : w; }
}

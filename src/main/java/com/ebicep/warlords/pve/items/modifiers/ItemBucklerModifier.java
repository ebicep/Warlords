package com.ebicep.warlords.pve.items.modifiers;

public class ItemBucklerModifier {

    public enum Curses implements ItemCurse<Curses> {
        LARGE,
        MASSIVE,
        KINGLY,
        COLOSSAL,
        GARGANTUAN

    }

    public enum Blessings implements ItemBlessing<Blessings> {
        DWARFISH,
        TINY,
        COMPACT,
        FEATHERY,
        DIAPHANOUS
    }

}

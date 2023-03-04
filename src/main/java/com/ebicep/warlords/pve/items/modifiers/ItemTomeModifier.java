package com.ebicep.warlords.pve.items.modifiers;

public class ItemTomeModifier {

    public enum Curses implements ItemCurse<Curses> {
        BRIEF,
        FLETTING,
        NIMBLE,
        ABRUPT,
        INSTANTANEOUS
    }

    public enum Blessings implements ItemBlessing<Blessings> {
        DELAYED,
        STRETCHED,
        PROLONGED,
        LINGERING,
        ENDURING
    }

}

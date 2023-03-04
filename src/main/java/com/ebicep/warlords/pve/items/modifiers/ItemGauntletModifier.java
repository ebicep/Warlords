package com.ebicep.warlords.pve.items.modifiers;

public class ItemGauntletModifier {

    public enum Blessings implements ItemBlessing<Blessings> {
        STRONG,
        POWERFUL,
        EXPLOSIVE,
        VIOLENT,
        BELIGERENT
    }

    public enum Curses implements ItemCurse<Curses> {
        DINKY,
        MEEK,
        KIND,
        GRACFUL,
        FRIENDLY

    }

}

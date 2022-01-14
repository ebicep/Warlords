package com.ebicep.warlords.classes.rogue.specs.apothecary;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Apothecary extends AbstractRogue {

    public Apothecary() {
        super("Apothecary", 5750, 305, 0,
                new ImpalingStrike(), //medy =( rip
                new HealingRemedy(), //shotty
                new temp(), //deathy
                new temp(), //curey
                new temp() //pharmy
        );
    }
}

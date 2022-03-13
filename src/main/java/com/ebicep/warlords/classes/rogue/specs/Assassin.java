package com.ebicep.warlords.classes.rogue.specs;

import com.ebicep.warlords.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Assassin extends AbstractRogue {

    public Assassin() {
        super("Assassin", 5200, 305, 0,
                new JudgementStrike(),
                new IncendiaryCurse(),
                new ShadowStep(),
                new SoulSwitch(),
                new OrderOfEviscerate()
        );
    }
}

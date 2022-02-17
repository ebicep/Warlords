package com.ebicep.warlords.classes.rogue.specs.assassin;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Assassin extends AbstractRogue {

    public Assassin() {
        super("Assassin", 5200, 305, 0,
                new JudgementStrike(),
                new IncendiaryCurse(),
                new BlindingAssault(),
                new SoulSwitch(),
                new OrderOfEviscerate()
        );
    }
}

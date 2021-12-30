package com.ebicep.warlords.classes.rogue.specs.untitledsupport;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Untitled2 extends AbstractRogue {

    public Untitled2() {
        super("Untitled Support", 5750, 305, 0,
                new JudgementStrike(),
                new IncendiaryCurse(),
                new BlindingAssault(),
                new CrossVital(),
                new OrderOfEviscerate()
        );
    }
}

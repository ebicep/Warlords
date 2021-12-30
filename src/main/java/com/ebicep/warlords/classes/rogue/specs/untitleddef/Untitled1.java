package com.ebicep.warlords.classes.rogue.specs.untitleddef;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Untitled1 extends AbstractRogue {

    public Untitled1() {
        super("Untitled Defense", 5750, 305, 0,
                new JudgementStrike(),
                new IncendiaryCurse(),
                new BlindingAssault(),
                new CrossVital(),
                new OrderOfEviscerate()
        );
    }
}

package com.ebicep.warlords.classes.rogue.specs.assassin;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.rogue.AbstractRogue;

public class Assassin extends AbstractRogue {

    public Assassin() {
        super("Assassin", 5750, 355, 0,
                new JudgementStrike(),
                new temp(),
                new temp(),
                new temp(),
                new temp()
        );
    }
}

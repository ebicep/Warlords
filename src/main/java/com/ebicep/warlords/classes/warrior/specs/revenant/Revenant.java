package com.ebicep.warlords.classes.warrior.specs.revenant;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Revenant extends AbstractWarrior {
    public Revenant() {
        super(6300, 305, 0,
                new CripplingStrike(),
                new RecklessCharge(),
                new GroundSlam("Ground Slam", -326, -441, 7.34f, 30, 35, 200),
                new OrbsOfLife(),
                new UndyingArmy());
    }
}

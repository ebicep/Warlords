package com.ebicep.warlords.classes.warrior.specs.defender;

import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;

public class Defender extends AbstractWarrior {

    public Defender() {
        super(7400, 305, 10,
                new Strike("Wounding Strike", -416, -556, 0, 100, 20, 200,
                        "§7Strike the targeted enemy player,\n" +
                                "§7causing §c416 §7- §c556 §7damage\n" +
                                "§7and §cwounding §7them for §63 §7seconds.\n" +
                                "§7A wounded player receives §c25% §7less\n" +
                                "§7healing for the duration of the effect."),

                new SeismicWave("Seismic Wave", -506, -685, 12, 60, 25, 200,
                        "§7Send a wave of incredible force forward\n" +
                                "§7that deals §c506 §7- §c685 §7damage\n" +
                                "§7to all enemies hit and knocks them back\n" +
                                "§7slightly."),

                new GroundSlam("Ground Slam", -326, -441, 7, 0, 15, 200,
                        "§7Slam the ground, creating a shockwave\n" +
                                "§7around you that deals §c326 §7- §c441\n" +
                                "§7damage and knocks enemies back slightly."),

                new Intervene(),
                new LastStand());
    }

}

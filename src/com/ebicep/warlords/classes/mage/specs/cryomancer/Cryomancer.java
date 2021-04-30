package com.ebicep.warlords.classes.mage.specs.cryomancer;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.IceBarrier;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Cryomancer extends AbstractMage {
    public Cryomancer(Player player) {
        super(player, 6200, 305, 20, 14, 10,
                new temp(),
                new temp(),
                new TimeWarp(),
                new ArcaneShield(),
                new IceBarrier());
    }
}

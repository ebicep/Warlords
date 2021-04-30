package com.ebicep.warlords.classes.mage.specs.aquamancer;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.abilties.ArcaneShield;
import com.ebicep.warlords.classes.abilties.HealingRain;
import com.ebicep.warlords.classes.abilties.TimeWarp;
import com.ebicep.warlords.classes.abilties.temp;
import com.ebicep.warlords.classes.mage.AbstractMage;
import org.bukkit.entity.Player;

public class Aquamancer extends AbstractMage {
    public Aquamancer(Player player) {
        super(player, 5200, 355, 20, 14, 0,
                new temp(),
                new temp(),
                new TimeWarp(),
                new ArcaneShield(),
                new HealingRain());
    }
}

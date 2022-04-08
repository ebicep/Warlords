package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.java.Pair;
import org.bukkit.entity.Player;

import java.util.List;

public class temp extends AbstractAbility {

    public temp() {
        super("temp", 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void updateDescription(Player player) {

    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        return null;
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {

        return true;
    }

}
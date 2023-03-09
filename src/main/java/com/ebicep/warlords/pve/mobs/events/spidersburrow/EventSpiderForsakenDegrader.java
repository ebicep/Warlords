package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.CripplingStrike;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import org.bukkit.Location;

public class EventSpiderForsakenDegrader extends Spider {

    public EventSpiderForsakenDegrader(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        // Applies crippling to enemies for 3s.
        CripplingStrike.cripple(attacker, receiver, name, 3 * 20);
    }
}

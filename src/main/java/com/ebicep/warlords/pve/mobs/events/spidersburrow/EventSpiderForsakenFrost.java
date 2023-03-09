package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import com.ebicep.warlords.util.warlords.PlayerFilterGeneric;
import org.bukkit.Location;

public class EventSpiderForsakenFrost extends Spider {

    public EventSpiderForsakenFrost(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        // Slows enemies by 20% every 3s.
        if (ticksElapsed % 60 == 0) {
            PlayerFilterGeneric.playingGameWarlordsPlayers(option.getGame())
                               .enemiesOf(warlordsNPC)
                               .forEach(warlordsPlayer -> warlordsPlayer.addSpeedModifier(warlordsPlayer, name, -20, 20, "BASE"));
        }
    }

}

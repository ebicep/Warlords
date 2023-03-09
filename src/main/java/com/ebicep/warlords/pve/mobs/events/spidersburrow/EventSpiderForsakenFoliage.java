package com.ebicep.warlords.pve.mobs.events.spidersburrow;

import com.ebicep.warlords.abilties.Earthliving;
import com.ebicep.warlords.game.option.PveOption;
import com.ebicep.warlords.pve.mobs.spider.Spider;
import org.bukkit.Location;

public class EventSpiderForsakenFoliage extends Spider {

    public EventSpiderForsakenFoliage(Location spawnLocation) {
        super(spawnLocation);
    }

    @Override
    public void onSpawn(PveOption option) {
        // Attacks are converted into Earth Living with double the proc chance as standard.
        Earthliving earthliving = new Earthliving();
        earthliving.setProcChance(earthliving.getProcChance() * 2);
        earthliving.setDuration(60 * 15);
        earthliving.onActivate(warlordsNPC, null);
    }

}
